package com.microservices.failure.failure_analyzer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthAlertProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "health-alerts";

    public void sendAlert(String serviceId, String previousStatus, String newStatus) {

        HealthAlertEvent event = new HealthAlertEvent(
                serviceId,
                previousStatus,
                newStatus,
                System.currentTimeMillis()
        );

        kafkaTemplate.send(TOPIC, serviceId, event);

        log.warn("Health alert published: {} went {} → {}",
                serviceId, previousStatus, newStatus);
    }
}
