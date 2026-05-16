package com.microservices.failure.failure_analyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.failure.failure_analyzer.dto.FailureHistoryResponse;
import com.microservices.failure.failure_analyzer.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthQueryService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public HealthResponse getHealth(String serviceId) {
        String status = redisTemplate.opsForValue().get("service:" + serviceId + ":healthStatus");
        String failureStr = redisTemplate.opsForValue().get("service:" + serviceId + ":failureCount");
        String heartbeatStr = redisTemplate.opsForValue().get("service:" + serviceId + ":lastHeartbeat");

        Long failureCount = failureStr != null ? Long.parseLong(failureStr) : 0L;
        Long lastHeartbeat = heartbeatStr != null ? Long.parseLong(heartbeatStr) : null;

        return HealthResponse.builder()
                .serviceId(serviceId)
                .status(status != null ? status : "UNKNOWN")
                .failureCount(failureCount)
                .lastHeartbeat(lastHeartbeat)
                .build();
    }

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
                        log.error("Failed to deserialize failure history entry: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(entry -> entry != null)
                .collect(Collectors.toList());
    }
}
