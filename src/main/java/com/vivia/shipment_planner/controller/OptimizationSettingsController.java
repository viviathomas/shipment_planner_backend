package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.model.OptimizationSettings;
import com.vivia.shipment_planner.service.OptimizationSettingsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/optimization-settings")

public class OptimizationSettingsController {

    private final OptimizationSettingsService service;

    public OptimizationSettingsController(OptimizationSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public OptimizationSettings get() {
        return service.getSettings();
    }

    @PostMapping
    public OptimizationSettings update(@RequestBody OptimizationSettings newSettings) {
        service.updateSettings(newSettings);
        return newSettings;
    }
}
