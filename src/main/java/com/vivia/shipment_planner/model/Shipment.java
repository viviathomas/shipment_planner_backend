package com.vivia.shipment_planner.model;

import java.util.List;

public class Shipment {
    private String shipmentId;
    private String pickup;
    private String delivery;
    private List<Order> assignedOrders;
    private double distance;
    private double cost;
    private double eta;
    private String laneId;
    private double laneCost;
    private double laneEmission;
    private List<Stop> stops;

    // getters / setters for all fields...
    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public String getPickup() { return pickup; }
    public void setPickup(String pickup) { this.pickup = pickup; }
    public String getDelivery() { return delivery; }
    public void setDelivery(String delivery) { this.delivery = delivery; }
    public List<Order> getAssignedOrders() { return assignedOrders; }
    public void setAssignedOrders(List<Order> assignedOrders) { this.assignedOrders = assignedOrders; }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public double getEta() { return eta; }
    public void setEta(double eta) { this.eta = eta; }
    public String getLaneId() { return laneId; }
    public void setLaneId(String laneId) { this.laneId = laneId; }
    public double getLaneCost() { return laneCost; }
    public void setLaneCost(double laneCost) { this.laneCost = laneCost; }
    public double getLaneEmission() { return laneEmission; }
    public void setLaneEmission(double laneEmission) { this.laneEmission = laneEmission; }
    public List<Stop> getStops() { return stops; }
    public void setStops(List<Stop> stops) { this.stops = stops; }

}
