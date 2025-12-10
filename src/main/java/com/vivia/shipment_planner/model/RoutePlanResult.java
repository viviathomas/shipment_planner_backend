package com.vivia.shipment_planner.model;

import java.util.List;

public class RoutePlanResult {

    private List<Shipment> shipments;
    private PerformanceReport performanceReport;
    private List<OrphanOrder> orphanOrders; // required by optimizer

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public PerformanceReport getPerformanceReport() {
        return performanceReport;
    }

    public void setPerformanceReport(PerformanceReport performanceReport) {
        this.performanceReport = performanceReport;
    }

    public List<OrphanOrder> getOrphanOrders() {
        return orphanOrders;
    }

    public void setOrphanOrders(List<OrphanOrder> orphanOrders) {
        this.orphanOrders = orphanOrders;
    }
}
