package unisolar.api.domain.dto.energy;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing weather conditions.
 * Used for transferring weather data between different layers of the application.
 *
 * @param condition       the current weather condition (e.g., "Sunny", "Cloudy").
 * @param temperature     the current temperature in degrees Celsius.
 * @param cloudCover      the percentage of cloud cover affecting solar irradiance (e.g., 0.75 for 75% cloud cover).
 * @param solarIrradiance the current solar irradiance in relevant units (e.g., W/m²).
 */
public record WeatherDTO(
        String condition,
        double temperature,
        double cloudCover,
        double solarIrradiance
) {}

