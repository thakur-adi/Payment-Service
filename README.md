## Payment Service

The service provides a unified payment abstraction over multiple payment gateways using Strategy, Adapter, and Factory design patterns. It supports secure payment processing through Stripe Checkout Sessions and Razorpay, webhook-based transaction verification, transaction persistence, card management, and a clean layered architecture. It is designed to explore backend payment processing patterns commonly used in modern distributed systems while following clean code and low-level design principles.

> **Note:** This service is part of a larger E-Commerce Microservices project.

---

## Highlights

* **Multi-gateway architecture** — Supports both Stripe and Razorpay behind a common abstraction using Strategy, Adapter, and Factory patterns
* **Webhook-driven transaction lifecycle** — Payment gateways asynchronously confirm transactions via webhooks, ensuring reliable payment status updates
* **Checkout Session integration** — Uses Stripe Checkout Sessions to support structured payment creation with customer details, metadata, success URLs, and cancel URLs
* **Transaction persistence** — Every completed payment is stored with gateway metadata and associated user information for auditing and future processing
* **Card management** — Supports storing user payment methods and card metadata through dedicated card endpoints
* **LLD-compliant design** — Business logic is isolated from third-party SDKs through clean Low-Level Design principles
* **Secrets externalized** — Payment gateway credentials and database configuration are loaded from environment variables

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
         ├── Persist transaction
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

### Transaction Management

* Persist successful payment transactions
* Maintain user-to-transaction relationships
* Store gateway-specific payment references
* Transaction history for auditing and future reconciliation

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
| `POST`   | `/payment-link`                   | Create a payment session|
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
  └──▶ Persist transaction
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

-> Client-side redirects are not a reliable source of truth. Webhooks provide server-to-server confirmation directly from the payment gateway, allowing the service to securely verify and persist completed transactions regardless of client behavior.

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

---

## Getting Started

```bash
# Clone the repository
git clone https://github.com/your-username/payment-service.git

# Configure environment variables

# Run the service
./mvnw spring-boot:run
```

---

## Known Gaps & Roadmap

* Kafka integration for publishing Payment Completed events
* Event-driven integration with Order Service
* Notification Service for receipt generation
* Docker containerization
* Support for additional payment gateways (PayPal, Square, etc.)
