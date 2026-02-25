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
@ToString(of = {"salesmanId", "name", "surname", "pesel"})
public class Salesman {

    Integer salesmanId;
    String name;
    String surname;
    String pesel;
    Set<Invoice> invoices;
    Integer userId;
    String userName;
}