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
import unisolar.api.domain.entity.User;
import unisolar.api.domain.repository.UserRepository;
import unisolar.api.infra.exception.ExceptionValidation;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDetailDTO> createUser(@RequestBody @Valid UserCreateDTO data, UriComponentsBuilder uriBuilder) {

        UserDetails existingUserByUsername = repository.findByUsername(data.username());
        if (existingUserByUsername != null) {
            throw new ExceptionValidation("Username already exists.");
        }

        UserDetails existingUserByEmail = repository.findByEmail(data.email());
        if (existingUserByEmail != null) {
            throw new ExceptionValidation("Email already exists.");
        }

        String encodedPassword = passwordEncoder.encode(data.password());

        var user = new User(data);
        user.setPassword(encodedPassword);

        repository.save(user);

        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDetailDTO(user));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<UserDetailDTO> updateUser(@RequestBody @Valid UserUpdateDTO data) {
        var user = repository.findById(data.id())
                .filter(User::isActive)
                .orElseThrow(() -> new ExceptionValidation("User not found or inactive"));

        user.updateInformations(data);
        return ResponseEntity.ok(new UserDetailDTO(user));
    }

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

    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        User user = (User) repository.findByUsername(username);

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(new UserDetailDTO(user));
    }
}
