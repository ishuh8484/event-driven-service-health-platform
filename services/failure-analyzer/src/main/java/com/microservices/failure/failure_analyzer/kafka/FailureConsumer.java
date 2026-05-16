package com.microservices.failure.failure_analyzer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailureConsumer {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final int FAILURE_THRESHOLD = 5;
    private static final int MAX_FAILURE_HISTORY = 50;

    @KafkaListener(
            topics = "failure-events",
            groupId = "failure-analyzer-group",
            containerFactory = "failureKafkaListenerContainerFactory"
    )
    public void consume(FailureEvent event) {

        String serviceId = event.getServiceId();
        log.warn("Failure received for service: {}", serviceId);

        // sliding window counter — INCR is atomic (thread-safe for concurrent consumers)
        String counterKey = "service:" + serviceId + ":failureCount";
        Long count = redisTemplate.opsForValue().increment(counterKey);

        // 60s TTL sirf first failure pe set karo — sliding window start
        if (count != null && count == 1) {
            redisTemplate.expire(counterKey, 60, TimeUnit.SECONDS);
        }

        log.warn("Failure count for {} is {}", serviceId, count);

        // failure history mein store karo (bounded Redis List)
        storeFailureHistory(serviceId, event);

        // threshold cross hua toh DEGRADED mark karo
        if (count != null && count >= FAILURE_THRESHOLD) {
            String healthKey = "service:" + serviceId + ":healthStatus";
            String currentStatus = redisTemplate.opsForValue().get(healthKey);

            if (!"UNHEALTHY".equals(currentStatus)) {
                redisTemplate.opsForValue().set(healthKey, "DEGRADED");
                log.warn("Service {} marked DEGRADED (high failure rate)", serviceId);
            }
        }
    }

    private void storeFailureHistory(String serviceId, FailureEvent event) {
        try {
            String historyKey = "service:" + serviceId + ":failureHistory";
            String eventJson = objectMapper.writeValueAsString(event);

            // LPUSH = newest first, LTRIM = bounded list
            redisTemplate.opsForList().leftPush(historyKey, eventJson);
            redisTemplate.opsForList().trim(historyKey, 0, MAX_FAILURE_HISTORY - 1);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize failure event: {}", e.getMessage());
        }
    }
}
