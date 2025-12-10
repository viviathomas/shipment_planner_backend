package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ShipmentPlannerService {

    @Autowired
    private LaneService laneService;

    private final AtomicInteger seq = new AtomicInteger(1);

    /**
     * Builds MULTI-STOP shipments by chaining orders:
     * Delhi → Hyderabad + Hyderabad → Chennai
     * becomes ONE shipment with 3 stops
     */
    public List<Shipment> planShipments(List<Order> orders) {

        List<Shipment> shipments = new ArrayList<>();
        Set<String> used = new HashSet<>();

        // sort to make chaining deterministic
        orders.sort(Comparator.comparing(Order::getSource));

        for (Order start : orders) {

            if (used.contains(start.getOrderId())) continue;

            List<Order> chain = new ArrayList<>();
            chain.add(start);
            used.add(start.getOrderId());

            String currentCity = start.getDestination();
            double totalWeight = start.getWeight();

            boolean extended = true;

            while (extended) {
                extended = false;

                for (Order next : orders) {
                    if (used.contains(next.getOrderId())) continue;

                    if (next.getSource().equalsIgnoreCase(currentCity)
                            && totalWeight + next.getWeight() <= 3000) {

                        chain.add(next);
                        used.add(next.getOrderId());
                        totalWeight += next.getWeight();
                        currentCity = next.getDestination();
                        extended = true;
                        break;
                    }
                }
            }

            // 🔴 ONLY build shipment if we have real chaining
            if (chain.size() >= 2) {
                shipments.add(buildMultiStopShipment(chain));
            }
        }

        return shipments;
    }

    /**
     * Converts chained orders into a SINGLE multi-stop shipment
     */
    private Shipment buildMultiStopShipment(List<Order> chain) {

        Shipment shipment = new Shipment();
        shipment.setShipmentId("SHP-" + System.currentTimeMillis() + "-" + seq.getAndIncrement());
        shipment.setAssignedOrders(chain);

        List<Stop> stops = new ArrayList<>();

        // Pickup
        stops.add(new Stop(chain.get(0).getSource(), null, null));

        // Intermediate stops (destinations except final)
        for (int i = 0; i < chain.size() - 1; i++) {
            stops.add(new Stop(chain.get(i).getDestination(), null, null));
        }

        // Final delivery
        stops.add(new Stop(chain.get(chain.size() - 1).getDestination(), null, null));

        shipment.setStops(stops);
        shipment.setPickup(stops.get(0).getLocation());
        shipment.setDelivery(stops.get(stops.size() - 1).getLocation());

        double totalWeight = chain.stream()
                .mapToDouble(Order::getWeight)
                .sum();

        Set<String> products = new HashSet<>();
        chain.forEach(o -> products.add(o.getProductType().toUpperCase()));

        laneService.findBestLaneForMoveStops(
                shipment.getPickup(),
                shipment.getDelivery(),
                stops.size(),
                totalWeight,
                products
        ).ifPresent(lane -> {
            shipment.setLaneId(lane.getLaneId());
            shipment.setDistance(lane.getDistance());
            shipment.setEta(lane.getEstimatedTime());
            shipment.setCost(lane.getBaseCost());
            shipment.setLaneCost(lane.getBaseCost());
            shipment.setLaneEmission(lane.getEmission());
        });

        return shipment;
    }
}
