package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeightedRouteOptimizer {

    // score function: lower is better
    private double computeScore(Lane lane, double alpha, double beta, double gamma) {
        return alpha * lane.getDistance() + beta * lane.getBaseCost() + gamma * lane.getEmission();
    }

    // check lane feasibility: capacity + product type compatibility
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

    // main entry: groups are OD -> list of orders
    public RoutePlanResult planShipments(Map<String, List<Order>> groups,
                                         LaneService laneService,
                                         double alpha, double beta, double gamma) {

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
                // no lanes: mark all orders orphaned for this OD
                for (Order o : orders) {
                    OrphanOrder or = new OrphanOrder();
                    or.setOrderId(o.getOrderId());
                    or.setSource(o.getSource());
                    or.setDestination(o.getDestination());
                    or.setReason("No lane configured for " + source + " -> " + destination);
                    orphans.add(or);
                }
                continue;
            }

            List<String> reasons = new ArrayList<>();
            Lane chosen = chooseBestLane(candidates, orders, alpha, beta, gamma, reasons);

            if (chosen == null) {
                // try to split: attempt to plan each order individually (fallback)
                for (Order o : orders) {
                    List<Order> single = Collections.singletonList(o);
                    List<String> r2 = new ArrayList<>();
                    Lane chosenSingle = chooseBestLane(candidates, single, alpha, beta, gamma, r2);
                    if (chosenSingle != null) {
                        shipments.add(buildShipment(chosenSingle, source, destination, single));
                    } else {
                        OrphanOrder or = new OrphanOrder();
                        or.setOrderId(o.getOrderId());
                        or.setSource(o.getSource());
                        or.setDestination(o.getDestination());
                        or.setReason(String.join("; ", r2.isEmpty() ? reasons : r2));
                        orphans.add(or);
                    }
                }
            } else {
                // chosen lane can handle all orders in group
                shipments.add(buildShipment(chosen, source, destination, orders));
            }
        }

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
