# 📋 Sofkify_BE — Product Backlog

> **GitHub Project: Sofkify Backend** · Table view with columns: Backlog · In Progress · In Review · Done  
> All stories follow **INVEST** (Independent · Negotiable · Valuable · Estimable · Small · Testable).  
> Acceptance criteria use **Gherkin** style (Given / When / Then).

---

## 📊 Project Board — Backlog

| ID | Title | Epic | Size | Status |
|---|---|---|---|---|
| [HU-001](#hu001) | Register New Customer Account | 🔵 user-api-auth | `S` | Backlog |
| [HU-002](#hu002) | Authenticate User via Email and Password | 🔵 user-api-auth | `S` | Backlog |
| [HU-003](#hu003) | Update User Profile Name and Email | 🔵 user-api-auth | `S` | Backlog |
| [HU-004](#hu004) | Promote CLIENTE User to ADMIN Role | 🔵 user-api-auth | `S` | Backlog |
| [HU-005](#hu005) | Soft-Deactivate User Account | 🔵 user-api-auth | `S` | Backlog |
| [HU-006](#hu006) | Add New Product to Catalog | 🟢 product-catalog-stock | `S` | Backlog |
| [HU-007](#hu007) | Browse and Retrieve Products from Catalog | 🟢 product-catalog-stock | `S` | Backlog |
| [HU-008](#hu008) | Filter Product Catalog by Status | 🟢 product-catalog-stock | `S` | Backlog |
| [HU-009](#hu009) | Decrement Product Stock via OrderCreatedEvent | 🟢 product-catalog-stock | `M` | Backlog |
| [HU-010](#hu010) | Add Product Item to Customer Cart | �� cart-management | `M` | Backlog |
| [HU-011](#hu011) | Retrieve Active Cart by Customer or Cart ID | �� cart-management | `S` | Backlog |
| [HU-012](#hu012) | Update Cart Item Quantity | �� cart-management | `S` | Backlog |
| [HU-013](#hu013) | Remove Product Item from Active Cart | �� cart-management | `S` | Backlog |
| [HU-014](#hu014) | Confirm Active Cart After Real-Time Stock Validation | �� cart-management | `M` | Backlog |
| [HU-015](#hu015) | Create Order from Confirmed Cart | 🔴 order-processing-events | `M` | Backlog |
| [HU-016](#hu016) | Advance Order Through Status Lifecycle | 🔴 order-processing-events | `M` | Backlog |
| [HU-017](#hu017) | Query Order History by Customer or Order ID | 🔴 order-processing-events | `S` | Backlog |
| [HU-018](#hu018) | Prevent Duplicate Domain Event Processing via Idempotency Table | 🔴 order-processing-events | `M` | Backlog |
| [HU-019](#hu019) | Enable Structured HTTP Access Logs Across All Services | ⚪ observability-ops | `S` | Backlog |
| [HU-020](#hu020) | Expose Spring Actuator Health Endpoints on All Services | ⚪ observability-ops | `S` | Backlog |
| [HU-021](#hu021) | Standardize Error Responses via GlobalExceptionHandler | ⚪ observability-ops | `S` | Backlog |
| [HU-022](#hu022) | Build Optimized Service Images with Multi-Stage Dockerfiles | 🟠 containerization-local-env | `M` | Backlog |
| [HU-023](#hu023) | Orchestrate Full Backend Stack via Docker Compose | 🟠 containerization-local-env | `M` | Backlog |
| [HU-024](#hu024) | Provide .env.example Template for Local Stack Configuration | 🟠 containerization-local-env | `S` | Backlog |
| [HU-025](#hu025) | Extend Stack with Frontend via Compose Override File | 🟠 containerization-local-env | `M` | Backlog |

---

## 🏗 Epic Overview

### 🔵 User API & Authentication
> Service: **user-service**

- [HU-001] **Register New Customer Account** · Size: `S`
- [HU-002] **Authenticate User via Email and Password** · Size: `S`
- [HU-003] **Update User Profile Name and Email** · Size: `S`
- [HU-004] **Promote CLIENTE User to ADMIN Role** · Size: `S`
- [HU-005] **Soft-Deactivate User Account** · Size: `S`

### 🟢 Product Catalog & Stock
> Service: **product-service**

- [HU-006] **Add New Product to Catalog** · Size: `S`
- [HU-007] **Browse and Retrieve Products from Catalog** · Size: `S`
- [HU-008] **Filter Product Catalog by Status** · Size: `S`
- [HU-009] **Decrement Product Stock via OrderCreatedEvent** · Size: `M`

### �� Cart Management
> Service: **cart-service**

- [HU-010] **Add Product Item to Customer Cart** · Size: `M`
- [HU-011] **Retrieve Active Cart by Customer or Cart ID** · Size: `S`
- [HU-012] **Update Cart Item Quantity** · Size: `S`
- [HU-013] **Remove Product Item from Active Cart** · Size: `S`
- [HU-014] **Confirm Active Cart After Real-Time Stock Validation** · Size: `M`

### 🔴 Order Processing & Events
> Service: **order-service**

- [HU-015] **Create Order from Confirmed Cart** · Size: `M`
- [HU-016] **Advance Order Through Status Lifecycle** · Size: `M`
- [HU-017] **Query Order History by Customer or Order ID** · Size: `S`
- [HU-018] **Prevent Duplicate Domain Event Processing via Idempotency Table** · Size: `M`

### ⚪ Observability & Ops
> Service: **all services**

- [HU-019] **Enable Structured HTTP Access Logs Across All Services** · Size: `S`
- [HU-020] **Expose Spring Actuator Health Endpoints on All Services** · Size: `S`
- [HU-021] **Standardize Error Responses via GlobalExceptionHandler** · Size: `S`

### 🟠 Containerization & Local Env
> Service: **Docker/Compose**

- [HU-022] **Build Optimized Service Images with Multi-Stage Dockerfiles** · Size: `M`
- [HU-023] **Orchestrate Full Backend Stack via Docker Compose** · Size: `M`
- [HU-024] **Provide .env.example Template for Local Stack Configuration** · Size: `S`
- [HU-025] **Extend Stack with Frontend via Compose Override File** · Size: `M`

---

## 📝 Story Details

### HU-001 — Register New Customer Account
<a name="hu001"></a>

**Epic:** 🔵 `user-api-auth` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:user-api-auth` `service:user-service` `type:feature` `layer:api`

**User Story:**  
> As a new visitor, I want to POST my email, password, and name to /api/users, so that I receive a CLIENTE account and can start shopping.

**Description:**  
A new visitor needs to register with email, password, and full name so they can access the platform as a CLIENTE. The system must enforce that all new accounts start with role=CLIENTE and status=ACTIVE, rejecting duplicate emails and invalid payloads.

**Endpoint:** `POST /api/users`  

**Acceptance Criteria:**

- Given a payload with valid email, password ≥ 8 characters, and non-blank name, When POST /api/users is called, Then the response is 201 with a UserResponse body containing a UUID id, role='CLIENTE', and status='ACTIVE'.
- Given a payload with a duplicate email already registered, When POST /api/users is called, Then the response is 409 Conflict and no new user is created.
- Given a payload with an invalid email format (missing '@'), When POST /api/users is called, Then the response is 400 with validation error message 'Email inválido'.
- Given a payload with a password shorter than 8 characters, When POST /api/users is called, Then the response is 400 with validation error 'Password debe tener al menos 8 caracteres'.
- Given a payload with a blank or missing name, When POST /api/users is called, Then the response is 400 with validation error 'Nombre es requerido'.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | No dependency on other stories; POST /api/users is a fully self-contained endpoint with its own PostgreSQL schema (sofkify_users). |
| **N — Negotiable** | Password hashing strategy (currently plain-text, future BCrypt), response shape, and duplicate-email HTTP status code (409 vs 422) are open to team decision. |
| **V — Valuable** | Enables new customers to create accounts, which is the entry point for the entire purchase funnel. |
| **E — Estimable** | Single controller method + domain factory method + JPA persistence; estimated 0.5–1 day. |
| **S — Small** | Scope is limited to one endpoint with input validation and one domain factory method. |
| **T — Testable** | Five distinct pass/fail Gherkin scenarios covering happy path and all validation branches. |

</details>

---

### HU-002 — Authenticate User via Email and Password
<a name="hu002"></a>

**Epic:** 🔵 `user-api-auth` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:user-api-auth` `service:user-service` `type:feature` `layer:api`

**User Story:**  
> As a registered user, I want to POST my credentials to /api/users/auth/login, so that I receive my userId and role to navigate the application appropriately.

**Description:**  
A registered user needs to log in using their email and password to receive their userId and role so the frontend can apply role-based access control (CLIENTE vs ADMIN). The system must return a clear failure message on bad credentials without revealing which field was wrong.

**Endpoint:** `POST /api/users/auth/login`  

**Acceptance Criteria:**

- Given a registered user with correct email and password, When POST /api/users/auth/login, Then the response is 200 with LoginResponse { success=true, userId (non-null UUID), email, name, role='CLIENTE' or 'ADMIN' }.
- Given a registered email with an incorrect password, When POST /api/users/auth/login, Then the response is 401 with LoginResponse { success=false, message='Credenciales inválidas' }.
- Given an email that does not exist in the system, When POST /api/users/auth/login, Then the response is 401 with LoginResponse { success=false, message='Credenciales inválidas' } (no user enumeration).
- Given a payload with a blank email or missing password, When POST /api/users/auth/login, Then the response is 400 with bean validation error details.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Self-contained endpoint; depends only on existing User records, not on other user stories. |
| **N — Negotiable** | JWT issuance, token expiry, and session storage strategy are deferred and negotiable as a separate security story. |
| **V — Valuable** | Authentication is the gateway for all role-guarded operations; without it the platform cannot identify its users. |
| **E — Estimable** | Single controller method + service lookup + credential comparison; estimated 0.5 day. |
| **S — Small** | One endpoint, four AC scenarios, no new domain model changes required. |
| **T — Testable** | Four distinct pass/fail scenarios; can be verified with curl or integration test against real DB. |

</details>

---

### HU-003 — Update User Profile Name and Email
<a name="hu003"></a>

**Epic:** 🔵 `user-api-auth` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:user-api-auth` `service:user-service` `type:feature` `layer:api`

**User Story:**  
> As a registered user, I want to PUT my new name and email to /api/users/{id}, so that my profile information stays current.

**Description:**  
An authenticated user needs to update their display name and/or email address so their account stays accurate. The system must apply domain validation rules (non-blank name, valid email) and refresh the updatedAt timestamp on the User aggregate.

**Endpoint:** `PUT /api/users/{id}`  

**Acceptance Criteria:**

- Given an existing user id, a valid new name, and a valid new email, When PUT /api/users/{id} with UpdateUserRequest, Then the response is 200 with UserResponse containing the updated name and email, and updatedAt > createdAt.
- Given an existing user id but an invalid email format in the request, When PUT /api/users/{id}, Then the response is 400 with validation error 'Email inválido'.
- Given a non-existent user id, When PUT /api/users/{id} with valid payload, Then the response is 404 with 'Usuario no encontrado: {id}'.
- Given an existing user id but a blank name, When PUT /api/users/{id}, Then the response is 400 with 'Nombre es requerido'.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Uses only the User aggregate already created by HU-001; no new dependencies. |
| **N — Negotiable** | Whether email uniqueness is re-validated on update (to avoid conflicts) is negotiable. |
| **V — Valuable** | Allows users to correct typos or change their email, reducing support tickets and improving data quality. |
| **E — Estimable** | One controller method + domain updateProfile method + JPA save; estimated 0.5 day. |
| **S — Small** | Four AC scenarios, one endpoint, one domain method. |
| **T — Testable** | Each AC has a clear HTTP status and response body assertion. |

</details>

---

### HU-004 — Promote CLIENTE User to ADMIN Role
<a name="hu004"></a>

**Epic:** 🔵 `user-api-auth` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:user-api-auth` `service:user-service` `type:feature` `layer:domain-rule`

**User Story:**  
> As an admin operator, I want to POST to /api/users/{id}/promote, so that a CLIENTE user gains ADMIN privileges.

**Description:**  
An administrator needs to elevate a CLIENTE user to ADMIN role so they can manage the product catalog and order operations. The domain enforces that only users with role=CLIENTE can be promoted, preventing double-promotion and preserving role integrity.

**Endpoint:** `POST /api/users/{id}/promote`  

**Acceptance Criteria:**

- Given a user with role=CLIENTE, When POST /api/users/{id}/promote, Then the response is 200 with UserResponse where role='ADMIN' and updatedAt is refreshed.
- Given a user already with role=ADMIN, When POST /api/users/{id}/promote, Then the response is 400 with error message 'Solo clientes pueden ser promovidos a ADMIN'.
- Given a non-existent user id, When POST /api/users/{id}/promote, Then the response is 404 with 'Usuario no encontrado: {id}'.
- Given a newly promoted ADMIN user, When POST /api/users/auth/login with their credentials, Then LoginResponse contains role='ADMIN'.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Operates on the existing User aggregate; no cross-service calls needed. |
| **N — Negotiable** | Whether promotion requires a specific calling user to be ADMIN (authorization) is deferred to a security story. |
| **V — Valuable** | Enables role-based access control differentiation, allowing product/order management by authorized staff. |
| **E — Estimable** | One controller method + domain rule guard; estimated 0.25 day. |
| **S — Small** | Minimal scope: 4 AC scenarios, one domain method, one endpoint. |
| **T — Testable** | All four scenarios have binary pass/fail assertions on HTTP status and role value. |

</details>

---

### HU-005 — Soft-Deactivate User Account
<a name="hu005"></a>

**Epic:** 🔵 `user-api-auth` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:user-api-auth` `service:user-service` `type:feature` `layer:domain-rule`

**User Story:**  
> As an admin, I want to DELETE /api/users/{id}, so that the user account is marked INACTIVE and access is revoked without data loss.

**Description:**  
An administrator needs to deactivate a user account (set status=INACTIVE) without permanently deleting the record, so audit history is preserved and the user can no longer log in or use the platform. The operation returns 204 with no body.

**Endpoint:** `DELETE /api/users/{id}`  

**Acceptance Criteria:**

- Given an existing active user, When DELETE /api/users/{id}, Then the response is 204 with no body, and the user record persists in the database with status=INACTIVE.
- Given a non-existent user id, When DELETE /api/users/{id}, Then the response is 404 with 'Usuario no encontrado: {id}'.
- Given a deactivated user, When GET /api/users/{id}, Then the response is 200 with UserResponse where status='INACTIVE'.
- Given a deactivated user, When POST /api/users/auth/login with valid credentials, Then the response is 401 (deactivated accounts cannot authenticate).

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Self-contained operation on the User aggregate; no external service dependencies. |
| **N — Negotiable** | Whether a BLOCKED status (vs INACTIVE) is used, or whether deactivated users get a grace-period login, is negotiable. |
| **V — Valuable** | Protects the platform from unauthorized access by former employees or compromised accounts without losing audit history. |
| **E — Estimable** | One controller method + domain deactivate() call; estimated 0.25 day. |
| **S — Small** | Four AC scenarios, one endpoint, one domain method, minimal logic. |
| **T — Testable** | Each scenario has a deterministic HTTP status and inspectable database state. |

</details>

---

### HU-006 — Add New Product to Catalog
<a name="hu006"></a>

**Epic:** 🟢 `product-catalog-stock` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:product-catalog-stock` `service:product-service` `type:feature` `layer:api`

**User Story:**  
> As a catalog manager, I want to POST a new product to /api/products, so that it appears in the catalog with status=ACTIVE and can be added to customer carts.

**Description:**  
A catalog manager needs to create new products with name, description, SKU, price, and initial stock so customers can discover and purchase them. The system enforces price > 0, stock ≥ 0, unique non-blank SKU, and sets the initial ProductStatus to ACTIVE.

**Endpoint:** `POST /api/products`  

**Acceptance Criteria:**

- Given a valid payload with name, non-blank SKU, price=9.99, and stock=100, When POST /api/products, Then the response is 201 with ProductResponse containing a UUID productId and status='ACTIVE'.
- Given a payload with price=0.00 or price < 0, When POST /api/products, Then the response is 400 with 'Product price must be greater than zero'.
- Given a payload with stock=-1, When POST /api/products, Then the response is 400 with 'Product stock cannot be negative'.
- Given a payload with a blank or missing SKU, When POST /api/products, Then the response is 400 with 'Product SKU is required'.
- Given a payload with name exceeding 200 characters, When POST /api/products, Then the response is 400 with 'Product name must not exceed 200 characters'.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | No dependency on other services at creation time; the product-service owns its own PostgreSQL schema (sofkify_products_bd). |
| **N — Negotiable** | SKU uniqueness enforcement, description max length, and initial status assignment are negotiable. |
| **V — Valuable** | Without products, no cart or order flow is possible; this is foundational to the e-commerce catalog. |
| **E — Estimable** | One controller method + domain factory + JPA persistence; estimated 0.5 day. |
| **S — Small** | Five AC scenarios, one endpoint, one domain factory method. |
| **T — Testable** | Each scenario maps to a specific HTTP status and error message assertion. |

</details>

---

### HU-007 — Browse and Retrieve Products from Catalog
<a name="hu007"></a>

**Epic:** 🟢 `product-catalog-stock` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:product-catalog-stock` `service:product-service` `type:feature` `layer:api`

**User Story:**  
> As a customer, I want to GET /api/products and /api/products/{id}, so that I can browse the catalog and view detailed product information before adding items to my cart.

**Description:**  
A customer needs to view all available products and look up individual product details by ID so they can make informed purchasing decisions. The endpoints return the full ProductResponse including current stock and status.


**Acceptance Criteria:**

- Given multiple products in the database, When GET /api/products, Then the response is 200 with a JSON array of ProductResponse objects each containing productId, name, sku, price, stock, and status.
- Given a valid UUID of an existing product, When GET /api/products/{id}, Then the response is 200 with the exact ProductResponse for that product.
- Given a UUID that does not exist in the database, When GET /api/products/{id}, Then the response is 404.
- Given no products in the database, When GET /api/products, Then the response is 200 with an empty JSON array [].

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Read-only operations; no cross-service dependencies; can be tested in isolation. |
| **N — Negotiable** | Pagination, sorting, search by name/SKU, and field projection are negotiable future enhancements. |
| **V — Valuable** | Enables customers to discover products; required by cart-service's ProductServiceAdapter for resolving product details. |
| **E — Estimable** | Two read-only controller methods + use case calls; estimated 0.5 day. |
| **S — Small** | Four AC scenarios across two endpoints, no write operations. |
| **T — Testable** | Response body structure and HTTP status fully verifiable via integration tests or curl. |

</details>

---

### HU-008 — Filter Product Catalog by Status
<a name="hu008"></a>

**Epic:** 🟢 `product-catalog-stock` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:product-catalog-stock` `service:product-service` `type:feature` `layer:api`

**User Story:**  
> As a catalog manager, I want to GET /api/products?status=ACTIVE, so that I see only the products available for purchase rather than the full unfiltered catalog.

**Description:**  
A catalog manager needs to filter products by their lifecycle status (ACTIVE, INACTIVE, or DELETED) so they can manage visibility and inventory efficiently. The query parameter ?status=VALUE is case-insensitive via toUpperCase() normalization.

**Endpoint:** `GET /api/products?status={STATUS}`  

**Acceptance Criteria:**

- Given a catalog with ACTIVE and INACTIVE products, When GET /api/products?status=ACTIVE, Then the response is 200 with only products where status='ACTIVE'.
- Given a catalog with ACTIVE and INACTIVE products, When GET /api/products?status=inactive (lowercase), Then the response is 200 with only INACTIVE products (case normalization applied).
- Given no products with DELETED status, When GET /api/products?status=DELETED, Then the response is 200 with an empty array [].
- Given the status param is omitted, When GET /api/products, Then the response is 200 with all products regardless of status.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Pure query enhancement on an existing endpoint; does not affect HU-006 or HU-007. |
| **N — Negotiable** | Whether invalid status values return 400 or empty list, and whether multiple status filters are supported, is negotiable. |
| **V — Valuable** | Reduces noise for both frontend catalog pages and backend inventory management dashboards. |
| **E — Estimable** | One query param branch in existing controller; estimated 0.25 day. |
| **S — Small** | Four AC scenarios, zero new domain model changes. |
| **T — Testable** | Response filter correctness verifiable by count/status assertions in integration tests. |

</details>

---

### HU-009 — Decrement Product Stock via OrderCreatedEvent
<a name="hu009"></a>

**Epic:** 🟢 `product-catalog-stock` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:product-catalog-stock` `service:product-service` `type:feature` `layer:messaging` `pattern:event-consumer`

**User Story:**  
> As the product-service, I want to consume OrderCreatedEvent from RabbitMQ, so that product stock is automatically decremented for each item in the new order.

**Description:**  
The product-service needs to consume OrderCreatedEvent messages from the RabbitMQ queue 'product.stock.decrement.queue' and decrement each product's stock by the ordered quantity, so inventory stays accurate after purchases without synchronous coupling to order-service.


**Acceptance Criteria:**

- Given a valid OrderCreatedEvent JSON on queue 'product.stock.decrement.queue' with orderId and items [{productId, quantity=3}], When consumed, Then the product's stock is decremented by 3 and the updated value is persisted.
- Given an event where the requested quantity exceeds available stock, When consumed, Then an InvalidProductStockException is thrown, stock remains unchanged, and the error is logged.
- Given a malformed JSON payload on the queue, When consumed, Then a JsonProcessingException is caught, an error is logged with the raw message, and a RuntimeException is thrown (enabling DLQ routing).
- Given the same orderId event delivered twice (RabbitMQ redelivery), When the second event is consumed, Then stock is NOT decremented again (idempotency guard via processed events).

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Async consumer; does not require order-service to be running during product-service development; testable with mock queue message. |
| **N — Negotiable** | DLQ routing strategy, retry backoff policy, and partial-order rollback on insufficient stock are negotiable. |
| **V — Valuable** | Maintains real-time inventory accuracy; prevents overselling after confirmed orders. |
| **E — Estimable** | Listener method + use case + domain method; estimated 1–1.5 days including RabbitMQ config. |
| **S — Small** | Scoped to one queue listener and one domain method; no new API surface. |
| **T — Testable** | Four distinct Gherkin scenarios; verifiable via spring-rabbit-test or integration test with embedded RabbitMQ. |

</details>

---

### HU-010 — Add Product Item to Customer Cart
<a name="hu010"></a>

**Epic:** �� `cart-management` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:cart-management` `service:cart-service` `type:feature` `layer:api` `pattern:cross-service`

**User Story:**  
> As a customer, I want to POST /api/carts/items with X-Customer-Id and {productId, quantity}, so that the product is added (or accumulated) in my active cart.

**Description:**  
A customer needs to add a product (by productId and quantity) to their active cart using the X-Customer-Id header for identification, so they can accumulate items before checkout. If no cart exists for the customer, one is created automatically. If the same product is added again, quantities are accumulated on the existing CartItem.

**Endpoint:** `POST /api/carts/items`  

**Acceptance Criteria:**

- Given a valid X-Customer-Id (ACTIVE user) and a valid productId (ACTIVE product), When POST /api/carts/items with quantity=2, Then the response is 201 with CartResponse containing the item with productName, productPrice, quantity=2, and subtotal=price*2.
- Given the same productId added a second time with quantity=3 to an existing cart, When POST /api/carts/items, Then the response is 201 with the CartItem showing accumulated quantity (original + 3) and an updated total.
- Given quantity=0 in the request, When POST /api/carts/items, Then the response is 400 with 'Quantity must be greater than zero'.
- Given an X-Customer-Id for an INACTIVE or non-existent user, When POST /api/carts/items, Then the response is 400.
- Given a productId for a non-existent or INACTIVE product, When POST /api/carts/items, Then the response is 400.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Can be developed independently; UserServiceAdapter and ProductServiceAdapter can be mocked in unit tests. |
| **N — Negotiable** | Whether cart is per-session or persistent, maximum items per cart, and product price snapshot strategy are negotiable. |
| **V — Valuable** | Central user action in the purchase funnel; without it no order can be created. |
| **E — Estimable** | Controller + use case + domain logic + two HTTP adapter calls; estimated 1.5 days. |
| **S — Small** | Scoped to one endpoint with clear domain invariants; cross-service calls are abstracted behind ports. |
| **T — Testable** | Five Gherkin scenarios with deterministic HTTP status and body assertions. |

</details>

---

### HU-011 — Retrieve Active Cart by Customer or Cart ID
<a name="hu011"></a>

**Epic:** �� `cart-management` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:cart-management` `service:cart-service` `type:feature` `layer:api`

**User Story:**  
> As a customer, I want to GET /api/carts (with X-Customer-Id) or GET /api/carts/{cartId}, so that I can see all my cart items and the running total.

**Description:**  
A customer needs to view their current cart contents (items, quantities, subtotals, total) by either their customerId or a specific cartId, so they can review items before confirming the purchase.


**Acceptance Criteria:**

- Given a customer with an active cart containing 2 items, When GET /api/carts with their X-Customer-Id, Then the response is 200 with CartResponse showing both items with correct subtotals and total.
- Given a known cartId UUID, When GET /api/carts/{cartId}, Then the response is 200 with the CartResponse for that specific cart.
- Given a customer with no cart, When GET /api/carts with their X-Customer-Id, Then the response is 404.
- Given a non-existent cartId UUID, When GET /api/carts/{cartId}, Then the response is 404.
- Given a CONFIRMED cart, When GET /api/carts/{cartId}, Then the response is 200 with status='CONFIRMED' in the body.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Read-only operation; does not alter cart state; no cross-service calls at query time. |
| **N — Negotiable** | Whether to include historical CONFIRMED carts in the response, or limit to ACTIVE only, is negotiable. |
| **V — Valuable** | Required for the frontend cart page to display current state and drive user decisions. |
| **E — Estimable** | Two read-only controller methods + use case queries; estimated 0.5 day. |
| **S — Small** | Five AC scenarios, two endpoints, zero domain mutations. |
| **T — Testable** | Response body assertions on item count, subtotals, total, and status. |

</details>

---

### HU-012 — Update Cart Item Quantity
<a name="hu012"></a>

**Epic:** �� `cart-management` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:cart-management` `service:cart-service` `type:feature` `layer:api`

**User Story:**  
> As a customer, I want to PUT /api/carts/items/{cartItemId} with a new quantity, so that the item count and subtotal in my cart are updated.

**Description:**  
A customer needs to change the quantity of an item already in their active cart (e.g., from 2 to 5) so they can adjust their order without removing and re-adding products. The domain must reject updates on CONFIRMED carts.

**Endpoint:** `PUT /api/carts/items/{cartItemId}`  

**Acceptance Criteria:**

- Given an ACTIVE cart with an item (qty=2), When PUT /api/carts/items/{cartItemId} with quantity=5, Then the response is 200 with CartResponse showing qty=5, updated subtotal, and updated total.
- Given quantity=0 in the request body, When PUT /api/carts/items/{cartItemId}, Then the response is 400 with a validation error.
- Given a cartItemId that does not exist in the customer's cart, When PUT /api/carts/items/{cartItemId}, Then the response is 400 or 404.
- Given a CONFIRMED cart, When PUT /api/carts/items/{cartItemId}, Then the response is 400 (modifications to confirmed carts are not allowed).

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Operates on an existing CartItem; no cross-service calls; can be tested with a pre-seeded cart. |
| **N — Negotiable** | Whether a quantity update should re-validate stock (from product-service) is negotiable. |
| **V — Valuable** | Improves UX by allowing in-place quantity adjustment without cart item removal/re-add cycle. |
| **E — Estimable** | One controller method + domain quantity update; estimated 0.5 day. |
| **S — Small** | Four AC scenarios, one endpoint, minimal domain changes. |
| **T — Testable** | Quantity and subtotal values in CartResponse provide clear numeric assertions. |

</details>

---

### HU-013 — Remove Product Item from Active Cart
<a name="hu013"></a>

**Epic:** �� `cart-management` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:cart-management` `service:cart-service` `type:feature` `layer:api`

**User Story:**  
> As a customer, I want to DELETE /api/carts/items/{cartItemId}, so that the unwanted product is removed from my cart and the total is recalculated.

**Description:**  
A customer needs to remove a specific item from their active cart so they can refine their selection before checkout. The system must prevent removal from CONFIRMED carts and return the updated cart state after deletion.

**Endpoint:** `DELETE /api/carts/items/{cartItemId}`  

**Acceptance Criteria:**

- Given an ACTIVE cart with 2 items, When DELETE /api/carts/items/{cartItemId} for one item, Then the response is 200 with CartResponse containing only the remaining 1 item and an updated total.
- Given a cartItemId that does not exist in the customer's cart, When DELETE /api/carts/items/{cartItemId}, Then the response is 400 or 404.
- Given a CONFIRMED cart, When DELETE /api/carts/items/{cartItemId}, Then the response is 400 (confirmed carts are immutable).
- Given an ACTIVE cart with exactly 1 item, When that item is deleted, Then the response is 200 with CartResponse showing an empty items array and total=0.00.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Operates only on the local Cart aggregate; no cross-service calls needed. |
| **N — Negotiable** | Whether removing the last item auto-deletes the cart or keeps it empty is negotiable. |
| **V — Valuable** | Essential UX feature; customers must be able to change their minds before checkout. |
| **E — Estimable** | One controller method + domain remove + JPA save; estimated 0.5 day. |
| **S — Small** | Four AC scenarios, one endpoint, one domain mutation. |
| **T — Testable** | Item count and total in CartResponse provide numeric pass/fail assertions. |

</details>

---

### HU-014 — Confirm Active Cart After Real-Time Stock Validation
<a name="hu014"></a>

**Epic:** �� `cart-management` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:cart-management` `service:cart-service` `type:feature` `layer:domain-rule` `pattern:cross-service` `pattern:domain-event`

**User Story:**  
> As a customer, I want to POST /api/carts/{cartId}/confirm, so that my cart is locked for ordering after confirming all items are in stock.

**Description:**  
A customer needs to confirm their active cart before an order is created. The system validates stock availability for ALL items atomically via StockValidationPort (backed by product-service), transitions the cart from ACTIVE to CONFIRMED, and publishes a CartConfirmedEvent. Partial validation failures reject the entire confirmation.

**Endpoint:** `POST /api/carts/{cartId}/confirm`  

**Acceptance Criteria:**

- Given an ACTIVE cart with all items having sufficient stock in product-service, When POST /api/carts/{cartId}/confirm, Then the response is 200 with ConfirmCartResponse and the cart status transitions to CONFIRMED.
- Given a cart where one product has qty=10 but only 5 in stock, When POST /api/carts/{cartId}/confirm, Then the response is 400 with InsufficientStockException referencing the specific productId, and the cart remains ACTIVE.
- Given a cart already in CONFIRMED state, When POST /api/carts/{cartId}/confirm again, Then the response is 400 with CartAlreadyConfirmedException.
- Given a non-existent cartId, When POST /api/carts/{cartId}/confirm, Then the response is 404.
- Given successful confirmation, When CartConfirmedEvent is published, Then the event contains eventId (UUID), cartId, customerId, all cart items with quantities and prices, and a non-null totalAmount.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Domain logic is encapsulated in Cart.confirm(); StockValidationPort can be mocked in unit tests; integration test against running product-service is a separate test scope. |
| **N — Negotiable** | Whether to validate ALL items and return all failures at once (vs fail-fast), and whether stock reservation is held, are negotiable. |
| **V — Valuable** | Prevents orders from being created for out-of-stock items; protects inventory integrity end-to-end. |
| **E — Estimable** | Use case orchestration + domain confirm + stock adapter integration; estimated 1.5 days. |
| **S — Small** | Five AC scenarios; bounded to one endpoint and one domain method transition. |
| **T — Testable** | Status transitions (ACTIVE→CONFIRMED) and event publication are verifiable; error scenarios have specific exception types. |

</details>

---

### HU-015 — Create Order from Confirmed Cart
<a name="hu015"></a>

**Epic:** 🔴 `order-processing-events` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:order-processing-events` `service:order-service` `type:feature` `layer:api` `pattern:domain-event` `pattern:idempotency`

**User Story:**  
> As a customer, I want to POST /api/orders/from-cart/{cartId}, so that my confirmed cart is converted into a PENDING order with an assigned UUID that I can track.

**Description:**  
A customer needs to create an order from a confirmed cart (POST /api/orders/from-cart/{cartId}) that captures all item details, prices, and total with BigDecimal precision, starts in PENDING status, and publishes an OrderCreatedEvent to RabbitMQ. The operation must be idempotent — the same cartId cannot create two orders.

**Endpoint:** `POST /api/orders/from-cart/{cartId}`  

**Acceptance Criteria:**

- Given a confirmed cartId with 2 items (qty=2 @$10.00, qty=3 @$5.00), When POST /api/orders/from-cart/{cartId}, Then the response is 201 with OrderResponse containing status='PENDING', totalAmount=35.00, and both OrderItemResponse entries with correct subtotals.
- Given the same confirmed cartId submitted a second time, When POST /api/orders/from-cart/{cartId}, Then the response is 409 Conflict and no second order is created.
- Given a non-existent cartId, When POST /api/orders/from-cart/{cartId}, Then the response is 404.
- Given a cartId that is not in CONFIRMED state (still ACTIVE), When POST /api/orders/from-cart/{cartId}, Then the response is 400.
- Given a successful order creation, When the order is created, Then an OrderCreatedEvent is published to RabbitMQ exchange 'order.exchange' with routing key 'order.created' containing orderId, customerId, cartId, all items with quantities, and totalAmount.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Requires a confirmed cart from cart-service, but the CartServicePort can be mocked; order-service owns its own DB schema. |
| **N — Negotiable** | Outbox pattern implementation (vs direct publish), payment workflow triggering, and cart cleanup post-order are negotiable. |
| **V — Valuable** | Core business outcome — converting a cart into a trackable order is the primary revenue-generating action. |
| **E — Estimable** | Use case + domain factory + RabbitMQ publish + idempotency guard; estimated 1.5 days. |
| **S — Small** | Five AC scenarios bounded to one endpoint and one domain factory method. |
| **T — Testable** | totalAmount arithmetic, HTTP status codes, and RabbitMQ message assertions are all verifiable. |

</details>

---

### HU-016 — Advance Order Through Status Lifecycle
<a name="hu016"></a>

**Epic:** 🔴 `order-processing-events` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:order-processing-events` `service:order-service` `type:feature` `layer:domain-rule`

**User Story:**  
> As an operations team member, I want to PUT /api/orders/{orderId}/status, so that I can advance or cancel an order through its lifecycle stages.

**Description:**  
An operations team member needs to transition orders through their defined status lifecycle (PENDING→CONFIRMED→SHIPPED→DELIVERED, with CANCELLED/FAILED as terminal states) via the API. The domain enforces valid transitions and rejects illegal state changes.

**Endpoint:** `PUT /api/orders/{orderId}/status`  

**Acceptance Criteria:**

- Given an order in PENDING status, When PUT /api/orders/{orderId}/status with { status: 'CONFIRMED' }, Then the response is 200 with OrderResponse where status='CONFIRMED'.
- Given an order in CONFIRMED status, When PUT /api/orders/{orderId}/status with { status: 'SHIPPED' }, Then the response is 200 with status='SHIPPED'.
- Given an order in CANCELLED status (terminal), When PUT /api/orders/{orderId}/status with { status: 'PENDING' }, Then the response is 400 with error 'Cannot change status of cancelled order'.
- Given an invalid transition (PENDING → SHIPPED, skipping CONFIRMED), When PUT /api/orders/{orderId}/status, Then the response is 400 with a transition error.
- Given a non-existent orderId, When PUT /api/orders/{orderId}/status, Then the response is 404.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Self-contained within order-service; uses only the Order aggregate and its status state machine. |
| **N — Negotiable** | Whether SHIPPED→DELIVERED requires a logistics webhook, and whether FAILED orders can be retried, are negotiable. |
| **V — Valuable** | Allows operations teams to track and manage orders through fulfillment, which is critical for customer communication and business reporting. |
| **E — Estimable** | One controller method + domain state machine validation; estimated 1 day including state machine coverage. |
| **S — Small** | Five AC scenarios bounded to one endpoint and one domain method. |
| **T — Testable** | Status transitions and rejection messages are fully deterministic and assertable. |

</details>

---

### HU-017 — Query Order History by Customer or Order ID
<a name="hu017"></a>

**Epic:** 🔴 `order-processing-events` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:order-processing-events` `service:order-service` `type:feature` `layer:api`

**User Story:**  
> As a customer, I want to GET /api/orders/{orderId} and GET /api/orders/customer/{customerId}, so that I can view my order details and full purchase history.

**Description:**  
A customer and operations team need to retrieve individual orders by ID and list all orders for a given customer, so they can track fulfillment status and audit purchase history. Both endpoints return the full OrderResponse including all items.


**Acceptance Criteria:**

- Given a valid orderId, When GET /api/orders/{orderId}, Then the response is 200 with the full OrderResponse including all items, totalAmount, and current status.
- Given a customerId with 3 orders in various statuses, When GET /api/orders/customer/{customerId}, Then the response is 200 with a list of 3 OrderResponse objects.
- Given a customerId with no orders, When GET /api/orders/customer/{customerId}, Then the response is 200 with an empty JSON array [].
- Given a non-existent orderId, When GET /api/orders/{orderId}, Then the response is 404.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Pure read operations on the order-service DB; no cross-service calls at query time. |
| **N — Negotiable** | Pagination, date filtering, and sorting by createdAt are negotiable future enhancements. |
| **V — Valuable** | Enables customer self-service order tracking and provides operations teams with order lookup capabilities. |
| **E — Estimable** | Two read-only controller methods + use case queries; estimated 0.5 day. |
| **S — Small** | Four AC scenarios across two endpoints; zero domain mutations. |
| **T — Testable** | List size, order status, and totalAmount are all assertable in integration tests. |

</details>

---

### HU-018 — Prevent Duplicate Domain Event Processing via Idempotency Table
<a name="hu018"></a>

**Epic:** 🔴 `order-processing-events` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:order-processing-events` `service:order-service` `service:product-service` `type:feature` `layer:infrastructure` `pattern:idempotency`

**User Story:**  
> As the platform, I need IdempotencyService to check and record processed eventIds, so that re-delivered RabbitMQ messages are safely ignored without corrupting business state.

**Description:**  
The system needs to track processed domain events in a ProcessedEvent table so that RabbitMQ message redeliveries (due to network issues or service restarts) do not cause duplicate stock decrements, duplicate order creations, or other duplicate side effects.


**Acceptance Criteria:**

- Given a new eventId never seen before, When isEventProcessed(eventId) is called, Then it returns false and processing proceeds normally.
- Given a successfully processed event, When markEventProcessed(eventId, 'OrderCreated', aggregateId) is called and then isEventProcessed(eventId) is checked, Then it returns true.
- Given the same orderId delivered twice to product-service consumer, When the second message is received, Then isEventProcessed returns true, stock decrement is skipped, and the duplicate is logged.
- Given two concurrent threads receiving the same eventId simultaneously, When both attempt to call markEventProcessed, Then only one succeeds (unique DB constraint) and the other either throws or returns gracefully, preventing double processing.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Idempotency layer is a cross-cutting infrastructure concern; can be implemented independently of specific business flows. |
| **N — Negotiable** | TTL for processed event records, cleanup jobs, and whether idempotency uses DB vs Redis are negotiable. |
| **V — Valuable** | Prevents critical business bugs (double stock decrements, duplicate orders) caused by the inherent at-least-once delivery guarantee of RabbitMQ. |
| **E — Estimable** | Domain entity + port interface + JPA repository + unique constraint; estimated 1 day. |
| **S — Small** | Four AC scenarios; scoped to the ProcessedEvent entity and IdempotencyService port. |
| **T — Testable** | Boolean return values of isEventProcessed() and DB state of processed_events table provide clear pass/fail assertions. |

</details>

---

### HU-019 — Enable Structured HTTP Access Logs Across All Services
<a name="hu019"></a>

**Epic:** ⚪ `observability-ops` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:observability-ops` `service:all` `type:ops` `layer:infrastructure`

**User Story:**  
> As a developer, I want structured Tomcat access logs enabled on all four services, so that I can trace the full HTTP request path from frontend to each microservice in docker compose logs.

**Description:**  
Developers and operators need to trace every inbound HTTP request (method, path, status, duration) across all four microservices so they can diagnose integration issues and verify end-to-end request flows during development. Tomcat access logging is already configured in user-service and order-service but missing in product-service and cart-service.


**Acceptance Criteria:**

- Given all four services running in Docker, When any HTTP request is received by user-service, Then docker compose logs user-service shows an entry with remote host, timestamp, method, path, HTTP status, and response time in milliseconds.
- Given product-service with access logging added to application.yml, When GET /api/products is called, Then docker compose logs product-service shows the access log entry with pattern '%h %l %u %t "%r" %s %b %D'.
- Given cart-service with access logging added to application.yml, When POST /api/carts/items is called, Then docker compose logs cart-service shows the request entry.
- Given a full purchase flow (login → add item → confirm cart → create order), When all steps complete, Then each HTTP hop is traceable in the respective service's log output with status codes and durations.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Pure configuration change to application.yml files; no code or domain changes required. |
| **N — Negotiable** | Log format pattern, log file rotation, and structured JSON logging (vs plaintext) are negotiable. |
| **V — Valuable** | Reduces debugging time significantly during integration; enables request correlation across services. |
| **E — Estimable** | Two application.yml edits; estimated 1 hour. |
| **S — Small** | Minimal scope: configuration-only change to two files. |
| **T — Testable** | Log output verifiable by running docker compose logs and grepping for expected request patterns. |

</details>

---

### HU-020 — Expose Spring Actuator Health Endpoints on All Services
<a name="hu020"></a>

**Epic:** ⚪ `observability-ops` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:observability-ops` `service:all` `type:ops` `layer:infrastructure`

**User Story:**  
> As an operator, I want GET /actuator/health on each microservice to report DB and broker connectivity, so that I know the real health status of every service in real time.

**Description:**  
Operations teams and Docker healthchecks need a standardized /actuator/health endpoint on all four microservices so the platform can detect unhealthy services (database down, RabbitMQ disconnected) and enable automated recovery. This capability is currently absent from all services (identified in HANDOVER_REPORT.md technical debt).


**Acceptance Criteria:**

- Given spring-boot-starter-actuator added to all four build.gradle files, When GET /actuator/health on any service, Then the response is 200 with { status: 'UP', components: { db: { status: 'UP' } } }.
- Given the PostgreSQL container for a service is stopped, When GET /actuator/health on that service, Then the response is 503 with { status: 'DOWN', components: { db: { status: 'DOWN', details: {...} } } }.
- Given RabbitMQ is unavailable, When GET /actuator/health on product-service or order-service, Then the response reflects { components: { rabbit: { status: 'DOWN' } } }.
- Given all services exposing /actuator/health, When docker-compose.yml healthcheck uses curl -f http://localhost:{port}/actuator/health, Then services reach 'healthy' state in docker ps output.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Dependency addition and config change per service; no cross-service dependencies. |
| **N — Negotiable** | Which health indicators to expose (liveness vs readiness), metrics endpoint exposure, and security of actuator endpoints are negotiable. |
| **V — Valuable** | Enables Docker healthchecks, Kubernetes probes, and load balancer health gates; reduces MTTR on production incidents. |
| **E — Estimable** | Four build.gradle edits + four application.yml additions; estimated 2 hours. |
| **S — Small** | Configuration-only with a new Gradle dependency; no business logic touched. |
| **T — Testable** | HTTP status code (200 vs 503) and JSON body components are directly assertable. |

</details>

---

### HU-021 — Standardize Error Responses via GlobalExceptionHandler
<a name="hu021"></a>

**Epic:** ⚪ `observability-ops` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:observability-ops` `service:all` `type:ops` `layer:api`

**User Story:**  
> As an API consumer, I want all error responses to follow a consistent JSON structure with status code, error type, and message, so that I can handle failures uniformly in the frontend.

**Description:**  
API consumers need consistent, machine-readable error responses across all microservices so that frontend clients and integration tests can reliably parse and display error information. All four services have GlobalExceptionHandler, but response structures and covered exception types should be audited for consistency.


**Acceptance Criteria:**

- Given a UserNotFoundException is thrown in user-service, When GET /api/users/{nonExistentId}, Then the response is 404 with a JSON body containing 'status': 404, 'error': 'Not Found', and a descriptive 'message'.
- Given a CartAlreadyConfirmedException is thrown in cart-service, When POST /api/carts/{cartId}/confirm on a confirmed cart, Then the response is 400 with consistent JSON error body matching the standard error structure.
- Given a bean validation failure (e.g., blank name), When any POST/PUT endpoint is called, Then the response is 400 with a JSON body listing each field violation with its message.
- Given an unhandled RuntimeException escapes to Tomcat, When any endpoint throws unexpectedly, Then the response is 500 with a JSON error body (not an HTML error page).

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Cross-cutting concern; can be standardized per service independently in any order. |
| **N — Negotiable** | Error body field names, inclusion of stack traces in dev mode, and i18n of messages are negotiable. |
| **V — Valuable** | Reduces frontend error-handling complexity and debugging time; prevents confusing HTML error responses. |
| **E — Estimable** | Audit 4 GlobalExceptionHandlers + harmonize response structure; estimated 0.5 day. |
| **S — Small** | Scoped to @ControllerAdvice classes; no domain or persistence changes. |
| **T — Testable** | Four AC scenarios with specific HTTP status and JSON body structure assertions. |

</details>

---

### HU-022 — Build Optimized Service Images with Multi-Stage Dockerfiles
<a name="hu022"></a>

**Epic:** 🟠 `containerization-local-env` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:containerization-local-env` `service:all` `type:devops` `layer:infrastructure`

**User Story:**  
> As a DevOps engineer, I want each service to have a multi-stage Dockerfile (gradle:8-jdk{17|21} build + eclipse-temurin:{17|21}-jre-jammy runtime), so that production images are minimal, secure, and reproducible.

**Description:**  
The development team needs reproducible, minimal Docker images for all four microservices using multi-stage builds (Gradle build stage + JRE-only runtime stage) so that container images contain only the compiled JAR and runtime, not the full JDK or Gradle cache.


**Acceptance Criteria:**

- Given docker build run against user-service Dockerfile, When the build completes, Then the final image is based on eclipse-temurin:21-jre-jammy and contains only /app/app.jar with no Gradle wrapper or source files.
- Given docker build run against product-service, cart-service, or order-service Dockerfiles, When the build completes, Then each image is based on eclipse-temurin:17-jre-jammy and starts the JAR on its configured port (8081/8083/8082 respectively).
- Given docker run on any service image with custom JAVA_OPTS, When the container starts, Then the JVM applies the memory flags from ENV JAVA_OPTS='-Xmx512m -Xms256m'.
- Given docker inspect on any runtime image, When checking image layers, Then no Gradle or JDK build tools are present (build artifacts stripped in multi-stage).

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Each Dockerfile is independently buildable; no cross-service dependencies at build time. |
| **N — Negotiable** | Base image versions (eclipse-temurin variants, Alpine vs Jammy), Gradle caching strategies, and JAVA_OPTS tuning are negotiable. |
| **V — Valuable** | Smaller, reproducible images reduce build times, security surface, and registry storage costs. |
| **E — Estimable** | Four Dockerfiles already exist; story is about verification, documentation, and gap-filling; estimated 0.5–1 day. |
| **S — Small** | Bounded to Dockerfile layer; no application code changes required. |
| **T — Testable** | docker build success, image layer inspection, and service startup on correct port are all verifiable. |

</details>

---

### HU-023 — Orchestrate Full Backend Stack via Docker Compose
<a name="hu023"></a>

**Epic:** 🟠 `containerization-local-env` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:containerization-local-env` `service:infrastructure` `type:devops` `layer:orchestration`

**User Story:**  
> As a developer, I want to run 'docker compose up -d' and have all 9 backend containers start in the correct order with health-gated dependencies, so the platform is ready for end-to-end testing.

**Description:**  
The development team needs a single docker-compose.yml to bring up the entire backend (4 PostgreSQL databases, RabbitMQ, and 4 microservices) on an isolated bridge network with dependency ordering, health-gated startup, and automatic service restarts, so any developer can run the platform locally with one command.


**Acceptance Criteria:**

- Given a populated .env file, When 'docker compose up -d' is executed, Then all 9 containers start and reach Running state with correct dependency ordering (PostgreSQL and RabbitMQ first, then microservices).
- Given all services running, When 'curl http://localhost:8080/api/users', 'curl http://localhost:8081/api/products', 'curl http://localhost:8082/api/orders', 'curl http://localhost:8083/api/carts' are executed, Then each returns an HTTP response (2xx or 4xx, not connection refused).
- Given a microservice container that fails to connect to its DB on first attempt, When restart: on-failure policy is active, Then the container automatically retries and eventually reaches Running state once the DB is healthy.
- Given 'docker compose down -v', When executed, Then all 9 containers stop and all named volumes (postgres-*-data, rabbitmq-data) are removed, leaving a clean state for the next 'up'.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | docker-compose.yml is a standalone infrastructure file; no application code dependencies. |
| **N — Negotiable** | Volume mount strategies, network subnet ranges, resource limits (CPU/memory), and whether to use profiles are negotiable. |
| **V — Valuable** | Eliminates 'works on my machine' problems; onboards new developers in minutes rather than hours. |
| **E — Estimable** | Compose file already exists; story is verification, healthcheck gap-filling, and documentation; estimated 0.5 day. |
| **S — Small** | Bounded to one compose file; operational scope only. |
| **T — Testable** | Container status (docker ps), port reachability (curl), and volume cleanup are directly verifiable. |

</details>

---

### HU-024 — Provide .env.example Template for Local Stack Configuration
<a name="hu024"></a>

**Epic:** 🟠 `containerization-local-env` · **Size:** `S` · **Status:** `Backlog`  
**Labels:** `epic:containerization-local-env` `service:infrastructure` `type:devops` `layer:configuration`

**User Story:**  
> As a new developer, I want to copy .env.example to .env without modifications and immediately run 'docker compose up -d', so that the full stack starts without any manual environment variable configuration.

**Description:**  
New team members need a .env.example template file with all required environment variables and default values so they can configure the full stack in one copy-paste step. The .env file must be gitignored while .env.example remains version-controlled.


**Acceptance Criteria:**

- Given .env.example in the repository root, When 'cp .env.example .env && docker compose up -d' is run on a clean machine, Then all services start without 'variable not set' errors.
- Given the .gitignore file, When 'git status' is run with an existing .env, Then .env is listed as untracked/ignored, and .env.example is listed as tracked.
- Given .env loaded by docker-compose.yml, When ${DB_USERNAME:-sofkify} is interpolated, Then each service connects to PostgreSQL using the 'sofkify' credentials defined in .env.example.
- Given VITE_* variables in .env loaded by docker-compose.integration.yml, When the frontend container starts, Then the Vite dev server exposes VITE_USERS_API_URL=http://localhost:8080/api to the browser.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Pure configuration artifact; no code changes; can be done in isolation. |
| **N — Negotiable** | Whether to use Docker secrets instead of .env for production, and whether to include commented-out optional variables, is negotiable. |
| **V — Valuable** | Reduces developer onboarding time from 30+ minutes to under 5 minutes; eliminates misconfiguration issues. |
| **E — Estimable** | File already exists; story is about verification, documentation, and gap audit; estimated 1 hour. |
| **S — Small** | Scoped to one file and its gitignore entry. |
| **T — Testable** | Docker startup success without env errors and git status output are direct pass/fail assertions. |

</details>

---

### HU-025 — Extend Stack with Frontend via Compose Override File
<a name="hu025"></a>

**Epic:** 🟠 `containerization-local-env` · **Size:** `M` · **Status:** `Backlog`  
**Labels:** `epic:containerization-local-env` `service:infrastructure` `service:frontend` `type:devops` `layer:orchestration`

**User Story:**  
> As a full-stack developer, I want to run 'docker compose -f docker-compose.yml -f docker-compose.integration.yml up -d --build', so that both backend and frontend start together and I can verify the complete purchase flow in a browser.

**Description:**  
The development team needs a docker-compose.integration.yml override that adds the Vite frontend to the stack alongside the four backend services, so that full end-to-end testing (browser → frontend → microservices) can be run with a single compose command. The frontend is sourced from a sibling repository at ../sofkify-fe.


**Acceptance Criteria:**

- Given sofkify-fe repository cloned at ../sofkify-fe relative to this repository root, When 'docker compose -f docker-compose.yml -f docker-compose.integration.yml up -d' is executed, Then a total of 10 containers start including the frontend on port 5173.
- Given all 4 backend microservices running, When the frontend container starts (depends_on: service_started), Then http://localhost:5173 returns the Vite-served frontend application.
- Given VITE_USERS_API_URL=http://localhost:8080/api injected into the frontend container, When the browser opens http://localhost:5173 and triggers a login action, Then the frontend calls http://localhost:8080/api/users/auth/login successfully.
- Given 'docker compose -f docker-compose.yml -f docker-compose.integration.yml ps', When all services are up, Then 10 containers appear: postgres-users, postgres-products, postgres-carts, postgres-orders, rabbitmq, user-service, product-service, cart-service, order-service, sofkify-frontend.

<details>
<summary>🔍 INVEST Validation</summary>

| Criterion | Validation |
|---|---|
| **I — Independent** | Override file extends the base compose without modifying it; depends on HU-023 (base compose) being in place. |
| **N — Negotiable** | Whether to build from Dockerfile vs use pre-built frontend image, Nginx vs Vite dev server, and whether to run tests in CI using this compose are negotiable. |
| **V — Valuable** | Enables full E2E testing of the complete purchase flow including the frontend without any manual local setup. |
| **E — Estimable** | Compose override file already exists; story is about verification, documentation, and .env alignment; estimated 0.5 day. |
| **S — Small** | One compose override file; no application code changes. |
| **T — Testable** | Container count, port reachability at :5173, and browser-visible frontend are directly verifiable. |

</details>

---

## 🏷 Label Taxonomy

| Label | Description |
|---|---|
| `epic:user-api-auth` | Stories for user-service (auth, registration, roles) |
| `epic:product-catalog-stock` | Stories for product-service (catalog, stock management) |
| `epic:cart-management` | Stories for cart-service (cart lifecycle, validation) |
| `epic:order-processing-events` | Stories for order-service (order lifecycle, events) |
| `epic:observability-ops` | Cross-cutting: health, logs, error handling |
| `epic:containerization-local-env` | Docker, Docker Compose, local env setup |
| `service:user-service` | Affects user-service |
| `service:product-service` | Affects product-service |
| `service:cart-service` | Affects cart-service |
| `service:order-service` | Affects order-service |
| `type:feature` | New feature implementation |
| `layer:api` | REST controller / API adapter layer |
| `layer:domain-rule` | Business logic in the domain model |
| `layer:messaging` | RabbitMQ consumers or publishers |
| `layer:infra` | Infrastructure / DevOps configuration |
| `pattern:cross-service` | Involves HTTP calls between microservices |
| `pattern:event-consumer` | RabbitMQ event consumer implementation |
| `pattern:domain-event` | Domain event publication |
| `pattern:idempotency` | Idempotency guard for at-least-once delivery |
| `size:S` | Small story (~0.25–0.5 day) |
| `size:M` | Medium story (~1–1.5 days) |
| `size:L` | Large story (> 2 days, consider splitting) |

---

## 📈 Summary

| Metric | Value |
|---|---|
| Total Stories | **25** |
| Size S (small, ~0.5d) | **16** |
| Size M (medium, ~1.5d) | **9** |
| Size L (large) | **0** |
| Epics | **6** |
| Services Covered | **4** (user · product · cart · order) |
| Architecture Patterns | Hexagonal · DDD · Event-Driven |

---

*Generated from `docs_IA/invest_user_stories.json` · Compliant with INVEST principles · Stories sized for ≤1 sprint delivery*