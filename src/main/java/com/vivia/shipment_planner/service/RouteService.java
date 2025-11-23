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

    public RouteService(WeightedRouteOptimizer optimizer, OrderService orderService, LaneService laneService) {
        this.optimizer = optimizer;
        this.orderService = orderService;
        this.laneService = laneService;
    }

    public RoutePlanResult planRoutes(RoutePlanRequest request) {
        // If request provides order IDs -> filter, otherwise use all uploaded orders
        List<Order> orders;
        if (request.getOrders() == null || request.getOrders().isEmpty()) {
            orders = orderService.getAllOrders();
        } else {
            orders = orderService.getOrders(request.getOrders());
        }

        if (orders == null || orders.isEmpty()) {
            throw new RuntimeException("No orders to plan. Upload orders or send order IDs in request.");
        }

        Map<String, List<Order>> groups = groupOrdersByOD(orders);
        return optimizer.planShipments(groups, laneService, request.getAlpha(), request.getBeta(), request.getGamma());
    }

    // helper
    private Map<String, List<Order>> groupOrdersByOD(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(o -> o.getSource() + "|" + o.getDestination()));
    }
}
