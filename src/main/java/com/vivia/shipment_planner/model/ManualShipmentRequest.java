package com.vivia.shipment_planner.model;

import java.util.List;

public class ManualShipmentRequest {

    private List<String> sourceShipmentIds;
    private List<String> stops;

    public List<String> getSourceShipmentIds() {
        return sourceShipmentIds;
    }

    public void setSourceShipmentIds(List<String> sourceShipmentIds) {
        this.sourceShipmentIds = sourceShipmentIds;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }
}
