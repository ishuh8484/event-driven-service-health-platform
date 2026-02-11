package com.microservices.registry.service_registry.model;


import lombok.Data;

@Data
public class HeartBeatRequest {
    private String serviceId;
}
