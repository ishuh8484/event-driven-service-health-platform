package com.microservices.notification.notification_service.service;

import com.microservices.notification.notification_service.kafka.HealthAlertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@Slf4j
public class NotificationService {

    // in-memory store for recent alerts (thread-safe)
    private final ConcurrentLinkedDeque<HealthAlertEvent> recentAlerts =
            new ConcurrentLinkedDeque<>();

    private static final int MAX_ALERTS = 50;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public void processAlert(HealthAlertEvent event) {

        recentAlerts.addFirst(event);
        if (recentAlerts.size() > MAX_ALERTS) {
            recentAlerts.removeLast();
        }

        String time = FORMATTER.format(Instant.ofEpochMilli(event.getTimestamp()));

        log.warn("ALERT | Service: {} | {} → {} | Time: {}",
                event.getServiceId(),
                event.getPreviousStatus(), event.getNewStatus(), time);
    }

    public List<HealthAlertEvent> getRecentAlerts() {
        return new ArrayList<>(recentAlerts);
    }
}
