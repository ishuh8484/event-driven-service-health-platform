package com.microservices.health.health_query_service.controller;

import com.microservices.health.health_query_service.dto.FailureHistoryResponse;
import com.microservices.health.health_query_service.dto.HealthStatusResponse;
import com.microservices.health.health_query_service.dto.SystemHealthSummary;
import com.microservices.health.health_query_service.service.HealthQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthQueryService healthQueryService;

    @GetMapping("/services")
    public List<HealthStatusResponse> getAllServices(
            @RequestParam(required = false) String status) {
        return healthQueryService.getAllServices(status);
    }

    @GetMapping("/summary")
    public SystemHealthSummary getSummary() {
        return healthQueryService.getSummary();
    }

    @GetMapping("/{serviceId}")
    public HealthStatusResponse getHealth(@PathVariable String serviceId) {
        return healthQueryService.getHealth(serviceId);
    }

    @GetMapping("/{serviceId}/failures")
    public List<FailureHistoryResponse> getFailureHistory(
            @PathVariable String serviceId,
            @RequestParam(defaultValue = "20") int limit) {
        return healthQueryService.getFailureHistory(serviceId, limit);
    }

    @GetMapping("/{serviceId}/timeline")
    public List<String> getStatusTimeline(
            @PathVariable String serviceId,
            @RequestParam(defaultValue = "20") int limit) {
        return healthQueryService.getStatusTimeline(serviceId, limit);
    }
}
