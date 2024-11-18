package unisolar.api.domain.dto.energy;

import java.util.List;

public record SystemStatusDTO(
        InstallationDTO installation,
        List<SolarPanelDTO> solarPanels,
        BatteryDTO battery,
        double currentConsumption,
        WeatherDTO currentWeather,
        double projectedSavings
) {}
