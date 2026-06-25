# Kaatha — Digital Credit Book Platform

Production-grade microservices platform replacing traditional paper-based Kaatha (credit ledgers) for local shops.

## Architecture

```
Client Apps → API Gateway (8080) → Eureka Discovery (8761)
                    ↓
    ┌───────────────┼───────────────────────────────────────┐
    │               │                                       │
 Auth          Shopkeeper                              Customer
 (8083)         (8081)                                  (8082)
    │               │                                       │
    └─────── Item (8084) ─ Transaction (8085) ─ Ledger (8086)
                    │              │                        │
              Invoice (8089)  Payment (8088)    Notification (8087)
                    │                                       │
              Report/Dashboard (8090)              Redis + RabbitMQ
```

## Services

| Service | Port | Purpose |
|---------|------|---------|
| discovery-service | 8761 | Eureka service registry |
| api-gateway | 8080 | Routing, JWT auth, CORS |
| auth-service | 8083 | OTP login, JWT, refresh tokens |
| shopkeeper-service | 8081 | Shopkeeper registration, Razorpay account |
| customer-service | 8082 | Per-shop customer records |
| item-service | 8084 | Product catalog & stock |
| transaction-service | 8085 | Purchases, payments, orchestration |
| ledger-service | 8086 | Outstanding balance per shop-customer |
| notification-service | 8087 | SMS log, in-app, SSE real-time |
| payment-service | 8088 | Razorpay orders & webhooks |
| invoice-service | 8089 | Invoice generation & PDF export |
| report-service | 8090 | Shopkeeper & customer dashboards |

## Key Design Decisions

- **Customer isolation**: Uniqueness is `shopkeeper_id + phone_number` — same phone can exist under multiple shops with independent ledgers.
- **OTP auth**: Redis-backed OTP with rate limiting; delivered via notification-service (SMS provider pluggable).
- **Credit flow**: Purchase → stock deduction → ledger update → invoice → notifications.
- **Online payments**: Razorpay Route transfers to shopkeeper linked accounts.
- **Real-time**: Server-Sent Events at `/notifications/stream/{phone}`.

## Quick Start

### Prerequisites
- Java 21, Maven 3.9+, Docker & Docker Compose

### Run with Docker

```bash
docker-compose up --build
```

### Run locally (development)

1. Start MySQL on port 3307, Redis on 6379
2. Start discovery-service first, then other services
3. All requests go through gateway: `http://localhost:8080`

### Environment Variables

| Variable | Description |
|----------|-------------|
| `RAZORPAY_KEY_ID` | Razorpay API key (mock mode if empty) |
| `RAZORPAY_KEY_SECRET` | Razorpay API secret |
| `REDIS_HOST` | Redis host for OTP storage |

## API Flows

### Shopkeeper Registration
1. `GET /shopkeepers/check-phone/{phone}` — check availability
2. `POST /auth/send-otp` with `forRegistration: true`
3. `POST /auth/verify-otp`
4. `POST /shopkeepers/register` — creates shopkeeper + Razorpay linked account

### Customer Addition (by shopkeeper)
1. `GET /customers/exists/{shopkeeperId}/{phone}`
2. If new: OTP → `POST /customers/register`
3. Ledger auto-created on first purchase

### Purchase
```json
POST /transactions/purchase
{
  "shopkeeperId": 1,
  "customerId": 1,
  "items": [{"itemId": 1, "quantity": 2}],
  "paymentStatus": "PENDING",
  "paymentMethod": "CASH",
  "tax": 0,
  "discount": 0
}
```

### Online Payment
1. `POST /payments/create-order` — get Razorpay order ID
2. Client completes payment via Razorpay checkout
3. `POST /payments/verify` — verify signature
4. Ledger updated, invoice generated

### Dashboards
- Shopkeeper: `GET /reports/shopkeeper/{id}/dashboard`
- Customer: `GET /reports/customer/{phone}/dashboard`

## Monitoring

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- RabbitMQ Management: http://localhost:15672 (kaatha/kaatha)

## Tech Stack

Java 21 · Spring Boot 3.5 · Spring Cloud · Eureka · Gateway · OpenFeign · MySQL · Redis · RabbitMQ · Razorpay · OpenPDF · Prometheus · Grafana · Docker

## Project Structure

```
KaathaApplication/
├── common-lib/           # Shared enums & events
├── discovery-service/
├── api-gateway/
├── auth-service/
├── shopkeeper-service/
├── customer-service/
├── item-service/
├── transaction-service/
├── ledger-service/
├── notification-service/
├── payment-service/
├── invoice-service/
├── report-service/
├── monitoring/           # Prometheus config
├── docker-compose.yml
└── init-db.sql
```

## Security

- JWT enforced at API Gateway for all routes except OTP, registration, and webhooks
- User context propagated via `X-User-Phone` and `X-User-Type` headers
- OTP rate-limited in Redis (5 attempts per 15 minutes)

## License

Proprietary — Kaatha Platform
