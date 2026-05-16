package com.microservices.registry.service_registry.controller;

import com.microservices.registry.service_registry.model.HeartBeatRequest;
import com.microservices.registry.service_registry.model.RegisterRequest;
import com.microservices.registry.service_registry.service.RegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registry")
@RequiredArgsConstructor
public class RegistryController {

    private final RegistryService registryService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        registryService.registerService(request.getServiceId());
        return "Service registered successfully";
    }

    @PostMapping("/heartbeat")
    public String heartbeat(@RequestBody HeartBeatRequest request) {
        registryService.heartbeat(request.getServiceId());
        return "Heartbeat received";
    }

    @PostMapping("/failure")
    public String simulateFailure(@RequestBody HeartBeatRequest request) {
        registryService.simulateFailure(request.getServiceId());
        return "Failure event sent";
    }

    @DeleteMapping("/{serviceId}")
    public Map<String, String> deregister(@PathVariable String serviceId) {
        registryService.deregisterService(serviceId, "MANUAL");
        return Map.of(
                "message", "Service deregistered successfully",
                "serviceId", serviceId
        );
    }
}
