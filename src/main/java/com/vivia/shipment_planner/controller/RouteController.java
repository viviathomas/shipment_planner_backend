package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.model.RoutePlanRequest;
import com.vivia.shipment_planner.model.RoutePlanResult;
import com.vivia.shipment_planner.service.RouteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/plan")
    public RoutePlanResult planRoute(@RequestBody RoutePlanRequest request) {
        return routeService.planRoutes(request);
    }
}
