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

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(
            topics = "heartbeat-events",
            containerFactory = "heartbeatKafkaListenerContainerFactory"
    )
    public void consume(HeartbeatEvent event) {

        String serviceId = event.getServiceId();
        log.info("Received heartbeat event for service: {}", serviceId);

        redisTemplate.opsForValue().set(
                "service:" + serviceId + ":lastHeartbeat",
                String.valueOf(System.currentTimeMillis())
        );

        // lifetime heartbeat counter — failure rate calculate karne ke liye
        redisTemplate.opsForValue().increment(
                "service:" + serviceId + ":heartbeatCount"
        );

        redisTemplate.opsForValue().set(
                "service:" + serviceId + ":healthStatus",
                "HEALTHY"
        );

        log.info("Updated Redis for service: {}", serviceId);
    }
}
