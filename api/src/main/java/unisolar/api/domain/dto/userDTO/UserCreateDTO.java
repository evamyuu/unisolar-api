package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(
        @NotBlank(message = "The username is required.")
        @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters.")
        String username,

        @NotBlank(message = "The password is required.")
        @Size(min = 8, message = "The password must be at least 8 characters long.")
        String password,
        @NotBlank(message = "The name is required.")
        String name,
        @NotBlank(message = "The email is required.")
        @Email(message = "The email must be valid.")
        String email
) {}

