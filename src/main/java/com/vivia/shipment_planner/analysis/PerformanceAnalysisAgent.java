package com.vivia.shipment_planner.analysis;

import com.vivia.shipment_planner.model.PerformanceReport;
import com.vivia.shipment_planner.model.Shipment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PerformanceAnalysisAgent {
    public PerformanceReport analyze(List<Shipment> shipments) {
        return analyze(shipments, "COST_OPTIMIZED");
    }

    public PerformanceReport analyze(List<Shipment> shipments, String strategy) {

        PerformanceReport report = new PerformanceReport();

        double totalDistance = 0;
        double totalEta = 0;
        double totalCost = 0;
        double totalEmission = 0;

        for (Shipment s : shipments) {
            totalDistance += s.getDistance();
            totalEta += s.getEta();
            totalCost += s.getCost();

            // simple emission formula (demo-friendly)
            totalEmission += s.getDistance() * 0.45;
        }

        report.setShipmentCount(shipments.size());
        report.setTotalDistance(totalDistance);
        report.setTotalFuel(totalDistance * 0.13); // demo conversion
        report.setAverageEta(shipments.isEmpty() ? 0 : totalEta / shipments.size());
        report.setTotalEmission(totalEmission);
        report.setTotalCost(totalCost);

        // -------------------------
        // STRATEGY MODE (Agentic AI)
        // -------------------------
        report.setStrategyApplied(strategy);

        switch (strategy) {
            case "ECO_OPTIMIZED":
                report.setStrategyReason("Lowest emissions selected among available lanes");
                break;
            case "TIME_OPTIMIZED":
                report.setStrategyReason("Fastest ETA prioritized");
                break;
            default:
                report.setStrategyReason("Cost efficiency prioritized");
        }

        // -------------------------
        // BEFORE vs AFTER (Mocked but valid)
        // -------------------------
        report.setBeforeEta(report.getAverageEta() * 1.15);
        report.setAfterEta(report.getAverageEta());

        report.setBeforeEmission(report.getTotalEmission() * 1.25);
        report.setAfterEmission(report.getTotalEmission());

        // -------------------------
        // SMART SUGGESTIONS (Agent Rules)
        // -------------------------
        List<String> suggestions = new ArrayList<>();

        if (report.getTotalEmission() > 400) {
            suggestions.add("High emissions detected. Consider greener lanes.");
        }

        if (report.getAverageEta() > 8) {
            suggestions.add("ETA is high. Reduce intermediate stops or use express lanes.");
        }

        if (report.getTotalCost() > 10000) {
            suggestions.add("High cost detected. Consolidating shipments may reduce cost.");
        }

        report.setSuggestions(suggestions);

        return report;
    }
}
