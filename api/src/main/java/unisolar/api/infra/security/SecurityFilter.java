package unisolar.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import unisolar.api.domain.repository.UserRepository;
import unisolar.api.service.TokenService;

import java.io.IOException;

/**
 * SecurityFilter is a custom filter that is executed once per request to process security-related actions.
 * It retrieves the JWT token from the request header, validates it, and sets the corresponding authentication in the security context.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;  // Service to handle JWT token-related operations

    @Autowired
    private UserRepository repository;  // Repository to fetch user details from the database

    /**
     * This method is executed for each incoming HTTP request. It retrieves the JWT token from the request header,
     * validates it, and sets the authentication context for the user associated with the token.
     *
     * @param request The HttpServletRequest object containing the request details.
     * @param response The HttpServletResponse object to send the response.
     * @param filterChain The FilterChain object to continue the request processing.
     * @throws ServletException If a servlet error occurs during request processing.
     * @throws IOException If an I/O error occurs during request processing.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = retrieveToken(request);  // Retrieve the JWT token from the request
        if(tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);  // Extract the subject (username) from the token
            var user = repository.findByUsername(subject);  // Fetch the user from the database
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());  // Create authentication object
            SecurityContextHolder.getContext().setAuthentication(authentication);  // Set the authentication in the security context
        }
        filterChain.doFilter(request, response);  // Continue with the filter chain
    }

    /**
     * Retrieves the JWT token from the Authorization header of the HTTP request.
     *
     * @param request The HttpServletRequest object containing the request details.
     * @return The JWT token as a string, or null if no token is present in the request header.
     */
    private String retrieveToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");  // Get the Authorization header
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "").trim();  // Extract and return the token
        }
        return null;  // Return null if no Authorization header is found
    }
}
