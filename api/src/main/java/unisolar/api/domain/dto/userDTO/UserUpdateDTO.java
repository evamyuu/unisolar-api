package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing the data required to update an existing user's information.
 * Used for transferring user update information between different layers of the application.
 *
 * @param id        the unique identifier of the user to be updated.
 * @param username  the username of the user, which must be between 1 and 100 characters long.
 * @param name      the full name of the user to be updated.
 * @param email     the email address of the user, which must be valid according to the email format.
 *
 * Validation rules:
 * - Username must be between 1 and 100 characters long.
 * - Email must be a valid email format.
 */
public record UserUpdateDTO(
        Long id,

        @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters.")
        String username,

        String name,

        @Email(message = "The email must be valid.")
        String email
) {}
