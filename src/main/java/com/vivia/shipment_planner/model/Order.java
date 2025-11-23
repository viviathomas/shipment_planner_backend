package com.vivia.shipment_planner.model;

import java.util.List;

public class Order {
    private String orderId;
    private String source;
    private String destination;
    private String productType;
    private double weight;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public static class Shipment {
        private String shipmentId;
        private String pickup;
        private String delivery;
        private List<Order> assignedOrders;
        private double distance;
        private double cost;
        private double eta;
        private List<Stop> stops;

        public String getShipmentId() {
            return shipmentId;
        }

        public void setShipmentId(String shipmentId) {
            this.shipmentId = shipmentId;
        }

        public String getPickup() {
            return pickup;
        }

        public void setPickup(String pickup) {
            this.pickup = pickup;
        }

        public String getDelivery() {
            return delivery;
        }

        public void setDelivery(String delivery) {
            this.delivery = delivery;
        }

        public List<Order> getAssignedOrders() {
            return assignedOrders;
        }

        public void setAssignedOrders(List<Order> assignedOrders) {
            this.assignedOrders = assignedOrders;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getEta() {
            return eta;
        }

        public void setEta(double eta) {
            this.eta = eta;
        }

        public List<Stop> getStops() {
            return stops;
        }

        public void setStops(List<Stop> stops) {
            this.stops = stops;
        }
    }
}

