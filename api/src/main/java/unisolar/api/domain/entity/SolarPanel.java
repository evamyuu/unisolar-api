package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solar_panels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SolarPanel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;
    private double currentPowerGeneration;
    private double totalPowerGenerated;
    private double efficiency;
    private String status;

    @ManyToOne
    @JoinColumn(name = "installation_id")
    private Installation installation;
}
