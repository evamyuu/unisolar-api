package unisolar.api.domain.dto.energy;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the current status of a solar energy system.
 * Used for transferring system status data between different layers of the application.
 *
 * @param installation       the details of the associated solar installation.
 * @param solarPanels        a list of solar panels associated with the installation.
 * @param battery            the details of the battery associated with the system.
 * @param currentConsumption the current energy consumption of the system in relevant units (e.g., kW).
 * @param currentWeather     the current weather conditions affecting the system.
 * @param projectedSavings   the projected energy savings based on the system's performance in relevant units (e.g., kWh).
 */
public record SystemStatusDTO(
        InstallationDTO installation,
        List<SolarPanelDTO> solarPanels,
        BatteryDTO battery,
        double currentConsumption,
        WeatherDTO currentWeather,
        double projectedSavings
) {}

