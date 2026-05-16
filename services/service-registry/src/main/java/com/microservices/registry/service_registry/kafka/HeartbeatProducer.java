package com.microservices.registry.service_registry.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeartbeatProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "heartbeat-events";

    //serviceId → heartbeat-events topic
    public void sendHeartbeat(String serviceId) {

        HeartbeatEvent event = new HeartbeatEvent(
                serviceId,
                System.currentTimeMillis()
        );

        kafkaTemplate.send(TOPIC, event);
    }
}
