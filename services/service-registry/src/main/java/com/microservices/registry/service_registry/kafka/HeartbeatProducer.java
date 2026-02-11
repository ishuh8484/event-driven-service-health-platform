package com.microservices.registry.service_registry.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeartbeatProducer {



    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "heartbeat-events";

    //serviceId → heartbeat-events topic
    public void sendHeartbeat(String serviceId) {
        kafkaTemplate.send(TOPIC, serviceId);
    }
}
