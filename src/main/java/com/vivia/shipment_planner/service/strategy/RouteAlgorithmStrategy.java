package com.vivia.shipment_planner.service.strategy;

import com.vivia.shipment_planner.model.Lane;
import com.vivia.shipment_planner.model.Order;

import java.util.List;

public interface RouteAlgorithmStrategy {

    Lane chooseBestLane(
            List<Lane> lanes,
            List<Order> orders,
            double alpha,
            double beta,
            double gamma
    );
}
