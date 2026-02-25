package pl.nowakowski.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.nowakowski.api.dto.CarToBuyDTO;
import pl.nowakowski.api.dto.mapper.CarMapper;
import pl.nowakowski.business.CarPurchaseService;
import pl.nowakowski.business.CarService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SalesmanController {

    private static final String SALESMAN = "/salesman";
    private static final String SALESMAN_CAR_ADD = "/salesman/car/add";
    private static final String SALESMAN_CAR_EDIT = "/salesman/car/edit";
    private static final String SALESMAN_CAR_DELETE = "/salesman/car/delete";
    private static final int CARS_PER_PAGE = 10;

    private final CarPurchaseService carPurchaseService;
    private final CarService carService;
    private final CarMapper carMapper;

    @GetMapping(value = SALESMAN)
    public String homePage(@RequestParam(required = false, defaultValue = "0") int page, Model model) {
        var allAvailableCars = carPurchaseService.availableCars().stream()
            .map(carMapper::map)
            .toList();

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

        model.addAttribute("availableCarDTOs", paginatedCars);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCars", totalCars);
        model.addAttribute("carsPerPage", CARS_PER_PAGE);

        return "salesman_portal";
    }
    
    @GetMapping(value = SALESMAN_CAR_ADD)
    public String addCarPage(Model model) {
        model.addAttribute("carToBuyDTO", CarToBuyDTO.builder().build());
        return "add_car";
    }
    
    @PostMapping(value = SALESMAN_CAR_ADD)
    public String addCar(
        @Valid @ModelAttribute("carToBuyDTO") CarToBuyDTO carToBuyDTO,
        RedirectAttributes redirectAttributes
    ) {
        // Validate VIN format
        String vin = carToBuyDTO.getVin();
        if (vin == null || !vin.matches("^[A-HJ-NPR-Z0-9]{17}$")) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Invalid VIN format! VIN must be exactly 17 characters containing only capital letters (excluding I, O, Q) and digits. " +
                "Please correct the VIN and try again. Example of valid VIN: 1FT7X2B60FEA74019");
            return "redirect:/salesman/car/add";
        }

        try {
            // Check if car with same VIN already exists
            var existingCar = carService.findOptionalCarToBuy(carToBuyDTO.getVin());
            if (existingCar.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "A car with VIN " + carToBuyDTO.getVin() + " already exists in the inventory.");
                return "redirect:/salesman/car/add";
            }
            
            // Save the new car
            carService.saveCarToBuy(carMapper.map(carToBuyDTO));
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Car " + carToBuyDTO.getBrand() + " " + carToBuyDTO.getModel() + 
                " (VIN: " + carToBuyDTO.getVin() + ") has been successfully added to the inventory.");
            
            return "redirect:/salesman";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error adding car: " + e.getMessage());
            return "redirect:/salesman/car/add";
        }
    }
    
    @PostMapping(value = SALESMAN_CAR_EDIT)
    public String editCar(
        @RequestParam("vin") String vin,
        @RequestParam("brand") String brand,
        @RequestParam("model") String model,
        @RequestParam("year") Integer year,
        @RequestParam("color") String color,
        @RequestParam("price") BigDecimal price,
        RedirectAttributes redirectAttributes
    ) {
        try {
            // Find the car by VIN
            var existingCarOpt = carService.findOptionalCarToBuy(vin);
            if (existingCarOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Car with VIN " + vin + " not found.");
                return "redirect:/salesman";
            }
            
            var existingCar = existingCarOpt.get();
            
            // Update the car with new values
            var updatedCar = existingCar
                .withBrand(brand)
                .withModel(model)
                .withYear(year)
                .withColor(color)
                .withPrice(price);
            
            // Save the updated car
            carService.saveCarToBuy(updatedCar);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Car " + brand + " " + model + 
                " (VIN: " + vin + ") has been successfully updated.");
            
            return "redirect:/salesman";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating car: " + e.getMessage());
            return "redirect:/salesman";
        }
    }
    
    @PostMapping(value = SALESMAN_CAR_DELETE)
    public String deleteCar(
        @RequestParam("vin") String vin,
        RedirectAttributes redirectAttributes
    ) {
        try {
            // Check if car exists
            var existingCar = carService.findOptionalCarToBuy(vin);
            if (existingCar.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Car with VIN " + vin + " not found.");
                return "redirect:/salesman";
            }
            
            // Delete the car
            carService.deleteCarToBuy(vin);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Car with VIN " + vin + " has been successfully deleted.");
            
            return "redirect:/salesman";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting car: " + e.getMessage());
            return "redirect:/salesman";
        }
    }
}
