package unisolar.api.domain.dto.userDTO;

import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        String oldPassword,
        @Size(min = 8, message = "The password must be at least 8 characters long.")
        String newPassword

){}
