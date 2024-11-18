package unisolar.api.domain.dto.energy;

import java.time.LocalDateTime;

public record EnergyConsumptionDTO(
        Long id,
        Long installationId,
        LocalDateTime timestamp,
        double consumption,
        double gridConsumption,
        double solarConsumption,
        double batteryConsumption
) {}
