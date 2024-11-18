package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unisolar.api.domain.entity.SolarPanel;

import java.util.List;

public interface SolarPanelRepository extends JpaRepository<SolarPanel, Long> {
    List<SolarPanel> findByInstallationId(Long installationId);
    List<SolarPanel> findByStatus(String status);
}
