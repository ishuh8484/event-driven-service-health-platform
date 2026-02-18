package com.microservices.registry.service_registry.service;

import com.microservices.registry.service_registry.kafka.FailureProducer;
import com.microservices.registry.service_registry.kafka.HeartbeatProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistryService {

    private final StringRedisTemplate redisTemplate;
    private final HeartbeatProducer heartbeatProducer;
    private final FailureProducer failureProducer;

    public void registerService(String serviceId){
        redisTemplate.opsForValue()
                .set("service:" + serviceId + ":registered", "true");
    }

    public void heartbeat(String serviceId){
        // Only publish event
        heartbeatProducer.sendHeartbeat(serviceId);
    }

    public void simulateFailure(String serviceId) {
        failureProducer.sendFailure(
                serviceId,
                "DB_ERROR",
                "Database connection timeout"
        );
    }
}
