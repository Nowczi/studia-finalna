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
public class MechanicDTO {

    String name;
    String surname;
    String userName;

    @Pattern(regexp = "^[0-9]{11}$", message = "PESEL must be 11 digits")
    String pesel;
    
    public String getDisplayName() {
        if (name != null && surname != null && userName != null) {
            return name + " " + surname + " - " + userName;
        } else if (name != null && surname != null) {
            return name + " " + surname;
        } else if (userName != null) {
            return userName;
        }
        return pesel;
    }
}
