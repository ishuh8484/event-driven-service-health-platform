package com.microservices.gateway.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

// Global filter — har request ko log karta hai + correlation ID add karta hai
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        long startTime = System.currentTimeMillis();

        // reuse client's correlation ID if provided, otherwise generate new one
        String correlationId = exchange.getRequest().getHeaders()
                .getFirst(CORRELATION_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        log.info("→ Gateway incoming: {} {} [correlationId={}]", method, path, correlationId);

        String finalCorrelationId = correlationId;
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_HEADER, finalCorrelationId)
                .build();

        exchange.getResponse().getHeaders().add(CORRELATION_HEADER, finalCorrelationId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("← Gateway response: {} {} → {} ({}ms) [correlationId={}]",
                            method, path, statusCode, duration, finalCorrelationId);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
