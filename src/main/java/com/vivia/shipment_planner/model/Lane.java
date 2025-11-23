package com.vivia.shipment_planner.model;

import java.util.Set;

public class Lane {
    private String laneId;
    private String source;
    private String destination;
    private double baseCost;
    private double distance;
    private double estimatedTime;
    private double emission;
    private double capacity; // total capacity (kg)
    private Set<String> allowedProductTypes; // e.g. {"Electronics","Perishables"} or {"ALL"}

    // getters / setters
    public String getLaneId() { return laneId; }
    public void setLaneId(String laneId) { this.laneId = laneId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public double getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(double estimatedTime) { this.estimatedTime = estimatedTime; }
    public double getEmission() { return emission; }
    public void setEmission(double emission) { this.emission = emission; }
    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = capacity; }
    public Set<String> getAllowedProductTypes() { return allowedProductTypes; }
    public void setAllowedProductTypes(Set<String> allowedProductTypes) { this.allowedProductTypes = allowedProductTypes; }
}
