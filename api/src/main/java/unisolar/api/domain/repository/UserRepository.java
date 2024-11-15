package unisolar.api.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import unisolar.api.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    UserDetails findByUsername(String username);

    // Find user by email
    UserDetails findByEmail(String email);

    @Query("SELECT u FROM User u WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND u.active = true")
    Page<User> searchByNameUsernameOrEmailAndActiveTrue(@Param("search") String search, Pageable pageable);


    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchByNameUsernameOrEmail(@Param("search") String search, Pageable pageable);

    // Find all active users with pagination
    Page<User> findAllByActiveTrue(Pageable pageable);

    // Find all users with pagination
    Page<User> findAll(Pageable pageable);
}
