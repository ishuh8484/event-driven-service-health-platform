package com.microservices.failure.failure_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    private String serviceId;

    @Builder.Default
    private String status = "UNKNOWN";

    @Builder.Default
    private Long failureCount = 0L;

    private Long lastHeartbeat;

}
