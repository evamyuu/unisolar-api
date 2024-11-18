package unisolar.api.domain.dto.energy;

public record BatteryDTO(
        Long id,
        double currentCharge,
        double capacity,
        int cycleCount,
        double temperature,
        String health,
        String status
) {}
