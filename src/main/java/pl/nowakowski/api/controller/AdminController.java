package pl.nowakowski.api.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.nowakowski.api.dto.UserDTO;
import pl.nowakowski.api.dto.UsersDTO;
import pl.nowakowski.api.dto.mapper.UserMapper;
import pl.nowakowski.business.UserManagementService;
import pl.nowakowski.domain.User;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Controller
@AllArgsConstructor
public class AdminController {

    public static final String ADMIN = "/admin";
    public static final String ADMIN_CREATE_USER = "/admin/create";
    public static final String ADMIN_DELETE_USER = "/admin/delete";
    public static final String ADMIN_TOGGLE_USER = "/admin/toggle";
    public static final String ADMIN_RESET_PASSWORD = "/admin/reset-password";

    private final UserManagementService userManagementService;
    private final UserMapper userMapper;

    @GetMapping(value = ADMIN)
    public ModelAndView adminPortal() {
        Map<String, ?> model = prepareAdminData();
        return new ModelAndView("admin_portal", model);
    }

    private Map<String, ?> prepareAdminData() {
        List<UserDTO> users = userManagementService.findAllUsers().stream()
                .map(userMapper::map)
                .toList();

        Set<String> availableRoles = userManagementService.getAvailableRoles();

        return Map.of(
                "usersDTO", UsersDTO.builder().users(users).build(),
                "availableRoles", availableRoles,
                "newUserDTO", UserDTO.buildDefault()
        );
    }

    // Password pattern: at least 12 characters, 1 uppercase, 1 lowercase, 1 special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{12,}$");
    
    // PESEL pattern: 11 digits
    private static final Pattern PESEL_DIGITS_PATTERN = Pattern.compile("^[0-9]{11}$");

    @PostMapping(value = ADMIN_CREATE_USER)
    public String createUser(
            @Valid @ModelAttribute("newUserDTO") UserDTO userDTO,
            @RequestParam("selectedRole") String selectedRole,
            @RequestParam("name") String name,
            @RequestParam("surname") String surname,
            @RequestParam("pesel") String pesel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        List<String> errors = new ArrayList<>();
        
        if (bindingResult.hasErrors()) {
            errors.add("Validation errors occurred in form fields.");
        }

        if (selectedRole == null || selectedRole.isEmpty()) {
            errors.add("Role must be selected.");
        }
        
        // Validate password requirements
        String password = userDTO.getPassword();
        if (password == null || password.isEmpty()) {
            errors.add("Password is required.");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errors.add("Password must be at least 12 characters long and contain at least one uppercase letter, one lowercase letter, and one special character (!@#$%^&* etc.).");
        }

        // Validate PESEL format and date
        String peselError = validatePesel(pesel);
        if (peselError != null) {
            errors.add(peselError);
        }

        if (!errors.isEmpty()) {
            model.addAllAttributes(prepareAdminData());
            model.addAttribute("errorMessages", errors);
            return "admin_portal";
        }

        try {
            User user = userMapper.map(userDTO);
            userManagementService.createUser(user, selectedRole, name, surname, pesel);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
            return "redirect:/admin";
        } catch (Exception e) {
            model.addAllAttributes(prepareAdminData());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin_portal";
        }
    }
    
    /**
     * Validates PESEL number including date validity.
     * Returns error message if invalid, null if valid.
     */
    private String validatePesel(String pesel) {
        if (pesel == null || pesel.isEmpty()) {
            return "PESEL is required.";
        }
        
        if (!PESEL_DIGITS_PATTERN.matcher(pesel).matches()) {
            return "PESEL must be exactly 11 digits.";
        }
        
        // Extract date components from PESEL
        int yearDigits = Integer.parseInt(pesel.substring(0, 2));
        int monthDigits = Integer.parseInt(pesel.substring(2, 4));
        int dayDigits = Integer.parseInt(pesel.substring(4, 6));
        
        // Determine century and actual month
        int century;
        int month;
        
        if (monthDigits >= 1 && monthDigits <= 12) {
            century = 1900;
            month = monthDigits;
        } else if (monthDigits >= 21 && monthDigits <= 32) {
            century = 2000;
            month = monthDigits - 20;
        } else if (monthDigits >= 41 && monthDigits <= 52) {
            century = 2100;
            month = monthDigits - 40;
        } else if (monthDigits >= 61 && monthDigits <= 72) {
            century = 2200;
            month = monthDigits - 60;
        } else if (monthDigits >= 81 && monthDigits <= 92) {
            century = 1800;
            month = monthDigits - 80;
        } else {
            return "Invalid month in PESEL.";
        }
        
        int year = century + yearDigits;
        int day = dayDigits;
        
        // Validate the date is valid (e.g., no February 31st)
        try {
            LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return "Invalid date in PESEL (e.g., February cannot have more than 29 days).";
        }
        
        return null; // Valid
    }

    @PostMapping(value = ADMIN_DELETE_USER)
    public String deleteUser(
            @RequestParam("userId") Integer userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userManagementService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete user: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping(value = ADMIN_TOGGLE_USER)
    public String toggleUserActive(
            @RequestParam("userId") Integer userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userManagementService.toggleUserActive(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot toggle user: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping(value = ADMIN_RESET_PASSWORD)
    public String resetUserPassword(
            @RequestParam("userId") Integer userId,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Validate password requirements
            if (newPassword == null || newPassword.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password is required.");
                return "redirect:/admin";
            }
            
            if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Password must be at least 12 characters long and contain at least one uppercase letter, one lowercase letter, and one special character (!@#$%^&* etc.).");
                return "redirect:/admin";
            }

            userManagementService.resetUserPassword(userId, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot reset password: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}
