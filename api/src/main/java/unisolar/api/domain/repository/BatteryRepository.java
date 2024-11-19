package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unisolar.api.domain.entity.Battery;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Battery entities in the database.
 * This interface extends JpaRepository to provide CRUD operations and custom queries for Battery entities.
 * It includes methods for retrieving batteries by installation ID and by health status.
 *
 * @see JpaRepository
 *
 * Methods:
 * - findByInstallationId(Long installationId): Retrieves a battery by its associated installation ID.
 * - findByHealth(String health): Retrieves a list of batteries based on their health status.
 */
public interface BatteryRepository extends JpaRepository<Battery, Long> {

    /**
     * Finds a battery by the associated installation ID.
     *
     * @param installationId the installation ID.
     * @return an Optional containing the battery if found, otherwise empty.
     */
    Optional<Battery> findByInstallationId(Long installationId);

    /**
     * Finds all batteries by their health status.
     *
     * @param health the health status of the battery (e.g., "Good", "Needs Maintenance").
     * @return a list of batteries matching the specified health status.
     */
    List<Battery> findByHealth(String health);
}
