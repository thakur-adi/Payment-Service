## Payment Service

The service provides a unified payment abstraction over multiple payment gateways using Strategy, Adapter, and Factory design patterns. It supports secure payment processing through Stripe Checkout Sessions and Razorpay, webhook-based transaction verification, transaction persistence, card management, and a clean layered architecture. It is designed to explore backend payment processing patterns commonly used in modern distributed systems while following clean code and low-level design principles.

> **Note:** This service is part of a larger E-Commerce Microservices project.

---

## Highlights

* **Multi-gateway architecture** — Supports both Stripe and Razorpay through a common abstraction using Strategy, Adapter, and Factory design patterns
* **Dedicated gateway integrations** — Stripe and Razorpay callbacks are handled by separate controllers, isolating provider-specific logic and simplifying future gateway integrations
* **Webhook-driven payment verification** — Payment status is confirmed through secure server-to-server webhooks rather than relying solely on client-side redirects
* **Immutable transaction history** — Every significant payment gateway interaction is persisted as a new transaction record, preserving the complete payment lifecycle for auditing, debugging, and reconciliation
* **Checkout Session integration** — Uses Stripe Checkout Sessions to support structured payment creation with customer details, metadata, success URLs, and cancel URLs
* **Card management** — Provides dedicated APIs for securely managing user payment methods and associated card metadata
* **LLD-compliant design** — Business logic is isolated from third-party SDKs through clean Low-Level Design principles with clear separation of responsibilities
* **Secrets externalized** — Payment gateway credentials and database configuration are loaded from environment variables rather than being hardcoded


---

## Architecture Overview

```
Client
  │
  ▼
Payment Controller
  │
  ▼
Payment Service
  │
  ▼
Payment Gateway Factory
  │
  ├───────────────┐
  ▼               ▼
Stripe Adapter   Razorpay Adapter
  │               │
Stripe SDK     Razorpay SDK
  │               │
  └──────┬────────┘
         ▼
Gateway Response
         │
         ▼
Webhook Controller
         │
         ├── Verify webhook signature
         ├── Create Transaction Record
         └── Update payment status
```

---

## Features

### Payment Processing

* **Multi-gateway support** — Runtime payment provider selection (Stripe / Razorpay)
* **Checkout Session creation** — Secure hosted payment pages with configurable customer details, metadata, success URL, and cancel URL
* **Webhook verification** — Gateway callbacks are validated before processing transactions
* **Success & Cancel URL handling** — Handles successful payments and user-initiated payment cancellations
* **Payment status tracking** — Maintains payment lifecycle through gateway callbacks

## Transaction Management

- Persist every significant gateway interaction as a new transaction record
- Immutable append-only transaction history
- Associate transactions with users and payments
- Store gateway-specific references and metadata
- Preserve the complete payment lifecycle for every transaction
- Simplify auditing, debugging, and reconciliation through historical records

Example payment lifecycle:

```text
Payment Created
        │
Checkout Session Created
        │
Payment Authorized
        │
Payment Captured
        │
Payment Successful
```

### Card Management

* Save user payment cards
* Retrieve saved cards
* Update saved card information
* Delete saved cards
* User-card relationship management

> Only non-sensitive card metadata is persisted (brand, last four digits, expiry, nickname, etc.). Full card numbers and CVVs are intentionally excluded from long-term storage.

### Reliability

* Global exception handling
* Structured API error responses
* Validation for incoming requests
* Gateway-specific exception handling

---

## Tech Stack

| Layer             | Technology                 |
| ----------------- | -------------------------- |
| Framework         | Spring Boot                |
| ORM               | Spring Data JPA            |
| Database          | MySQL,InnoDb               |
| Payment Providers | Stripe, Razorpay           |
| Design Patterns   | Strategy, Adapter, Factory |
| Build Tool        | Maven                      |

---

## API Endpoints

All endpoints are prefixed with the context path "/payment".

### Payments

