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

# Event-driven Postman Results

This section contains Postman screenshots captured while exercising the event-driven platform endpoints and flows (requests, responses, and observed state). Each screenshot is embedded below with a short description you can refine.

### Screenshot 2026-05-10 142449
![Screenshot 2026-05-10 142449](./EventDriven_Postman_results/Screenshot-2026-05-10-142449.png)
POST request/response that triggered an event (verify endpoint and response payload).

### Screenshot 2026-05-10 142556
![Screenshot 2026-05-10 142556](./EventDriven_Postman_results/Screenshot-2026-05-10-142556.png)
Health-check or status response for a service.

### Screenshot 2026-05-10 142659
![Screenshot 2026-05-10 142659](./EventDriven_Postman_results/Screenshot-2026-05-10-142659.png)
Event consumption or downstream behavior after publish.

### Screenshot 2026-05-10 142722
![Screenshot 2026-05-10 142722](./EventDriven_Postman_results/Screenshot-2026-05-10-142722.png)
Filtered GET request example (e.g., services by status).

### Screenshot 2026-05-10 142802
![Screenshot 2026-05-10 142802](./EventDriven_Postman_results/Screenshot-2026-05-10-142802.png)
Error or failure event payload used by the failure analyzer.

### Screenshot 2026-05-10 142829
![Screenshot 2026-05-10 142829](./EventDriven_Postman_results/Screenshot-2026-05-10-142829.png)
Request/response example (replace with specific description if needed).

### Screenshot 2026-05-10 142947
![Screenshot 2026-05-10 142947](./EventDriven_Postman_results/Screenshot-2026-05-10-142947.png)
Timeline or history query response demonstrating recent events.

### Screenshot 2026-05-10 143042
![Screenshot 2026-05-10 143042](./EventDriven_Postman_results/Screenshot-2026-05-10-143042.png)
Service registry registration flow or acknowledgement.

### Screenshot 2026-05-10 143136
![Screenshot 2026-05-10 143136](./EventDriven_Postman_results/Screenshot-2026-05-10-143136.png)
Service failure count or Redis-backed state snapshot.

### Screenshot 2026-05-10 143224
![Screenshot 2026-05-10 143224](./EventDriven_Postman_results/Screenshot-2026-05-10-143224.png)
Kafka message payload or consumer response capture.

### Screenshot 2026-05-10 143256
![Screenshot 2026-05-10 143256](./EventDriven_Postman_results/Screenshot-2026-05-10-143256.png)
Additional example — replace with descriptive caption.

### Screenshot 2026-05-10 143345
![Screenshot 2026-05-10 143345](./EventDriven_Postman_results/Screenshot-2026-05-10-143345.png)
Failure analyzer output or diagnostic response.

### Screenshot 2026-05-10 143416
![Screenshot 2026-05-10 143416](./EventDriven_Postman_results/Screenshot-2026-05-10-143416.png)
Health summary or aggregated system status response.

### Screenshot 2026-05-10 143454
![Screenshot 2026-05-10 143454](./EventDriven_Postman_results/Screenshot-2026-05-10-143454.png)
Final example screenshot — replace with final caption.
