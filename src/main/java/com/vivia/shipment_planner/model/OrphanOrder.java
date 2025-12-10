package com.vivia.shipment_planner.model;

import java.util.List;

public class OrphanOrder {
    private Order order;
    private List<SuggestedLane> suggestedLanes;

    public OrphanOrder() {}

    public OrphanOrder(Order order, List<SuggestedLane> suggestedLanes) {
        this.order = order;
        this.suggestedLanes = suggestedLanes;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<SuggestedLane> getSuggestedLanes() {
        return suggestedLanes;
    }

    public void setSuggestedLanes(List<SuggestedLane> suggestedLanes) {
        this.suggestedLanes = suggestedLanes;
    }

    public static class SuggestedLane {
        private String laneId;
        private String source;
        private String destination;
        private String productType;
        private double baseCost;
        private double distance;
        private int matchScore;
        private String matchReason;

        public SuggestedLane() {}

        public SuggestedLane(Lane lane, int matchScore, String matchReason) {
            this.laneId = lane.getLaneId();
            this.source = lane.getSource();
            this.destination = lane.getDestination();
            this.baseCost = lane.getBaseCost();
            this.distance = lane.getDistance();
            this.matchScore = matchScore;
            this.matchReason = matchReason;


            if (lane.getAllowedProductTypes() != null && !lane.getAllowedProductTypes().isEmpty()) {
                this.productType = String.join(", ", lane.getAllowedProductTypes());
            }
        }


        public String getLaneId() { return laneId; }
        public void setLaneId(String laneId) { this.laneId = laneId; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }

        public String getProductType() { return productType; }
        public void setProductType(String productType) { this.productType = productType; }

        public double getBaseCost() { return baseCost; }
        public void setBaseCost(double baseCost) { this.baseCost = baseCost; }

        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }

        public int getMatchScore() { return matchScore; }
        public void setMatchScore(int matchScore) { this.matchScore = matchScore; }

        public String getMatchReason() { return matchReason; }
        public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
    }
}