package com.microservices.failure.failure_analyzer.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailureConsumer {

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "failure-events", groupId = "failure-analyzer-group")
    public void consume(FailureEvent event) {

        log.warn("Failure received for service: {}", event.getServiceId());

        String counterKey = "service:" + event.getServiceId() + ":failureCount";

        Long count = redisTemplate.opsForValue().increment(counterKey);

        log.warn("Failure count for {} is {}", event.getServiceId(), count);
    }
}
