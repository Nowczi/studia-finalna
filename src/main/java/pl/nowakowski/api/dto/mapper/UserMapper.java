package pl.nowakowski.api.dto.mapper;

import org.springframework.stereotype.Component;
import pl.nowakowski.api.dto.UserDTO;
import pl.nowakowski.domain.User;
import pl.nowakowski.infrastructure.security.RoleEntity;
import pl.nowakowski.infrastructure.security.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO map(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .password(user.getPassword())
                .active(user.getActive())
                .passwordChangeRequired(user.getPasswordChangeRequired())
                .roles(user.getRoles())
                .build();
    }

    public User map(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .id(dto.getId())
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .active(dto.getActive())
                .passwordChangeRequired(dto.getPasswordChangeRequired())
                .roles(dto.getRoles())
                .build();
    }

    public User mapFromEntity(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .userName(entity.getUserName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .active(entity.getActive())
                .passwordChangeRequired(entity.getPasswordChangeRequired())
                .roles(entity.getRoles().stream()
                        .map(RoleEntity::getRole)
                        .collect(Collectors.toSet()))
                .build();
    }

    public UserEntity mapToEntity(User user, Set<RoleEntity> roles) {
        if (user == null) {
            return null;
        }

        // Build entity - ID will be auto-generated if null
        UserEntity.UserEntityBuilder builder = UserEntity.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .password(user.getPassword())
                .active(user.getActive())
                .passwordChangeRequired(user.getPasswordChangeRequired())
                .roles(roles);

        // Only set ID if it's not null (for updates)
        if (user.getId() != null) {
            builder.id(user.getId());
        }

        return builder.build();
    }
}
