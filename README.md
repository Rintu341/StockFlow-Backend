# StockFlow

A multi-tenant inventory and order management API built with Spring Boot — designed to demonstrate real-world backend patterns: JWT authentication, role-based access control (RBAC), and SaaS-style multi-tenancy on a single shared database.

Think of it as a scaled-down version of how platforms like Shopify isolate one business's data from another, while every business shares the same underlying application and infrastructure.

## Overview

StockFlow lets multiple independent businesses ("tenants") manage their own product inventory and order fulfillment workflow, all through the same API and database — with strict data isolation between tenants, and three distinct user roles per business.

**Core capabilities:**
- **Multi-tenancy** — every business's data (users, products, orders) is scoped to its own tenant. One tenant can never see or access another's data, even though everything lives in the same tables.
- **JWT authentication** — stateless, token-based login. No server-side session storage.
- **Role-based access control** — three roles per business (`ADMIN`, `MANAGER`, `STAFF`), each with different permissions enforced at the API layer via `@PreAuthorize`.
- **Inventory management** — full CRUD on products, with stock levels tracked per tenant.
- **Order workflow** — a real state machine: `PENDING → APPROVED → PACKED → SHIPPED` (or `REJECTED`), with stock automatically deducted on approval.

## Tech Stack

| Layer | Technology | Why |
|---|---|---|
| Language / Framework | Java 21, Spring Boot 4 | Industry-standard for enterprise backend APIs |
| Security | Spring Security, JWT (jjwt) | Stateless auth suited to multi-client (web/mobile/desktop) consumption |
| Persistence | Spring Data JPA, PostgreSQL | Relational fit for inherently relational data (tenants → users/products/orders) |
| Validation | Jakarta Bean Validation | Declarative request validation, consistent error contracts |
| Build | Maven | — |

## Architecture

The project follows a standard layered architecture:

```
controller/   → HTTP request/response handling only
service/      → Business logic, tenant scoping, workflow rules
repository/   → Spring Data JPA interfaces
entity/       → Database models
dto/          → Request/response contracts, decoupled from entities
security/     → UserPrincipal (Spring Security adapter), JWT filter
exception/    → Custom exceptions + centralized global handler
config/       → Security configuration
```

**Design decisions worth noting:**
- Tenant scoping is enforced once, centrally, in each service's data-access methods — never left to individual controllers to remember.
- A resource belonging to another tenant returns `404 Not Found`, identical to a resource that doesn't exist at all — this avoids leaking the existence of other tenants' data.
- DTOs separate the API contract from the database schema, so internal fields never leak into responses and the persistence model can evolve independently.

## Data Model

- **Tenant** — the isolation boundary. Every business gets one row here.
- **User** — belongs to exactly one Tenant, has exactly one Role (`ADMIN`, `MANAGER`, `STAFF`).
- **Product** — belongs to exactly one Tenant. Tracks name, category, price, and stock quantity.
- **Order** — belongs to exactly one Tenant. Header record holding status and timestamp.
- **OrderItem** — the join table between Order and Product, capturing quantity per line item.

## Authentication & Authorization

1. A business registers via `/api/auth/register-tenant`, creating its Tenant and first Admin user in one step.
2. Additional users (Manager/Staff) join an existing tenant via `/api/auth/register`, using the tenant's ID.
3. Login returns a signed JWT (`/api/auth/login`).
4. Every subsequent request carries the token in `Authorization: Bearer <token>`.
5. A filter validates the token's signature and expiry on each request, and loads the current user's tenant and role from the database — a request never trusts tenant or role information from the client itself.

**Role permissions:**

| Action | Admin | Manager | Staff |
|---|---|---|---|
| View products/orders | ✅ | ✅ | ✅ |
| Create/update products | ✅ | ✅ | ❌ |
| Delete products | ✅ | ❌ | ❌ |
| Approve/reject orders | ✅ | ✅ | ❌ |
| Create orders, mark packed/shipped | ✅ | ✅ | ✅ |

## Order Workflow

```
PENDING → APPROVED → PACKED → SHIPPED
    ↓
REJECTED
```

Stock is deducted at the **approval** step, not at order creation — an order only commits against inventory once a Manager or Admin has confirmed it can actually be fulfilled. Each transition is validated: an order can't be shipped before it's packed, or approved twice.

## Getting Started

### Prerequisites
- Java 21+
- PostgreSQL
- Maven

### Setup

1. Clone the repo and create a database:
   ```sql
   CREATE DATABASE stockflow;
   ```

2. Set the required environment variables:
   ```
   JWT_SECRET=<a long, random string>
   JWT_EXPIRATION=86400000
   ```

3. Configure `src/main/resources/application.properties` with your local PostgreSQL credentials.

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Reference

| Method | Endpoint | Auth Required | Roles |
|---|---|---|---|
| POST | `/api/auth/register-tenant` | No | — |
| POST | `/api/auth/register` | No | — |
| POST | `/api/auth/login` | No | — |
| GET | `/api/products` | Yes | Any |
| POST | `/api/products` | Yes | Admin, Manager |
| PUT | `/api/products/{id}` | Yes | Admin, Manager |
| DELETE | `/api/products/{id}` | Yes | Admin |
| POST | `/api/orders` | Yes | Any |
| GET | `/api/orders` | Yes | Any |
| PUT | `/api/orders/{id}/approve` | Yes | Admin, Manager |
| PUT | `/api/orders/{id}/reject` | Yes | Admin, Manager |
| PUT | `/api/orders/{id}/pack` | Yes | Any |
| PUT | `/api/orders/{id}/ship` | Yes | Any |

## Roadmap

- [ ] Payment integration (Stripe) — a `PAYMENT_PENDING`/`PAID` state between order approval and fulfillment
- [ ] Pagination and search/filter on product and order listings
- [ ] Companion Compose Multiplatform frontend (Android, iOS, Desktop)

## License

MIT
