package com.microservices.failure.failure_analyzer.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailureEvent {

    private String serviceId;
    private String errorType;
    private String message;
    private long timestamp;
}