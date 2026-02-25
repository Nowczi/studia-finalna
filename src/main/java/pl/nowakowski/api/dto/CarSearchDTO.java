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
public class CarSearchDTO {

    private String brand;
    private String model;
    private Integer yearFrom;
    private Integer yearTo;
    private String color;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
}
