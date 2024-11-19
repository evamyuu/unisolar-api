package unisolar.api.controller;

import jakarta.validation.Valid;
import unisolar.api.domain.dto.securityDTO.AuthenticationData;
import unisolar.api.domain.dto.securityDTO.JWTTokenData;
import unisolar.api.service.TokenService;
import unisolar.api.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling authentication requests.
 */
@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager; // Spring Security AuthenticationManager for handling authentication.

    @Autowired
    private TokenService tokenService; // Service responsible for token generation and management.

    /**
     * Handles login requests.
     *
     * @param data the authentication data containing username and password.
     *             Must be valid as per defined constraints.
     * @return a ResponseEntity containing a JWT token if authentication is successful.
     */
    @PostMapping
    public ResponseEntity login(@RequestBody @Valid AuthenticationData data) {
        // Create an authentication token using the provided username and password.
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());

        // Authenticate the user using the AuthenticationManager.
        var authentication = manager.authenticate(authenticationToken);

        // Generate a JWT token for the authenticated user.
        var jwtToken = tokenService.generateToken((User) authentication.getPrincipal());

        // Return the JWT token wrapped in a response entity.
        return ResponseEntity.ok(new JWTTokenData(jwtToken));
    }
}
