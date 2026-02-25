package pl.nowakowski.api.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.nowakowski.api.dto.CarHistoryDTO;
import pl.nowakowski.api.dto.mapper.CarMapper;
import pl.nowakowski.business.CarService;

import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping(CarHistoryRestController.API_CAR)
public class CarHistoryRestController {

    public static final String API_CAR = "/api/car";
    public static final String CAR_VIN_HISTORY = "/{carVin}/history";

    private final CarService carService;
    private final CarMapper carMapper;

    @GetMapping(value = CAR_VIN_HISTORY)
    public ResponseEntity<CarHistoryDTO> carHistory(
            @PathVariable String carVin
    ) {
        if (Objects.isNull(carVin)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok(carMapper.map(carService.findCarHistoryByVin(carVin)));

    }

}
