package com.microservices.registry.service_registry.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeregistrationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "deregistration-events";

    public void sendDeregistration(String serviceId, String reason) {

        DeregistrationEvent event = new DeregistrationEvent(
                serviceId,
                System.currentTimeMillis(),
                reason
        );

        kafkaTemplate.send(TOPIC, serviceId, event);
        log.warn("Deregistration event published: {} (reason: {})", serviceId, reason);
    }
}
