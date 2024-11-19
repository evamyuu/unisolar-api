package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unisolar.api.domain.entity.Installation;

import java.util.List;

/**
 * Repository interface for managing Installation entities in the database.
 * This interface extends JpaRepository to provide CRUD operations and custom queries for Installation entities.
 * It includes methods for retrieving installations by user ID and status.
 *
 * @see JpaRepository
 *
 * Methods:
 * - findByUserId(Long userId): Retrieves a list of installations associated with a specific user.
 * - findByStatus(String status): Retrieves a list of installations with a specific status.
 */
public interface InstallationRepository extends JpaRepository<Installation, Long> {

    /**
     * Finds all installations associated with a given user ID.
     *
     * @param userId the ID of the user.
     * @return a list of installations associated with the specified user.
     */
    List<Installation> findByUserId(Long userId);

    /**
     * Finds all installations with a given status.
     *
     * @param status the status of the installations.
     * @return a list of installations that match the specified status.
     */
    List<Installation> findByStatus(String status);
}
