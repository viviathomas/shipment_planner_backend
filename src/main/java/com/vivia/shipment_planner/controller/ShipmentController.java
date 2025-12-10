package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.model.ManualShipmentRequest;
import com.vivia.shipment_planner.model.MoveStopsRequest;
import com.vivia.shipment_planner.model.PerformanceReport;
import com.vivia.shipment_planner.model.Shipment;
import com.vivia.shipment_planner.service.ShipmentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    // --------------------------------------------------
    // MOVE STOPS + OPTIONAL RE-OPTIMIZATION
    // --------------------------------------------------
    @PostMapping("/shipments/move-stops")
    public Shipment moveStops(@RequestBody MoveStopsRequest request) {
        return shipmentService.moveStops(request);
    }
    @GetMapping("/shipments/{shipmentId}/performance")
    public PerformanceReport analyzePerformance(
            @PathVariable String shipmentId
    ) {
        return shipmentService.analyzePerformance(shipmentId);
    }
    @PostMapping("/shipments/manual")
    public Shipment createManualShipment(
            @RequestBody ManualShipmentRequest request
    ) {
        return shipmentService.createManualShipment(request);
    }


}
