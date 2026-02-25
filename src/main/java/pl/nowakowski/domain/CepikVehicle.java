package pl.nowakowski.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CepikVehicle {

    String cepikId;
    String brand;
    String model;
    String type;
    BigDecimal engineCapacity;
    Integer weight;
    String fuel;
}
