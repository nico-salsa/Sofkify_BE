# Sofkify Docker - Diagramas y Flujos Visuales

## 🏗️ Arquitectura Completa del Sistema

### Vista General

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             HOST (Tu Computadora)                           │
│  Windows / Mac / Linux - Ejecuta: docker-compose up -d --build              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  PUERTOS EXPUESTOS (Acceso desde Host):                                    │
│  ───────────────────────────────────────────────────────────────────────   │
│                                                                             │
│  5432 ──────────────── PostgreSQL (sofkify_users)                          │
│  5433 ──────────────── PostgreSQL (sofkify_products_bd)                    │
│  5434 ──────────────── PostgreSQL (sofkify_cars_bd)                        │
│  5435 ──────────────── PostgreSQL (sofkify_orders_bd)                      │
│  5672 ──────────────── RabbitMQ AMQP                                       │
│  15672 ─────────────── RabbitMQ Management UI (http://localhost:15672)     │
│  8080 ──────────────── user-service API                                    │
│  8081 ──────────────── product-service API                                 │
│  8082 ──────────────── order-service API                                   │
│  8083 ──────────────── cart-service API                                    │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │              SOFKIFY-NETWORK (Red Bridge Privada)                   │  │
│  │         (Contenedores se comunican entre sí internamente)           │  │
│  │                                                                     │  │
│  │  BASES DE DATOS:                                                   │  │
│  │  ─────────────────────────────────────────────────────────────    │  │
│  │                                                                     │  │
│  │  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐  │  │
│  │  │ postgres-users   │ │postgres-products │ │ postgres-carts   │  │  │
│  │  │ :5432 interno    │ │:5433 interno     │ │ :5432 interno    │  │  │
│  │  │ sofkify_users    │ │sofkify_products_ │ │ sofkify_cars_bd  │  │  │
│  │  │ [Vol. users-data]│ │    bd            │ │ [Vol. carts-data]│  │  │
│  │  │ ✓ HEALTHY       │ │ [Vol. prod-data] │ │ ✓ HEALTHY       │  │  │
│  │  │ ✓ READY         │ │ ✓ HEALTHY       │ │ ✓ READY         │  │  │
│  │  └────────────────┬─┘ └────────┬────────┘ └────────┬─────────┘  │  │
│  │            (autenticación) (inventario)    (carrito)            │  │
│  │                  │                              │                 │  │
│  │  ┌───────────────▼──────────────┐                              │  │
│  │  │  postgres-orders:5432        │                              │  │
│  │  │  sofkify_orders_bd           │                              │  │
│  │  │  [Vol. orders-data]          │                              │  │
│  │  │  ✓ HEALTHY ✓ READY          │                              │  │
│  │  └────────────────────────────┘                               │  │
│  │                                                                     │  │
│  │  MESSAGE BROKER:                                                  │  │
│  │  ──────────────────────────────────────────────────────────────  │  │
│  │                                                                     │  │
│  │  ┌────────────────────────────────────────┐                      │  │
│  │  │         RabbitMQ (Message Broker)      │                      │  │
│  │  │                                        │                      │  │
│  │  │  ├─ Exchange: order.exchange           │                      │  │
│  │  │  │  └─ Routing Key: order.created      │                      │  │
│  │  │  │     └─ Queue: product.stock...      │                      │  │
│  │  │  │                                     │                      │  │
│  │  │  ├─ Exchange: product.exchange         │                      │  │
│  │  │  │  └─ Routing Key: stock.decremented │                      │  │
│  │  │  │     └─ Queue: order.stock.update    │                      │  │
│  │  │  │                                     │                      │  │
│  │  │  └─ AMQP: 5672 (interno)               │                      │  │
│  │  │  └─ Management: 15672 (expuesto)       │                      │  │
│  │  │  │  Usuario: guest                     │                      │  │
│  │  │  │  Contraseña: guest                  │                      │  │
│  │  │  │  [Vol. rabbitmq-data]               │                      │  │
│  │  │  │  [Vol. rabbitmq-logs]               │                      │  │
│  │  │  └─ ✓ HEALTHY ✓ READY                  │                      │  │
│  │  │                                        │                      │  │
│  │  └────────────────────────────────────────┘                      │  │
│  │                                                                     │  │
│  │  MICROSERVICIOS (Spring Boot):                                   │  │
│  │  ───────────────────────────────────────────────────────────    │  │
│  │                                                                     │  │
│  │  ┌────────────────┐  ┌──────────────────┐  ┌─────────────────┐ │  │
│  │  │ user-service   │  │product-service   │  │ order-service   │ │  │
│  │  │ :8080 local    │  │ :8081 local      │  │ :8082 local     │ │  │
│  │  │ Java 21        │  │ Java 17          │  │ Java 17         │ │  │
│  │  │ Spring Boot    │  │ Spring Boot      │  │ Spring Boot     │ │  │
│  │  │ Conecta a:     │  │ Conecta a:       │  │ Conecta a:      │ │  │
│  │  │ postgres-users │  │ postgres-products│  │ postgres-orders │ │  │
│  │  │                │  │ + RabbitMQ       │  │ + RabbitMQ      │ │  │
│  │  │ ✓ READY        │  │ ✓ READY          │  │ ✓ READY         │ │  │
│  │  └────────────────┘  └──────────────────┘  └─────────────────┘ │  │
│  │                                                                     │  │
│  │  ┌────────────────────────────────────────┐                      │  │
│  │  │ cart-service:8083 local                │                      │  │
│  │  │ Java 17                                │                      │  │
│  │  │ Spring Boot                            │                      │  │
│  │  │ Conecta a: postgres-carts              │                      │  │
│  │  │ Llamadas HTTP a: product-service      │                      │  │
│  │  │ ✓ READY                                │                      │  │
│  │  └────────────────────────────────────────┘                      │  │
│  │                                                                     │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────────┘

LEYENDA:
✓ HEALTHY  = Health check pasó
✓ READY    = Servicio listo para recibir tráfico
[Vol. xxx] = Volumen persistente
```

---

## 🔄 Flujos de Comunicación

### 1. Crear un Producto (REST Síncrono)

```
┌──────────────────┐
│  Cliente HTTP    │
│  (Navegador)     │
└────────┬─────────┘
         │
         │ POST http://localhost:8081/api/v1/products
         │ Body: { name, price, stock }
         │
         ▼
┌──────────────────────────────────────────────────┐
│ product-service (Puerto 8081)                    │
│                                                  │
│  ┌────────────────────────────────────────────┐ │
│  │ ProductRestController                      │ │
│  │ @PostMapping("/api/v1/products")          │ │
│  └═════════════────────┬══════════════════════┘ │
│                        │                        │
│                        ▼                        │
│  ┌────────────────────────────────────────────┐ │
│  │ CreateProductService                       │ │
│  │ execute(ProductRequest)                    │ │
│  │ ├─ Valida datos                           │ │
│  │ ├─ Crea entity                            │ │
│  │ └─ Guarda en BD                           │ │
│  └═════════════════════╤══════════════════════┘ │
│                        │                        │
└────────────────────────┼────────────────────────┘
                         │
                         ▼
                ┌────────────────────┐
                │ PostgreSQL         │
                │ postgres-products  │
                │ (puerto 5433)      │
                │                    │
                │ INSERT INTO        │
                │ products(...)      │
                │ VALUES(...)        │
                │                    │
                │ sofkify_products_bd│
                └────────────────────┘
                 
         (Datos guardados en BD)
         
┌──────────────────┐
│  201 Created     │
│  { id, name ... }│
│  ← Respuesta     │
└──────────────────┘
```

---

### 2. Agregar Producto al Carrito (REST + Validación)

```
┌──────────────────┐
│  Cliente HTTP    │
│  (Navegador)     │
└────────┬─────────┘
         │
         │ POST http://localhost:8083/api/v1/carts/123/items
         │ Body: { productId, quantity }
         │
         ▼
┌──────────────────────────────────────────────────┐
│ cart-service (Puerto 8083)                       │
│                                                  │
│  ┌────────────────────────────────────────────┐ │
│  │ CartRestController                         │ │
│  │ @PostMapping("/api/v1/carts/{id}/items")  │ │
│  └═════════════════════╤════════════════════┘ │
│                        │                       │
│                        ▼                       │
│  ┌────────────────────────────────────────────┐ │
│  │ AddItemToCartService                       │ │
│  │ execute(cartId, itemRequest)               │ │
│  │                                            │ │
│  │ 1. Valida carrito existe                  │ │
│  │    └─ Query: postgres-carts               │ │
│  │                                            │ │
│  │ 2. Valida producto existe                 │ │
│  │    └─ HTTP GET product-service:8081       │ │
│  │        /api/v1/products/123               │ │
│  │                                            │ │
│  │ 3. Valida stock disponible                │ │
│  │    └─ Verifica cantidad en respuesta      │ │
│  │                                            │ │
│  │ 4. Agrega item al carrito                 │ │
│  │    └─ INSERT en postgres-carts            │ │
│  │                                            │ │
│  │ 5. Retorna carrito actualizado            │ │
│  └════════════════════╤═════════════════════┘ │
└──────────────────────┼───────────────────────┘
                       │
          ┌────────────┴────────────┐
          │                         │
          ▼                         ▼
    postgres-carts          product-service
    (Valida/Guarda)        (Consulta stock)
                           ↑
                           │ HTTP GET
                           │ :8081/api/v1/products/123
                           │
                ┌──────────────────────┐
                │ PostgreSQL           │
                │ postgres-products    │
                │ (puerto 5433)        │
                │                      │
                │ SELECT * FROM        │
                │ products WHERE id=123│
                └──────────────────────┘

┌──────────────────────┐
│  200 OK               │
│  { carrito actualizado
│    items: [...],      │
│    total: $XXX }      │
└──────────────────────┘
```

---

### 3. Crear Orden (REST + Evento Asíncrono)

```
┌──────────────────┐
│  Cliente HTTP    │
│  (Navegador)     │
└────────┬─────────┘
         │
         │ POST http://localhost:8082/api/v1/orders
         │ Body: { cartId }
         │
         ▼
┌──────────────────────────────────────────────────┐
│ order-service (Puerto 8082)                      │
│                                                  │
│  ┌────────────────────────────────────────────┐ │
│  │ OrderRestController                        │ │
│  │ @PostMapping("/api/v1/orders")            │ │
│  └═════════════════════╤═════════════════════┘ │
│                        │                        │
│                        ▼                        │
│  ┌────────────────────────────────────────────┐ │
│  │ CreateOrderService                         │ │
│  │ execute(createOrderRequest)                │ │
│  │                                            │ │
│  │ 1. Valida carrito existe y está confirmado│ │
│  │ 2. Crea orden en BD                        │ │
│  │ 3. AHORA → PUBLICA EVENTO ASÍNCRONO !!!    │ │
│  │    └─ OrderCreatedEvent                   │ │
│  │       { orderId, items, timestamp }       │ │
│  │                                            │ │
│  │ 4. ENVÍA EVENTO A RABBITMQ                 │ │
│  │    └─ exchange: order.exchange             │ │
│  │    └─ routing key: order.created           │ │
│  │    └─ queue: product.stock.decrement      │ │
│  │                                            │ │
│  │ 5. Retorna respuesta (sin esperar respuesta)
│  └═══════════════════╤═════════════════════╝ │
└──────────────────────┼───────────────────────┘
                       │
       ┌───────────────┴────────────────┐
       │                                │
       ▼                                ▼
┌──────────────────┐           ┌──────────────────┐
│ postgres-orders  │           │ RabbitMQ         │
│ (puerto 5435)    │           │ (puerto 5672)    │
│                  │           │                  │
│ INSERT INTO      │           │ PUBLICA:         │
│ orders(...)      │           │ ┌──────────────┐ │
│                  │           │ │ OrderCreated │ │
│ sofkify_orders_bd│           │ │ Event        │ │
│                  │           │ │ {orderId,    │ │
│ ORDER GUARDADA ✓ │           │ │  items,      │ │
│                  │           │ │  timestamp}  │ │
└──────────────────┘           │ └──────────────┘ │
                               │ En cola para:    │
                               │ product-service  │
                               │ order-service    │
                               └──────────────────┘

  ┌─────────────────────────────────────────────────────────┐
  │ 201 Created                                             │
  │ { orderId: 456, status: PENDING, items: [...] }        │
  │ ← Respuesta INMEDIATA (no espera que se procese evento) │
  └─────────────────────────────────────────────────────────┘

  === MIENTRAS TANTO (En Background) ===
  
         ▼
┌──────────────────────────────────────────────────┐
│ product-service ESCUCHA RABBITMQ                 │
│ (Consumer: @RabbitListener)                      │
│                                                  │
│ 1. RECIBE: OrderCreatedEvent de la queue        │
│    "product.stock.decrement.queue"              │
│                                                  │
│ 2. PROCESA:                                      │
│    Para cada item en el event:                  │
│    ├─ Consulta stock en postgres-products      │ │
│    ├─ Decrementa cantidad                       │ │
│    ├─ Guarda cambios                            │ │
│    └─ PUBLICA: stock.decremented Event         │ │
│       A exchange: product.exchange               │ │
│       Routing key: stock.decremented             │ │
│                                                  │
│ 3. RESULTADO: Stock actualizado en BD           │ │
│    order-service recibe confirmación             │ │
└──────────────────────────────────────────────────┘

         ▼
┌──────────────────────────────────────────────────┐
│ order-service TAMBIÉN ESCUCHA stock.decremented  │
│ (Consumer: @RabbitListener)                      │
│                                                  │
│ 1. RECIBE: StockDecrementedEvent                │ │
│                                                  │
│ 2. ACTUALIZA orden:                              │ │
│    ├─ Status: PENDING → CONFIRMED               │ │
│    ├─ Timestamp de confirmación                  │ │
│    └─ Guarda cambios en postgres-orders         │ │
│                                                  │
│ 3. RESULTADO: Orden completada                  │ │
└──────────────────────────────────────────────────┘
```

**Resumen**: Crear orden es rápido (1 evento síncrono), pero el procesamiento del stock ocurre en **background asíncrono**.

---

## 🏥 Health Checks

### Secuencia de Startup

```
docker-compose up -d --build
│
├─ 1. Levanta PostgreSQL containers
│  ├─ postgres-users   → pg_isready cada 10s
│  ├─ postgres-products → pg_isready cada 10s
│  ├─ postgres-carts   → pg_isready cada 10s
│  └─ postgres-orders  → pg_isready cada 10s
│     [ejecuta init-db.sql en cada uno]
│     [Espera: 30-45 segundos]
│
├─ 2. Levanta RabbitMQ container
│  └─ rabbitmq → rabbitmq-diagnostics ping_alarms
│     [Espera: 20-30 segundos]
│
├─ 3. Levanta product-service
│  ├─ ESPERA A: postgres-products HEALTHY ✓
│  ├─ ESPERA A: rabbitmq HEALTHY ✓
│  └─ Inicia Spring Boot
│     [Espera: 10-15 segundos]
│
├─ 4. Levanta order-service
│  ├─ ESPERA A: postgres-orders HEALTHY ✓
│  ├─ ESPERA A: rabbitmq HEALTHY ✓
│  └─ Inicia Spring Boot
│
├─ 5. Levanta user-service
│  ├─ ESPERA A: postgres-users HEALTHY ✓
│  └─ Inicia Spring Boot
│
└─ 6. Levanta cart-service
   ├─ ESPERA A: postgres-carts HEALTHY ✓
   └─ Inicia Spring Boot

TOTAL: ~60-90 segundos para todo

docker-compose ps

STATUS ESPERADO:
─────────────────────────────────────────
NAME                    STATUS
─────────────────────────────────────────
postgres-users          Up 2 minutes (healthy)
postgres-products       Up 2 minutes (healthy)
postgres-carts          Up 2 minutes (healthy)
postgres-orders         Up 2 minutes (healthy)
rabbitmq                Up 2 minutes (healthy)
user-service            Up 1 minute
product-service         Up 1 minute
order-service           Up 1 minute
cart-service            Up 1 minute
─────────────────────────────────────────
```

---

## 🔌 Dependencias en Diagram

```
                    ┌─────────────────┐
                    │ docker-compose  │
                    │ up -d --build   │
                    └────────┬────────┘
                             │
             ┌───────────────┼───────────────┐
             │               │               │
             ▼               ▼               ▼
        postgres-*      rabbitmq       Nada (espera a deps)
        (sin deps)      (sin deps)
             │               │
     ┌──────┴────────────────┴──────────────────┐
     │         Todos HEALTHY (condición)        │
     │  pg_isready = ok, diagnositcs = ok      │
     └──────┬─────────────────────────────────┘
            │
    ┌───────┴────────────────┬──────────────────┐
    │                        │                  │
    ▼                        ▼                  ▼
user-service         product-service      order-service
(espera a:)          (espera a:)           (espera a:)
postgres-users       postgres-products    postgres-orders
                     rabbitmq             rabbitmq
                         │                    │
                         └────┬───────────────┘
                              │
                              ▼
                        cart-service
                        (espera a:)
                        postgres-carts

Las flechas muestran:
─────────────────────
→ Dependencias de inicio
→ "Espera a que esté HEALTHY antes de iniciar"
```

---

## 📊 Tabla de Puertos Mapeados

```
SERVICE              PUERTO LOCAL    PUERTO INTERNO   PROTOCOLO
──────────────────────────────────────────────────────────────
postgres-users       5432            5432             TCP (SQL)
postgres-products    5433            5432             TCP (SQL)
postgres-carts       5434            5432             TCP (SQL)
postgres-orders      5435            5432             TCP (SQL)
rabbitmq AMQP        5672            5672             TCP (AMQP)
rabbitmq UI          15672           15672            TCP (HTTP)
user-service         8080            8080             TCP (HTTP)
product-service      8081            8081             TCP (HTTP)
order-service        8082            8082             TCP (HTTP)
cart-service         8083            8083             TCP (HTTP)

NOTA: Puertos internos son los que los contenedores usan entre sí.
      Puertos locales son los que accedes desde tu máquina.
      
Ejemplo:
- Acceder desde host:  curl http://localhost:8081/api/v1/products
- Acceder desde cart:  curl http://product-service:8081/api/v1/products
                       (sin localhost, sin puerto local, usa nombre de contenedor)
```

---

## 🌐 Comunicación Interna vs Externa

```
COMUNICACIÓN INTERNA (dentro de sofkify-network)
─────────────────────────────────────────────────

cart-service (8083) → product-service (8081)
   
   URL: http://product-service:8081/api/v1/products
        └────────────────────┬──────────────────────┘
         (Nombre de contenedor, puerto interno)
         
   No necesita puerto local (5433 no se usa internamente)
   DNS privado: product-service → IP del contenedor


COMUNICACIÓN EXTERNA (desde tu máquina)
──────────────────────────────────────

Tu Navegador (localhost 8081) → product-service (8081)

   URL: http://localhost:8081/api/v1/products
        └───────┬─────────┘
         (Puerto mapeado al host)
         
   Puerto 8081 está expuesto (EXPOSE en Dockerfile)
   Docker mapea localhost:8081 → product-service:8081


DIAGRAMA:
─────────

HOST (Tu Máquina)               DOCKER NETWORK (sofkify-network)
┌─────────────────┐            ┌──────────────────────────────────┐
│                 │            │                                  │
│  localhost:8081 │            │  product-service:8081            │
│  (Tu navegador) ├───────────>│  (Contenedor Spring Boot)        │
│                 │   Puerto   │  └─> postgresql:5432             │
│                 │   mapeado  │      (Contenedor PostgreSQL)     │
│  localhost:5433 │   ←───┐    │                                  │
│  (Tu psql CLI)  │        │   │  cart-service:8083               │
│                 │        │   │  (Contenedor)                    │
└─────────────────┘        │   │     └─> product-service:8081    │
                           │   │         (Comunicación interna)   │
                           │   └──────────────────────────────────┘
                           │
            Puerto mapeado del host a contenedor
            El contenedor "ve" a los demás por nombre
```

---

## 🔗 Resumen Visual de Conectividad

```
MATRIZ DE CONECTIVIDAD
──────────────────────

FROM              TO                        TYPE        PORT/PROTO
──────────────────────────────────────────────────────────────────
Host (localhost)  user-service             HTTP REST   8080
Host (localhost)  product-service          HTTP REST   8081
Host (localhost)  order-service            HTTP REST   8082
Host (localhost)  cart-service             HTTP REST   8083
Host (localhost)  rabbitmq UI              HTTP        15672
Host (localhost)  pg-users                 psql        5432
Host (localhost)  pg-products              psql        5433
Host (localhost)  pg-carts                 psql        5434
Host (localhost)  pg-orders                psql        5435

user-service      pg-users                 SQL         5432 (interno)
product-service   pg-products              SQL         5432 (interno)
product-service   rabbitmq                 AMQP        5672 (interno)
order-service     pg-orders                SQL         5432 (interno)
order-service     rabbitmq                 AMQP        5672 (interno)
cart-service      pg-carts                 SQL         5432 (interno)
cart-service      product-service          HTTP REST   8081 (interno)

rabbitmq          Nada                     N/A         N/A
pg-users          Nada                     N/A         N/A
pg-products       Nada                     N/A         N/A
pg-carts          Nada                     N/A         N/A
pg-orders         Nada                     N/A         N/A

NOTAS:
──────
- "Interno" = Usa nombre de contenedor en URL
- No hay puertos locales para comunicación interna
- Cada contenedor puede "ver" solo dentro de sofkify-network
- Host solo ve puertos expuestos (5432-5435, 5672, 15672, 8080-8083)
```

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0

