package com.vivia.shipment_planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
public class ShipmentPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShipmentPlannerApplication.class, args);
    }
}
