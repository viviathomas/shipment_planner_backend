package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.analysis.PerformanceAnalysisAgent;
import com.vivia.shipment_planner.model.PerformanceReport;
import com.vivia.shipment_planner.model.Shipment;
import com.vivia.shipment_planner.service.ShipmentHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final ShipmentHistoryService history;
    private final PerformanceAnalysisAgent agent;

    public PerformanceController(ShipmentHistoryService history,
                                 PerformanceAnalysisAgent agent) {
        this.history = history;
        this.agent = agent;
    }

    @GetMapping
    public PerformanceReport analyze(
            @RequestParam(defaultValue = "COST_OPTIMIZED") String strategy
    ) {
        List<Shipment> shipments = history.getAll();
        return agent.analyze(shipments, strategy);
    }
}
