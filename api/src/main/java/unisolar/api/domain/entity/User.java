package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import unisolar.api.domain.dto.userDTO.UserUpdateDTO;
import unisolar.api.domain.dto.userDTO.UserCreateDTO;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity class representing a user in the system.
 * This class implements the UserDetails interface for Spring Security authentication and authorization.
 *
 * @param id          the unique identifier of the user.
 * @param username    the username used for authentication.
 * @param password    the password used for authentication (stored in an encrypted form).
 * @param name        the full name of the user.
 * @param email       the email address of the user.
 * @param createdAt   the timestamp when the user account was created.
 * @param deletedAt   the timestamp when the user account was deactivated (if applicable).
 * @param active      indicates whether the user account is active.
 *
 * This entity contains user-related information, including authentication data, status, and related methods.
 * It integrates with Spring Security for authentication and authorization, providing roles and handling account states.
 */
@Table(name = "users")
@Entity(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String name;
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * Sets the createdAt field before the entity is persisted to the database.
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * Constructs a new User object from the provided UserCreateDTO data transfer object.
     * @param data the UserCreateDTO containing information to initialize the User.
     */
    public User(UserCreateDTO data) {
        this.active = true;
        this.username = data.username();
        this.password = data.password();
        this.name = data.name();
        this.email = data.email();
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Updates the user information based on the provided UserUpdateDTO data transfer object.
     * @param data the UserUpdateDTO containing updated user information.
     */
    public void updateInformations(UserUpdateDTO data) {
        if (data.username() != null) {
            this.username = data.username();
        }
        if (data.name() != null) {
            this.name = data.name();
        }
        if (data.email() != null) {
            this.email = data.email();
        }
    }

    /**
     * Returns the authorities granted to the user (role-based access control).
     * @return a collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Gets the user's password.
     * @return the password of the user.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user's username.
     * @return the username of the user.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Checks if the user's account is expired.
     * @return true since account expiration is not supported.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Checks if the user's account is locked.
     * @return true since account locking is not supported.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Checks if the user's credentials are expired.
     * @return true since credential expiration is not supported.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if the user's account is enabled.
     * @return true if the account is active, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    /**
     * Deactivates the user by setting the deletedAt timestamp and marking the account as inactive.
     */
    public void deactivate() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    /**
     * Checks if the user is active.
     * @return true if the user is active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }
}
