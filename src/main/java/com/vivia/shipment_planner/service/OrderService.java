package com.vivia.shipment_planner.service;

import com.vivia.shipment_planner.model.Order;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final List<Order> orderStore = new ArrayList<>();

    public List<Order> uploadOrdersFromCSV(InputStreamReader reader) throws Exception {
        orderStore.clear();

        try (BufferedReader br = new BufferedReader(reader)) {
            String line = br.readLine(); // header

            if (line == null) {
                throw new RuntimeException("Empty CSV file");
            }

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 5) continue;

                Order order = new Order();
                order.setOrderId(data[0].trim());
                order.setSource(data[1].trim());
                order.setDestination(data[2].trim());
                order.setProductType(data[3].trim());
                order.setWeight(Double.parseDouble(data[4].trim()));

                orderStore.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace(); // VERY IMPORTANT
            throw new RuntimeException("CSV parsing failed: " + e.getMessage());
        }

        return orderStore;
    }


    public List<Order> getAllOrders() {
        return orderStore;
    }

    public List<Order> getOrders(List<String> orderIDs) {
        if (orderIDs == null || orderIDs.isEmpty()) {
            return new ArrayList<>(orderStore);
        }
        Set<String> ids = orderIDs.stream().map(String::trim).collect(Collectors.toSet());
        return orderStore.stream()
                .filter(o -> ids.contains(o.getOrderId()))
                .collect(Collectors.toList());
    }
}
