# Event-Driven Service Health & Failure Monitoring Platform

## Tech Stack
- Java 21
- Spring Boot
- Apache Kafka (KRaft mode)
- Redis
- Docker Compose

## Architecture
- Service Registry (Producer)
- Kafka Event Backbone
- Redis for Fast State

## How to Run

### Start Infra
docker compose up -d

### Run Service
Run ServiceRegistryApplication

### Test
POST /api/registry/register
POST /api/registry/heartbeat


# Event-Driven Service Health & Failure Monitoring Platform

A backend-focused microservices project that demonstrates:
- Event-driven communication using Apache Kafka (KRaft mode)
- Redis-backed state management
- Service registration and heartbeat tracking
- Docker-based infrastructure setup

---

## 🏗 Architecture

Service Registry:
- REST API for service registration
- Publishes heartbeat events to Kafka
- Stores last heartbeat timestamp in Redis

Kafka:
- Runs in KRaft mode (no Zookeeper)
- Single-node local development setup

Redis:
- Stores fast-changing transient state
- Used for heartbeat timestamps


🧠 Failure Events Pipeline – Design & Challenges
📌 Problem

When implementing failure-events, we encountered a deserialization failure:

ClassNotFoundException: com.microservices.registry.service_registry.kafka.FailureEvent

📌 Root Cause

Spring Kafka’s JsonSerializer automatically adds the producer’s fully qualified class name in message headers.

Since the consumer runs in a separate microservice with a different package structure, it attempted to load the producer’s class — which does not exist in the consumer.

This caused:

MessageConversionException

RecordDeserializationException

Consumer crash loop

📌 Solution

We configured the consumer to:

Ignore type headers

Use a local FailureEvent class

Configuration:

spring:
kafka:
consumer:
value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
properties:
spring.json.trusted.packages: "*"
spring.json.use.type.headers: false
spring.json.value.default.type: com.microservices.failure.failure_analyzer.kafka.FailureEvent


This ensures:

Services are loosely coupled

Only JSON structure matters

No dependency on producer package names

📌 Redis Atomic Failure Counter

Failures are tracked using Redis atomic increment:

redisTemplate.opsForValue().increment("service:{id}:failureCount");


This leverages Redis INCR command which is:

Atomic

Thread-safe

Lock-free

High performance




