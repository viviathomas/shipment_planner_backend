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


        List<Order> orders = (request.getOrders() == null || request.getOrders().isEmpty())
                ? orderService.getAllOrders()
                : orderService.getOrders(request.getOrders());

        if (orders.isEmpty()) {
            throw new RuntimeException("No orders to plan.");
        }


        Map<String, List<Order>> grouped = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getSource() + "|" + o.getDestination()));


        RoutePlanResult result = optimizer.planShipments(
                grouped,
                laneService
        );


        // Save successful shipments into history
        if (result.getShipments() != null) {
            shipmentHistoryService.addAll(result.getShipments());
        }

        return result;
    }
}
