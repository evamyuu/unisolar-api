package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity class representing energy consumption data in the system.
 * This class is mapped to the "energy_consumption" table in the database.
 *
 * @param id                the unique identifier for the energy consumption record.
 * @param installation      the installation associated with this energy consumption record.
 * @param timestamp         the timestamp when the energy consumption was recorded.
 * @param consumption       the total energy consumed (measured in relevant units).
 * @param gridConsumption   the amount of energy consumed from the grid.
 * @param solarConsumption  the amount of energy consumed from solar power.
 * @param batteryConsumption the amount of energy consumed from the battery.
 *
 * This entity represents energy consumption data for a particular installation, capturing various
 * sources of consumption such as grid, solar, and battery.
 */
@Entity
@Table(name = "energy_consumption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EnergyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "installation_id")
    private Installation installation;

    private LocalDateTime timestamp;
    private double consumption;
    private double gridConsumption;
    private double solarConsumption;
    private double batteryConsumption;

    /**
     * Constructor for creating an EnergyConsumption object with specified values for solar, grid,
     * and battery consumption.
     *
     * @param solarConsumption the amount of energy consumed from solar power.
     * @param gridConsumption  the amount of energy consumed from the grid.
     * @param batteryConsumption the amount of energy consumed from the battery.
     */
    public EnergyConsumption(double solarConsumption, double gridConsumption, double batteryConsumption) {
    }
}
