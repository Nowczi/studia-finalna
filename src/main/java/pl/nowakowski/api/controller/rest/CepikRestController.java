package pl.nowakowski.api.controller.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.nowakowski.api.dto.CepikVehicleDTO;
import pl.nowakowski.api.dto.mapper.CepikVehicleMapper;
import pl.nowakowski.business.CepikService;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping(CepikRestController.API_CEPIK)
public class CepikRestController {

    public static final String API_CEPIK = "/api/cepik";
    public static final String CEPIK_RANDOM = "/random";

    private final CepikService cepikService;
    private final CepikVehicleMapper cepikVehicleMapper;

    @Operation(summary = "Get CEPIK random vehicle")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "CEPIK vehicle found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = CepikVehicleDTO.class
                                    )
                            )
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid dates interval supplied",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "CEPIK vehicle not found",
                    content = @Content)
    })
    @GetMapping(value = CEPIK_RANDOM)
    public CepikVehicleDTO cepikRandomVehicle(
            @Parameter(description = "First vehicle registration date interval beginning")
            @RequestParam(value = "firstRegistrationDateFrom")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            final LocalDate firstRegistrationDateFrom,
            @Parameter(description = "First vehicle registration date interval ending")
            @RequestParam(value = "firstRegistrationDateTo")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            final LocalDate firstRegistrationDateTo
    ) {
        return cepikVehicleMapper
                .map(cepikService.findRandom(firstRegistrationDateFrom, firstRegistrationDateTo));
    }
}
