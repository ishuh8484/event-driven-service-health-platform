package com.microservices.failure.failure_analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthScheduler {

    private final HealthEvaluationService healthEvaluationService;

    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void checkHealth() {
        System.out.println("Running health evaluation...");
        healthEvaluationService.evaluateHealth();
    }
}
