package com.microservices.failure.failure_analyzer.service;

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

    private static final long TIMEOUT_MS = 30000; //30 seconds

    public void evaluateHealth(){

        Set<String> keys = redisTemplate.keys("service:*:lastHeartbeat");

        if(keys==null || keys.isEmpty()){
            return;
        }

        long now = System.currentTimeMillis();

        for(String key : keys){
            String serviceId = key.split(":")[1];

            String lastHeartbeatStr = redisTemplate.opsForValue().get(key);

            if (lastHeartbeatStr == null) continue;

            long lastHeartbeat = Long.parseLong(lastHeartbeatStr);

            long diff = now - lastHeartbeat;

            if (diff > TIMEOUT_MS) {
                redisTemplate.opsForValue().set(
                        "service:" + serviceId + ":healthStatus",
                        "UNHEALTHY"
                );

                log.warn("Service {} marked UNHEALTHY (no heartbeat)", serviceId);
            }
        }
    }
}
