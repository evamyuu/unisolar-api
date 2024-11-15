package unisolar.api.infra.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import unisolar.api.domain.entity.User;
import unisolar.api.domain.repository.UserRepository;

@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setName("user");
            user.setEmail("user@gmail.com");

            String rawPassword = "password";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);

            userRepository.save(user);
        }

        System.out.println("User seeded");
    }
}