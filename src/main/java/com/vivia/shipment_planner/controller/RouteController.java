package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.model.RoutePlanRequest;
import com.vivia.shipment_planner.model.RoutePlanResult;
import com.vivia.shipment_planner.model.Shipment;
import com.vivia.shipment_planner.service.RouteService;
import com.vivia.shipment_planner.service.ShipmentHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;
    private final ShipmentHistoryService history;

    public RouteController(RouteService routeService, ShipmentHistoryService history) {
        this.routeService = routeService;
        this.history = history;
    }

    @PostMapping("/plan")
    public RoutePlanResult planRoute(@RequestBody RoutePlanRequest request) {

        RoutePlanResult result = routeService.planRoutes(request);
        return result;
    }

    @GetMapping("/history")
    public List<Shipment> getHistory() {
        return history.getAll();
    }

    @PutMapping("/history/clear")
    public void clearHistory() {
        history.clear();
    }
}
