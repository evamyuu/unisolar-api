package unisolar.api.domain.dto.energy;

/**
 * Data Transfer Object (DTO) representing a battery's details.
 * Used for transferring battery data between different layers of the application.
 *
 * @param id              the unique identifier of the battery.
 * @param currentCharge   the current charge level of the battery in percentage or relevant unit.
 * @param capacity        the total capacity of the battery in relevant unit (e.g., kWh).
 * @param cycleCount      the number of charge/discharge cycles the battery has undergone.
 * @param temperature     the current temperature of the battery in degrees Celsius.
 * @param health          a string indicating the overall health status of the battery.
 * @param status          a string indicating the operational status of the battery (e.g., "Operational").
 */
public record BatteryDTO(
        Long id,
        double currentCharge,
        double capacity,
        int cycleCount,
        double temperature,
        String health,
        String status
) {}
