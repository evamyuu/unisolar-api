package unisolar.api.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unisolar.api.Priority;
import unisolar.api.domain.entity.Battery;
import unisolar.api.domain.entity.MaintenanceAlert;
import unisolar.api.domain.entity.SolarPanel;
import unisolar.api.domain.repository.SolarPanelRepository;
import unisolar.api.domain.repository.BatteryRepository;
import unisolar.api.infra.exception.ExceptionValidation;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceService {
    SolarPanelRepository solarPanelRepository;
    BatteryRepository batteryRepository;

    @Autowired
    public MaintenanceService(SolarPanelRepository solarPanelRepository, BatteryRepository batteryRepository) {
        this.solarPanelRepository = solarPanelRepository;
        this.batteryRepository = batteryRepository;
    }

    public List<MaintenanceAlert> checkSystemHealth(Long installationId) {
        List<MaintenanceAlert> alerts = new ArrayList<>();

        // Verificar saúde dos painéis solares
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

        // Verificar saúde da bateria
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
