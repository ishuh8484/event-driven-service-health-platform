package com.microservices.failure.failure_analyzer.service;

import com.microservices.failure.failure_analyzer.kafka.HealthAlertProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthEvaluationService {

    private final StringRedisTemplate redisTemplate;
    private final HealthAlertProducer healthAlertProducer;

    // heartbeat kitne purana ho toh UNHEALTHY maano (30 seconds)
    private static final long HEARTBEAT_TIMEOUT_MS = 30000;
    // failure count threshold for DEGRADED
    private static final int FAILURE_THRESHOLD = 5;
    private static final int MAX_TIMELINE_ENTRIES = 50;

    public void evaluateHealth() {

        Set<String> healthKeys = redisTemplate.keys("service:*:healthStatus");

        if (healthKeys == null || healthKeys.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();

        for (String healthKey : healthKeys) {

            String serviceId = healthKey.split(":")[1];
            String heartbeatKey = "service:" + serviceId + ":lastHeartbeat";
            String failureKey = "service:" + serviceId + ":failureCount";

            String currentStatus = redisTemplate.opsForValue().get(healthKey);
            if (currentStatus == null) currentStatus = "UNKNOWN";

            String heartbeatStr = redisTemplate.opsForValue().get(heartbeatKey);

            boolean unhealthy = false;
            boolean degraded = false;

            // check heartbeat — agar timeout ho gaya toh service is down
            if (heartbeatStr == null) {
                unhealthy = true;
            } else {
                long lastHeartbeat = Long.parseLong(heartbeatStr);
                if (now - lastHeartbeat > HEARTBEAT_TIMEOUT_MS) {
                    unhealthy = true;
                }
            }

            // check failure count — sliding window mein kitne failures aaye
            String failureStr = redisTemplate.opsForValue().get(failureKey);
            if (failureStr != null) {
                long failureCount = Long.parseLong(failureStr);
                if (failureCount >= FAILURE_THRESHOLD) {
                    degraded = true;
                }
            }

            String newStatus;
            if (degraded) {
                newStatus = "DEGRADED";
            } else if (unhealthy) {
                newStatus = "UNHEALTHY";
            } else {
                newStatus = "HEALTHY";
            }

            redisTemplate.opsForValue().set(healthKey, newStatus);

            if (!newStatus.equals(currentStatus)) {

                if ("HEALTHY".equals(newStatus)) {
                    redisTemplate.delete(failureKey);
                    log.info("Reset failure count for recovered service: {}", serviceId);
                }

                // status change history store karo for timeline
                storeStatusTimeline(serviceId, currentStatus, newStatus, now);

                // alert publish karo Kafka pe
                healthAlertProducer.sendAlert(serviceId, currentStatus, newStatus);

                log.warn("Service {} status changed: {} → {}", serviceId, currentStatus, newStatus);
            } else {
                log.info("Service {} status unchanged: {}", serviceId, newStatus);
            }
        }
    }

    private void storeStatusTimeline(String serviceId, String oldStatus, String newStatus, long timestamp) {
        String timelineKey = "service:" + serviceId + ":statusHistory";
        String entry = timestamp + ":" + oldStatus + "→" + newStatus;

        redisTemplate.opsForList().leftPush(timelineKey, entry);
        redisTemplate.opsForList().trim(timelineKey, 0, MAX_TIMELINE_ENTRIES - 1);
    }
}
