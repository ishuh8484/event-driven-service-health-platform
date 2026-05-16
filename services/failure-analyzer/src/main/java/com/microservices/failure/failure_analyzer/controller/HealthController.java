package com.microservices.failure.failure_analyzer.controller;

import com.microservices.failure.failure_analyzer.dto.FailureHistoryResponse;
import com.microservices.failure.failure_analyzer.dto.HealthResponse;
import com.microservices.failure.failure_analyzer.service.HealthQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthQueryService healthQueryService;

    @GetMapping("/{serviceId}")
    public HealthResponse getHealth(@PathVariable String serviceId) {
        return healthQueryService.getHealth(serviceId);
    }

    @GetMapping("/{serviceId}/failures")
    public List<FailureHistoryResponse> getFailureHistory(
            @PathVariable String serviceId,
            @RequestParam(defaultValue = "20") int limit) {
        return healthQueryService.getFailureHistory(serviceId, limit);
    }
}
