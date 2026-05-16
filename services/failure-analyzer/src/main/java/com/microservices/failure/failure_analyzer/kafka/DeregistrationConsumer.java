package com.microservices.failure.failure_analyzer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

// Cleans up failure-analyzer's Redis keys when a service gets deregistered
@Component
@RequiredArgsConstructor
@Slf4j
public class DeregistrationConsumer {

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(
            topics = "deregistration-events",
            containerFactory = "deregistrationKafkaListenerContainerFactory"
    )
    public void consume(DeregistrationEvent event) {

        String serviceId = event.getServiceId();
        log.warn("Received deregistration event for: {} (reason: {})",
                serviceId, event.getReason());

        Set<String> remainingKeys = redisTemplate.keys("service:" + serviceId + ":*");
        if (remainingKeys != null && !remainingKeys.isEmpty()) {
            redisTemplate.delete(remainingKeys);
            log.info("Cleaned up {} remaining Redis keys for deregistered service: {}",
                    remainingKeys.size(), serviceId);
        }
    }
}
