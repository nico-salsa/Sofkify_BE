# Contratos de Eventos – Ecommerce Asíncrono

## 1. Propósito del Documento

Este documento define los **eventos de integración** emitidos por los bounded contexts del dominio Ecommerce
y los **consumidores esperados** dentro del ecosistema.

Este archivo:

* Define contratos explícitos de comunicación asíncrona.
* No define reglas de negocio (ver `context.md`).
* No define arquitectura interna de los microservicios (ver `architecture.md`).
* No describe flujos funcionales completos.

---

## 2. Alcance del Documento

Los eventos definidos en este documento corresponden a los **bounded contexts implementados** en el sistema.

Este documento es **evolutivo** y se ampliará a medida que:

* Nuevos bounded contexts comiencen a emitir eventos.
* Se agreguen nuevos eventos a bounded contexts existentes.
* Se incorporen nuevos consumidores.

La ausencia de un evento para un bounded context indica que **aún no se ha implementado** o **no requiere comunicación asíncrona**.

---

## 3. Reglas Generales de Eventos

Las siguientes reglas aplican a **todos los eventos de integración** emitidos por cualquier bounded context:

* Un evento representa un **hecho de negocio que ya ocurrió**.
* Los eventos **no son comandos**.
* El productor **no conoce ni espera respuesta** de los consumidores.
* Un evento puede ser consumido por uno o más servicios.
* **No se garantiza entrega única** (idempotencia requerida en consumers).
* Un evento puede procesarse más de una vez.
* El orden de procesamiento no está garantizado.
* Los eventos de integración se derivan de Domain Events internos.

Estas reglas no se redefinen por bounded context.

### 3.1 Idempotencia

Todos los consumers **DEBEN** ser idempotentes.

* Procesar el mismo evento múltiples veces no debe generar inconsistencias.
* Se recomienda usar identificadores únicos de evento para detectar duplicados.

### 3.2 Formato de Eventos

Todos los eventos de integración utilizan **JSON** como formato de serialización.

Estructura base recomendada:

```json
{
  "eventId": "uuid",
  "eventType": "OrderCreated",
  "occurredOn": "2026-02-13T10:30:00Z",
  "aggregateId": "order-123",
  "payload": {
    // Datos específicos del evento
  }
}
```

---

## 4. Eventos por Bounded Context

Esta sección documenta los eventos emitidos por cada bounded context implementado en el sistema.

Cada bounded context define:
* Qué eventos emite
* Quién es el productor
* Qué consumidores existen
* El contrato del evento (estructura de datos)

---

## 4.1 Bounded Context: Order

### Productor

* **order-service**

### Eventos Emitidos

#### `OrderCreated`

**Descripción:**  
Se emite cuando un User confirma su Cart y se genera una Order exitosamente.

**Routing Key:**  
`order.created`

**Exchange:**  
`order.exchange` (tipo: `topic`)

