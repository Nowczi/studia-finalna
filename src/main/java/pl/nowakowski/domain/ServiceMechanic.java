package pl.nowakowski.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
@EqualsAndHashCode(of = "serviceMechanicId")
@ToString(of = {"serviceMechanicId", "hours", "comment", "quantity"})
public class ServiceMechanic {

    Integer serviceMechanicId;
    Integer hours;
    String comment;
    Integer quantity;
    CarServiceRequest carServiceRequest;
    Mechanic mechanic;
    Service service;

}
