package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing a request to change a user's password.
 * Used for transferring data required to change the password of a user.
 *
 * @param oldPassword  the current password of the user.
 * @param newPassword  the new password the user wishes to set, which must meet specific security criteria.
 *
 * Validation rules:
 * - The new password must be at least 8 characters long.
 * - The new password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.
 */
public record ChangePasswordDTO(
        String oldPassword,

        @Size(min = 8, message = "The password must be at least 8 characters long.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "The password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
        String newPassword
) {}
