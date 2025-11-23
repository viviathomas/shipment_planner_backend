package com.vivia.shipment_planner.controller;

import com.vivia.shipment_planner.model.Lane;
import com.vivia.shipment_planner.service.LaneService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lanes")
public class LaneController {

    private final LaneService laneService;

    public LaneController(LaneService laneService) {
        this.laneService = laneService;
    }

    @GetMapping
    public List<Lane> getAllLanes() {
        return laneService.getAllLanes();
    }
}
