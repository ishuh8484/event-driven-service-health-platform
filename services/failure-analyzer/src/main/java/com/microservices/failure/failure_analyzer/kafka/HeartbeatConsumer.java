package com.microservices.failure.failure_analyzer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartbeatConsumer {

    //Kafka stores it.
    //Failure Analyzer:
    //Consumes event
    //Logs it
    //Updates Redis
    //Completely independent

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "heartbeat-events", groupId = "failure-analyzer-group")
    public void consume(String serviceId) {

        long now = System.currentTimeMillis();

        log.info("Received heartbeat event for service: {}", serviceId);

        // to Store last heartbeat timestamp
        redisTemplate.opsForValue().set(
                "service:" + serviceId + ":lastHeartbeat",
                String.valueOf(now)
        );


        // to initially mark healthy
        redisTemplate.opsForValue().set(
                "service:" + serviceId + ":healthStatus",
                "HEALTHY"
        );

        log.info("Updated Redis for service: {}", serviceId);
    }
}
