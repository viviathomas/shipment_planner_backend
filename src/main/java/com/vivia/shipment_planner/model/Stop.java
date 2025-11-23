package com.vivia.shipment_planner.model;

public class Stop {
    private String location;
    private String arrivalTime;
    private String departureTime;

    public Stop() {}

    public Stop(String location, String arrivalTime, String departureTime) {
        this.location = location;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
}
