package com.vivia.shipment_planner.model;

import java.util.List;

public class RoutePlanResult {
    private List<Shipment> shipments;
    private List<OrphanOrder> orphanOrders;

    public List<Shipment> getShipments() { return shipments; }
    public void setShipments(List<Shipment> shipments) { this.shipments = shipments; }

    public List<OrphanOrder> getOrphanOrders() { return orphanOrders; }
    public void setOrphanOrders(List<OrphanOrder> orphanOrders) { this.orphanOrders = orphanOrders; }
}
