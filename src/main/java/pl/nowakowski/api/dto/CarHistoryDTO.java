package pl.nowakowski.api.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarHistoryDTO {

    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be exactly 17 characters containing only capital letters (excluding I, O, Q) and digits")
    private String carVin;
    private List<ServiceRequestDTO> carServiceRequests;

    public static CarHistoryDTO buildDefault() {
        return CarHistoryDTO.builder()
            .carVin("empty")
            .carServiceRequests(Collections.emptyList())
            .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceRequestDTO {
        private String carServiceRequestNumber;
        private String receivedDateTime;
        private String completedDateTime;
        private String customerComment;
        // Changed from services to serviceWorks to include mechanic details
        private List<ServiceWorkDTO> serviceWorks;
        private List<PartDTO> parts;
    }
    
    /**
     * DTO for service work performed by a mechanic, including their comment and hours.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceWorkDTO {
        private String serviceCode;
        private String description;
        private BigDecimal price;
        private String mechanicName;
        private String mechanicSurname;
        private Integer hours;
        private String mechanicComment;
    }
}
