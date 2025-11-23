package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.Lane;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LaneService {

    private final List<Lane> lanes = new ArrayList<>();

    public LaneService() {
        try {
            // Load lanes.csv from src/main/resources
            loadFromClasspathCsv("lanes.csv");
            System.out.println("✅ Loaded lanes from resources: " + lanes.size());
        } catch (Exception e) {
            System.out.println("⚠ Warning: Could not load lanes.csv from resources. Using fallback.");
        }

        // Fallback if nothing loaded
        if (lanes.isEmpty()) {
            Lane l1 = new Lane();
            l1.setLaneId("LN-PUNE-HYD-01");
            l1.setSource("Pune");
            l1.setDestination("Hyderabad");
            l1.setBaseCost(550.0);
            l1.setDistance(140.0);
            l1.setEstimatedTime(3.5);
            l1.setEmission(45.0);
            l1.setCapacity(500.0);
            l1.setAllowedProductTypes(new HashSet<>(Arrays.asList("ECOMMERCE", "GENERAL", "ELECTRONICS")));

            lanes.add(l1);
            System.out.println("⚠ Using fallback lane: LN-PUNE-HYD-01");
        }
    }

    // -----------------------------
    //      GETTERS
    // -----------------------------

    public List<Lane> getAllLanes() {
        return Collections.unmodifiableList(lanes);
    }

    public List<Lane> getLanesForOD(String source, String destination) {
        return lanes.stream()
                .filter(l -> l.getSource().equalsIgnoreCase(source)
                        && l.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }

    // -----------------------------
    //      CSV LOADER
    // -----------------------------

    private void loadFromClasspathCsv(String filename) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        if (classLoader.getResourceAsStream(filename) == null) {
            throw new RuntimeException("lanes.csv not found in resources");
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(classLoader.getResourceAsStream(filename)))) {

            String header = br.readLine(); // skip header
            if (header == null) return;

            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",", -1);
                if (cols.length < 9) continue;

                Lane lane = new Lane();
                lane.setLaneId(cols[0].trim());
                lane.setSource(cols[1].trim());
                lane.setDestination(cols[2].trim());
                lane.setBaseCost(Double.parseDouble(cols[3].trim()));
                lane.setDistance(Double.parseDouble(cols[4].trim()));
                lane.setEstimatedTime(Double.parseDouble(cols[5].trim()));
                lane.setEmission(Double.parseDouble(cols[6].trim()));
                lane.setCapacity(Double.parseDouble(cols[7].trim()));

                // Allowed products
                Set<String> allowed = Arrays.stream(cols[8].split(";"))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());

                if (allowed.isEmpty()) allowed.add("ALL");

                lane.setAllowedProductTypes(allowed);

                lanes.add(lane);
            }
        }
    }
}
