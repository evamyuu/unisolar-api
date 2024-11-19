package unisolar.api.domain.dto.energy;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing energy consumption details.
 * Used for transferring energy consumption data between different layers of the application.
 *
 * @param id                 the unique identifier of the energy consumption record.
 * @param installationId     the ID of the associated installation where the consumption was recorded.
 * @param timestamp          the timestamp indicating when the consumption data was recorded.
 * @param consumption        the total energy consumption in relevant units (e.g., kWh).
 * @param gridConsumption    the portion of energy consumed from the power grid in relevant units.
 * @param solarConsumption   the portion of energy consumed from solar generation in relevant units.
 * @param batteryConsumption the portion of energy consumed from battery storage in relevant units.
 */
public record EnergyConsumptionDTO(
        Long id,
        Long installationId,
        LocalDateTime timestamp,
        double consumption,
        double gridConsumption,
        double solarConsumption,
        double batteryConsumption
) {}
