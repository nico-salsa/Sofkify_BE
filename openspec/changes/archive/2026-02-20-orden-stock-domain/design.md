# Design: orden-stock-domain

## Context

### Background
Sistema e-commerce **Sofkify_BE** con 4 microservicios (user, product, cart, order) implementando arquitectura hexagonal y DDD táctico. Actualmente carece de validación de stock centralizada durante confirmación de carritos y publicación confiable de eventos de dominio.

### Current State
- `cart-service`: Gestiona carritos sin validación de stock al confirmar
- `order-service`: Sin lógica de creación desde cart confirmado  
- `product-service`: Stock disponible pero no consultado en tiempo real
- Comunicación: REST síncrona + RabbitMQ asíncrona (exchange: `order.exchange`)

### Constraints
- **Arquitectura hexagonal**: Domain libre de dependencias, Infrastructure implementa adapters
- **Java 17**: cart/order/product services; Java 21: user-service  
- **PostgreSQL**: BD independiente por microservicio
- **RabbitMQ**: Sin docker-compose, usar testcontainers para pruebas
- **SOLID + DDD**: Agregados, eventos de dominio, ubiquitous language

### Stakeholders
- **Humano A** (este diseño): Dominio/aplicación + pruebas unitarias TDD
- **Humano B**: Infrastructure adapters (REST/RabbitMQ/DB) + integración  
- **Humano C**: QA/E2E + validación OpenSpec

## Goals / Non-Goals

### Goals
- **Consistencia transaccional**: Confirmar cart → crear orden → publicar evento como unidad atómica
- **Validación de stock en tiempo real**: Consulta síncrona a product-service antes de confirmar
- **Idempotencia**: Evitar procesamiento duplicado de eventos `OrderCreatedEvent`
- **Domain-driven design**: Agregados `Cart`/`Order` con reglas de negocio encapsuladas
- **Testabilidad**: Cobertura ≥80% en domain, mocks para puertos de salida

### Non-Goals
- **Reserva temporal de stock**: Stock se valida en momento de confirmación, no se reserva  
- **Rollback distribuido**: No implementar 2PC, usar eventual consistency
- **UI/Frontend**: Solo backend APIs, sin cambios en clientes
- **Autentificación/Autorización**: Asumir usuario ya autenticado
- **Métricas avanzadas**: Solo logs básicos, sin dashboard custom

## Decisions

### D1: Arquitectura de agregados distribuidos

**Decisión**: `Cart` (cart-service) y `Order` (order-service) como agregados separados, comunicados via eventos de dominio.

**Alternativas consideradas**:
- **A1**: `Cart` y `Order` en mismo servicio → Rechazado: viola bounded context independence
- **A2**: Compartir base de datos → Rechazado: antipatrón microservices  

**Rationale**: Mantiene autonomía de servicios y permite evolución independiente. `Cart` se confirma localmente, dispara evento, `Order` se crea reactivamente.

**Implementación**:
```java
// cart-service/domain/model/Cart.java  
public class Cart {
    public void confirm(StockValidationPort stockValidator) {
        validateStock(stockValidator);
        this.status = CartStatus.CONFIRMED;
        // Publicar evento via puerto de salida
        domainEvents.add(new CartConfirmedEvent(this.id, this.items));
    }
}
```

### D2: Validación de stock síncrona vs asíncrona

**Decisión**: Validación **síncrona** durante confirmación de cart via HTTP a product-service.

**Alternativas consideradas**:
- **A1**: Validación asíncrona vía eventos → Rechazado: UX pobre, usuario no sabe si falla
- **A2**: Cache local de stock → Rechazado: riesgo de inconsistencia alta

**Rationale**: Usuario necesita feedback inmediato sobre disponibilidad. Fallo rápido mejora UX.

**Implementación**:
```java
// cart-service/domain/ports/out/StockValidationPort.java
public interface StockValidationPort {
    boolean isStockAvailable(UUID productId, int requestedQuantity);
}

// cart-service/infrastructure/adapters/out/http/ProductServiceClient.java
// Implementa StockValidationPort con RestTemplate/WebClient
```

### D3: Idempotencia de eventos de dominio

**Decisión**: Idempotencia por `eventId` (UUID) + `orderId`, almacenado en tabla `processed_events` por servicio.

**Alternativas consideradas**:
- **A1**: Sin idempotencia → Rechazado: riesgo duplicación stock
- **A2**: Idempotencia por `orderId` únicamente → Rechazado: no cubre reenvíos del broker

**Rationale**: `eventId` único evita procesamiento duplicado por reenvíos RabbitMQ, `orderId` evita creación de órdenes duplicadas.

**Implementación**:
```java
// order-service/domain/event/OrderCreatedEvent.java
public class OrderCreatedEvent {
    private final UUID eventId = UUID.randomUUID(); // Generado una vez
    private final UUID orderId;
    private final List<OrderItem> items;
    // ...
}

// order-service/infrastructure/adapters/out/persistence/ProcessedEventRepository.java
public interface ProcessedEventRepository {
    boolean isProcessed(UUID eventId);
    void markProcessed(UUID eventId, UUID orderId);
}
```

