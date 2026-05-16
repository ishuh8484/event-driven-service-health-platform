# Event-Driven Service Health & Failure Monitoring Platform

A compact event-driven microservices example that demonstrates service registration, heartbeat publishing, failure detection, and lightweight state using Apache Kafka and Redis. Intended for local development, demos, and experimentation.

## Tech stack
- Java 21 (Spring Boot)
- Apache Kafka (KRaft) — event backbone
- Redis — fast state (timestamps, atomic counters)
- Docker Compose — local infra
- Maven — build and run

## Services (brief)
- api-gateway : routes client requests to internal services and enforces rate limits.
- service-registry : registers services, accepts heartbeats, publishes heartbeat events to Kafka, writes lastHeartbeat to Redis.
- failure-analyzer : consumes Kafka events, applies detection rules, increments `service:{id}:failureCount` in Redis and emits diagnostics.
- health-query-service : reads/aggregates state from Redis and exposes health endpoints and summaries.
- notification-service : consumes diagnostics/notifications and forwards alerts or messages.

## High-level flow
1. Services register with `service-registry` and post periodic heartbeats.
2. `service-registry` stores quick state in Redis and publishes heartbeat events to Kafka.
3. `failure-analyzer` consumes events, applies rules, and increments failure counters in Redis when necessary.
4. `health-query-service` provides read endpoints for status and aggregates data for UIs/ops.
5. `notification-service` handles diagnostics/alerts produced by the analyzer.

## Main APIs (examples)
- Service Registry (via API Gateway)
  - POST /api/registry/register — register a service
  - POST /api/registry/heartbeat — submit a heartbeat
- Health Query (via API Gateway)
  - GET /health/{serviceId} — service status and last heartbeat
  - GET /health/services?status={UP|DOWN} — list services (filter by status)
- Notifications (via API Gateway)
  - POST /api/notifications — send/forward a notification

## Quick start
1. Start infra: `docker compose up -d` (starts Kafka + Redis containers from `infra/`)
2. Run services from your IDE or with Maven: `./mvnw -pl <module> spring-boot:run`
3. Exercise the main APIs with Postman or curl (see `EventDriven_Postman_results/` for example screenshots)


# Event-driven Postman Results

This section contains Postman screenshots captured while exercising the event-driven platform endpoints and flows (requests, responses, and observed state)

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


