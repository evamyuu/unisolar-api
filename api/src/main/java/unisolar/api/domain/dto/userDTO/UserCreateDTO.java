package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing the data required to create a new user.
 * Used for transferring user creation information between different layers of the application.
 *
 * @param username  the username chosen by the user, which must be between 1 and 100 characters long.
 * @param password  the password chosen by the user, which must be at least 8 characters long and meet certain security criteria.
 *                  It must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.
 * @param name      the full name of the user, which must be between 1 and 100 characters long.
 * @param email     the email address of the user, which must be valid according to the email format.
 *
 * Validation rules:
 * - Username must not be blank and must be between 1 and 100 characters.
 * - Password must be at least 8 characters long and meet complexity requirements (uppercase, lowercase, digit, and special character).
 * - Name must not be blank and must be between 1 and 100 characters.
 * - Email must be a valid email format and not be blank.
 */
public record UserCreateDTO(
        @NotBlank(message = "The username is required.")
        @Size(min = 1, max = 100, message = "The username must be between 1 and 100 characters.")
        String username,

        @NotBlank(message = "The password is required.")
        @Size(min = 8, message = "The password must be at least 8 characters long.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "The password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
        String password,

        @NotBlank(message = "The name is required.")
        @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters.")
        String name,

        @NotBlank(message = "The email is required.")
        @Email(message = "The email must be valid.")
        String email
) {}
