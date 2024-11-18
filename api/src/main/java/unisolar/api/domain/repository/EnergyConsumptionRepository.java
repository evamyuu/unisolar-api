package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import unisolar.api.domain.entity.EnergyConsumption;

import java.time.LocalDateTime;
import java.util.List;

public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long> {
    List<EnergyConsumption> findByInstallationIdAndTimestampBetween(
            Long installationId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(e.consumption) FROM EnergyConsumption e " +
            "WHERE e.installation.id = :installationId " +
            "AND e.timestamp BETWEEN :start AND :end")
    Double calculateTotalConsumption(Long installationId, LocalDateTime start, LocalDateTime end);
}
