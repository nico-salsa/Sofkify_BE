# Proposal: orden-stock-domain

## Why

El proceso de confirmación de carrito y creación de órdenes carece de validación de stock centralizada en el dominio y de publicación confiable de eventos. Esto causa inconsistencias entre el inventario real y las órdenes creadas, afectando la fiabilidad del e-commerce. Se requiere implementar la lógica de dominio para validar stock, crear órdenes y emitir eventos de forma idempotente, alineado con arquitectura hexagonal.

## What Changes

- **Nuevo caso de uso `ConfirmCartUseCase`**: Valida stock disponible, transiciona cart a CONFIRMED, crea orden PENDING y dispara evento `OrderCreatedEvent`.
- **Agregado `Order`**: Modelo de dominio con estado, items y reglas de negocio.
- **Domain Event `OrderCreatedEvent`**: Evento inmutable que representa la creación de una orden.
- **Idempotencia en dominio**: Mecanismo para evitar procesamiento duplicado de eventos.
- **Value Objects**: `OrderItem`, `Money` para precios, `Quantity` para cantidades.
- **Excepciones de dominio**: `InsufficientStockException`, `CartAlreadyConfirmedException`.

## Capabilities

### New Capabilities

- `confirm-cart`: Caso de uso para confirmar un cart activo, validar stock y transicionar su estado.
- `create-order`: Lógica de dominio para crear orden a partir de cart confirmado con items y totales.
- `order-events`: Publicación de `OrderCreatedEvent` desde el dominio hacia puertos de salida.
- `stock-validation`: Puerto de salida para consultar disponibilidad de stock antes de confirmar.
- `domain-idempotency`: Mecanismo de idempotencia para procesamiento de eventos en dominio.

### Modified Capabilities

_(No hay specs existentes que modificar)_

## Impact

### Código afectado

| Servicio | Paquete | Cambios |
|----------|---------|---------|
| `cart-service` | `domain.model` | Agregado `Cart` → método `confirm()`, validaciones |
| `cart-service` | `domain.ports.in` | Nuevo `ConfirmCartUseCase` |
| `cart-service` | `domain.ports.out` | Nuevo `StockValidationPort`, `OrderEventPublisher` |
| `order-service` | `domain.model` | Nuevo agregado `Order`, entidad `OrderItem` |
| `order-service` | `domain.event` | Nuevo `OrderCreatedEvent` |
| `order-service` | `domain.exception` | `InsufficientStockException`, `CartAlreadyConfirmedException` |

### APIs impactadas

- `POST /api/v1/carts/{cartId}/confirm` → endpoint REST que invoca el caso de uso

### Dependencias

- Comunicación síncrona: `cart-service` → `product-service` (validación stock)
- Comunicación asíncrona: `OrderCreatedEvent` via RabbitMQ (exchange: `order.exchange`)

### Sistemas afectados

- PostgreSQL: `sofkify_carts_bd`, `sofkify_orders_bd`
- RabbitMQ: exchange `order.exchange`, routing key `order.created`

---

## Criterios de Aceptación (Gherkin)

### CA-01: Happy path - Stock disponible

```gherkin
Feature: Confirmación de carrito con stock disponible

  Scenario: Confirmar cart exitosamente cuando hay stock
    Given un cart ACTIVO con id "cart-123" perteneciente al cliente "customer-456"
    And el cart contiene los siguientes items:
      | productId   | productName | quantity | price  |
      | prod-001    | Laptop      | 2        | 999.99 |
      | prod-002    | Mouse       | 5        | 29.99  |
    And el producto "prod-001" tiene stock disponible >= 2
    And el producto "prod-002" tiene stock disponible >= 5
    When el usuario confirma el cart "cart-123"
    Then se crea una orden con estado PENDING
    And la orden contiene los mismos items del cart
    And el cart "cart-123" queda en estado CONFIRMED
    And se publica un evento "OrderCreatedEvent" con los items de la orden
```

### CA-02: Error - Stock insuficiente

```gherkin
Feature: Rechazo por stock insuficiente

  Scenario: Rechazar confirmación cuando no hay stock suficiente
    Given un cart ACTIVO con id "cart-789" 
    And el cart contiene un item:
      | productId   | productName | quantity | price  |
      | prod-003    | Monitor     | 10       | 299.99 |
    And el producto "prod-003" tiene stock disponible = 3
    When el usuario intenta confirmar el cart "cart-789"
    Then la operación falla con código 409 CONFLICT
    And el mensaje de error es "Stock not available for product: prod-003"
    And no se crea ninguna orden
    And no se publica ningún evento
    And el cart "cart-789" permanece en estado ACTIVO
```

### CA-03: Borde - Idempotencia de eventos

```gherkin
Feature: Idempotencia en procesamiento de eventos

  Scenario: Procesar evento duplicado sin efectos secundarios
    Given una orden existente con id "order-111"
    And el evento "OrderCreatedEvent" para "order-111" ya fue procesado
    When se recibe nuevamente el evento "OrderCreatedEvent" para "order-111"
    Then el evento se ignora sin errores
    And el stock no se decrementa nuevamente
    And se registra log indicando "Evento duplicado ignorado: order-111"
```

### CA-04: Integración - Fallo de broker

```gherkin
Feature: Resiliencia ante fallo de RabbitMQ

  Scenario: Manejar fallo de publicación de evento
    Given un cart ACTIVO "cart-555" con stock válido
    And el broker RabbitMQ no está disponible
    When el usuario confirma el cart "cart-555"
    Then la orden se crea con estado PENDING
    And el cart queda en estado CONFIRMED
    And el evento "OrderCreatedEvent" queda pendiente de publicación
    And se programa reintento con backoff exponencial
    And se registra métrica "order.event.publish.retry"
```

---

## Agentes sugeridos por fase

| Fase | Artefacto | Agente recomendado | Propósito |
|------|-----------|-------------------|-----------|
| Actual | `proposal.md` | **iris-requirements** | Transformar requerimientos en prompts técnicos |
| Siguiente | `specs/*.md` | **architect.agent** | Definir contratos y especificaciones formales |
| Siguiente | `design.md` | **architect.agent** | Decisiones arquitectónicas hexagonal/DDD |
| Implementación | Dominio | **tdd_BE_agent** | TDD RED/GREEN/REFACTOR en dominio |
| Validación | Tests | **qa.agent** | Verificar cobertura y escenarios Gherkin |
| E2E | Integración | **playwright mcp relay** | Validar flujo API end-to-end |
