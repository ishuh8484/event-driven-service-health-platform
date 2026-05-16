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
<img width="1919" height="866" alt="Screenshot-2026-05-10-142449" src="https://github.com/user-attachments/assets/03ea09f5-27c6-42c6-98aa-6b15f5cfee06" />


POST request/response that triggered an event (verify endpoint and response payload).

### Screenshot 2026-05-10 142556
<img width="1919" height="904" alt="Screenshot-2026-05-10-142556" src="https://github.com/user-attachments/assets/d603d709-2f91-42ce-a227-947f8d94dfb1" />

Health-check or status response for a service.

### Screenshot 2026-05-10 142659
<img width="1919" height="888" alt="Screenshot-2026-05-10-142659" src="https://github.com/user-attachments/assets/82434fb0-78e5-4ab8-9011-ae979b7f0497" />

Event consumption or downstream behavior after publish.

<img width="1919" height="899" alt="Screenshot-2026-05-10-142722" src="https://github.com/user-attachments/assets/5f96a6e8-90bd-47e0-b56e-147585d9f0f2" />

Filtered GET request example (e.g., services by status).

<img width="1918" height="906" alt="Screenshot-2026-05-10-142802" src="https://github.com/user-attachments/assets/0e275536-4a58-4629-bf9e-f2f2cbf9ef80" />

Error or failure event payload used by the failure analyzer.

<img width="1919" height="903" alt="Screenshot-2026-05-10-142829" src="https://github.com/user-attachments/assets/c0508800-c893-4203-8991-e1536f1ee3cc" />

Request/response example (replace with specific description if needed).

<img width="1919" height="887" alt="Screenshot-2026-05-10-142947" src="https://github.com/user-attachments/assets/c460e315-d497-4698-8ecf-82c8e4cf87ed" />

Timeline or history query response demonstrating recent events.

<img width="1919" height="889" alt="Screenshot-2026-05-10-143042" src="https://github.com/user-attachments/assets/76cc9fa4-c1dd-495e-a869-0c50b37c2a32" />

Service registry registration flow or acknowledgement.

<img width="1919" height="899" alt="Screenshot-2026-05-10-143136" src="https://github.com/user-attachments/assets/25adf5ee-1e26-48f0-b2ee-3e64d5b4e3ed" />

Service failure count or Redis-backed state snapshot.

<img width="1919" height="901" alt="Screenshot-2026-05-10-143224" src="https://github.com/user-attachments/assets/b11c3e9d-5e9f-4a1a-852b-1f5bc5f3d6a5" />

Kafka message payload or consumer response capture.

<img width="1919" height="898" alt="Screenshot-2026-05-10-143256" src="https://github.com/user-attachments/assets/2b3e61f3-f203-48c3-a012-fd523b799b16" />

Additional example — replace with descriptive caption.

<img width="1919" height="903" alt="Screenshot-2026-05-10-143345" src="https://github.com/user-attachments/assets/af3fccd9-9db3-4ed0-afa8-b1ff52ea6738" />

Failure analyzer output or diagnostic response.

<img width="1919" height="902" alt="Screenshot-2026-05-10-143416" src="https://github.com/user-attachments/assets/513c34a7-71fd-45f6-a1b9-d5c29d28e865" />

Health summary or aggregated system status response.

<img width="1919" height="900" alt="Screenshot-2026-05-10-143454" src="https://github.com/user-attachments/assets/c5d07136-6ce6-4df2-a80a-b3b8f285b44c" />

Final example screenshot — replace with final caption.
