package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.model.Order;
import com.vivia.shipment_planner.model.OrphanOrder;
import com.vivia.shipment_planner.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadOrders(@RequestParam("file") MultipartFile file) {
        try {
            List<Order> orders = orderService.uploadOrdersFromCSV(
                    new InputStreamReader(file.getInputStream())
            );
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @GetMapping("/orphan")
    public ResponseEntity<List<OrphanOrder>> getOrphanOrders() {
        try {
            List<OrphanOrder> orphans = orderService.getOrphanOrders();
            return ResponseEntity.ok(orphans);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ⭐ NEW: Assign lane to order
    @PostMapping("/{orderId}/assign-lane")
    public ResponseEntity<?> assignLaneToOrder(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request) {
        try {
            String laneId = request.get("laneId");
            if (laneId == null || laneId.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "laneId is required"));
            }

            Order updatedOrder = orderService.assignLaneToOrder(orderId, laneId);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to assign lane"));
        }
    }
}
