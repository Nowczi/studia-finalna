package pl.nowakowski.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that ensures users with password_change_required=true can only access
 * the change-password page and logout. All other requests are redirected to
 * the change-password page.
 */
@Component
public class PasswordChangeFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public PasswordChangeFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        
        // Allow access to these paths without checking password change status
        if (isAllowedPath(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            String userName = authentication.getName();
            
            // Check if password change is required
            Boolean passwordChangeRequired = userRepository.findPasswordChangeRequiredByUserName(userName);
            
            if (Boolean.TRUE.equals(passwordChangeRequired)) {
                // User must change password - redirect to change-password page
                if (!requestUri.contains("/change-password")) {
                    response.sendRedirect("/car-dealership/change-password");
                    return;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAllowedPath(String requestUri) {
        // Allow login page, logout, static resources, and error pages
        return requestUri.contains("/login") 
                || requestUri.contains("/logout")
                || requestUri.contains("/error")
                || requestUri.startsWith("/car-dealership/images/")
                || requestUri.contains("/swagger-ui")
                || requestUri.contains("/api/");
    }
}
