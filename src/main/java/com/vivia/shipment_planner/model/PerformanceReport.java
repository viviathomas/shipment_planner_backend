package com.vivia.shipment_planner.model;

import java.util.List;

public class PerformanceReport {

    private int shipmentCount;
    private double totalDistance;
    private double totalFuel;
    private double averageEta;
    private double totalEmission;
    private double totalCost;

    private String strategyApplied;
    private String strategyReason;

    private Double beforeEta;
    private Double afterEta;
    private Double beforeEmission;
    private Double afterEmission;

    private List<String> suggestions;

    // getters & setters

    public int getShipmentCount() { return shipmentCount; }
    public void setShipmentCount(int shipmentCount) { this.shipmentCount = shipmentCount; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public double getTotalFuel() { return totalFuel; }
    public void setTotalFuel(double totalFuel) { this.totalFuel = totalFuel; }

    public double getAverageEta() { return averageEta; }
    public void setAverageEta(double averageEta) { this.averageEta = averageEta; }

    public double getTotalEmission() { return totalEmission; }
    public void setTotalEmission(double totalEmission) { this.totalEmission = totalEmission; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getStrategyApplied() { return strategyApplied; }
    public void setStrategyApplied(String strategyApplied) { this.strategyApplied = strategyApplied; }

    public String getStrategyReason() { return strategyReason; }
    public void setStrategyReason(String strategyReason) { this.strategyReason = strategyReason; }

    public Double getBeforeEta() { return beforeEta; }
    public void setBeforeEta(Double beforeEta) { this.beforeEta = beforeEta; }

    public Double getAfterEta() { return afterEta; }
    public void setAfterEta(Double afterEta) { this.afterEta = afterEta; }

    public Double getBeforeEmission() { return beforeEmission; }
    public void setBeforeEmission(Double beforeEmission) { this.beforeEmission = beforeEmission; }

    public Double getAfterEmission() { return afterEmission; }
    public void setAfterEmission(Double afterEmission) { this.afterEmission = afterEmission; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
