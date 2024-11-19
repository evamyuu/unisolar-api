package unisolar.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * SecurityConfigurations is a configuration class that sets up the security settings for the application.
 * It configures authentication, authorization, CORS, and session management, ensuring the security of the API.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    /**
     * Configures the security filter chain for the application, including CORS, CSRF, session management,
     * and URL-based authorization.
     *
     * @param http HttpSecurity instance to configure the security settings.
     * @return SecurityFilterChain object with the configured security rules.
     * @throws Exception If any error occurs while setting up security configurations.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));  // Allowing CORS for a specific origin
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .csrf(csrf -> csrf.disable())  // Disabling CSRF protection for stateless authentication
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Using stateless sessions
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/login", "/user/register", "/chat/**", "/css/**", "/js/**", "/img/**").permitAll();  // Public endpoints
                    req.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll();  // Swagger endpoints
                    req.anyRequest().authenticated();  // All other requests require authentication
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)  // Adding custom security filter before default filter
                .build();
    }

    /**
     * Provides the AuthenticationManager bean for managing authentication.
     *
     * @param configuration AuthenticationConfiguration instance to retrieve the AuthenticationManager.
     * @return AuthenticationManager instance.
     * @throws Exception If any error occurs while setting up the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Provides the PasswordEncoder bean, which is used for encoding passwords before storing them in the database.
     *
     * @return PasswordEncoder instance (BCryptPasswordEncoder).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Using BCrypt for password encoding
    }
}
