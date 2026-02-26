package pl.nowakowski.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.nowakowski.api.dto.CepikVehicleDTO;
import pl.nowakowski.api.dto.mapper.CepikVehicleMapper;
import pl.nowakowski.business.CepikService;
import pl.nowakowski.domain.CepikVehicle;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CepikController {

    public static final String CEPIK = "/cepik";
    public static final String CEPIK_SEARCH = "/cepik/search";

    private final CepikService cepikService;
    private final CepikVehicleMapper cepikVehicleMapper;

    @GetMapping(CEPIK)
    public ModelAndView cepikPage() {
        Map<String, Object> model = new HashMap<>();
        
        // Default date range: last 5 years
        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);
        
        model.put("defaultDateFrom", fiveYearsAgo);
        model.put("defaultDateTo", today);
        
        return new ModelAndView("cepik_lookup", model);
    }

    @GetMapping(CEPIK_SEARCH)
    public ModelAndView searchCepik(
            @RequestParam(value = "firstRegistrationDateFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate firstRegistrationDateFrom,
            @RequestParam(value = "firstRegistrationDateTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate firstRegistrationDateTo,
            Model model
    ) {
        Map<String, Object> modelMap = new HashMap<>();
        
        // Default date range: last 5 years if not provided
        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);
        
        if (firstRegistrationDateFrom == null) {
            firstRegistrationDateFrom = fiveYearsAgo;
        }
        if (firstRegistrationDateTo == null) {
            firstRegistrationDateTo = today;
        }
        
        modelMap.put("defaultDateFrom", firstRegistrationDateFrom);
        modelMap.put("defaultDateTo", firstRegistrationDateTo);
        
        try {
            // Fetch ALL vehicles from CEPIK
            List<CepikVehicle> vehicles = cepikService.findAll(firstRegistrationDateFrom, firstRegistrationDateTo);
            
            // Map to DTOs
            List<CepikVehicleDTO> vehicleDTOs = vehicles.stream()
                    .map(cepikVehicleMapper::map)
                    .toList();
            
            modelMap.put("vehicles", vehicleDTOs);
            modelMap.put("successMessage", String.format("Found %d vehicle(s) in CEPIK database!", vehicleDTOs.size()));
        } catch (Exception e) {
            modelMap.put("errorMessage", "Could not find vehicles: " + e.getMessage());
        }
        
        return new ModelAndView("cepik_lookup", modelMap);
    }
}
