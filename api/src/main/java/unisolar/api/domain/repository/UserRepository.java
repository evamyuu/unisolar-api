package unisolar.api.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import unisolar.api.domain.entity.User;

/**
 * Repository interface for managing User entities in the database.
 * This interface extends JpaRepository to provide CRUD operations, as well as custom queries for User entities.
 * It includes methods for finding users by username, email, and searching with pagination.
 *
 * @see JpaRepository
 *
 * Methods:
 * - findByUsername(String username): Retrieves a UserDetails object for a user by their username.
 * - findByEmail(String email): Retrieves a UserDetails object for a user by their email.
 * - searchByNameUsernameOrEmailAndActiveTrue(String search, Pageable pageable): Searches for users with a given search term
 *   (username, email, or name) and only returns active users, with pagination support.
 * - searchByNameUsernameOrEmail(String search, Pageable pageable): Searches for users by username, email, or name without checking if the user is active, with pagination.
 * - findAllByActiveTrue(Pageable pageable): Retrieves a page of all active users, with pagination support.
 * - findAll(Pageable pageable): Retrieves a page of all users, with pagination support.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user.
     * @return a UserDetails object for the user with the given username.
     */
    UserDetails findByUsername(String username);

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user.
     * @return a UserDetails object for the user with the given email.
     */
    UserDetails findByEmail(String email);

    /**
     * Searches for users by name, username, or email, and only returns active users.
     * This search is case-insensitive and supports pagination.
     *
     * @param search the search term (username, email, or name).
     * @param pageable pagination information.
     * @return a page of active users matching the search criteria.
     */
    @Query("SELECT u FROM User u WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND u.active = true")
    Page<User> searchByNameUsernameOrEmailAndActiveTrue(@Param("search") String search, Pageable pageable);

    /**
     * Searches for users by name, username, or email without filtering by active status.
     * This search is case-insensitive and supports pagination.
     *
     * @param search the search term (username, email, or name).
     * @param pageable pagination information.
     * @return a page of users matching the search criteria.
     */
    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchByNameUsernameOrEmail(@Param("search") String search, Pageable pageable);

    /**
     * Finds all active users, with pagination support.
     *
     * @param pageable pagination information.
     * @return a page of all active users.
     */
    Page<User> findAllByActiveTrue(Pageable pageable);

    /**
     * Finds all users, with pagination support.
     *
     * @param pageable pagination information.
     * @return a page of all users.
     */
    Page<User> findAll(Pageable pageable);
}
