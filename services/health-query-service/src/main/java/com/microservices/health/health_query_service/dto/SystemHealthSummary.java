package com.microservices.health.health_query_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemHealthSummary {

    private int totalServices;
    private int healthyCount;
    private int degradedCount;
    private int unhealthyCount;
    private int unknownCount;
}

