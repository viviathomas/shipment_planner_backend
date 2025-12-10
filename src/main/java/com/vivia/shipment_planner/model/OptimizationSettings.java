package com.vivia.shipment_planner.model;

public class OptimizationSettings {
    private double distanceWeight;
    private double costWeight;
    private double emissionWeight;

    public OptimizationSettings() {}

    public OptimizationSettings(double distanceWeight, double costWeight, double emissionWeight) {
        this.distanceWeight = distanceWeight;
        this.costWeight = costWeight;
        this.emissionWeight = emissionWeight;
    }

    public double getDistanceWeight() { return distanceWeight; }
    public void setDistanceWeight(double distanceWeight) { this.distanceWeight = distanceWeight; }

    public double getCostWeight() { return costWeight; }
    public void setCostWeight(double costWeight) { this.costWeight = costWeight; }

    public double getEmissionWeight() { return emissionWeight; }
    public void setEmissionWeight(double emissionWeight) { this.emissionWeight = emissionWeight; }
}
