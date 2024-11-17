package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
