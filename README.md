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




