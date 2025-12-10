package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.*;
import com.vivia.shipment_planner.service.strategy.RouteAlgorithmStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeightedRouteOptimizer {

    private static final Logger log =
            LoggerFactory.getLogger(WeightedRouteOptimizer.class);

    private final OptimizationSettingsService optimizationSettingsService;
    private final OrderService orderService;
    private final RouteAlgorithmStrategy routeAlgorithm;

    @Autowired
    public WeightedRouteOptimizer(
            OptimizationSettingsService optimizationSettingsService,
            OrderService orderService,
            RouteAlgorithmStrategy routeAlgorithm
    ) {
        this.optimizationSettingsService = optimizationSettingsService;
        this.orderService = orderService;
        this.routeAlgorithm = routeAlgorithm;
    }

    // -------------------- FEASIBILITY CHECK --------------------
    private boolean isFeasible(
            Lane lane,
            List<Order> orders,
            List<String> reasons
    ) {
        double totalWeight =
                orders.stream().mapToDouble(Order::getWeight).sum();

        if (totalWeight > lane.getCapacity()) {
            reasons.add(
                    "Total weight " + totalWeight +
                            " exceeds lane capacity " + lane.getCapacity()
            );
            return false;
        }

        Set<String> allowedProducts =
                lane.getAllowedProductTypes()
                        .stream()
                        .map(String::toUpperCase)
                        .collect(Collectors.toSet());

        if (!allowedProducts.contains("ALL")) {
            for (Order o : orders) {
                String product = o.getProductType().toUpperCase();
                if (!allowedProducts.contains(product)) {
                    reasons.add(
                            "Product " + product +
                                    " not allowed on lane " + lane.getLaneId()
                    );
                    return false;
                }
            }
        }

        return true;
    }

    // -------------------- MAIN PLANNER --------------------
    public RoutePlanResult planShipments(
            Map<String, List<Order>> groups,
            LaneService laneService
    ) {

        // 1️⃣ LOAD OPTIMIZATION WEIGHTS
        OptimizationSettings w = optimizationSettingsService.getSettings();

        double alpha = w.getDistanceWeight();
        double beta  = w.getCostWeight();
        double gamma = w.getEmissionWeight();

        log.info("RAW WEIGHTS → α={}, β={}, γ={}", alpha, beta, gamma);

        // 2️⃣ NORMALIZE WEIGHTS
        double sum = alpha + beta + gamma;
        if (sum == 0) {
            alpha = beta = gamma = 1.0 / 3.0;
        } else {
            alpha /= sum;
            beta  /= sum;
            gamma /= sum;
        }

        log.info("NORMALIZED WEIGHTS → α={}, β={}, γ={}", alpha, beta, gamma);

        List<Shipment> shipments = new ArrayList<>();
        List<OrphanOrder> orphans = new ArrayList<>();

        // 3️⃣ PROCESS EACH SOURCE–DESTINATION GROUP
        for (Map.Entry<String, List<Order>> entry : groups.entrySet()) {

            String key = entry.getKey();
            List<Order> orders = entry.getValue();

            String[] parts = key.split("\\|");
            String source = parts[0];
            String destination = parts[1];

            List<Lane> candidates =
                    laneService.getLanesForOD(source, destination);

            if (candidates.isEmpty()) {
                orders.forEach(o -> {
                    OrphanOrder orphan = new OrphanOrder();
                    orphan.setOrder(o);
                    orphan.setSuggestedLanes(new ArrayList<>());
                    orphans.add(orphan);
                });
                continue;
            }

            log.info(
                    "CANDIDATE LANES for {} → {}",
                    key,
                    candidates.stream()
                            .map(Lane::getLaneId)
                            .collect(Collectors.toList())
            );

            // 4️⃣ FILTER FEASIBLE LANES
            List<Lane> feasible = new ArrayList<>();
            List<String> reasons = new ArrayList<>();

            for (Lane lane : candidates) {
                if (isFeasible(lane, orders, reasons)) {
                    feasible.add(lane);
                }
            }

            if (feasible.isEmpty()) {
                orders.forEach(o -> {
                    OrphanOrder orphan = new OrphanOrder();
                    orphan.setOrder(o);
                    orphan.setSuggestedLanes(new ArrayList<>());
                    orphans.add(orphan);
                });
                continue;
            }

            // 5️⃣ DELEGATE TO STRATEGY (CORE OPTIMIZATION)
            Lane chosen =
                    routeAlgorithm.chooseBestLane(
                            feasible,
                            orders,
                            alpha,
                            beta,
                            gamma
                    );

            if (chosen != null) {
                shipments.add(
                        buildShipment(chosen, source, destination, orders)
                );
            }
        }

        orderService.setOrphans(orphans);

        RoutePlanResult result = new RoutePlanResult();
        result.setShipments(shipments);
        result.setOrphanOrders(orphans);
        return result;
    }

    // -------------------- SHIPMENT BUILDER --------------------
    private Shipment buildShipment(
            Lane lane,
            String pickup,
            String delivery,
            List<Order> assigned
    ) {
        Shipment s = new Shipment();
        s.setShipmentId(
                "SHP-" + System.currentTimeMillis() +
                        "-" + (int)(Math.random() * 1000)
        );
        s.setPickup(pickup);
        s.setDelivery(delivery);
        s.setAssignedOrders(assigned);

        s.setDistance(lane.getDistance());
        s.setCost(lane.getBaseCost());
        s.setEta(lane.getEstimatedTime());
        s.setLaneId(lane.getLaneId());
        s.setLaneCost(lane.getBaseCost());
        s.setLaneEmission(lane.getEmission());

        s.setStops(
                Arrays.asList(createStop(pickup), createStop(delivery))
        );

        return s;
    }

    private Stop createStop(String location) {
        Stop stop = new Stop();
        stop.setLocation(location);
        return stop;
    }
}
