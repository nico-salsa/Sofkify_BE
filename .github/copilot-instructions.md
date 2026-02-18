# Copilot Instructions - Sofkify Backend

## Project Context
- This repository implements an e-commerce backend as independent microservices:
  - `user-service` (port 8080)
  - `product-service` (port 8081)
  - `order-service` (port 8082)
  - `cart-service` (port 8083)
- Architecture must follow Hexagonal + DDD tactical patterns.

## Mandatory Architecture Rules
- Keep dependency direction: `infrastructure -> application -> domain`.
- `domain` must stay framework-agnostic:
  - No Spring annotations.
  - No JPA annotations.
  - No HTTP, RabbitMQ, or persistence details.
- `application` orchestrates use cases and ports:
  - Use explicit `Port In` and `Port Out`.
  - Coordinate aggregates here, not in controllers.
- `infrastructure` contains adapters only:
  - REST controllers, persistence adapters, messaging adapters, config.

## API and Integration Conventions
- Keep REST endpoints under `/api/...`.
- Use DTOs for request/response in infrastructure layer.
- Do not expose persistence entities directly in controllers.
- RabbitMQ contracts in current system include:
  - Exchange: `order.exchange`
  - Routing key: `order.created`
  - Queue: `product.stock.decrement.queue`

## Business Rules to Preserve
- Users must be registered to operate.
- Product price must be greater than 0.
- Product stock cannot be negative.
- Only active products with stock can be added to cart.
- One active cart per user.
- Orders are created from carts and drive asynchronous stock update.

## Implementation Guidance
- Prefer small, explicit use cases over large service classes.
- Keep controllers thin: validate/map/call use case/return response.
- Keep mappers explicit between domain <-> entity <-> DTO.
- Add or update tests when behavior changes.

## Runtime and Tooling Notes
- `user-service` requires Java 21 toolchain.
- Other services use Java 17 toolchain.
- Databases are service-specific (`sofkify_users`, `sofkify_products_bd`, `sofkify_cars_bd`, `sofkify_orders_bd`).

## Out of Scope
- Do not introduce payment, shipping, or invoicing logic unless explicitly requested.
- Do not add service discovery/config server patterns unless explicitly requested.

