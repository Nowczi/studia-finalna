package pl.nowakowski.api.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.nowakowski.api.dto.CarServiceMechanicProcessingUnitDTO;
import pl.nowakowski.api.dto.CarServiceRequestDTO;
import pl.nowakowski.api.dto.MechanicDTO;
import pl.nowakowski.api.dto.PartDTO;
import pl.nowakowski.api.dto.ServiceDTO;
import pl.nowakowski.api.dto.mapper.CarServiceRequestMapper;
import pl.nowakowski.api.dto.mapper.MechanicMapper;
import pl.nowakowski.api.dto.mapper.PartMapper;
import pl.nowakowski.api.dto.mapper.ServiceMapper;
import pl.nowakowski.business.CarServiceProcessingService;
import pl.nowakowski.business.CarServiceRequestService;
import pl.nowakowski.business.PartCatalogService;
import pl.nowakowski.business.ServiceCatalogService;
import pl.nowakowski.business.dao.MechanicDAO;
import pl.nowakowski.domain.CarServiceProcessingRequest;
import pl.nowakowski.domain.CarServiceRequest;
import pl.nowakowski.domain.Mechanic;
import pl.nowakowski.domain.Part;
import pl.nowakowski.infrastructure.security.UserEntity;
import pl.nowakowski.infrastructure.security.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class MechanicController {

    public static final String MECHANIC = "/mechanic";
    public static final String MECHANIC_WORK_UNIT = "/mechanic/workUnit";
    public static final String MECHANIC_WORK = "/mechanic/work/{serviceRequestNumber}";

    private final CarServiceProcessingService carServiceProcessingService;
    private final CarServiceRequestService carServiceRequestService;

    private final PartCatalogService partCatalogService;
    private final ServiceCatalogService serviceCatalogService;
    private final CarServiceRequestMapper carServiceRequestMapper;
    private final MechanicMapper mechanicMapper;
    private final PartMapper partMapper;
    private final ServiceMapper serviceMapper;
    private final UserRepository userRepository;
    private final MechanicDAO mechanicDAO;

    @GetMapping(value = MECHANIC)
    public ModelAndView mechanicCheckPage() {
        Map<String, Object> data = new HashMap<>(prepareNecessaryData());
        
        // Get current logged-in user's name
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(userName);
        
        if (user != null) {
            // Try to find mechanic profile for the user
            var mechanicOptional = mechanicDAO.findByUserId(user.getId());
            if (mechanicOptional.isPresent()) {
                Mechanic mechanic = mechanicOptional.get();
                data.put("mechanicName", mechanic.getName());
                data.put("mechanicSurname", mechanic.getSurname());
            } else {
                // User is not a mechanic (e.g., Admin), use username or default greeting
                // Capitalize first letter of username for better display
                String displayName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
                data.put("mechanicName", displayName);
                data.put("mechanicSurname", "");
            }
        } else {
            // Fallback if user not found
            data.put("mechanicName", "User");
            data.put("mechanicSurname", "");
        }
        
        return new ModelAndView("mechanic_service", data);
    }
    
    @GetMapping(value = MECHANIC_WORK)
    public ModelAndView mechanicWorkPage(@PathVariable String serviceRequestNumber) {
        Map<String, Object> data = new HashMap<>(prepareNecessaryData());
        
        // Find the specific service request WITH DETAILS (including service mechanics and parts)
        CarServiceRequest serviceRequest = carServiceRequestService
            .findServiceRequestByNumberWithDetails(serviceRequestNumber)
            .orElse(null);
        
        // Get completed work for this service request
        List<CompletedWorkDTO> completedWork = getCompletedWorkForRequest(serviceRequest);
        
        // Build default DTO with carVin pre-populated from service request
        CarServiceMechanicProcessingUnitDTO dto = CarServiceMechanicProcessingUnitDTO.buildDefault();
        if (serviceRequest != null) {
            dto.setCarVin(serviceRequest.getCar() != null ? serviceRequest.getCar().getVin() : null);
        }
        
        data.put("serviceRequest", serviceRequest != null ? carServiceRequestMapper.map(serviceRequest) : null);
        data.put("completedWork", completedWork);
        data.put("carServiceProcessDTO", dto);
        
        return new ModelAndView("mechanic_work_unit", data);
    }

    // GET endpoint for workUnit - handles validation failures by redirecting to mechanic page
    @GetMapping(value = MECHANIC_WORK_UNIT)
    public String mechanicWorkUnitGet() {
        return "redirect:/mechanic";
    }

    private Map<String, ?> prepareNecessaryData() {
        var availableServiceRequests = getAvailableServiceRequests();
        var availableCarVins = availableServiceRequests.stream().map(CarServiceRequestDTO::getCarVin).toList();
        var availableMechanics = getAvailableMechanics();
        var parts = findParts();
        var services = findServices();
        var partSerialNumbers = preparePartSerialNumbers(parts);
        var serviceCodes = services.stream().map(ServiceDTO::getServiceCode).toList();

        return Map.of(
            "availableServiceRequestDTOs", availableServiceRequests,
            "availableCarVins", availableCarVins,
            "availableMechanicDTOs", availableMechanics,
            "partDTOs", parts,
            "partSerialNumbers", partSerialNumbers,
            "serviceDTOs", services,
            "serviceCodes", serviceCodes,
            "carServiceProcessDTO", CarServiceMechanicProcessingUnitDTO.buildDefault()
        );
    }

    @PostMapping(value = MECHANIC_WORK_UNIT)
    public String mechanicWorkUnit(
        @Valid @ModelAttribute("carServiceRequestProcessDTO") CarServiceMechanicProcessingUnitDTO dto,
        BindingResult bindingResult,
        ModelMap modelMap,
        RedirectAttributes redirectAttributes
    ) {
        // If there are validation errors, redirect with error message
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Validation error: " + errorMessage);
            return "redirect:/mechanic";
        }
        
        try {
            // Process each part if multiple parts are provided
            boolean partsProcessed = false;
            if (dto.getParts() != null && !dto.getParts().isEmpty()) {
                for (CarServiceMechanicProcessingUnitDTO.PartItemDTO partItem : dto.getParts()) {
                    if (partItem.getSerialNumber() != null && !partItem.getSerialNumber().isEmpty()) {
                        CarServiceMechanicProcessingUnitDTO partDto = CarServiceMechanicProcessingUnitDTO.builder()
                            .mechanicPesel(dto.getMechanicPesel())
                            .carVin(dto.getCarVin())
                            .partSerialNumber(partItem.getSerialNumber())
                            .partQuantity(partItem.getQuantity())
                            .serviceCode(dto.getServiceCode())
                            .hours(dto.getHours())
                            .mechanicComment(dto.getMechanicComment())
                            .done(false) // Parts are processed without marking done
                            .build();
                        
                        CarServiceProcessingRequest request = carServiceRequestMapper.map(partDto);
                        carServiceProcessingService.process(request);
                        partsProcessed = true;
                    }
                }
            }
            
            // Only process the main request if no parts were processed
            // This prevents duplicate service entries when parts are included
            if (!partsProcessed) {
                CarServiceProcessingRequest mainRequest = carServiceRequestMapper.map(dto);
                carServiceProcessingService.process(mainRequest);
            } else if (Boolean.TRUE.equals(dto.getDone())) {
                // If parts were processed AND done is checked, mark the service request as completed
                // WITHOUT creating an additional service_mechanic entry
                carServiceProcessingService.completeServiceRequest(dto.getCarVin());
            }
            
            if (Boolean.TRUE.equals(dto.getDone())) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Work completed successfully! Service request has been marked as done.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Work saved successfully! You can continue working on this request.");
            }
            
            return "redirect:/mechanic";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving work: " + e.getMessage());
            return "redirect:/mechanic";
        }
    }

    private List<CarServiceRequestDTO> getAvailableServiceRequests() {
        return carServiceRequestService.availableServiceRequests().stream()
            .map(carServiceRequestMapper::map)
            .toList();
    }

    private List<MechanicDTO> getAvailableMechanics() {
        return carServiceRequestService.availableMechanics().stream()
            .map(mechanicMapper::map)
            .toList();
    }

    private List<PartDTO> findParts() {
        return partCatalogService.findAll().stream()
            .map(partMapper::map)
            .toList();
    }

    private List<ServiceDTO> findServices() {
        return serviceCatalogService.findAll().stream()
            .map(serviceMapper::map)
            .toList();
    }

    private List<String> preparePartSerialNumbers(List<PartDTO> parts) {
        List<String> partSerialNumbers = new ArrayList<>(parts.stream()
            .map(PartDTO::getSerialNumber)
            .toList());
        partSerialNumbers.add(Part.NONE);
        return partSerialNumbers;
    }
    
    /**
     * Gets completed work for a service request by extracting service mechanics and parts.
     * This method now receives the full CarServiceRequest domain object with all details.
     * Services and parts are expanded based on quantity - if quantity is 3, it appears 3 times.
     */
    private List<CompletedWorkDTO> getCompletedWorkForRequest(CarServiceRequest request) {
        if (request == null) {
            return new ArrayList<>();
        }
        
        List<CompletedWorkDTO> completedWork = new ArrayList<>();
        
        // Get service mechanics (work done) - expand based on quantity
        if (request.getServiceMechanics() != null) {
            for (var serviceMechanic : request.getServiceMechanics()) {
                int quantity = serviceMechanic.getQuantity() != null ? serviceMechanic.getQuantity() : 1;
                // Add entry for each unit of quantity
                for (int i = 0; i < quantity; i++) {
                    CompletedWorkDTO work = new CompletedWorkDTO();
                    work.setMechanicName(serviceMechanic.getMechanic() != null ? 
                        serviceMechanic.getMechanic().getName() + " " + serviceMechanic.getMechanic().getSurname() : "Unknown");
                    work.setServiceName(serviceMechanic.getService() != null ? 
                        serviceMechanic.getService().getDescription() : "Unknown Service");
                    work.setHours(serviceMechanic.getHours());
                    work.setComment(serviceMechanic.getComment());
                    completedWork.add(work);
                }
            }
        }
        
        // Get service parts (parts used) - expand based on quantity
        if (request.getServiceParts() != null) {
            for (var servicePart : request.getServiceParts()) {
                int quantity = servicePart.getQuantity() != null ? servicePart.getQuantity() : 1;
                // Add entry for each unit of quantity
                for (int i = 0; i < quantity; i++) {
                    CompletedWorkDTO work = new CompletedWorkDTO();
                    work.setMechanicName("-");
                    work.setPartName(servicePart.getPart() != null ? 
                        servicePart.getPart().getDescription() : "Unknown Part");
                    work.setPartQuantity(1); // Each entry represents 1 unit
                    work.setComment("-");
                    completedWork.add(work);
                }
            }
        }
        
        return completedWork;
    }
    
    // Inner DTO for completed work display
    public static class CompletedWorkDTO {
        private String mechanicName;
        private String serviceName;
        private Integer hours;
        private String comment;
        private String partName;
        private Integer partQuantity;
        
        // Getters and setters
        public String getMechanicName() { return mechanicName; }
        public void setMechanicName(String mechanicName) { this.mechanicName = mechanicName; }
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public Integer getHours() { return hours; }
        public void setHours(Integer hours) { this.hours = hours; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public String getPartName() { return partName; }
        public void setPartName(String partName) { this.partName = partName; }
        public Integer getPartQuantity() { return partQuantity; }
        public void setPartQuantity(Integer partQuantity) { this.partQuantity = partQuantity; }
    }
}
