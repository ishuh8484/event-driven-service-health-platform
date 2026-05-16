package com.microservices.registry.service_registry.service;

import com.microservices.registry.service_registry.kafka.DeregistrationProducer;
import com.microservices.registry.service_registry.kafka.FailureProducer;
import com.microservices.registry.service_registry.kafka.HeartbeatProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistryService {

    private final StringRedisTemplate redisTemplate;
    private final HeartbeatProducer heartbeatProducer;
    private final FailureProducer failureProducer;
    private final DeregistrationProducer deregistrationProducer;

    public void registerService(String serviceId) {
        redisTemplate.opsForValue()
                .set("service:" + serviceId + ":registered", "true");

        redisTemplate.opsForValue()
                .set("service:" + serviceId + ":registeredAt",
                        String.valueOf(System.currentTimeMillis()));

        log.info("Service registered: {}", serviceId);
    }

    public void heartbeat(String serviceId) {
        redisTemplate.opsForValue()
                .set("service:" + serviceId + ":lastHeartbeat",
                        String.valueOf(System.currentTimeMillis()));
        heartbeatProducer.sendHeartbeat(serviceId);
    }

    public void simulateFailure(String serviceId) {
        failureProducer.sendFailure(
                serviceId,
                "DB_ERROR",
                "Database connection timeout"
        );
    }

    public void deregisterService(String serviceId, String reason) {

        String registeredKey = "service:" + serviceId + ":registered";
        String isRegistered = redisTemplate.opsForValue().get(registeredKey);

        if (isRegistered == null) {
            log.warn("Service {} is not registered, nothing to deregister", serviceId);
            return;
        }

        // delete all Redis keys for this service
        Set<String> keys = redisTemplate.keys("service:" + serviceId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Deleted {} Redis keys for service: {}", keys.size(), serviceId);
        }

        // publish deregistration event so other services can clean up too
        deregistrationProducer.sendDeregistration(serviceId, reason);

        log.warn("Service deregistered: {} (reason: {})", serviceId, reason);
    }
}
