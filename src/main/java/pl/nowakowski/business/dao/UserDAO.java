package pl.nowakowski.business.dao;

import pl.nowakowski.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    Optional<User> findById(Integer id);

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User user);

    void deleteById(Integer id);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    /**
     * Resets the password for a user.
     *
     * @param userId the ID of the user whose password should be reset
     * @param newPassword the new password (already encoded)
     * @return the updated user
     */
    User resetPassword(Integer userId, String newPassword);

    /**
     * Updates the password change required flag for a user.
     *
     * @param userId the ID of the user
     * @param passwordChangeRequired true if user must change password on next login
     * @return the updated user
     */
    User updatePasswordChangeRequired(Integer userId, Boolean passwordChangeRequired);

    /**
     * Changes the user's password and clears the password change required flag.
     *
     * @param userId the ID of the user
     * @param newPassword the new password (already encoded)
     * @return the updated user
     */
    User changePassword(Integer userId, String newPassword);
}
