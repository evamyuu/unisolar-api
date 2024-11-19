package unisolar.api.domain.dto.securityDTO;

/**
 * Data Transfer Object (DTO) representing authentication data.
 * Used for transferring user login information between different layers of the application.
 *
 * @param username the username of the user attempting to authenticate.
 * @param password the password of the user attempting to authenticate.
 */
public record AuthenticationData(String username, String password) {
}
