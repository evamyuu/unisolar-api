package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unisolar.api.domain.entity.SolarPanel;

import java.util.List;

/**
 * Repository interface for managing SolarPanel entities in the database.
 * This interface extends JpaRepository to provide CRUD operations and custom queries for SolarPanel entities.
 * It includes methods for retrieving solar panels by installation ID and status.
 *
 * @see JpaRepository
 *
 * Methods:
 * - findByInstallationId(Long installationId): Retrieves a list of solar panels associated with a specific installation.
 * - findByStatus(String status): Retrieves a list of solar panels with a specific status.
 */
public interface SolarPanelRepository extends JpaRepository<SolarPanel, Long> {

    /**
     * Finds all solar panels associated with a given installation ID.
     *
     * @param installationId the ID of the installation.
     * @return a list of solar panels associated with the specified installation.
     */
    List<SolarPanel> findByInstallationId(Long installationId);

    /**
     * Finds all solar panels with a given status.
     *
     * @param status the status of the solar panels.
     * @return a list of solar panels that match the specified status.
     */
    List<SolarPanel> findByStatus(String status);
}