### D4: Manejo de fallos de broker RabbitMQ

**Decisión**: **Outbox pattern** con tabla `outbox_events` para garantizar eventual delivery.

**Alternativas consideradas**:
- **A1**: Fallo silencioso → Rechazado: pérdida de eventos críticos
- **A2**: Reintento síncrono → Rechazado: bloquea confirmación de cart

**Rationale**: Garantiza que eventos se publican eventualmente, incluso si RabbitMQ falla temporalmente.

**Implementación**:
```java
// cart-service/infrastructure/adapters/out/persistence/OutboxEventRepository.java
// Almacena eventos pendientes de publicación
// Scheduled job procesa outbox cada N segundos
```

### D5: Value Objects para tipado fuerte

**Decisión**: `Money`, `Quantity`, `ProductId` como value objects inmutables.

**Alternativas consideradas**:
- **A1**: Primitivos Java (BigDecimal, int, UUID) → Rechazado: pierde semántica de dominio
- **A2**: Records Java 17 → **Elegido**: conciso e inmutable

**Rationale**: Tipado fuerte previene errores (no confundir quantity con price), encapsula validaciones.

**Implementación**:
```java
// Shared kernel o en cada servicio
public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
```

## Risks / Trade-offs

### R1: Latencia por validación síncrona de stock
**Risk**: `cart-service` → `product-service` HTTP añade latencia vs consulta local.  
**Mitigation**: Timeout aggressive (2s), circuit breaker, fallback a validación asíncrona post-confirmación.

### R2: Inconsistencia temporal entre cart confirmado y orden creada  
**Risk**: Cart CONFIRMED pero evento falla, orden no se crea.  
**Mitigation**: Outbox pattern + scheduled retry. Monitoring de eventos pendientes.

### R3: Contención por validación de stock concurrente
**Risk**: Múltiples carts validando mismo producto simultáneamente, race condition.  
**Mitigation**: **No mitigar**, asumimos stock alto. Si es problema futuro: locking optimista en product-service.

### R4: Evolución de esquema de eventos
**Risk**: Cambios en `OrderCreatedEvent` rompen consumers existentes.  
**Mitigation**: Versionado de eventos (`event_version: "v1"`), backward compatibility por 2 versiones.

### R5: Testing sin infraestructura real
**Risk**: Tests unitarios no cubren integration failures.  
**Mitigation**: Testcontainers para PostgreSQL/RabbitMQ en integration tests, mocks para unit tests.

## Migration Plan

### Fase 1: Dominio (Humano A - actual)
1. **TDD Red**: Crear test `confirmCartWithValidStock_shouldCreatePendingOrder()`
2. **TDD Green**: Implementar mínimo `Cart.confirm()` + `Order` agregado  
3. **TDD Refactor**: Extraer value objects, clean code
4. **Repetir**: Para cada CA (stock insuficiente, idempotencia, broker failure)

### Fase 2: Infrastructure (Humano B)  
1. **REST adapter**: `POST /api/v1/carts/{cartId}/confirm` controller
2. **HTTP client**: `ProductServiceClient` implementando `StockValidationPort`
3. **RabbitMQ**: Publisher/Consumer para `OrderCreatedEvent`  
4. **JPA**: Repositories para `Cart`, `Order`, `OutboxEvent`, `ProcessedEvent`
5. **Integration tests**: Con testcontainers

### Fase 3: QA/E2E (Humano C)
1. **Gherkin scenarios**: Actualizar `TEST_PLAN.md` con CAs del proposal
2. **E2E automation**: Playwright tests validando flujo completo
3. **Performance**: Load testing validación de stock bajo concurrencia
4. **OpenSpec validation**: Contratos API sincronizados

### Rollback Strategy
- **Domain changes**: Reversible via feature flags  
- **DB schema**: Flyway down migrations preparadas
- **RabbitMQ**: Backward compatible events, drain old events antes de rollback

## Open Questions

### Q1: ¿Timeout para validación de stock?  
**Options**: 2s aggressive vs 5s conservative  
**Decision needed by**: Antes de implementar `ProductServiceClient`
**Owner**: Humano B (infrastructure)

### Q2: ¿Batch size para outbox processing?
**Options**: Individual (1) vs batch (10) vs configurable  
**Decision needed by**: Antes de scheduled job implementation
**Owner**: Humano B (infrastructure)

### Q3: ¿Logs estructurados con correlationId?
**Options**: SLF4J simple vs structured logging con Logback  
**Decision needed by**: Antes de deploy a QA
**Owner**: Humano C (observability)

---

## Agentes sugeridos próximos pasos

| Artifact | Agente | Cuando cambiar |
|----------|--------|----------------|
| `specs/*.md` | **iris-requirements** (actual) | Continuar hasta terminar artifacts |
| `tasks.md` | **iris-requirements** (actual) | Después de completar specs |
| Implementación | **tdd_BE_agent** | Después de completar todos artifacts OpenSpec |  
| E2E validation | **playwright mcp relay** | Tras implementación + levantar app |