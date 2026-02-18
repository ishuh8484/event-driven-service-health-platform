package com.microservices.registry.service_registry.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FailureProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendFailure(String serviceId, String errorType, String message) {

        FailureEvent event = new FailureEvent(
                serviceId,
                errorType,
                message,
                System.currentTimeMillis()
        );

        kafkaTemplate.send("failure-events", event);
    }
}
