package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.analysis.PerformanceAnalysisAgent;
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
    private final PerformanceAnalysisAgent performanceAgent;

    public RouteService(
            WeightedRouteOptimizer optimizer,
            OrderService orderService,
            LaneService laneService,
            ShipmentHistoryService shipmentHistoryService,
            PerformanceAnalysisAgent performanceAgent
    ) {
        this.optimizer = optimizer;
        this.orderService = orderService;
        this.laneService = laneService;
        this.shipmentHistoryService = shipmentHistoryService;
        this.performanceAgent = performanceAgent;
    }

    public RoutePlanResult planRoutes(RoutePlanRequest request) {

        // 1️⃣ Fetch orders
        List<Order> orders = (request.getOrders() == null || request.getOrders().isEmpty())
                ? orderService.getAllOrders()
                : orderService.getOrders(request.getOrders());

        if (orders.isEmpty()) {
            throw new RuntimeException("No orders to plan");
        }

        // 2️⃣ Group orders
        Map<String, List<Order>> grouped = orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getSource() + "|" + o.getDestination()
                ));

        // 3️⃣ Run EXISTING optimizer (DO NOT TOUCH)
        RoutePlanResult result = optimizer.planShipments(grouped, laneService);

        // 🛑 SAFETY CHECK
        if (result.getShipments() == null || result.getShipments().isEmpty()) {
            return result;
        }

        // 4️⃣ SAVE shipments (THIS WAS MISSING / BROKEN)
        shipmentHistoryService.addAll(result.getShipments());

        // 5️⃣ Analyze performance (READ ONLY)
        PerformanceReport report =
                performanceAgent.analyze(result.getShipments());

        result.setPerformanceReport(report);

        return result;
    }
}
