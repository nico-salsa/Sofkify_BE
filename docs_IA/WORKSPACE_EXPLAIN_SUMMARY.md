# Workspace Explain Summary - Sofkify Backend

## Scope
- Repository: `Sofkify_BE`
- Architecture style: microservices + hexagonal architecture (ports and adapters)
- Main stack: Java (17/21), Spring Boot, PostgreSQL, RabbitMQ, Gradle

## Services and Responsibilities
- `user-service` (`:8080`)
  - User registration and login
  - User profile and role management
- `product-service` (`:8081`)
  - Product catalog and stock control
  - Consumes order-created events to process stock updates
- `order-service` (`:8082`)
  - Creates orders from carts
  - Publishes order events
- `cart-service` (`:8083`)
  - Active cart lifecycle
  - Validates users/products through service integrations

## Integration Boundaries
- Synchronous integration: REST calls between services when direct validation is needed.
- Asynchronous integration: RabbitMQ events for order/stock flow.
  - Exchange: `order.exchange`
  - Routing key: `order.created`
  - Queue: `product.stock.decrement.queue`

## Layering Rules
- Dependency direction MUST be:
  - `infrastructure -> application -> domain`
- `domain` is framework-agnostic:
  - no Spring/JPA/web/messaging dependencies
- Use cases and orchestration belong to `application`
- Controllers/adapters in `infrastructure` stay thin and orchestration-free

## Domain Guardrails
- User must be registered to operate
- Product price MUST be greater than zero
- Product stock MUST NOT be negative
- Only active products with stock can be added to cart
- Order creation triggers asynchronous stock processing

## Contract and Ownership Constraints
- This context setup does not change runtime API behavior.
- This context setup does not change RabbitMQ contracts.
- Each service owns its own database/schema and MUST NOT be accessed directly by other services.
- No cross-service table coupling is allowed.
