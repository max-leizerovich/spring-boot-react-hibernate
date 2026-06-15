package com.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Lightweight API health endpoint for frontend connectivity checks.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * Returns a simple OK payload when the API is reachable.
     *
     * @return status map
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
