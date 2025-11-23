package com.vivia.shipment_planner.model;

public class OrphanOrder {
    private String orderId;
    private String source;
    private String destination;
    private String reason;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
