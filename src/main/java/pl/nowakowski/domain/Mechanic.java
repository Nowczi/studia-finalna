package pl.nowakowski.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.util.Set;

@With
@Value
@Builder
@EqualsAndHashCode(of = "pesel")
@ToString(of = {"mechanicId", "name", "surname", "pesel"})
public class Mechanic {

    Integer mechanicId;
    String name;
    String surname;
    String pesel;
    Set<ServiceMechanic> serviceMechanics;
    Integer userId;
    String userName;
}