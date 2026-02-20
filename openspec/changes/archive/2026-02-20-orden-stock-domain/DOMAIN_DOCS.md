# Domain Documentation - Order & Stock Synchronization

## Domain Model

### Aggregates

**Cart** (`cart-service`)
- Root: `Cart`
- Entities: `CartItem`
- Status: `CartStatus` (ACTIVE, CONFIRMED, EXPIRED)
- Key Operation: `confirm(StockValidationPort)` - validates stock and transitions to CONFIRMED
- Events: `CartConfirmedEvent`

**Order** (`order-service`)  
- Root: `Order`
- Entities: `OrderItem`
- Status: `OrderStatus` (PENDING, CONFIRMED, CANCELLED)
- Factory: `Order.createFromCart(cartId, customerId, items)` - creates order with auto-generated ID
- Events: `OrderCreatedEvent`

### Value Objects

- **Money**: Immutable record with `amount` (BigDecimal) and `currency` (String)
- **Quantity**: Immutable integer wrapper with validation (must be > 0)

### Domain Events

**CartConfirmedEvent** (cart-service)
```java
- UUID eventId (auto-generated)
- UUID cartId
- UUID customerId  
- List<CartItem> items
- BigDecimal totalAmount
- LocalDateTime occurredAt
```

**OrderCreatedEvent** (order-service)
```java
- UUID eventId (auto-generated)
- UUID orderId
- UUID cartId
- UUID customerId
- BigDecimal totalAmount
- List<OrderItem> items
- LocalDateTime occurredAt
```

## Ports & Adapters

### Input Ports (Use Cases)

**cart-service:**
- `ConfirmCartUseCase` → `ConfirmCartService`
  - Input: `ConfirmCartRequest(cartId, customerId)`
  - Output: `ConfirmCartResponse(cartId, success, message)`

**order-service:**
- `CreateOrderUseCase` → `CreateOrderService`
  - Input: `CreateOrderRequest(cartId, customerId)`
  - Output: `CreateOrderResponse(orderId, cartId, success, message)`

### Output Ports (Infrastructure Dependencies)

**cart-service:**
- `CartRepository`: findById, save, findByCustomerId, deleteById
- `StockValidationPort`: isStockAvailable, getAvailableStock, reserveStock, returnStock
- `EventPublisher`: publish(DomainEvent)

**order-service:**
- `OrderRepositoryPort`: save, findById, findByCustomerId, existsByCartId
- `EventPublisherPort`: publish(OrderCreatedEvent)
- `IdempotencyService`: isEventProcessed, markEventProcessed

## Event Flow

1. Cart confirmed → `CartConfirmedEvent` published to RabbitMQ
2. Order service consumes event → Creates Order → `OrderCreatedEvent` published
3. Product service consumes `OrderCreatedEvent` → Decrements stock

### Idempotency

- Orders: `existsByCartId()` prevents duplicate orders for same cart
- Events: `IdempotencyService` + `ProcessedEvent` entity tracks `eventId` to prevent duplicate processing

## Testing Patterns

### Domain Tests
- Pure unit tests with no mocks
- Example: `CartTest.shouldConfirmCartSuccessfully()`

### Service Tests  
- Mock ports (repositories, external services)
- Example: `ConfirmCartServiceTest` mocks `CartRepository`, `StockValidationPort`, `EventPublisher`

### Test Data Builders
Use constructors directly:
```java
Cart cart = new Cart(UUID.randomUUID(), customerId);
cart.addItem(productId, "Product Name", new BigDecimal("10.00"), 2);
```

## Gherkin Coverage

All scenarios from `specs/*.md` are covered:
- ✅ Cart confirmation with stock validation
- ✅ Order creation from confirmed cart
- ✅ Idempotency (duplicate cart prevention)
- ✅ Domain exception handling (InsufficientStock, CartAlreadyConfirmed)

## Handoff to Infrastructure Team

**Required Implementations:**

1. **JPA Entities** (map domain aggregates to DB)
   - `CartJpaEntity`, `OrderJpaEntity` with version fields for optimistic locking

2. **Repository Adapters**
   - Implement `CartRepository` → `CartJpaRepository`  
   - Implement `OrderRepositoryPort` → `OrderJpaRepository`

3. **RabbitMQ Publishers**
   - `RabbitMQEventPublisher` (cart-service) - already done
   - `RabbitMQEventPublisherAdapter` (order-service) - already done

4. **HTTP Clients**
   - `ProductServiceClient` implements `StockValidationPort` (RestTemplate/WebClient)

5. **Idempotency Persistence**
   - `ProcessedEventRepository` (JPA) with `processed_events` table

6. **REST Controllers**
   - `CartRestController` → `/api/v1/carts/{id}/confirm`
   - `OrderRestController` → `/api/v1/orders`

---

**Architecture Notes:**
- Domain layer is framework-agnostic (no Spring/JPA annotations)
- All dependencies point inward (Dependency Inversion Principle)
- Events are generated in domain, published in application layer
- Idempotency handled at both application (existsByCartId) and domain (ProcessedEvent) levels
