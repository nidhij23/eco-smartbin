package com.nearby_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> checkHealth() {
        return Map.of(
                "status", "UP",
                "service", "nearby-service",
                "timestamp", LocalDateTime.now().toString()
        );
    }
}