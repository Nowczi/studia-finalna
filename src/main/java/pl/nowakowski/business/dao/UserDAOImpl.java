package pl.nowakowski.business.dao;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.nowakowski.api.dto.mapper.UserMapper;
import pl.nowakowski.domain.User;
import pl.nowakowski.infrastructure.security.RoleEntity;
import pl.nowakowski.infrastructure.security.RoleRepository;
import pl.nowakowski.infrastructure.security.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final pl.nowakowski.infrastructure.security.UserRepository securityUserRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(Integer id) {
        return securityUserRepository.findById(id.longValue())
                .map(userMapper::mapFromEntity);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        UserEntity entity = securityUserRepository.findByUserName(userName);
        return Optional.ofNullable(userMapper.mapFromEntity(entity));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return securityUserRepository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .map(userMapper::mapFromEntity);
    }

    @Override
    public List<User> findAll() {
        return securityUserRepository.findAll().stream()
                .map(userMapper::mapFromEntity)
                .toList();
    }

    @Override
    public User save(User user) {
        Set<RoleEntity> roleEntities = user.getRoles().stream()
                .map(roleRepository::findByRole)
                .collect(Collectors.toSet());

        UserEntity entity = userMapper.mapToEntity(user, roleEntities);
        UserEntity saved = securityUserRepository.save(entity);
        // Force flush to get the generated ID
        securityUserRepository.flush();
        return userMapper.mapFromEntity(saved);
    }

    @Override
    public void deleteById(Integer id) {
        securityUserRepository.deleteById(id.longValue());
    }

    @Override
    public boolean existsByUserName(String userName) {
        return securityUserRepository.findByUserName(userName) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public User resetPassword(Integer userId, String newPassword) {
        UserEntity userEntity = securityUserRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        userEntity.setPassword(newPassword);
        userEntity.setPasswordChangeRequired(true);  // Force password change on next login
        UserEntity saved = securityUserRepository.save(userEntity);
        securityUserRepository.flush();
        
        return userMapper.mapFromEntity(saved);
    }

    @Override
    public User updatePasswordChangeRequired(Integer userId, Boolean passwordChangeRequired) {
        UserEntity userEntity = securityUserRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        userEntity.setPasswordChangeRequired(passwordChangeRequired);
        UserEntity saved = securityUserRepository.save(userEntity);
        securityUserRepository.flush();
        
        return userMapper.mapFromEntity(saved);
    }

    @Override
    public User changePassword(Integer userId, String newPassword) {
        UserEntity userEntity = securityUserRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        userEntity.setPassword(newPassword);
        userEntity.setPasswordChangeRequired(false);  // Clear the flag after password change
        UserEntity saved = securityUserRepository.save(userEntity);
        securityUserRepository.flush();
        
        return userMapper.mapFromEntity(saved);
    }
}
