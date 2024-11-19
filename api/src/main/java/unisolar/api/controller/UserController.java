package unisolar.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import unisolar.api.domain.dto.userDTO.ChangePasswordDTO;
import unisolar.api.domain.dto.userDTO.UserCreateDTO;
import unisolar.api.domain.dto.userDTO.UserDetailDTO;
import unisolar.api.domain.dto.userDTO.UserUpdateDTO;
import unisolar.api.domain.entity.Battery;
import unisolar.api.domain.entity.Installation;
import unisolar.api.domain.entity.SolarPanel;
import unisolar.api.domain.entity.User;
import unisolar.api.domain.repository.UserRepository;
import unisolar.api.infra.exception.ExceptionValidation;

import java.util.Arrays;

/**
 * Controller responsible for managing user-related operations such as
 * registration, updating user details, password changes, and retrieving user data.
 */
@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    @Autowired
    private UserRepository repository; // Repository for user persistence and retrieval.

    @Autowired
    private PasswordEncoder passwordEncoder; // Utility for encoding and verifying passwords.

    /**
     * Registers a new user in the system.
     *
     * @param data        the user data to be registered.
     * @param uriBuilder  utility for building URIs for created resources.
     * @return a ResponseEntity containing the created user's details.
     */
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDetailDTO> createUser(@RequestBody @Valid UserCreateDTO data, UriComponentsBuilder uriBuilder) {
        // Check for existing username or email.
        UserDetails existingUserByUsername = repository.findByUsername(data.username());
        if (existingUserByUsername != null) {
            throw new ExceptionValidation("Username already exists.");
        }

        UserDetails existingUserByEmail = repository.findByEmail(data.email());
        if (existingUserByEmail != null) {
            throw new ExceptionValidation("Email already exists.");
        }

        // Encode password and create user entity.
        String encodedPassword = passwordEncoder.encode(data.password());
        var user = new User(data);
        user.setPassword(encodedPassword);

        repository.save(user); // Persist the user.

        // Build URI for the created user and return the response.
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDetailDTO(user));
    }

    /**
     * Updates user information.
     *
     * @param data the updated user data.
     * @return a ResponseEntity containing the updated user's details.
     */
    @PutMapping
    @Transactional
    public ResponseEntity<UserDetailDTO> updateUser(@RequestBody @Valid UserUpdateDTO data) {
        var user = repository.findById(data.id())
                .filter(User::isActive)
                .orElseThrow(() -> new ExceptionValidation("User not found or inactive"));

        user.updateInformations(data);
        return ResponseEntity.ok(new UserDetailDTO(user));
    }

    /**
     * Deactivates a user account.
     *
     * @param id the ID of the user to deactivate.
     * @return a ResponseEntity with a success message.
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        var user = repository.findById(id)
                .filter(User::isActive)
                .orElseThrow(() -> new ExceptionValidation("User not found or already inactive"));

        user.deactivate();
        repository.save(user);
        return ResponseEntity.ok("User deactivated successfully");
    }

    /**
     * Changes the user's password.
     *
     * @param id               the ID of the user.
     * @param changePasswordDTO the DTO containing the old and new passwords.
     * @return a ResponseEntity with a success message or an error message if the old password is incorrect.
     */
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        var user = repository.findById(id)
                .filter(User::isActive)
                .orElseThrow(() -> new ExceptionValidation("User not found or inactive"));

        if (passwordEncoder.matches(changePasswordDTO.oldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
            repository.save(user);
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password incorrect");
        }
    }

    /**
     * Retrieves the currently authenticated user's details.
     *
     * @param authentication the current authentication object.
     * @return a ResponseEntity containing the authenticated user's details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        User user = (User) repository.findByUsername(username);

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(new UserDetailDTO(user));
    }

    /**
     * Retrieves a user's installation details (simulated for demonstration).
     *
     * @param userId the ID of the user whose installation details are to be retrieved.
     * @return a ResponseEntity containing the installation details.
     */
    public ResponseEntity<Installation> getUserInstallation(Long userId) {
        // Simulating user retrieval.
        User user = repository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build(); // Return 404 if user not found.
        }

        // Simulating installation data.
        SolarPanel panel1 = new SolarPanel(1L, "Roof", 250.0, 1000.0, 0.8, "Operational", null);
        SolarPanel panel2 = new SolarPanel(2L, "Garage", 240.0, 950.0, 0.75, "Operational", null);
        Battery battery = new Battery(1L, 85.0, 100.0, 120, 25.0, "Good", "Operational", null);

        Installation installation = new Installation(
                1L,
                user,
                Arrays.asList(panel1, panel2),
                battery,
                null,
                "Active",
                2000.0,
                800.0
        );

        // Return the simulated installation data.
        return ResponseEntity.ok(installation);
    }
}
