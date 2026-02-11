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