| Method   | Endpoint                          | Description             |
| -------- | --------------------------------- | ----------------------- |
| `POST`   | `/pay`                            | Create a payment session|
| `POST`   | `/card`                           | Save a payment card     |
| `PUT`    | `/card/{cardId}`                  | Update card information |
| `DELETE` | `/card/{cardId}/{userId}`         | Delete a saved card     |



### Stripe Callback APIs

| Method   | Endpoint                          | Description                          |
| -------- | --------------------------------- | ------------------------------------ |
| `POST`   | `/stripe/success`                 | Receive gateway successful callbacks |
| `POST`   | `/stripe/failure`                 | Receive gateway cancelled callbacks  |
| `POST`   | `/stripe/webhook`                 | Receive gateway webhook callbacks    |


### Razorpay Callback APIs

| Method   | Endpoint                          | Description                          |
| -------- | --------------------------------- | ------------------------------------ |
| `POST`   | `/razorpay/success`               | Receive gateway successful callbacks |
| `POST`   | `/razorpay/failure`               | Receive gateway cancelled callbacks  |
| `POST`   | `/razorpay/webhook`               | Receive gateway webhook callbacks    |



---

## Payment Flow

```
CREATE PAYMENT

  └──▶ Client requests payment
  └──▶ Factory selects appropriate payment gateway
  └──▶ Adapter converts request to gateway format
  └──▶ Gateway creates hosted checkout session
  └──▶ Return checkout URL
```

```
SUCCESS FLOW

  └──▶ Customer completes payment
  └──▶ Gateway redirects to Success URL
  └──▶ Gateway asynchronously sends webhook
  └──▶ Verify webhook signature
  └──▶ Persist New Transaction Record
  └──▶ Update payment status
```

```
CANCEL FLOW

  └──▶ Customer cancels payment
  └──▶ Gateway redirects to Cancel URL
  └──▶ Payment remains incomplete
```

---

## Design Decisions

**Why Checkout Sessions instead of Payment Links?**

-> Checkout Sessions provide greater flexibility by allowing structured payment creation with customer information, metadata, success and cancel URLs, and richer payment configuration. This aligns better with an e-commerce payment workflow and simplifies maintaining relationships between users, payments, transactions, and future order records.

**Why Strategy + Adapter + Factory?**

-> Payment providers expose different APIs and SDKs. The combination of Strategy, Adapter, and Factory patterns isolates provider-specific implementations from business logic, making the service easily extensible for additional payment gateways without modifying existing payment workflows.

**Why webhooks?**

-> Client-side redirects are not a reliable source of truth. Webhooks provide server-to-server confirmation directly from the payment gateway, allowing the service to securely verify and persist completed transactions regardless of client behaviour.

### Why an immutable transaction history?

-> Payment gateways emit multiple events throughout a payment's lifecycle (checkout creation, authorization, capture, success, failure, refunds, etc.). Rather than updating a single transaction record, the service persists every significant gateway interaction as a new transaction entry.

This append-only approach preserves the complete lifecycle of every payment, simplifies debugging and reconciliation, provides a detailed audit trail, and prepares the service for future event-driven integrations.

---

## Environment Variables

Sensitive configuration is externalized via environment variables.

| Variable                | Description           |
| ----------------------- | --------------------- |
| `db_url`                | JDBC connection URL   |
| `db_username`           | Database username     |  
| `stripe_api_key`        | Stripe API Key        |
| `stripe_endpoint_secret`| Stripe Webhook Secret |
| `razorpay_key_id`       | Razorpay API ID       |
| `razorpay_key_secret`   | Razorpay Secret Key   |
| `eureka_server_url`     | Eureka Server URL     |

---
<!--
## Getting Started

```bash
# Clone the repository
git clone https://github.com/your-username/payment-service.git

# Configure environment variables

# Run the service
./mvnw spring-boot:run
```

---
-->
## Known Gaps & Roadmap

* Kafka integration for publishing Payment Completed events
* Event-driven integration with Order Service
* Notification Service for receipt generation
* Docker containerization
* Support for additional payment gateways (PayPal, Square, etc.)
