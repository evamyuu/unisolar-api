package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing a battery in the system.
 * This class is mapped to the "batteries" table in the database.
 *
 * @param id           the unique identifier of the battery.
 * @param currentCharge the current charge of the battery (measured in relevant units).
 * @param capacity     the total capacity of the battery (measured in relevant units).
 * @param cycleCount   the number of charge/discharge cycles the battery has undergone.
 * @param temperature  the current temperature of the battery.
 * @param health       the health status of the battery (e.g., "Good", "Needs Replacement").
 * @param status       the operational status of the battery (e.g., "Operational", "Inactive").
 * @param installation the installation associated with this battery.
 *
 * This entity is part of a larger system where batteries are associated with installations, and it uses JPA annotations
 * to map the fields to database columns.
 */
@Entity
@Table(name = "batteries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Battery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double currentCharge;
    private double capacity;
    private int cycleCount;
    private double temperature;
    private String health;
    private String status;

    @OneToOne
    @JoinColumn(name = "installation_id")
    private Installation installation;
}
