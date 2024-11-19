package unisolar.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import unisolar.api.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * TokenService is a service responsible for generating and verifying JSON Web Tokens (JWT)
 * for authentication and authorization purposes in the Unisolar API.
 */
@Service
public class TokenService {

    // Secret key for signing the JWT, injected from application properties
    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Generates a JWT token for the given user. The token contains the user's username as the subject,
     * the issuer as "Unisolar API", and an expiration date set to 2 hours from the current time.
     *
     * @param user The user for whom the token will be generated.
     * @return The generated JWT token as a string.
     * @throws RuntimeException if there is an error during token creation.
     */
    public String generateToken(User user) {
        try {
            // Define the algorithm for signing the JWT with HMAC256
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Unisolar API") // Issuer of the token
                    .withSubject(user.getUsername()) // Subject of the token (user's username)
                    .withExpiresAt(expirationDate()) // Expiration date of the token
                    .sign(algorithm); // Sign the token using the algorithm
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error generating JWT token", exception);
        }
    }

    /**
     * Extracts the subject (username) from the provided JWT token.
     *
     * @param jwtToken The JWT token to extract the subject from.
     * @return The subject (username) of the token.
     * @throws RuntimeException if the token is invalid or expired.
     */
    public String getSubject(String jwtToken) {
        try {
            // Define the algorithm for verifying the JWT with HMAC256
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Unisolar API") // Ensure the token's issuer is valid
                    .build()
                    .verify(jwtToken) // Verify the token
                    .getSubject(); // Retrieve the subject (username)
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid or expired JWT token!");
        }
    }

    /**
     * Calculates the expiration date for the JWT token, which is 2 hours from the current time.
     *
     * @return The expiration date as an Instant.
     */
    private Instant expirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
