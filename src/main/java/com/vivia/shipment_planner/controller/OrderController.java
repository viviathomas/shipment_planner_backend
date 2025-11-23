package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.model.Order;
import com.vivia.shipment_planner.service.OrderService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/upload")
    public List<Order> uploadOrders(@RequestParam("file") MultipartFile file) throws Exception {
        return orderService.uploadOrdersFromCSV(new InputStreamReader(file.getInputStream()));
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getAllOrders();
    }
}
