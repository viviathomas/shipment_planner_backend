package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.*;
import com.vivia.shipment_planner.service.strategy.RouteAlgorithmStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    private final ShipmentHistoryService history;
    private final LaneService laneService;
    private final OptimizationSettingsService settingsService;
    private final RouteAlgorithmStrategy algorithm;

    public ShipmentService(
            ShipmentHistoryService history,
            LaneService laneService,
            OptimizationSettingsService settingsService,
            RouteAlgorithmStrategy algorithm
    ) {
        this.history = history;
        this.laneService = laneService;
        this.settingsService = settingsService;
        this.algorithm = algorithm;
    }

    // --------------------------------------------------
    // MOVE STOPS (SAFE, VALIDATED)
    // --------------------------------------------------
    public Shipment moveStops(MoveStopsRequest req) {

        Shipment shipment = history.getAllForMoveStops().stream()
                .filter(s -> s.getShipmentId().equals(req.getShipmentId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shipment not found"
                ));

        List<String> newOrder = req.getStopOrder();

        if (newOrder == null || newOrder.size() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid stop order"
            );
        }

        if (!newOrder.get(0).equalsIgnoreCase(shipment.getPickup()) ||
                !newOrder.get(newOrder.size() - 1).equalsIgnoreCase(shipment.getDelivery())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pickup must be first and delivery must be last"
            );
        }

        List<Stop> updatedStops = new ArrayList<>();
        for (String location : newOrder) {
            Stop stop = new Stop();
            stop.setLocation(location);
            updatedStops.add(stop);
        }

        shipment.setStops(updatedStops);

        if (req.isReoptimize()) {

            double totalWeight = shipment.getAssignedOrders()
                    .stream()
                    .mapToDouble(Order::getWeight)
                    .sum();

            Set<String> products = shipment.getAssignedOrders()
                    .stream()
                    .map(o -> o.getProductType().toUpperCase())
                    .collect(Collectors.toSet());

            Optional<Lane> laneOpt = laneService.findBestLaneForMoveStops(
                    shipment.getPickup(),
                    shipment.getDelivery(),
                    updatedStops.size(),
                    totalWeight,
                    products
            );

            if (laneOpt.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No suitable lane found after stop reordering"
                );
            }

            Lane bestLane = laneOpt.get();

            if (!req.isAllowLaneChange()
                    && !bestLane.getLaneId().equals(shipment.getLaneId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Lane change is not allowed"
                );
            }

            shipment.setLaneId(bestLane.getLaneId());
            shipment.setDistance(bestLane.getDistance());
            shipment.setEta(bestLane.getEstimatedTime());
            shipment.setLaneCost(bestLane.getBaseCost());
            shipment.setLaneEmission(bestLane.getEmission());
        }

        return shipment;
    }

    // --------------------------------------------------
    // PERFORMANCE ANALYSIS (AGENT LOGIC)
    // --------------------------------------------------
    public PerformanceReport analyzePerformance(String shipmentId) {

        Shipment shipment = history.getAll().stream()
                .filter(s -> s.getShipmentId().equals(shipmentId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shipment not found"
                ));

        double distance = shipment.getDistance();
        double eta = shipment.getEta();

        double fuel = distance * 0.25;
        double emissions = distance * 0.4;
        double cost = distance * 6.5;

        List<String> suggestions = new ArrayList<>();

        if (emissions > 400)
            suggestions.add("High emissions detected. Consider greener lane.");

        if (eta > 8)
            suggestions.add("ETA is high. Reduce intermediate stops.");

        if (cost > 10000)
            suggestions.add("Cost optimization possible by consolidating shipments.");

        PerformanceReport report = new PerformanceReport();
        report.setShipmentCount(1);
        report.setTotalDistance(distance);
        report.setTotalFuel(fuel);
        report.setAverageEta(eta);
        report.setTotalEmission(emissions);
        report.setTotalCost(cost);
        report.setSuggestions(suggestions);

        report.setStrategyApplied("RULE_BASED_ANALYSIS");
        report.setStrategyReason("Evaluated ETA, emissions, and cost thresholds");

        return report;
    }
    // --------------------------------------------------
// MANUAL SHIPMENT CREATION (NO OPTIMIZATION)
// --------------------------------------------------
    public Shipment createManualShipment(ManualShipmentRequest req) {

        if (req.getStops() == null || req.getStops().size() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least two stops required"
            );
        }

        Shipment shipment = new Shipment();
        shipment.setShipmentId("SHP-" + System.currentTimeMillis());
        shipment.setPickup(req.getStops().get(0));
        shipment.setDelivery(req.getStops().get(req.getStops().size() - 1));

        List<Stop> stops = req.getStops().stream().map(loc -> {
            Stop s = new Stop();
            s.setLocation(loc);
            return s;
        }).toList();

        shipment.setStops(stops);

        // ❌ NO distance / ETA / lane / emissions
        shipment.setDistance(0);
        shipment.setEta(0);
        shipment.setLaneId(null);
        shipment.setLaneCost(0);
        shipment.setLaneEmission(0);

        history.addAll(List.of(shipment));

        return shipment;
    }


}
