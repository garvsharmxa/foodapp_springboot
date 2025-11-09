package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.Entity.Health;
import com.garv.foodApp.foodApp.Service.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping()
public class HealthController {

    @Autowired
    private HealthService healthService;

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @GetMapping("/health")
    public Health getHealth() {
        logger.info("Health API called");
        return healthService.getHealthStatus();
    }
}
