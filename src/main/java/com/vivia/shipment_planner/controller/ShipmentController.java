package com.vivia.shipment_planner.web;

import com.vivia.shipment_planner.model.Order;
import com.vivia.shipment_planner.model.Shipment;
import com.vivia.shipment_planner.service.OrderService;
import com.vivia.shipment_planner.service.ShipmentHistoryService;
import com.vivia.shipment_planner.service.ShipmentPlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ShipmentController {

    @Autowired private OrderService orderService;
    @Autowired private ShipmentPlannerService planner;
    @Autowired private ShipmentHistoryService history;

    // returns history (past + any previously planned shipments)
    @GetMapping("/shipments")
    public List<Shipment> getShipments() {
        return history.getAll();
    }

    /**
     * Plan shipments for given orderIds (or all if omitted).
     * Stores planned shipments into history and returns them to caller.
     *
     * Request JSON:
     * { "orderIds": ["ORD-1","ORD-2"] }  // optional
     */
    @PostMapping("/plan")
    public List<Shipment> plan(@RequestBody(required = false) Map<String, Object> body) {
        List<String> orderIds = null;
        if (body != null && body.containsKey("orderIds")) {
            Object v = body.get("orderIds");
            if (v instanceof List<?>) {
                orderIds = new ArrayList<>();
                for (Object it : (List<?>) v) if (it != null) orderIds.add(it.toString());
            }
        }

        List<Order> orders = (orderIds == null || orderIds.isEmpty())
                ? orderService.getAllOrders()
                : orderService.getOrders(orderIds);

        List<Shipment> planned = planner.planShipments(orders);

        // save to history so /api/shipments shows them later
        history.addAll(planned);

        return planned; // return ONLY the current planned shipments to the caller (planning UI)
    }
}
