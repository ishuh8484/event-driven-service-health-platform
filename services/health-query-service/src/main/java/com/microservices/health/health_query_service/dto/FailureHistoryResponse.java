package com.microservices.health.health_query_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureHistoryResponse {

    private String serviceId;
    private String errorType;
    private String message;
    private long timestamp;
}
