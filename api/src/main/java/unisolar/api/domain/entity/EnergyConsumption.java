package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    public EnergyConsumption(double solarConsumption, double gridConsumption, double batteryConsumption) {
    }
}
