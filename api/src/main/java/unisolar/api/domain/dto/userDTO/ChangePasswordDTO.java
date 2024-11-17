package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        String oldPassword,

        @Size(min = 8, message = "The password must be at least 8 characters long.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "The password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
        String newPassword
) {}
