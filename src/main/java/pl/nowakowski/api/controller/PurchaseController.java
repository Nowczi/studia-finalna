package pl.nowakowski.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.nowakowski.api.dto.CarPurchaseDTO;
import pl.nowakowski.api.dto.CarSearchDTO;
import pl.nowakowski.api.dto.CarToBuyDTO;
import pl.nowakowski.api.dto.SalesmanDTO;
import pl.nowakowski.api.dto.mapper.CarMapper;
import pl.nowakowski.api.dto.mapper.CarPurchaseMapper;
import pl.nowakowski.api.dto.mapper.SalesmanMapper;
import pl.nowakowski.business.CarPurchaseService;
import pl.nowakowski.business.CustomerService;
import pl.nowakowski.domain.CarPurchaseRequest;
import pl.nowakowski.domain.Invoice;
import pl.nowakowski.domain.Salesman;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class PurchaseController {

    static final String PURCHASE = "/purchase";
    private static final int CARS_PER_PAGE = 10;

    private final CarPurchaseService carPurchaseService;
    private final CarPurchaseMapper carPurchaseMapper;
    private final CarMapper carMapper;
    private final SalesmanMapper salesmanMapper;
    private final CustomerService customerService;

    @GetMapping(value = PURCHASE)
    public ModelAndView carPurchasePage(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal priceFrom,
            @RequestParam(required = false) BigDecimal priceTo,
            @RequestParam(required = false, defaultValue = "0") int page) {
        
        Map<String, ?> modelData = prepareCarPurchaseData(brand, model, yearFrom, yearTo, color, priceFrom, priceTo, page);
        return new ModelAndView("car_purchase", modelData);
    }

    private Map<String, ?> prepareCarPurchaseData(String brand, String model, Integer yearFrom, Integer yearTo,
                                                   String color, BigDecimal priceFrom, BigDecimal priceTo, int page) {
        
        List<CarToBuyDTO> allAvailableCars;
        
        // Check if any search parameters are provided
        boolean hasSearchParams = brand != null || model != null || yearFrom != null || yearTo != null 
                || color != null || priceFrom != null || priceTo != null;
        
        if (hasSearchParams) {
            // Perform search with filters
            allAvailableCars = carPurchaseService.searchAvailableCars(
                    brand, model, yearFrom, yearTo, color, priceFrom, priceTo)
                .stream()
                .map(carMapper::map)
                .toList();
        } else {
            // Get all available cars
            allAvailableCars = carPurchaseService.availableCars().stream()
                .map(carMapper::map)
                .toList();
        }
        
        // Pagination logic
        int totalCars = allAvailableCars.size();
        int totalPages = (int) Math.ceil((double) totalCars / CARS_PER_PAGE);
        
        // Ensure page is within valid range
        if (page < 0) page = 0;
        if (totalPages > 0 && page >= totalPages) page = totalPages - 1;
        
        // Get cars for current page
        int fromIndex = page * CARS_PER_PAGE;
        int toIndex = Math.min(fromIndex + CARS_PER_PAGE, totalCars);
        List<CarToBuyDTO> paginatedCars = allAvailableCars.subList(fromIndex, toIndex);
        
        var availableCarVins = allAvailableCars.stream()
            .map(CarToBuyDTO::getVin)
            .toList();
        var availableSalesmen = carPurchaseService.availableSalesmen().stream()
            .map(salesmanMapper::map)
            .toList();
        
        return Map.of(
            "availableCarDTOs", paginatedCars,
            "availableCarVins", availableCarVins,
            "availableSalesmen", availableSalesmen,
            "carPurchaseDTO", CarPurchaseDTO.buildDefaultData(),
            "carSearchDTO", new CarSearchDTO(brand, model, yearFrom, yearTo, color, priceFrom, priceTo),
            "currentPage", page,
            "totalPages", totalPages,
            "totalCars", totalCars,
            "carsPerPage", CARS_PER_PAGE
        );
    }

    // Validation patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s-]{9,20}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{2}-[0-9]{3}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @PostMapping(value = PURCHASE)
    public String makePurchase(
        @Valid @ModelAttribute("carPurchaseDTO") CarPurchaseDTO carPurchaseDTO,
        BindingResult bindingResult,
        ModelMap model,
        RedirectAttributes redirectAttributes
    ) {
        // Check if using existing customer
        boolean isExistingCustomer = existingCustomerEmailExists(carPurchaseDTO.getExistingCustomerEmail());
        
        // Validate based on customer type
        List<String> errors = new ArrayList<>();
        
        if (isExistingCustomer) {
            // Validate existing customer email format
            if (!EMAIL_PATTERN.matcher(carPurchaseDTO.getExistingCustomerEmail()).matches()) {
                errors.add("Invalid email format. Please use format: example@domain.com");
            }
        } else {
            // Validate new customer fields
            errors.addAll(validateNewCustomer(carPurchaseDTO));
        }
        
        // If there are validation errors, redirect back with error messages
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessages", errors);
            redirectAttributes.addFlashAttribute("carPurchaseDTO", carPurchaseDTO);
            return "redirect:/purchase";
        }
        
        try {
            CarPurchaseRequest request = carPurchaseMapper.map(carPurchaseDTO);
            Invoice invoice = carPurchaseService.purchase(request);

            if (isExistingCustomer) {
                model.addAttribute("existingCustomerEmail", carPurchaseDTO.getExistingCustomerEmail());
            } else {
                model.addAttribute("customerName", carPurchaseDTO.getCustomerName());
                model.addAttribute("customerSurname", carPurchaseDTO.getCustomerSurname());
            }

            model.addAttribute("invoiceNumber", invoice.getInvoiceNumber());

            return "car_purchase_done";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing purchase: " + e.getMessage());
            redirectAttributes.addFlashAttribute("carPurchaseDTO", carPurchaseDTO);
            return "redirect:/purchase";
        }
    }
    
    private List<String> validateNewCustomer(CarPurchaseDTO dto) {
        List<String> errors = new ArrayList<>();
        
        // Name validation
        if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
            errors.add("Name is required.");
        } else if (dto.getCustomerName().trim().length() < 2) {
            errors.add("Name must be at least 2 characters long.");
        }
        
        // Surname validation
        if (dto.getCustomerSurname() == null || dto.getCustomerSurname().trim().isEmpty()) {
            errors.add("Surname is required.");
        } else if (dto.getCustomerSurname().trim().length() < 2) {
            errors.add("Surname must be at least 2 characters long.");
        }
        
        // Email validation
        if (dto.getCustomerEmail() == null || dto.getCustomerEmail().trim().isEmpty()) {
            errors.add("Email is required.");
        } else if (!EMAIL_PATTERN.matcher(dto.getCustomerEmail()).matches()) {
            errors.add("Invalid email format. Please use format: example@domain.com");
        } else {
            // Check if email already exists in database
            try {
                customerService.findCustomer(dto.getCustomerEmail().trim());
                // If we get here, the customer exists
                errors.add("This email already exists in our database. Please use the 'Existing Customer' section or use a different email.");
            } catch (Exception e) {
                // Customer not found, which is what we want for new customers
            }
        }
        
        // Phone validation
        if (dto.getCustomerPhone() == null || dto.getCustomerPhone().trim().isEmpty()) {
            errors.add("Phone number is required.");
        } else if (!PHONE_PATTERN.matcher(dto.getCustomerPhone().trim()).matches()) {
            errors.add("Invalid phone format. Please use format: +XX XXX XXX XXX or similar (9-20 digits, can include spaces, +, and -). Example: +48 123 456 789");
        }
        
        // Address validation
        if (dto.getCustomerAddressCountry() == null || dto.getCustomerAddressCountry().trim().isEmpty()) {
            errors.add("Country is required.");
        }
        
        if (dto.getCustomerAddressCity() == null || dto.getCustomerAddressCity().trim().isEmpty()) {
            errors.add("City is required.");
        }
        
        // Postal code validation (Polish format: XX-XXX)
        if (dto.getCustomerAddressPostalCode() == null || dto.getCustomerAddressPostalCode().trim().isEmpty()) {
            errors.add("Postal code is required.");
        } else if (!POSTAL_CODE_PATTERN.matcher(dto.getCustomerAddressPostalCode().trim()).matches()) {
            errors.add("Invalid postal code format. Please use Polish format: XX-XXX (e.g., 50-001)");
        }
        
        if (dto.getCustomerAddressStreet() == null || dto.getCustomerAddressStreet().trim().isEmpty()) {
            errors.add("Street address is required.");
        }
        
        return errors;
    }

    private boolean existingCustomerEmailExists(String email) {
        return Objects.nonNull(email) && !email.isBlank();
    }
}
