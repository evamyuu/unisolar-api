package unisolar.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unisolar.api.enums.Priority;
import unisolar.api.domain.entity.Battery;
import unisolar.api.domain.entity.MaintenanceAlert;
import unisolar.api.domain.entity.SolarPanel;
import unisolar.api.domain.repository.SolarPanelRepository;
import unisolar.api.domain.repository.BatteryRepository;
import unisolar.api.infra.exception.ExceptionValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * MaintenanceService is a service class responsible for checking the health of the solar panel system
 * and battery associated with a specific installation, and generating appropriate maintenance alerts.
 */
@Service
public class MaintenanceService {
    private final SolarPanelRepository solarPanelRepository;
    private final BatteryRepository batteryRepository;

    /**
     * Constructor for MaintenanceService that injects the required repositories for solar panels and batteries.
     *
     * @param solarPanelRepository The repository to interact with solar panel data.
     * @param batteryRepository The repository to interact with battery data.
     */
    @Autowired
    public MaintenanceService(SolarPanelRepository solarPanelRepository, BatteryRepository batteryRepository) {
        this.solarPanelRepository = solarPanelRepository;
        this.batteryRepository = batteryRepository;
    }

    /**
     * Checks the health of the solar panel system and battery for a given installation ID.
     * It generates maintenance alerts for issues like low efficiency of solar panels and high cycle count of batteries.
     *
     * @param installationId The ID of the installation to check the system health for.
     * @return A list of maintenance alerts based on the health checks.
     * @throws ExceptionValidation If the battery is not found for the given installation ID.
     */
    public List<MaintenanceAlert> checkSystemHealth(Long installationId) {
        List<MaintenanceAlert> alerts = new ArrayList<>();

        // Check the health of the solar panels
        List<SolarPanel> panels = solarPanelRepository.findByInstallationId(installationId);
        for (SolarPanel panel : panels) {
            if (panel.getEfficiency() < 0.7) {
                alerts.add(new MaintenanceAlert(
                        "LOW_EFFICIENCY",
                        "Solar panel " + panel.getId() + " has low efficiency",
                        Priority.HIGH
                ));
            }
        }

        // Check the health of the battery
        Battery battery = batteryRepository.findByInstallationId(installationId)
                .orElseThrow(() -> new ExceptionValidation("Battery not found"));

        if (battery.getCycleCount() > 1000) {
            alerts.add(new MaintenanceAlert(
                    "HIGH_CYCLE_COUNT",
                    "Battery cycle count is high, consider replacement",
                    Priority.MEDIUM
            ));
        }

        return alerts;
    }
}
