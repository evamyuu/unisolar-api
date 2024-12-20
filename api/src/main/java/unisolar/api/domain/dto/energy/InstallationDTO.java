package unisolar.api.domain.dto.energy;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing details of a solar installation.
 * Used for transferring installation data between different layers of the application.
 *
 * @param id                  the unique identifier of the installation.
 * @param userId              the ID of the user associated with the installation.
 * @param installationDate    the date and time when the installation was completed.
 * @param status              the current operational status of the installation (e.g., "Active").
 * @param totalPowerGenerated the total amount of power generated by the installation in relevant units (e.g., kWh).
 * @param totalEnergySaved    the total amount of energy saved by the installation in relevant units (e.g., kWh).
 */
public record InstallationDTO(
        Long id,
        Long userId,
        LocalDateTime installationDate,
        String status,
        double totalPowerGenerated,
        double totalEnergySaved
) {}
