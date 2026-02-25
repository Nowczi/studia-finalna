package pl.nowakowski.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@With
@Value
@Builder
@EqualsAndHashCode(of = "email")
@ToString(of = {"customerId", "name", "surname", "email"})
public class Customer {

    Integer customerId;
    String name;
    String surname;
    String phone;
    String email;
    Address address;
    Set<Invoice> invoices;
    Set<CarServiceRequest> carServiceRequests;

    public Set<Invoice> getInvoices() {
        return Objects.isNull(invoices) ? new HashSet<>() : invoices;
    }

    public Set<CarServiceRequest> getCarServiceRequests() {
        return Objects.isNull(carServiceRequests) ? new HashSet<>() : carServiceRequests;
    }
}
