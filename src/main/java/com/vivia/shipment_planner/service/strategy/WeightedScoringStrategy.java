package com.vivia.shipment_planner.service.strategy;

import com.vivia.shipment_planner.model.Lane;
import com.vivia.shipment_planner.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeightedScoringStrategy implements RouteAlgorithmStrategy {

    @Override
    public Lane chooseBestLane(
            List<Lane> lanes,
            List<Order> orders,
            double alpha,
            double beta,
            double gamma
    ) {

        Lane best = null;
        double bestScore = Double.POSITIVE_INFINITY;

        for (Lane lane : lanes) {
            double score =
                    alpha * lane.getDistance() +
                            beta  * lane.getBaseCost() +
                            gamma * lane.getEmission();

            if (score < bestScore) {
                best = lane;
                bestScore = score;
            }
        }

        return best;
    }
}
