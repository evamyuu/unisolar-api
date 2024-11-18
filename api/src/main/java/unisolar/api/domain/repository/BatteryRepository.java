package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unisolar.api.domain.entity.Battery;

import java.util.List;
import java.util.Optional;

public interface BatteryRepository extends JpaRepository<Battery, Long> {
    Optional<Battery> findByInstallationId(Long installationId);
    List<Battery> findByHealth(String health);
}