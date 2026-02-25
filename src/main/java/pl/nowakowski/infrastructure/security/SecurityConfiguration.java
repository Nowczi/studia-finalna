package pl.nowakowski.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationSuccessHandler customLoginSuccessHandler;
    private final PasswordEncoder passwordEncoder;
    private final PasswordChangeFilter passwordChangeFilter;

    @Autowired
    public SecurityConfiguration(
            @Lazy AuthenticationSuccessHandler customLoginSuccessHandler,
            PasswordEncoder passwordEncoder,
            PasswordChangeFilter passwordChangeFilter) {
        this.customLoginSuccessHandler = customLoginSuccessHandler;
        this.passwordEncoder = passwordEncoder;
        this.passwordChangeFilter = passwordChangeFilter;
    }

    @Bean
    public AuthenticationManager authManager(
            HttpSecurity http,
            UserDetailsService userDetailService
    )
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
    SecurityFilterChain securityEnabled(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/login", "/error", "/images/oh_no.png", "/change-password").permitAll()
                .requestMatchers(("/swagger-ui/**")).permitAll()
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                .requestMatchers("/mechanic/**").hasAnyAuthority("MECHANIC", "ADMIN")
                .requestMatchers("/salesman/**", "/purchase/**", "/service/**").hasAnyAuthority("SALESMAN", "ADMIN")
                .requestMatchers("/cepik/**").hasAnyAuthority("SALESMAN", "MECHANIC", "ADMIN")
                .requestMatchers("/", "/car/**", "/images/**").hasAnyAuthority("ADMIN")
                .requestMatchers("/api/**").hasAnyAuthority("REST_API", "ADMIN")
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .successHandler(customLoginSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();

        // Add password change filter after authentication filter
        http.addFilterAfter(passwordChangeFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "false")
    SecurityFilterChain securityDisabled(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests()
                .anyRequest()
                .permitAll();

        return http.build();
    }

}
