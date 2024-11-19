package unisolar.api.domain.dto.securityDTO;

/**
 * Data Transfer Object (DTO) representing a JWT token.
 * Used for transferring the generated JWT token between different layers of the application.
 *
 * @param token the JWT token issued after successful authentication.
 */
public record JWTTokenData(String token) {
}
