package pl.nowakowski.api.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.nowakowski.business.UserManagementService;
import pl.nowakowski.domain.User;

import java.util.regex.Pattern;

@Controller
@AllArgsConstructor
@Validated
public class PasswordChangeController {

    public static final String CHANGE_PASSWORD = "/change-password";
    
    // Password pattern: at least 12 characters, 1 uppercase, 1 lowercase, 1 special character
    private static final Pattern PASSWORD_PATTERN = 
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{12,}$");

    private final UserManagementService userManagementService;

    @GetMapping(CHANGE_PASSWORD)
    public String showChangePasswordPage(Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Check if password change is actually required
        if (!userManagementService.isPasswordChangeRequired(userName)) {
            return "redirect:/car-dealership";
        }
        
        model.addAttribute("passwordChangeDTO", new PasswordChangeDTO());
        return "change_password";
    }

    @PostMapping(CHANGE_PASSWORD)
    public String changePassword(
            @ModelAttribute("passwordChangeDTO") PasswordChangeDTO passwordChangeDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Validate passwords match
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "change_password";
        }
        
        // Validate password requirements
        if (!PASSWORD_PATTERN.matcher(passwordChangeDTO.getNewPassword()).matches()) {
            model.addAttribute("errorMessage", 
                    "Password must be at least 12 characters long and contain at least one uppercase letter, one lowercase letter, and one special character (!@#$%^&* etc.).");
            return "change_password";
        }
        
        try {
            User user = userManagementService.findUser(userName);
            userManagementService.changeUserPassword(user.getId(), passwordChangeDTO.getNewPassword());
            
            // Add success message that will be displayed on login page after logout
            redirectAttributes.addFlashAttribute("passwordResetSuccess", "Password has been changed successfully! Please log in with your new password.");
            
            // Redirect to logout - user will be redirected to login page with success message
            return "redirect:/logout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error changing password: " + e.getMessage());
            return "change_password";
        }
    }

    @Data
    public static class PasswordChangeDTO {
        @NotBlank(message = "New password is required")
        @Size(min = 12, message = "Password must be at least 12 characters")
        private String newPassword;
        
        @NotBlank(message = "Please confirm your password")
        private String confirmPassword;
    }
}
