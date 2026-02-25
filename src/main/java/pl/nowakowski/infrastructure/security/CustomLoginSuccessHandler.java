package pl.nowakowski.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomLoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String userName = authentication.getName();
        
        // Check if password change is required - use direct query to avoid lazy loading issues
        Boolean passwordChangeRequired = userRepository.findPasswordChangeRequiredByUserName(userName);
        
        if (Boolean.TRUE.equals(passwordChangeRequired)) {
            response.sendRedirect("/car-dealership/change-password");
            return;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("MECHANIC")) {
                response.sendRedirect("/car-dealership/mechanic");
                return;
            } else if (role.equals("SALESMAN")) {
                response.sendRedirect("/car-dealership/salesman");
                return;
            } else if (role.equals("ADMIN")) {
                response.sendRedirect("/car-dealership");
                return;
            } else if (role.equals("REST_API")) {
                response.sendRedirect("/car-dealership/api");
                return;
            }
        }

        // Default redirect if no specific role matched
        response.sendRedirect("/car-dealership");
    }
}
