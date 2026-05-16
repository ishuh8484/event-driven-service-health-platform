package com.microservices.notification.notification_service.controller;

import com.microservices.notification.notification_service.kafka.HealthAlertEvent;
import com.microservices.notification.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/recent")
    public List<HealthAlertEvent> getRecentAlerts() {
        return notificationService.getRecentAlerts();
    }
}

