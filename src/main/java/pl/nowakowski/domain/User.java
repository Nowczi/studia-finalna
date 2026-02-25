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
@EqualsAndHashCode(of = "userName")
@ToString(of = {"id", "userName", "email", "active"})
public class User {

    Integer id;
    String userName;
    String email;
    String password;
    Boolean active;
    Boolean passwordChangeRequired;
    Set<String> roles;
}
