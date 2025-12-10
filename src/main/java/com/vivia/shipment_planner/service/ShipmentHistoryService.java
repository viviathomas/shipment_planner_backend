package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.Shipment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShipmentHistoryService {

    private final List<Shipment> history = new ArrayList<>();

    public void addAll(List<Shipment> shipments) {
        history.addAll(shipments);
    }

    public List<Shipment> getAllForMoveStops() {
        return history;
    }

    public synchronized List<Shipment> getAll(){
        return new ArrayList<>(history);
    }

    public synchronized void clear() {
        history.clear();
    }
}
