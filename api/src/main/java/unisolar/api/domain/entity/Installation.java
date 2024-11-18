package unisolar.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "installations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Installation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "installation")
    private List<SolarPanel> solarPanels;

    @OneToOne(mappedBy = "installation")
    private Battery battery;

    private LocalDateTime installationDate;
    private String status;
    private double totalPowerGenerated;
    private double totalEnergySaved;
}

