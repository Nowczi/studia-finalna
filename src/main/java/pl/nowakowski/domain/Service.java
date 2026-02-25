package pl.nowakowski.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;
import java.util.Set;

@With
@Value
@Builder
@EqualsAndHashCode(of = "serviceCode")
@ToString(of = {"serviceId", "serviceCode", "description", "price"})
public class Service {

    Integer serviceId;
    String serviceCode;
    String description;
    BigDecimal price;
    Set<ServiceMechanic> serviceMechanics;
}