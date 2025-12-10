package com.vivia.shipment_planner.model;

import java.util.List;

public class MoveStopsRequest {

    private String shipmentId;
    private List<String> stopOrder;
    private boolean reoptimize;     // 🔥 key flag
    private boolean allowLaneChange;

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public List<String> getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(List<String> stopOrder) {
        this.stopOrder = stopOrder;
    }

    public boolean isReoptimize() {
        return reoptimize;
    }

    public void setReoptimize(boolean reoptimize) {
        this.reoptimize = reoptimize;
    }

    public boolean isAllowLaneChange() {
        return allowLaneChange;
    }

    public void setAllowLaneChange(boolean allowLaneChange) {
        this.allowLaneChange = allowLaneChange;
    }
}
