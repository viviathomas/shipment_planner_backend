package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.Shipment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShipmentHistoryService {

    private final List<Shipment> history = new ArrayList<>();

    public synchronized void addAll(List<Shipment> shipments) {
        if (shipments == null || shipments.isEmpty()) return;
        history.addAll(shipments);
    }

    public synchronized List<Shipment> getAll() {
        return new ArrayList<>(history);
    }

    public synchronized void clear() {
        history.clear();
    }
}
