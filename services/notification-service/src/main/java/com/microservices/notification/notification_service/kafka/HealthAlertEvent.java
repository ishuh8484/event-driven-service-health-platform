package com.microservices.notification.notification_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthAlertEvent {

    private String serviceId;
    private String previousStatus;
    private String newStatus;
    private long timestamp;
}
