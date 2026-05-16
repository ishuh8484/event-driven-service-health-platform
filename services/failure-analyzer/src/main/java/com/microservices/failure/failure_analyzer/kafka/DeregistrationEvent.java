package com.microservices.failure.failure_analyzer.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeregistrationEvent {

    private String serviceId;
    private long timestamp;
    private String reason;
}
