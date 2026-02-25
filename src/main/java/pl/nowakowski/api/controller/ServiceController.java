package pl.nowakowski.api.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.nowakowski.api.dto.CarServiceCustomerRequestDTO;
import pl.nowakowski.api.dto.mapper.CarServiceRequestMapper;
import pl.nowakowski.business.CarServiceRequestService;
import pl.nowakowski.business.CustomerService;
import pl.nowakowski.domain.CarServiceRequest;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Controller
@AllArgsConstructor
public class ServiceController {

    private static final String SERVICE_NEW = "/service/new";
    private static final String SERVICE_REQUEST = "/service/request";
    
    // Validation patterns
    private static final Pattern VIN_PATTERN = Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s-]{9,20}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{2}-[0-9]{3}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final CarServiceRequestService carServiceRequestService;
    private final CarServiceRequestMapper carServiceRequestMapper;
    private final CustomerService customerService;

    @GetMapping(value = SERVICE_NEW)
    public ModelAndView carServicePage() {
        Map<String, ?> model = Map.of(
            "carServiceRequestDTO", CarServiceCustomerRequestDTO.buildDefault()
        );
        return new ModelAndView("car_service_request", model);
    }

    @PostMapping(value = SERVICE_REQUEST)
    public String makeServiceRequest(
        @Valid @ModelAttribute("carServiceRequestDTO") CarServiceCustomerRequestDTO carServiceCustomerRequestDTO,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        // Determine if it's a new or existing customer
        boolean isNewCustomer = carServiceCustomerRequestDTO.isNewCarCandidate();
        
        // Validate based on customer type
        List<String> errors = new ArrayList<>();
        
        // Validate VIN
        String vin = isNewCustomer ? carServiceCustomerRequestDTO.getCarVin() : carServiceCustomerRequestDTO.getExistingCarVin();
        if (vin == null || vin.trim().isEmpty()) {
            errors.add("VIN is required.");
        } else if (!VIN_PATTERN.matcher(vin.trim()).matches()) {
            errors.add("Invalid VIN format. VIN must be exactly 17 characters containing only capital letters (excluding I, O, Q) and digits. Example: 1FT7X2B60FEA74019");
        }
        
        if (isNewCustomer) {
            // Validate new customer fields
            errors.addAll(validateNewCustomerForService(carServiceCustomerRequestDTO));
        } else {
            // Validate existing customer email
            String email = carServiceCustomerRequestDTO.getExistingCustomerEmail();
            if (email == null || email.trim().isEmpty()) {
                errors.add("Email is required for existing customers.");
            } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                errors.add("Invalid email format. Please use format: example@domain.com");
            }
        }
        
        // Validate comment
        if (carServiceCustomerRequestDTO.getCustomerComment() == null || carServiceCustomerRequestDTO.getCustomerComment().trim().isEmpty()) {
            errors.add("Service comment is required. Please describe the issue with your car.");
        }
        
        // If there are validation errors, redirect back with error messages
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessages", errors);
            redirectAttributes.addFlashAttribute("carServiceRequestDTO", carServiceCustomerRequestDTO);
            return "redirect:/service/new";
        }
        
        try {
            // Generate service request number before saving
            String serviceRequestNumber = generateCarServiceRequestNumber();
            
            CarServiceRequest serviceRequest = carServiceRequestMapper.map(carServiceCustomerRequestDTO);
            carServiceRequestService.makeServiceRequest(serviceRequest);
            
            // Add service request number to redirect attributes
            redirectAttributes.addFlashAttribute("serviceRequestNumber", serviceRequestNumber);
            
            return "redirect:/service/request/done";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error creating service request: " + e.getMessage());
            redirectAttributes.addFlashAttribute("carServiceRequestDTO", carServiceCustomerRequestDTO);
            return "redirect:/service/new";
        }
    }
    
    private List<String> validateNewCustomerForService(CarServiceCustomerRequestDTO dto) {
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
        } else if (!EMAIL_PATTERN.matcher(dto.getCustomerEmail().trim()).matches()) {
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
        
        // Car details validation for new cars
        if (dto.getCarBrand() == null || dto.getCarBrand().trim().isEmpty()) {
            errors.add("Car brand is required.");
        }
        
        if (dto.getCarModel() == null || dto.getCarModel().trim().isEmpty()) {
            errors.add("Car model is required.");
        }
        
        if (dto.getCarYear() == null) {
            errors.add("Car year is required.");
        } else {
            int currentYear = java.time.Year.now().getValue();
            if (dto.getCarYear() < 1900 || dto.getCarYear() > currentYear + 1) {
                errors.add("Car year must be between 1900 and " + (currentYear + 1) + ".");
            }
        }
        
        return errors;
    }
    
    @GetMapping(value = "/service/request/done")
    public ModelAndView serviceRequestDonePage() {
        return new ModelAndView("car_service_request_done");
    }
    
    private String generateCarServiceRequestNumber() {
        OffsetDateTime when = OffsetDateTime.now(ZoneId.of("Europe/Warsaw"));
        return "%s.%s.%s-%s.%s.%s.%s".formatted(
            when.getYear(),
            when.getMonthValue(),
            when.getDayOfMonth(),
            when.getHour(),
            when.getMinute(),
            when.getSecond(),
            randomInt(10, 100)
        );
    }
    
    @SuppressWarnings("SameParameterValue")
    private int randomInt(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }
}
