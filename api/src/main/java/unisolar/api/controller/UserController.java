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
    public ResponseEntity updateUser(@RequestBody @Valid UserUpdateDTO data) {
        var user = repository.getReferenceById(data.id());
        user.updateInformations(data);
        return ResponseEntity.ok(new UserDetailDTO(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deactivateUser(@PathVariable Long id) {
        var user = repository.getReferenceById(id);
        user.deactivate();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(changePasswordDTO.oldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
                repository.save(user);
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password incorrect");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        UserDetails userDetails = repository.findByUsername(username);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = (User) userDetails;

        UserDetailDTO userDetailDTO = new UserDetailDTO(user);

        return ResponseEntity.ok(userDetailDTO);
    }

}
