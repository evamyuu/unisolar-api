package unisolar.api.infra.config;

        import org.springframework.boot.CommandLineRunner;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Component;
        import org.springframework.beans.factory.annotation.Autowired;
        import unisolar.api.domain.entity.User;
        import unisolar.api.domain.repository.UserRepository;

/**
 * UserSeeder is a component that runs during the startup of the Spring Boot application.
 * It checks if a user with the username "user" already exists in the database, and if not, it seeds a default user.
 * This class implements CommandLineRunner to execute logic after the Spring Boot application has started.
 * It also uses PasswordEncoder to securely encode the user's password before saving it.
 *
 * Dependencies:
 * - UserRepository: The repository to interact with the User entity in the database.
 * - PasswordEncoder: A service to encode passwords securely.
 */
@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * This method runs on application startup. It checks if a user with the username "user" exists in the database.
     * If no such user is found, it creates a new User entity with predefined data and saves it in the repository.
     * The password for the user is encoded before it is saved.
     * After the user is seeded, a message "User seeded" is printed to the console.
     *
     * @param args command-line arguments passed to the application at startup.
     */
    @Override
    public void run(String... args) throws Exception {

        seedUser("user", "user@gmail.com", "password");
    }

    /**
     * Helper method to check if a user exists and create one if it does not exist.
     *
     * @param username The username of the user.
     * @param email The email of the user.
     * @param rawPassword The raw password (which will be encoded).
     */
    public void seedUser(String username, String email, String rawPassword) {

        if (userRepository.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setName(username);

            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);

            userRepository.save(user);
            System.out.println("User seeded.");
        }
    }
}

