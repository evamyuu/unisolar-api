package unisolar.api.domain.dto.userDTO;

import unisolar.api.domain.entity.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the detailed information of a user.
 * Used for transferring user details between different layers of the application.
 *
 * @param id        the unique identifier of the user.
 * @param username  the username of the user.
 * @param name      the full name of the user.
 * @param email     the email address of the user.
 * @param active    a boolean indicating whether the user is active.
 * @param createdAt the date and time when the user was created.
 * @param deletedAt the date and time when the user was deleted, if applicable.
 *
 * This constructor initializes the UserDetailDTO from a User entity.
 */
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
