package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.OptimizationSettings;
import org.springframework.stereotype.Service;

@Service
public class OptimizationSettingsService {

    private OptimizationSettings settings =
            new OptimizationSettings(0.15, 0.27, 0.58); // default weights

    public OptimizationSettings getSettings() {
        return settings;
    }

    public void updateSettings(OptimizationSettings newSettings) {
        this.settings = newSettings;
    }
}
