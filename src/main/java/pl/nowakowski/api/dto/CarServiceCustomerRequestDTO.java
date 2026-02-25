package pl.nowakowski.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarServiceCustomerRequestDTO {

    private String existingCustomerEmail;

    private String customerName;
    private String customerSurname;
    private String customerPhone;
    private String customerEmail;
    private String customerAddressCountry;
    private String customerAddressCity;
    private String customerAddressPostalCode;
    private String customerAddressStreet;

    // Removed @Pattern validation - handled in controller
    private String existingCarVin;
    private String existingCarBrand;
    private String existingCarModel;
    private Integer existingCarYear;

    // Removed @Pattern validation - handled in controller
    private String carVin;
    private String carBrand;
    private String carModel;
    private Integer carYear;

    private String customerComment;

    public static CarServiceCustomerRequestDTO buildDefault() {
        return CarServiceCustomerRequestDTO.builder()
            .existingCustomerEmail("")
            .existingCarVin("")
            .customerComment("")
            .customerName("")
            .customerSurname("")
            .customerPhone("")
            .customerEmail("")
            .customerAddressCountry("")
            .customerAddressCity("")
            .customerAddressPostalCode("")
            .customerAddressStreet("")
            .carVin("")
            .carBrand("")
            .carModel("")
            .existingCarBrand("")
            .existingCarModel("")
            .build();
    }

    public boolean isNewCarCandidate() {
        return Objects.isNull(getExistingCustomerEmail())
            || getExistingCustomerEmail().isBlank()
            || Objects.isNull(getExistingCarVin())
            || getExistingCarVin().isBlank();

    }
}
