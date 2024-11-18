package unisolar.api.domain.dto.energy;

import java.time.LocalDateTime;

public record InstallationDTO(
        Long id,
        Long userId,
        LocalDateTime installationDate,
        String status,
        double totalPowerGenerated,
        double totalEnergySaved
) {}