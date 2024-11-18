package unisolar.api.domain.dto.energy;

import java.util.List;

public record WeatherDTO(
        String condition,
        double temperature,
        double cloudCover,
        double solarIrradiance
) {}
