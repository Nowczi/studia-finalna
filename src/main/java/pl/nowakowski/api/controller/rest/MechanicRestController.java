package pl.nowakowski.api.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.nowakowski.api.dto.CarServiceMechanicProcessingUnitDTO;
import pl.nowakowski.api.dto.CarServiceRequestDTO;
import pl.nowakowski.api.dto.CarServiceRequestsDTO;
import pl.nowakowski.api.dto.mapper.CarServiceRequestMapper;
import pl.nowakowski.business.CarServiceProcessingService;
import pl.nowakowski.business.CarServiceRequestService;
import pl.nowakowski.domain.CarServiceProcessingRequest;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(MechanicRestController.API_MECHANIC)
public class MechanicRestController {

    public static final String API_MECHANIC = "/api/mechanic";
    public static final String AVAILABLE_SERVICE_REQUESTS = "/availableServiceRequests";
    public static final String MECHANIC_WORK_UNIT = "/workUnit";

    private final CarServiceRequestService carServiceRequestService;
    private final CarServiceProcessingService carServiceProcessingService;
    private final CarServiceRequestMapper carServiceRequestMapper;

    @GetMapping(value = AVAILABLE_SERVICE_REQUESTS)
    public CarServiceRequestsDTO availableServiceRequests(){
        return getCarServiceRequestsDTO();
    }

    @PostMapping(value = MECHANIC_WORK_UNIT)
    public CarServiceRequestsDTO mechanicPerformWorkUnit(
            @Valid @RequestBody CarServiceMechanicProcessingUnitDTO processingUnitDTO
            ){
        CarServiceProcessingRequest request = carServiceRequestMapper.map(processingUnitDTO);
        carServiceProcessingService.process(request);
        return getCarServiceRequestsDTO();
    }

    private CarServiceRequestsDTO getCarServiceRequestsDTO() {
        return CarServiceRequestsDTO.builder()
                .carServiceRequests(getAvailableCarServiceRequests())
                .build();
    }

    private List<CarServiceRequestDTO> getAvailableCarServiceRequests() {
        return carServiceRequestService.availableServiceRequests().stream()
                .map(carServiceRequestMapper::map)
                .toList();
    }

}