**Payload:**

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "OrderCreated",
  "occurredOn": "2026-02-13T10:30:00Z",
  "aggregateId": "order-123",
  "payload": {
    "orderId": "order-123",
    "userId": "user-456",
    "items": [
      {
        "productId": "product-789",
        "quantity": 2,
        "price": 29.99
      }
    ],
    "totalAmount": 59.98,
    "status": "CREATED",
    "createdAt": "2026-02-13T10:30:00Z"
  }
}
```

**Campos:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `eventId` | String (UUID) | Identificador único del evento |
| `eventType` | String | Tipo de evento: `OrderCreated` |
| `occurredOn` | String (ISO 8601) | Timestamp de generación del evento |
| `aggregateId` | String | ID de la Order creada |
| `payload.orderId` | String | ID de la Order |
| `payload.userId` | String | ID del User que creó la Order |
| `payload.items` | Array | Lista de items de la Order |
| `payload.items[].productId` | String | ID del Product |
| `payload.items[].quantity` | Integer | Cantidad solicitada |
| `payload.items[].price` | Decimal | Precio unitario al momento de la compra |
| `payload.totalAmount` | Decimal | Monto total de la Order |
| `payload.status` | String | Estado inicial: `CREATED` |
| `payload.createdAt` | String (ISO 8601) | Timestamp de creación de la Order |

**Reglas de Negocio:**

* Se emite únicamente cuando la Order se persiste exitosamente.
* Debe incluir snapshot de los productos (precio al momento de compra).
* El `userId` debe ser un User válido y autenticado.

### Consumidores

* **product-service**
    * **Propósito**: Decrementar stock de los productos incluidos en la Order.
    * **Queue**: `stock-decrement.queue`

---

## 4.2 Bounded Context: Product

### Productor

* **product-service**

### Eventos Emitidos

#### `StockDecremented`

**Descripción:**  
Se emite cuando product-service procesa exitosamente un evento `OrderCreated` y decrementa el stock de los productos.

**Routing Key:**  
`stock.decremented`

**Exchange:**  
`order.exchange` (tipo: `topic`)

**Payload:**

```json
{
  "eventId": "660e8400-e29b-41d4-a716-446655440001",
  "eventType": "StockDecremented",
  "occurredOn": "2026-02-13T10:30:05Z",
  "aggregateId": "product-789",
  "payload": {
    "orderId": "order-123",
    "items": [
      {
        "productId": "product-789",
        "quantity": 2,
        "newStock": 48
      }
    ],
    "decrementedAt": "2026-02-13T10:30:05Z"
  }
}
```

**Campos:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `eventId` | String (UUID) | Identificador único del evento |
| `eventType` | String | Tipo de evento: `StockDecremented` |
| `occurredOn` | String (ISO 8601) | Timestamp de generación del evento |
| `aggregateId` | String | ID del Product principal afectado |
| `payload.orderId` | String | ID de la Order que originó el decremento |
| `payload.items` | Array | Lista de productos con stock decrementado |
| `payload.items[].productId` | String | ID del Product |
| `payload.items[].quantity` | Integer | Cantidad decrementada |
| `payload.items[].newStock` | Integer | Nuevo stock disponible del producto |
| `payload.decrementedAt` | String (ISO 8601) | Timestamp del decremento |

**Reglas de Negocio:**

* Solo se emite si el decremento fue exitoso para **todos** los productos.
* Si algún producto no tiene stock suficiente, NO se emite este evento (ver `OrderFailed`).
* El stock debe ser >= 0 después del decremento.

### Consumidores

* **order-service**
    * **Propósito**: Actualizar el estado de la Order a `CONFIRMED` o `PROCESSING`.
    * **Queue**: `order-status.queue`

---

#### `OrderFailed`

**Descripción:**  
Se emite cuando product-service **no puede** decrementar el stock de los productos (ej: stock insuficiente).

**Routing Key:**  
`order.failed`

**Exchange:**  
`order.exchange` (tipo: `topic`)

**Payload:**

```json
{
  "eventId": "770e8400-e29b-41d4-a716-446655440002",
  "eventType": "OrderFailed",
  "occurredOn": "2026-02-13T10:30:05Z",
  "aggregateId": "order-123",
  "payload": {
    "orderId": "order-123",
    "reason": "INSUFFICIENT_STOCK",
    "details": "Product product-789 has insufficient stock. Required: 2, Available: 1",
    "failedProducts": [
      {
        "productId": "product-789",
        "requestedQuantity": 2,
        "availableStock": 1
      }
    ],
    "failedAt": "2026-02-13T10:30:05Z"
  }
}
```

**Campos:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `eventId` | String (UUID) | Identificador único del evento |
| `eventType` | String | Tipo de evento: `OrderFailed` |
| `occurredOn` | String (ISO 8601) | Timestamp de generación del evento |
| `aggregateId` | String | ID de la Order que falló |
| `payload.orderId` | String | ID de la Order |
| `payload.reason` | String | Código de razón del fallo |
| `payload.details` | String | Descripción detallada del error |
| `payload.failedProducts` | Array | Lista de productos que causaron el fallo |
| `payload.failedProducts[].productId` | String | ID del Product |
| `payload.failedProducts[].requestedQuantity` | Integer | Cantidad solicitada |
| `payload.failedProducts[].availableStock` | Integer | Stock disponible al momento del fallo |
| `payload.failedAt` | String (ISO 8601) | Timestamp del fallo |

**Códigos de Razón:**

| Código | Descripción |
|--------|-------------|
| `INSUFFICIENT_STOCK` | Uno o más productos no tienen stock suficiente |
| `PRODUCT_NOT_FOUND` | Uno o más productos no existen |
| `PRODUCT_INACTIVE` | Uno o más productos están desactivados |

**Reglas de Negocio:**

* Se emite cuando **cualquier** producto de la Order no puede procesarse.
* **No se decrementa stock** de ningún producto si falla alguno (atomicidad).
* Debe incluir información detallada para debugging.

### Consumidores

* **order-service**
    * **Propósito**: Actualizar el estado de la Order a `FAILED` y registrar la razón.
    * **Queue**: `order-status.queue`

---

## 4.3 Bounded Context: User

### Estado Actual

El bounded context **User** no emite eventos de integración en la implementación actual.

**Eventos conceptuales del dominio** (no implementados técnicamente):

* `UserCreated`
* `UserUpdated`
* `UserDeleted`
* `UserRoleChanged`

Estos eventos **no tienen contratos técnicos definidos** y no se publican mediante mensajería.

Su implementación queda pendiente para futuras iteraciones.

---

## 4.4 Bounded Context: Cart

### Estado Actual

El bounded context **Cart** no emite eventos de integración en la implementación actual.

La comunicación entre `cart-service` y otros servicios se realiza de forma **síncrona** mediante APIs REST.

**Eventos conceptuales del dominio** (no implementados técnicamente):

* `CartCreated`
* `ProductAddedToCart`
* `ProductRemovedFromCart`
* `CartConfirmed` (se materializa como `OrderCreated` en order-service)

Su implementación queda pendiente para futuras iteraciones.

---

## 5. Configuración de Mensajería

### 5.1 RabbitMQ

**Exchange Principal:**

| Exchange | Tipo | Durable |
|----------|------|---------|
| `order.exchange` | `topic` | Sí |

**Queues:**

| Queue | Durable | Binding (Routing Key) |
|-------|---------|----------------------|
| `stock-decrement.queue` | Sí | `order.created` |
| `order-status.queue` | Sí | `stock.decremented`, `order.failed` |

**Routing Keys:**

| Routing Key | Productor | Consumidor(es) |
|-------------|-----------|----------------|
| `order.created` | order-service | product-service |
| `stock.decremented` | product-service | order-service |
| `order.failed` | product-service | order-service |

---

## 6. Patrones de Consistencia

### 6.1 Saga Coreografiada: Creación de Order

**Flujo exitoso:**

1. **order-service** crea Order → emite `OrderCreated`
2. **product-service** consume `OrderCreated` → decrementa stock → emite `StockDecremented`
3. **order-service** consume `StockDecremented` → actualiza Order a `CONFIRMED`

**Flujo con fallo:**

1. **order-service** crea Order → emite `OrderCreated`
2. **product-service** consume `OrderCreated` → detecta stock insuficiente → emite `OrderFailed`
3. **order-service** consume `OrderFailed` → actualiza Order a `FAILED`

### 6.2 Idempotencia en Consumers

Todos los consumers deben implementar idempotencia mediante:

* Registro de eventos procesados (event log).
* Validación de `eventId` antes de procesar.
* Operaciones de negocio idempotentes (ej: actualización por ID).

---

## 7. Versionado de Eventos

### Estado Actual

Los eventos **no implementan versionado explícito** en la versión actual del sistema.

Todos los eventos se consideran **versión implícita 1**.

### Evolución Futura

Cuando se requiera modificar un contrato de evento:

* **Cambios compatibles** (agregar campos opcionales): mantener mismo evento.
* **Cambios incompatibles** (remover/renombrar campos): crear nuevo evento versionado.

Ejemplo:

* `OrderCreated` (actual)
* `OrderCreatedV2` (futuro)

---

## 8. Monitoreo y Observabilidad

### Recomendaciones

* Incluir `eventId` único en todos los eventos para trazabilidad.
* Loggear eventos emitidos y consumidos con nivel INFO.
* Implementar dead-letter queues para eventos fallidos.
* Configurar alertas para queues con mensajes no procesados.

---

## 9. Evolución del Documento

Este documento se modifica cuando:

* Un bounded context comienza a emitir nuevos eventos.
* Se modifica el contrato de un evento existente (con versionado).
* Se agregan nuevos consumidores a un evento.
* Se cambia la configuración de mensajería (exchanges, queues, routing keys).

Cada cambio debe:

* Ser revisado por el arquitecto.
* Documentar el motivo del cambio.
* Actualizar las tablas de configuración.

---

## 10. Relación con Otros Documentos

* `context.md`: define los hechos y reglas del dominio que originan los eventos.
* `architecture.md`: define cómo se publican y consumen los eventos técnicamente.
* `HU-*.md`: definen implementaciones concretas de producers y consumers.
* `AI_WORKFLOW.md`: define el uso controlado de IA para implementar eventos.

Este documento define **la comunicación asíncrona**, no el dominio ni la arquitectura interna.

---

## 11. Glosario

| Término | Definición |
|---------|------------|
| **Domain Event** | Evento generado dentro del dominio, sin dependencias técnicas |
| **Integration Event** | Evento publicado mediante infraestructura para comunicación entre servicios |
| **Producer** | Bounded context que emite un evento |
| **Consumer** | Bounded context que procesa un evento |
| **Routing Key** | Clave utilizada para enrutar mensajes en el exchange |
| **Queue** | Cola donde se almacenan mensajes para un consumer |
| **Exchange** | Componente de RabbitMQ que recibe mensajes y los enruta a queues |
| **Idempotencia** | Capacidad de procesar el mismo evento múltiples veces sin efectos secundarios |
| **Saga** | Patrón para gestionar transacciones distribuidas mediante eventos |