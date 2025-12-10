package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeightedRouteOptimizer {
    @Autowired
    private OptimizationSettingsService optimizationSettingsService;

    @Autowired
    OrderService orderService;

    private double computeScore(Lane lane, double alpha, double beta, double gamma) {
        return alpha * lane.getDistance() + beta * lane.getBaseCost() + gamma * lane.getEmission();
    }

    private boolean isFeasible(Lane lane, List<Order> orders, List<String> reasons) {
        double totalWeight = orders.stream().mapToDouble(Order::getWeight).sum();
        if (totalWeight > lane.getCapacity()) {
            reasons.add("Total weight " + totalWeight + " exceeds lane capacity " + lane.getCapacity());
            return false;
        }

        Set<String> laneProducts = lane.getAllowedProductTypes().stream().map(String::toUpperCase).collect(Collectors.toSet());
        if (!laneProducts.contains("ALL")) {
            for (Order o : orders) {
                if (!laneProducts.contains(o.getProductType().toUpperCase())) {
                    reasons.add("Product type " + o.getProductType() + " not allowed on lane " + lane.getLaneId());
                    return false;
                }
            }
        }
        return true;
    }

    private Lane chooseBestLane(List<Lane> lanes, List<Order> orders, double alpha, double beta, double gamma, List<String> outReasons) {
        Lane best = null;
        double bestScore = Double.POSITIVE_INFINITY;
        for (Lane lane : lanes) {
            List<String> reasons = new ArrayList<>();
            if (!isFeasible(lane, orders, reasons)) {
                outReasons.addAll(reasons);
                continue;
            }
            double score = computeScore(lane, alpha, beta, gamma);
            if (score < bestScore) {
                best = lane;
                bestScore = score;
            }
        }
        return best;
    }

    public RoutePlanResult planShipments(Map<String, List<Order>> groups,
                                         LaneService laneService) {

        // 🔥 Pull dynamic weights from backend
        OptimizationSettings w = optimizationSettingsService.getSettings();
        double alpha = w.getDistanceWeight();
        double beta  = w.getCostWeight();
        double gamma = w.getEmissionWeight();

        List<Shipment> shipments = new ArrayList<>();
        List<OrphanOrder> orphans = new ArrayList<>();

        for (Map.Entry<String, List<Order>> e : groups.entrySet()) {

            String odKey = e.getKey();
            String[] parts = odKey.split("\\|");
            String source = parts[0];
            String destination = parts[1];
            List<Order> orders = e.getValue();

            List<Lane> candidates = laneService.getLanesForOD(source, destination);

            if (candidates.isEmpty()) {
                for (Order o : orders) {
                    OrphanOrder orphan = new OrphanOrder();
                    orphan.setOrder(o);
                    orphan.setSuggestedLanes(new ArrayList<>());
                    orphans.add(orphan);
                }
                continue;
            }

            List<String> reasons = new ArrayList<>();
            Lane chosen = chooseBestLane(candidates, orders, alpha, beta, gamma, reasons);

            if (chosen == null) {
                for (Order o : orders) {
                    List<Order> singleOrderList = Collections.singletonList(o);
                    List<String> r2 = new ArrayList<>();

                    Lane chosenSingle = chooseBestLane(candidates, singleOrderList, alpha, beta, gamma, r2);

                    if (chosenSingle != null) {
                        shipments.add(buildShipment(chosenSingle, source, destination, singleOrderList));
                    } else {

                        OrphanOrder orphan = new OrphanOrder();
                        orphan.setOrder(o);

                        List<OrphanOrder.SuggestedLane> suggestions = new ArrayList<>();
                        for (Lane lane : candidates) {
                            OrphanOrder.SuggestedLane s = new OrphanOrder.SuggestedLane(
                                    lane,
                                    20,
                                    String.join("; ", r2.isEmpty() ? reasons : r2)
                            );
                            suggestions.add(s);
                        }

                        orphan.setSuggestedLanes(suggestions);
                        orphans.add(orphan);
                    }
                }

            } else {
                shipments.add(buildShipment(chosen, source, destination, orders));
            }
        }

        orderService.setOrphans(orphans);

        RoutePlanResult result = new RoutePlanResult();
        result.setShipments(shipments);
        result.setOrphanOrders(orphans);
        return result;
    }


    private Shipment buildShipment(Lane lane, String pickup, String delivery, List<Order> assigned) {
        Shipment s = new Shipment();
        s.setShipmentId("SHP-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000));
        s.setPickup(pickup);
        s.setDelivery(delivery);
        s.setAssignedOrders(new ArrayList<>(assigned));
        s.setDistance(lane.getDistance());
        s.setCost(lane.getBaseCost());
        s.setEta(lane.getEstimatedTime());
        s.setLaneId(lane.getLaneId());
        s.setLaneCost(lane.getBaseCost());
        s.setLaneEmission(lane.getEmission());
        s.setStops(Arrays.asList(createStop(pickup), createStop(delivery)));
        return s;
    }

    private Stop createStop(String loc) {
        Stop stop = new Stop();
        stop.setLocation(loc);
        stop.setArrivalTime(null);
        stop.setDepartureTime(null);
        return stop;
    }
}
