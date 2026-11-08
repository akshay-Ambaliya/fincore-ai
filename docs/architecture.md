
# FinCore AI - Architecture

## 1. Architecture Style

Microservices Architecture

## 2. Core Principles

1. Database per Service
2. Independent Deployment
3. API Gateway as the Entry Point
4. Event-Driven Communication
5. Multi-Tenant Architecture
6. Secure Service-to-Service Communication
7. Independent Service Ownership

## 3. Microservices

| Service | Responsibility |
|---|---|
| Identity | Authentication and users |
| Organization | Organizations and memberships |
| Account | Financial accounts |
| Transaction | Income, expenses and transfers |
| Ledger | Double-entry bookkeeping |
| Budget | Budget management |
| Reporting | Financial reports |
| Notification | Email and notifications |

## 4. Infrastructure

- PostgreSQL
- Redis
- Apache Kafka
- Docker
- Spring Cloud Gateway

## 5. Communication

Synchronous:
- REST APIs
- OpenFeign

Asynchronous:
- Apache Kafka
- Domain Events

## 6. Multi-Tenancy

Supported tenant types:
- PERSONAL
- ORGANIZATION

Each financial record must be associated
with the appropriate tenant.

Tenant isolation must be enforced
at the backend service level.

## 7. Database Strategy

Database per Service.

Services must not directly access
another service's database.

## 8. Development Strategy

1. Backend first
2. Frontend later
3. Start with core infrastructure
4. Implement services independently
5. Introduce distributed systems gradually
6. Integrate AI in the final phase
