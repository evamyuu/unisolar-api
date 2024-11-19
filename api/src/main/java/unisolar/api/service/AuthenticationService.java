package unisolar.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import unisolar.api.domain.repository.UserRepository;

/**
 * AuthenticationService is a service that implements the UserDetailsService interface.
 * It is responsible for loading user details based on the username, allowing Spring Security
 * to authenticate users during login.
 */
@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository repository;  // Repository to fetch user data

    /**
     * This method is called by Spring Security to load user details during authentication.
     * It retrieves the user from the repository based on the provided username.
     *
     * @param username The username of the user to be loaded.
     * @return A UserDetails object representing the user.
     * @throws UsernameNotFoundException If no user is found with the provided username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user by username from the repository
        return repository.findByUsername(username);
    }
}
