package com.microservices.notification.notification_service.kafka;

import com.microservices.notification.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "health-alerts",
            containerFactory = "alertKafkaListenerContainerFactory"
    )
    public void consume(HealthAlertEvent event) {
        log.info("Received health alert: {} went {} → {}",
                event.getServiceId(), event.getPreviousStatus(), event.getNewStatus());

        notificationService.processAlert(event);
    }
}
