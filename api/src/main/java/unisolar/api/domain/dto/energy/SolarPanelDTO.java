package unisolar.api.domain.dto.energy;

public record SolarPanelDTO(
        Long id,
        String location,
        double currentPowerGeneration,
        double totalPowerGenerated,
        double efficiency,
        String status
) {}

