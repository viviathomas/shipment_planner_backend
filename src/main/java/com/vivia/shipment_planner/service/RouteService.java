package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final WeightedRouteOptimizer optimizer;
    private final OrderService orderService;
    private final LaneService laneService;
    private final ShipmentHistoryService shipmentHistoryService;

    public RouteService(
            WeightedRouteOptimizer optimizer,
            OrderService orderService,
            LaneService laneService,
            ShipmentHistoryService shipmentHistoryService
    ) {
        this.optimizer = optimizer;
        this.orderService = orderService;
        this.laneService = laneService;
        this.shipmentHistoryService = shipmentHistoryService;
    }

    public RoutePlanResult planRoutes(RoutePlanRequest request) {

        // Fetch relevant orders
        List<Order> orders = (request.getOrders() == null || request.getOrders().isEmpty())
                ? orderService.getAllOrders()
                : orderService.getOrders(request.getOrders());

        if (orders.isEmpty()) {
            throw new RuntimeException("No orders to plan.");
        }

        // Group by O|D (source-destination)
        Map<String, List<Order>> grouped = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getSource() + "|" + o.getDestination()));

        // Call optimizer — this returns both shipments + orphan orders
        RoutePlanResult result = optimizer.planShipments(
                grouped,
                laneService,
                request.getAlpha(),
                request.getBeta(),
                request.getGamma()
        );

        // Save successful shipments into history
        if (result.getShipments() != null) {
            shipmentHistoryService.addAll(result.getShipments());
        }

        return result;
    }
}
