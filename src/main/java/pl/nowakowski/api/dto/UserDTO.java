package pl.nowakowski.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;

    @NotBlank
    @Size(min = 5, max = 32)
    private String userName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 5)
    private String password;

    private Boolean active;

    private Boolean passwordChangeRequired;

    private Set<String> roles;

    public static UserDTO buildDefault() {
        return UserDTO.builder()
                .userName("")
                .email("")
                .password("")
                .active(true)
                .passwordChangeRequired(false)
                .roles(Set.of("SALESMAN"))
                .build();
    }
}
