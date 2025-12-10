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
            loadFromClasspathCsv("lanes.csv");
            System.out.println("✅ Loaded lanes: " + lanes.size());
        } catch (Exception e) {
            System.out.println("⚠ Failed to load lanes.csv, using fallback");
            createFallbackLane();
        }
    }

    // ---------------- BASIC APIs ----------------

    public List<Lane> getAllLanes() {
        return Collections.unmodifiableList(lanes);
    }

    public List<Lane> getLanesForOD(String source, String destination) {
        return lanes.stream()
                .filter(l -> l.getSource().equalsIgnoreCase(source))
                .filter(l -> l.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }

    // ---------------- MOVE STOPS SUPPORT ----------------

    public Optional<Lane> findBestLaneForMoveStops(
            String source,
            String destination,
            int stopCount,
            double totalWeight,
            Set<String> productTypes
    ) {
        return lanes.stream()
                .filter(l -> l.getSource().equalsIgnoreCase(source))
                .filter(l -> l.getDestination().equalsIgnoreCase(destination))
                .filter(l -> l.getMaxStops() >= stopCount)
                .filter(l -> l.getCapacity() >= totalWeight)
                .filter(l ->
                        l.getAllowedProductTypes().contains("ALL") ||
                                productTypes.stream().anyMatch(p -> l.getAllowedProductTypes().contains(p))
                )
                .min(Comparator.comparingDouble(Lane::getBaseCost));
    }

    // ---------------- CSV LOADER ----------------

    private void loadFromClasspathCsv(String filename) throws Exception {

        ClassLoader cl = getClass().getClassLoader();
        if (cl.getResourceAsStream(filename) == null) {
            throw new RuntimeException("lanes.csv not found");
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(cl.getResourceAsStream(filename)))) {

            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                String[] c = line.split(",", -1);
                if (c.length < 10) continue;

                Lane l = new Lane();
                l.setLaneId(c[0].trim());
                l.setSource(c[1].trim());
                l.setDestination(c[2].trim());
                l.setBaseCost(Double.parseDouble(c[3]));
                l.setDistance(Double.parseDouble(c[4]));
                l.setEstimatedTime(Double.parseDouble(c[5]));
                l.setEmission(Double.parseDouble(c[6]));
                l.setCapacity(Double.parseDouble(c[7]));
                l.setMaxStops(Integer.parseInt(c[8]));

                Set<String> products = Arrays.stream(c[9].split(";"))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());

                if (products.isEmpty()) products.add("ALL");
                l.setAllowedProductTypes(products);

                lanes.add(l);
            }
        }
    }

    private void createFallbackLane() {
        Lane l = new Lane();
        l.setLaneId("LN-FALLBACK-01");
        l.setSource("Delhi");
        l.setDestination("Chennai");
        l.setBaseCost(7000);
        l.setDistance(950);
        l.setEstimatedTime(18);
        l.setEmission(550);
        l.setCapacity(1500);
        l.setMaxStops(5);
        l.setAllowedProductTypes(Set.of("ALL"));
        lanes.add(l);
    }
}
