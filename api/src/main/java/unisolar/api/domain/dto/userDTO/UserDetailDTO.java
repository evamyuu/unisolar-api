package unisolar.api.domain.dto.userDTO;

import unisolar.api.domain.entity.User;

import java.time.LocalDateTime;

public record UserDetailDTO(
        Long id,
        String username,
        String name,
        String email,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime deletedAt


) {
    public UserDetailDTO(User user) {
        this(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getActive(), user.getCreatedAt(), user.getDeletedAt());
    }

}

