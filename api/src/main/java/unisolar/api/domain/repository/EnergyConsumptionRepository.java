package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import unisolar.api.domain.entity.EnergyConsumption;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing EnergyConsumption entities in the database.
 * This interface extends JpaRepository to provide CRUD operations and custom queries for EnergyConsumption entities.
 * It includes methods for retrieving energy consumption data by installation ID and time range,
 * as well as calculating the total consumption over a period.
 *
 * @see JpaRepository
 *
 * Methods:
 * - findByInstallationIdAndTimestampBetween(Long installationId, LocalDateTime start, LocalDateTime end):
 *   Retrieves a list of energy consumption records for a specific installation within a given time range.
 * - calculateTotalConsumption(Long installationId, LocalDateTime start, LocalDateTime end):
 *   Calculates the total energy consumption for a specific installation between two timestamps.
 */
public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long> {

    /**
     * Finds all energy consumption records for a given installation and timestamp range.
     *
     * @param installationId the ID of the installation.
     * @param start the start of the time range.
     * @param end the end of the time range.
     * @return a list of energy consumption records matching the specified criteria.
     */
    List<EnergyConsumption> findByInstallationIdAndTimestampBetween(
            Long installationId, LocalDateTime start, LocalDateTime end);

    /**
     * Calculates the total energy consumption for a specific installation between two timestamps.
     *
     * @param installationId the ID of the installation.
     * @param start the start of the time range.
     * @param end the end of the time range.
     * @return the total energy consumption for the installation in the specified time range.
     */
    @Query("SELECT SUM(e.consumption) FROM EnergyConsumption e " +
            "WHERE e.installation.id = :installationId " +
            "AND e.timestamp BETWEEN :start AND :end")
    Double calculateTotalConsumption(Long installationId, LocalDateTime start, LocalDateTime end);
}
