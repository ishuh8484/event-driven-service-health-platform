package com.microservices.health.health_query_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatusResponse {

    private String serviceId;

    @Builder.Default
    private String status = "UNKNOWN";

    @Builder.Default
    private Long failureCount = 0L;

    private Long lastHeartbeat;
    private Long registeredAt;
    private Long uptimeMillis;

    @Builder.Default
    private Long heartbeatCount = 0L;

    @Builder.Default
    private Double failureRatePercent = 0.0;
}
