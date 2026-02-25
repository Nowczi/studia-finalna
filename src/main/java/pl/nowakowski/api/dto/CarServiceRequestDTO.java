package pl.nowakowski.api.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarServiceRequestDTO {

    private String carServiceRequestNumber;
    private String receivedDateTime;
    private String completedDateTime;
    private String customerComment;

    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be exactly 17 characters containing only capital letters (excluding I, O, Q) and digits")
    private String carVin;
    
    // Car details
    private String carBrand;
    private String carModel;
    private Integer carYear;
}
