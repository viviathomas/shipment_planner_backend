package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.Lane;
import com.vivia.shipment_planner.model.Order;
import com.vivia.shipment_planner.model.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ShipmentPlannerService {

    @Autowired
    private LaneService laneService;

    private final AtomicInteger seq = new AtomicInteger(1);

    /**
     * Plans shipments from a list of orders.
     * Groups orders by OD, matches lanes, and packs orders respecting lane capacity.
     */
    public List<Shipment> planShipments(List<Order> orders) {
        List<Shipment> finalShipments = new ArrayList<>();

        // Group orders by OD (source + destination)
        Map<String, List<Order>> groupedOD = orders.stream()
                .collect(Collectors.groupingBy(o ->
                        o.getSource().trim().toUpperCase() + "||" +
                                o.getDestination().trim().toUpperCase()
                ));

        for (Map.Entry<String, List<Order>> entry : groupedOD.entrySet()) {

            List<Order> odOrders = new ArrayList<>(entry.getValue());
            String[] parts = entry.getKey().split("\\|\\|");
            String source = parts[0];
            String destination = parts[1];

            // Find all lanes matching this OD
            List<Lane> lanes = laneService.getLanesForOD(source, destination);

            if (lanes.isEmpty()) {
                // No matching lane → put everything in UNASSIGNED
                packOrders("UNASSIGNED-" + source + "-" + destination,
                        odOrders, null, finalShipments);
            } else {
                // Try each lane one by one
                List<Order> remaining = new ArrayList<>(odOrders);

                for (Lane lane : lanes) {
                    Set<String> allowed = lane.getAllowedProductTypes()
                            .stream().map(String::toUpperCase)
                            .collect(Collectors.toSet());

                    List<Order> compatible = remaining.stream()
                            .filter(o -> allowed.contains("ALL") ||
                                    allowed.contains(o.getProductType().toUpperCase()))
                            .collect(Collectors.toList());

                    if (compatible.isEmpty()) continue;

                    remaining.removeAll(compatible);

                    // Pack by lane capacity
                    packOrders(lane.getLaneId(), compatible, lane, finalShipments);
                }

                // Any order left → UNASSIGNED
                if (!remaining.isEmpty()) {
                    packOrders("UNASSIGNED-" + source + "-" + destination,
                            remaining, null, finalShipments);
                }
            }
        }

        return finalShipments;
    }

    /**
     * Packs orders into shipments respecting weight capacity using First-Fit Decreasing
     */
    private void packOrders(String laneId, List<Order> orders, Lane lane, List<Shipment> output) {
        if (orders == null || orders.isEmpty()) return;

        orders.sort(Comparator.comparingDouble(Order::getWeight).reversed());

        double maxCap = (lane == null) ? Double.MAX_VALUE : lane.getCapacity();
        List<Shipment> active = new ArrayList<>();

        for (Order o : orders) {
            boolean placed = false;

            for (Shipment s : active) {
                double currentWeight = s.getAssignedOrders().stream()
                        .mapToDouble(Order::getWeight).sum();

                if (currentWeight + o.getWeight() <= maxCap) {
                    s.getAssignedOrders().add(o);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                Shipment shipment = new Shipment();
                shipment.setShipmentId("SHP-" + seq.getAndIncrement());
                shipment.setLaneId(laneId);
                shipment.setPickup(o.getSource());
                shipment.setDelivery(o.getDestination());
                shipment.setAssignedOrders(new ArrayList<>(List.of(o)));

                if (lane != null) {
                    shipment.setDistance(lane.getDistance());
                    shipment.setEta(lane.getEstimatedTime());
                    shipment.setLaneCost(lane.getBaseCost());
                    shipment.setLaneEmission(lane.getEmission());
                } else {
                    shipment.setDistance(0);
                    shipment.setEta(0);
                    shipment.setLaneCost(0);
                    shipment.setLaneEmission(0);
                }

                active.add(shipment);
            }
        }

        output.addAll(active);
    }
}
