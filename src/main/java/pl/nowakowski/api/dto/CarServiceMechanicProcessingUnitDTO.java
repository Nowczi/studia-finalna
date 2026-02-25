package pl.nowakowski.api.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarServiceMechanicProcessingUnitDTO {

    private String mechanicPesel;

    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be exactly 17 characters containing only capital letters (excluding I, O, Q) and digits")
    private String carVin;

    // Keep single part fields for backward compatibility
    private String partSerialNumber;
    private Integer partQuantity;

    // New list for multiple parts
    @Builder.Default
    private List<PartItemDTO> parts = new ArrayList<>();

    private String serviceCode;
    private Integer hours;
    private String mechanicComment;
    private Boolean done;

    public static CarServiceMechanicProcessingUnitDTO buildDefault() {
        return CarServiceMechanicProcessingUnitDTO.builder()
            .partQuantity(0)
            .hours(1)
            .mechanicComment("")
            .done(false)
            .parts(new ArrayList<>())
            .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartItemDTO {
        private String serialNumber;
        private Integer quantity;
        private String description;
    }
}
