package pl.nowakowski.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CepikVehicleDTO {

    private String cepikId;
    private String brand;
    private String model;
    private String type;
    private BigDecimal engineCapacity;
    private Integer weight;
    private String fuel;

}
