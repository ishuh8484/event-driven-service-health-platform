package com.microservices.health.health_query_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.health.health_query_service.dto.FailureHistoryResponse;
import com.microservices.health.health_query_service.dto.HealthStatusResponse;
import com.microservices.health.health_query_service.dto.SystemHealthSummary;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthQueryService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Circuit breaker lagaya hai — agar Redis down ho toh fallback response dega
    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getHealthFallback")
    public HealthStatusResponse getHealth(String serviceId) {

        String status = redisTemplate.opsForValue().get("service:" + serviceId + ":healthStatus");
        String failureStr = redisTemplate.opsForValue().get("service:" + serviceId + ":failureCount");
        String heartbeatStr = redisTemplate.opsForValue().get("service:" + serviceId + ":lastHeartbeat");
        String registeredAtStr = redisTemplate.opsForValue().get("service:" + serviceId + ":registeredAt");
        String heartbeatCountStr = redisTemplate.opsForValue().get("service:" + serviceId + ":heartbeatCount");

        Long failureCount = failureStr != null ? Long.parseLong(failureStr) : 0L;
        Long lastHeartbeat = heartbeatStr != null ? Long.parseLong(heartbeatStr) : null;
        Long registeredAt = registeredAtStr != null ? Long.parseLong(registeredAtStr) : null;
        Long heartbeatCount = heartbeatCountStr != null ? Long.parseLong(heartbeatCountStr) : 0L;

        Long uptimeMillis = registeredAt != null ? System.currentTimeMillis() - registeredAt : null;

        double failureRate = heartbeatCount > 0
                ? (failureCount * 100.0) / heartbeatCount
                : 0.0;

        return HealthStatusResponse.builder()
                .serviceId(serviceId)
                .status(status != null ? status : "UNKNOWN")
                .failureCount(failureCount)
                .lastHeartbeat(lastHeartbeat)
                .registeredAt(registeredAt)
                .uptimeMillis(uptimeMillis)
                .heartbeatCount(heartbeatCount)
                .failureRatePercent(Math.round(failureRate * 100.0) / 100.0)
                .build();
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getAllServicesFallback")
    public List<HealthStatusResponse> getAllServices(String statusFilter) {

        Set<String> registeredKeys = redisTemplate.keys("service:*:registered");

        if (registeredKeys == null || registeredKeys.isEmpty()) {
            return Collections.emptyList();
        }

        List<HealthStatusResponse> services = registeredKeys.stream()
                .map(key -> {
                    String serviceId = key.split(":")[1];
                    return getHealth(serviceId);
                })
                .collect(Collectors.toList());

        if (statusFilter != null && !statusFilter.isBlank()) {
            String normalizedFilter = statusFilter.toUpperCase().trim();
            services = services.stream()
                    .filter(s -> normalizedFilter.equals(s.getStatus()))
                    .collect(Collectors.toList());
        }

        return services;
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getSummaryFallback")
    public SystemHealthSummary getSummary() {

        List<HealthStatusResponse> allServices = getAllServices(null);

        int healthy = 0, degraded = 0, unhealthy = 0, unknown = 0;

        for (HealthStatusResponse service : allServices) {
            switch (service.getStatus()) {
                case "HEALTHY" -> healthy++;
                case "DEGRADED" -> degraded++;
                case "UNHEALTHY" -> unhealthy++;
                default -> unknown++;
            }
        }

        return SystemHealthSummary.builder()
                .totalServices(allServices.size())
                .healthyCount(healthy)
                .degradedCount(degraded)
                .unhealthyCount(unhealthy)
                .unknownCount(unknown)
                .build();
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getFailureHistoryFallback")
    public List<FailureHistoryResponse> getFailureHistory(String serviceId, int limit) {

        String historyKey = "service:" + serviceId + ":failureHistory";
        int safeLimit = Math.max(1, Math.min(limit, 50));

        List<String> jsonEntries = redisTemplate.opsForList()
                .range(historyKey, 0, safeLimit - 1);

        if (jsonEntries == null || jsonEntries.isEmpty()) {
            return Collections.emptyList();
        }

        return jsonEntries.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, FailureHistoryResponse.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize failure history: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(entry -> entry != null)
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getStatusTimelineFallback")
    public List<String> getStatusTimeline(String serviceId, int limit) {
        String timelineKey = "service:" + serviceId + ":statusHistory";
        int safeLimit = Math.max(1, Math.min(limit, 50));

        List<String> entries = redisTemplate.opsForList()
                .range(timelineKey, 0, safeLimit - 1);
        return entries != null ? entries : Collections.emptyList();
    }

    // --- Circuit breaker fallback methods ---

    public HealthStatusResponse getHealthFallback(String serviceId, Throwable t) {
        log.warn("Circuit breaker OPEN for getHealth({}): {}", serviceId, t.getMessage());
        return HealthStatusResponse.builder()
                .serviceId(serviceId)
                .status("UNKNOWN")
                .failureCount(0L)
                .build();
    }

    public List<HealthStatusResponse> getAllServicesFallback(String statusFilter, Throwable t) {
        log.warn("Circuit breaker OPEN for getAllServices: {}", t.getMessage());
        return Collections.emptyList();
    }

    public SystemHealthSummary getSummaryFallback(Throwable t) {
        log.warn("Circuit breaker OPEN for getSummary: {}", t.getMessage());
        return SystemHealthSummary.builder().totalServices(0).build();
    }

    public List<FailureHistoryResponse> getFailureHistoryFallback(String serviceId, int limit, Throwable t) {
        log.warn("Circuit breaker OPEN for getFailureHistory({}): {}", serviceId, t.getMessage());
        return Collections.emptyList();
    }

    public List<String> getStatusTimelineFallback(String serviceId, int limit, Throwable t) {
        log.warn("Circuit breaker OPEN for getStatusTimeline({}): {}", serviceId, t.getMessage());
        return Collections.emptyList();
    }
}
