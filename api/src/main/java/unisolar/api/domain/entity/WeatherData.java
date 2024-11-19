package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity class representing weather data associated with solar energy systems.
 * This entity stores data about weather conditions, including temperature, cloud cover, and solar irradiance,
 * which can impact energy production in solar panel systems.
 *
 * @param id               the unique identifier of the weather data.
 * @param timestamp        the date and time when the weather data was recorded.
 * @param condition        the general weather condition (e.g., sunny, cloudy, rainy).
 * @param temperature      the temperature at the time the data was recorded, in degrees Celsius.
 * @param cloudCover       the percentage of cloud cover at the time of data collection.
 * @param solarIrradiance  the level of solar irradiance measured, which affects solar power generation.
 *
 * This entity is used for storing and retrieving weather-related data used in analyzing solar energy production.
 */
@Entity
@Table(name = "weather_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String condition;
    private double temperature;
    private double cloudCover;
    private double solarIrradiance;
}
