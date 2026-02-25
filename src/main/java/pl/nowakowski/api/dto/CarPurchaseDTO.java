package pl.nowakowski.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.*;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarPurchaseDTO {

    @Email
    private String existingCustomerEmail;

    private String customerName;
    private String customerSurname;
    
    // Modified pattern to allow empty string for existing customers
    // The pattern now matches either:
    // 1. Empty string (for existing customers who don't need to provide phone)
    // 2. Valid phone format: +XX XXX XXX XXX (for new customers)
    @Pattern(regexp = "^(?:|[+]\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3})$")
    private String customerPhone;
    
    @Email
    private String customerEmail;
    private String customerAddressCountry;
    private String customerAddressCity;
    private String customerAddressPostalCode;
    private String customerAddressStreet;

    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be exactly 17 characters containing only capital letters (excluding I, O, Q) and digits")
    private String carVin;
    private String salesmanPesel;

    public static CarPurchaseDTO buildDefaultData() {
        return CarPurchaseDTO.builder()
            .customerName("")
            .customerSurname("")
            .customerPhone("")
            .customerEmail("")
            .customerAddressCountry("")
            .customerAddressCity("")
            .customerAddressPostalCode("")
            .customerAddressStreet("")
            .existingCustomerEmail("")
            .build();
    }

    public Map<String, String> asMap() {
        Map<String, String> result = new HashMap<>();
        ofNullable(customerName).ifPresent(value -> result.put("customerName", value));
        ofNullable(customerSurname).ifPresent(value -> result.put("customerSurname", value));
        ofNullable(customerPhone).ifPresent(value -> result.put("customerPhone", value));
        ofNullable(customerEmail).ifPresent(value -> result.put("customerEmail", value));
        ofNullable(existingCustomerEmail).ifPresent(value -> result.put("existingCustomerEmail", value));
        ofNullable(customerAddressCountry).ifPresent(value -> result.put("customerAddressCountry", value));
        ofNullable(customerAddressCity).ifPresent(value -> result.put("customerAddressCity", value));
        ofNullable(customerAddressPostalCode).ifPresent(value -> result.put("customerAddressPostalCode", value));
        ofNullable(customerAddressStreet).ifPresent(value -> result.put("customerAddressStreet", value));
        ofNullable(carVin).ifPresent(value -> result.put("carVin", value));
        ofNullable(salesmanPesel).ifPresent(value -> result.put("salesmanPesel", value));
        return result;
    }
}
