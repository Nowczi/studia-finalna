package pl.nowakowski.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserName(String userName);

    @Query("SELECT u.passwordChangeRequired FROM UserEntity u WHERE u.userName = :userName")
    Boolean findPasswordChangeRequiredByUserName(@Param("userName") String userName);
}
