package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.Lane;
import com.vivia.shipment_planner.model.Order;
import com.vivia.shipment_planner.model.OrphanOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final List<Order> orderStore = new ArrayList<>();
    private List<OrphanOrder> orphanStore = new ArrayList<>();

    @Autowired
    private LaneService laneService;

    public List<Order> uploadOrdersFromCSV(InputStreamReader reader) throws Exception {
        orderStore.clear();

        try (BufferedReader br = new BufferedReader(reader)) {
            String line = br.readLine(); // header

            if (line == null) {
                throw new RuntimeException("Empty CSV file");
            }

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 5) continue;

                Order order = new Order();
                order.setOrderId(data[0].trim());
                order.setSource(data[1].trim());
                order.setDestination(data[2].trim());
                order.setProductType(data[3].trim());
                order.setWeight(Double.parseDouble(data[4].trim()));
                // assignedLaneId will be null initially

                orderStore.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("CSV parsing failed: " + e.getMessage());
        }

        return orderStore;
    }

    public List<Order> getAllOrders() {
        return orderStore;
    }
    public void setOrphans(List<OrphanOrder> orphans) {
        this.orphanStore = orphans;
    }

    public List<OrphanOrder> getOrphans() {
        return orphanStore;
    }

    public List<Order> getOrders(List<String> orderIDs) {
        if (orderIDs == null || orderIDs.isEmpty()) {
            return new ArrayList<>(orderStore);
        }
        Set<String> ids = orderIDs.stream().map(String::trim).collect(Collectors.toSet());
        return orderStore.stream()
                .filter(o -> ids.contains(o.getOrderId()))
                .collect(Collectors.toList());
    }

    // ⭐ NEW: Get orphan orders with suggested lanes
    public List<OrphanOrder> getOrphanOrders() {
        List<OrphanOrder> orphans = orphanStore.stream()
                .collect(Collectors.toList());

        List<Lane> allLanes = laneService.getAllLanes();

        return orphans;
    }

    private List<OrphanOrder.SuggestedLane> findSuggestedLanes(Order order, List<Lane> allLanes) {
        List<OrphanOrder.SuggestedLane> suggestions = new ArrayList<>();

        for (Lane lane : allLanes) {
            int score = 0;
            List<String> reasons = new ArrayList<>();

            // Exact origin match
            if (normalizeLocation(lane.getSource()).equals(normalizeLocation(order.getSource()))) {
                score += 40;
                reasons.add("Exact origin match");
            }

            // Exact destination match
            if (normalizeLocation(lane.getDestination()).equals(normalizeLocation(order.getDestination()))) {
                score += 40;
                reasons.add("Exact destination match");
            }

            // Product type match
            if (lane.getAllowedProductTypes() != null && order.getProductType() != null) {
                String normalizedOrderType = order.getProductType().trim().toUpperCase();
                if (lane.getAllowedProductTypes().contains("ALL") ||
                        lane.getAllowedProductTypes().contains(normalizedOrderType)) {
                    score += 20;
                    reasons.add("Product type compatible");
                }
            }

            // Only include lanes with at least origin OR destination match
            if (score >= 40) {
                suggestions.add(new OrphanOrder.SuggestedLane(
                        lane,
                        score,
                        String.join(", ", reasons)
                ));
            }
        }

        // Sort by match score (highest first)
        suggestions.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));

        return suggestions;
    }

    // ⭐ NEW: Assign a lane to an order
    public Order assignLaneToOrder(String orderId, String laneId) {
        Order order = orderStore.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Verify lane exists
        List<Lane> allLanes = laneService.getAllLanes();
        boolean laneExists = allLanes.stream()
                .anyMatch(l -> l.getLaneId().equals(laneId));

        if (!laneExists) {
            throw new RuntimeException("Lane not found: " + laneId);
        }

        order.setAssignedLaneId(laneId);
        return order;
    }

    // ⭐ NEW: Get order by ID
    public Order getOrderById(String orderId) {
        return orderStore.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    private String normalizeLocation(String location) {
        return location == null ? "" : location.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}