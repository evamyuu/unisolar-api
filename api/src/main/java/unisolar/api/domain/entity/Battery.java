package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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
