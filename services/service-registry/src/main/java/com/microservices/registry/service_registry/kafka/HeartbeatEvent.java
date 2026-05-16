package com.microservices.registry.service_registry.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeartbeatEvent {

    private String serviceId;
    private long timestamp;
}