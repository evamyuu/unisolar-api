package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        Long id,

        @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters.")
        String username,

        String name,
        @Email(message = "The email must be valid.")
        String email
) {}

