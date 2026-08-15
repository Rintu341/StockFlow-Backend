# StockFlow

A multi-tenant inventory and order management API built with Spring Boot — designed to demonstrate real-world backend patterns: JWT authentication, role-based access control (RBAC), invite-based onboarding, and SaaS-style multi-tenancy on a single shared database.

Think of it as a scaled-down version of how platforms like Shopify isolate one business's data from another, while every business shares the same underlying application and infrastructure.

## Overview

StockFlow lets multiple independent businesses ("tenants") manage their own product inventory and order fulfillment workflow, all through the same API and database — with strict data isolation between tenants, and three distinct user roles per business.

**Core capabilities:**
- **Multi-tenancy** — every business's data (users, products, orders) is scoped to its own tenant. One tenant can never see or access another's data, even though everything lives in the same tables.
- **JWT authentication** — stateless, token-based login by email. No server-side session storage.
- **Invite-only onboarding** — only an Admin can add Managers or Staff to their business; there is no open self-registration into an existing tenant. New accounts receive a system-generated temporary password and are required to change it before accessing anything else.
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
security/     → UserPrincipal (Spring Security adapter)
config/       → Security config, JwtFilter, PasswordChangeFilter
exception/    → Custom exceptions + centralized global handler
```

**Design decisions worth noting:**
- Tenant scoping is enforced once, centrally, in each service's data-access methods — never left to individual controllers to remember.
- A resource belonging to another tenant returns `404 Not Found`, identical to a resource that doesn't exist at all — this avoids leaking the existence of other tenants' data.
- DTOs separate the API contract from the database schema, so internal fields never leak into responses and the persistence model can evolve independently.
- Email is the unique account identifier; username is a non-unique display name. This separates "how you log in" from "what people see," matching how most real systems distinguish identity from display.
- Cross-cutting concerns (JWT validation, the forced password-change gate, global exception handling) are implemented as filters/advice rather than checks repeated inside individual controllers or services.

## Data Model

- **Tenant** — the isolation boundary. Every business gets one row here.
- **User** — belongs to exactly one Tenant, has exactly one Role (`ADMIN`, `MANAGER`, `STAFF`). Unique by email, not username.
- **Product** — belongs to exactly one Tenant. Tracks name, category, price, and stock quantity.
- **Order** — belongs to exactly one Tenant. Header record holding status and timestamp.
- **OrderItem** — the join table between Order and Product, capturing quantity per line item.

## Authentication & Authorization

1. A business registers via `/api/auth/register-tenant`, creating its Tenant and first Admin user in one step. The founding Admin sets their own password.
2. Additional users (Manager/Staff) can only be added by an existing Admin, via `/api/auth/register` — this endpoint is itself protected by `@PreAuthorize("hasRole('ADMIN')")`, and the new user's tenant is taken from the requesting Admin's own token, never from the request body.
3. New accounts are issued a system-generated temporary password (returned directly in the response for now; email delivery is a planned next step — see Roadmap).
4. A dedicated filter (`PasswordChangeFilter`) blocks every endpoint except `/api/auth/change-password` until a user with a temporary password has set their own.
5. Login (`/api/auth/login`) is by **email**, not username, and returns a signed JWT.
6. Every subsequent request carries the token in `Authorization: Bearer <token>`.
7. `JwtFilter` validates the token's signature and expiry on each request, and loads the current user's tenant and role from the database — a request never trusts tenant or role information from the client itself.

**Role permissions:**

| Action | Admin | Manager | Staff |
|---|---|---|---|
| View products/orders | ✅ | ✅ | ✅ |
| Create/update products | ✅ | ✅ | ❌ |
| Delete products | ✅ | ❌ | ❌ |
| Invite Manager/Staff accounts | ✅ | ❌ | ❌ |
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

Endpoints are not yet versioned (e.g. `/api/v1/`) — see Roadmap. Introducing a version prefix now is a planned, low-cost change that will let future breaking changes ship as `/api/v2/` without affecting existing clients.

| Method | Endpoint | Auth Required | Roles |
|---|---|---|---|
| POST | `/api/auth/register-tenant` | No | — |
| POST | `/api/auth/register` | Yes | Admin |
| POST | `/api/auth/login` | No | — |
| PUT | `/api/auth/change-password` | Yes | Any |
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

- [ ] API versioning (`/api/v1/` path prefix) to allow future breaking changes without disrupting existing clients
- [ ] Email delivery for invite-based onboarding (currently the temporary password is returned directly in the API response)
- [ ] Payment integration (Stripe) — a `PAYMENT_PENDING`/`PAID` state between order approval and fulfillment
- [ ] Pagination and search/filter on product and order listings
- [ ] Companion Compose Multiplatform frontend (Android, iOS, Desktop)

## License

MIT