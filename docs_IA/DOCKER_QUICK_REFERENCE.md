# Docker Quick Reference & Architecture Overview

## 🗺️ Arquitectura Visual Simplificada

```
┌─────────────────────────────────────────────────────────────────────────┐
│                                HOST MACHINE                             │
│  (Windows/Mac/Linux - El equipo donde corres docker-compose)            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  PUERTOS EXPUESTOS:                                                    │
│  ─────────────────────────────────────────────────────────────────    │
│  5432, 5433, 5434, 5435  ←─→  PostgreSQL Databases                   │
│  5672, 15672             ←─→  RabbitMQ (AMQP + Management UI)        │
│  8080, 8081, 8082, 8083  ←─→  Microservices APIs                     │
│  3000                    ←─→  Frontend (Futuro)                       │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐  │
│  │           DOCKER NETWORK: sofkify-network (Bridge)             │  │
│  │  (Contenedores se comunican entre sí internamente)             │  │
│  ├─────────────────────────────────────────────────────────────────┤  │
│  │                                                                  │  │
│  │  CONTENEDORES (Servicios aislados)                             │  │
│  │  ════════════════════════════════════════════════════════      │  │
│  │                                                                  │  │
│  │  DB TIER (PostgreSQL):                                         │  │
│  │  ─────────────────────                                         │  │
│  │  ┌─────────────────┐ ┌──────────────────┐                     │  │
│  │  │ postgres-users  │ │postgres-products │ ┌──────────────┐    │  │
│  │  │  :5432 interno  │ │ :5433 interno    │ │postgres-carts│    │  │
│  │  │ sofkify_users   │ │sofkify_products_ │ │:5434 interno │    │  │
│  │  │                 │ │       bd         │ │sofkify_cars_ │    │  │
│  │  │ [Vol. Data]     │ │ [Vol. Data]      │ │      bd      │    │  │
│  │  └────────┬────────┘ └────────┬─────────┘ │ [Vol. Data]  │    │  │
│  │           │                   │           └──────┬───────┘    │  │
│  │  ┌────────▼──────────────────▼──────┐                         │  │
│  │  │  postgres-orders:5435 interno    │                         │  │
│  │  │  sofkify_orders_bd               │                         │  │
│  │  │  [Vol. Data]                     │                         │  │
│  │  └────────────────────────────────┘                           │  │
│  │                                                                  │  │
│  │  MESSAGE BROKER TIER:                                          │  │
│  │  ──────────────────                                            │  │
│  │  ┌────────────────────────────────┐                            │  │
│  │  │  rabbitmq:5672 (interno)       │                            │  │
│  │  │  Management UI: 15672 (expuesto)│                           │  │
│  │  │  [Vol. Data], [Vol. Logs]       │                           │  │
│  │  │  user: guest / pass: guest      │                           │  │
│  │  └────────────────────────────────┘                            │  │
│  │                                                                  │  │
│  │  APP TIER (Spring Boot):                                       │  │
│  │  ───────────────────────                                       │  │
│  │  ┌────────────────────┐ ┌─────────────────┐ ┌──────────────┐  │  │
│  │  │  user-service     │ │product-service │ │order-service │  │  │
│  │  │  :8080 local      │ │ :8081 local    │ │ :8082 local  │  │  │
│  │  │  Java 21          │ │ Java 17        │ │ Java 17      │  │  │
│  │  │  Conecta ─────────┼─────────────────┼────────────┬──┘  │  │
│  │  │  a postgres-users │ │postgres-products│ postgres-orders  │  │
│  │  └────────────────────┘ │ + rabbitmq     │ + rabbitmq       │  │
│  │                          └─────────────────┘ └──────────────┘  │  │
│  │                                                                  │  │
│  │  ┌────────────────────────────────┐                            │  │
│  │  │  cart-service:8083 local       │                            │  │
│  │  │  Java 17                       │                            │  │
│  │  │  Conecta a postgres-carts      │                            │  │
│  │  └────────────────────────────────┘                            │  │
│  │                                                                  │  │
│  │  COMUNICACIÓN INTERNA (no expuesta, solo dentro de la red):    │  │
│  │  ──────────────────────────────────────────────────────────   │  │
│  │  • cart-service → product-service (validar stock)             │  │
│  │  • order-service → rabbitmq (publicar eventos)               │  │
│  │  • product-service → rabbitmq (consumir eventos)             │  │
│  │                                                                  │  │
│  └─────────────────────────────────────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📊 Tabla de Puertos y Servicios

| Servicio | Puerto Local | Puerto Interno | Función |
|----------|-------------|---|---------|
| **user-service** | 8080 | 8080 | Gestión de usuarios |
| **product-service** | 8081 | 8081 | Catálogo de productos |
| **order-service** | 8082 | 8082 | Gestión de órdenes |
| **cart-service** | 8083 | 8083 | Carritos de compra |
| **postgres-users** | 5432 | 5432 | BD de usuarios |
| **postgres-products** | 5433 | 5432 | BD de productos |
| **postgres-carts** | 5434 | 5432 | BD de carritos |
| **postgres-orders** | 5435 | 5432 | BD de órdenes |
| **rabbitmq** AMQP | 5672 | 5672 | Message broker |
| **rabbitmq** UI | 15672 | 15672 | Management console |

---

## ⚡ Comandos Más Usados

### Inicio y Control

```bash
# Levantar todo
docker-compose up -d --build

# Ver estado
docker-compose ps

# Detener todo
docker-compose down

# Reiniciar un servicio
docker-compose restart product-service

# Ver logs (tiempo real)
docker-compose logs -f product-service
```

### Base de Datos

```bash
# Conectar a una BD
docker-compose exec postgres-products psql -U postgres -d sofkify_products_bd

# Ver qué hay en una tabla
docker-compose exec postgres-products psql -U postgres -d sofkify_products_bd -c "SELECT * FROM products;"

# Backup de una BD
docker-compose exec postgres-products pg_dump -U postgres sofkify_products_bd > backup.sql

# Restore de un backup
docker-compose exec -T postgres-products psql -U postgres sofkify_products_bd < backup.sql
```

### RabbitMQ

```bash
# Acceder a la UI
# http://localhost:15672 (guest/guest)

# Ver queues desde línea de comandos
docker-compose exec rabbitmq rabbitmqctl list_queues

# Ver exchanges
docker-compose exec rabbitmq rabbitmqctl list_exchanges

# Purge una queue (⚠️ borra mensajes)
docker-compose exec rabbitmq rabbitmqctl purge_queue product.stock.decrement.queue
```

### Limpieza

```bash
# Eliminar contenedores pero mantener datos
docker-compose down

# Limpiar todo (incluyendo volúmenes/datos)
docker-compose down -v

# Limpiar imágenes sin usar
docker image prune -a

# Limpiar volúmenes no usados
docker volume prune
```

---

## 🚀 Setup Inicial (5 minutos)

```bash
# 1. Clonar repo
git clone https://github.com/nico-salsa/Sofkify_BE.git
cd Sofkify_BE

# 2. Crear .env (opcional, usa valores por defecto)
cp .env.example .env

# 3. Levantar servicios
docker-compose up -d --build

# 4. Esperar a que todo esté healthy (30-60 segundos)
docker-compose ps

# 5. Probar servicios
curl http://localhost:8081/api/v1/products
curl http://localhost:15672  # RabbitMQ UI (guest/guest)
```

---

## 🔔 Señales de que está OK

✅ Checklist de validación:

```bash
# Todos deberían estar en estado "running" o "up"
docker-compose ps

# Todos deberían estar "healthy"
docker-compose ps --format "table {{.Service}}\t{{.Status}}" | grep -v healthy && echo "❌ Algo no está healthy" || echo "✓ Todo healthy"

# Los servicios deberían responder
curl http://localhost:8081/api/v1/products && echo "✓ Product Service OK" || echo "❌ Product Service no responde"

# PostgreSQL debería estar accesible
docker-compose exec postgres-users psql -U postgres -d sofkify_users -c "SELECT 1" && echo "✓ PostgreSQL OK" || echo "❌ PostgreSQL error"

# RabbitMQ debería estar accesible
curl -u guest:guest http://localhost:15672/api/overview && echo "✓ RabbitMQ OK" || echo "❌ RabbitMQ error"
```

---

## 📝 Variables de Entorno por Servicio

### Todos los Servicios (heredan)
```env
JAVA_TOOL_OPTIONS=-Dspring.profiles.active=docker
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

### Solo user-service
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-users:5432/sofkify_users
```

### Solo product-service
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-products:5433/sofkify_products_bd
SPRING_RABBITMQ_HOST=rabbitmq
```

### Solo order-service
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-orders:5435/sofkify_orders_bd
SPRING_RABBITMQ_HOST=rabbitmq
```

### Solo cart-service
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-carts:5434/sofkify_cars_bd
```

---

## 🔗 Cómo se Comunican los Servicios

### Tránsito Directo (REST)
```
┌──────────────┐      HTTP      ┌──────────────────┐
│cart-service  │──────────────>│product-service   │
│(8083)        │  /api/v1/...  │(8081)            │
└──────────────┘                └──────────────────┘
  URL: http://product-service:8081/api/v1/products
```

### Tránsito Asíncrono (RabbitMQ)
```
┌──────────────┐                ┌──────────────────┐
│order-service │─────────────>│product-service   │
│              │  OrderCreated │                  │
└──────────────┘  Event        └──────────────────┘
          │
          └─> RabbitMQ (intermediario)
              Exchange: order.exchange
              Queue: product.stock.decrement.queue
```

---

## 🆘 Problemas Comunes - Quick Fix

| Problema | Síntoma | Solución |
|----------|---------|----------|
| Servicios no inician | Logs con errores | `docker-compose logs <service>` |
| BD no conecta | `Connection refused` | Esperar a que healthcheck pase (30s) |
| Puerto en uso | `Address already in use` | Cambiar puerto en `.env` |
| RabbitMQ offline | Servicios no publican | `docker-compose restart rabbitmq` |
| Volumen corrupto | BD vacía o vieja | `docker volume rm sofkify_postgres-*-data` |
| Sin espacio | Disk full | `docker system prune -a --volumes` |

---

## 📚 Documentación por Tema

| Tema | Archivo |
|------|---------|
| Arquitectura general | [DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md) |
| Cómo agregar nuevos servicios | [DOCKER_EXTENSIBILITY.md](docs_IA/DOCKER_EXTENSIBILITY.md) |
| Solucionar problemas | [DOCKER_TROUBLESHOOTING.md](docs_IA/DOCKER_TROUBLESHOOTING.md) |
| Guía de uso rápido | [DOCKER.md](DOCKER.md) |
| Configuración principal | [docker-compose.yml](docker-compose.yml) |
| Variables de entorno | [.env.example](.env.example) |

---

## 🎯 Flujos de Caso de Uso

### Crear un Producto

```
1. POST /api/v1/products (product-service:8081)
   └─> Valida datos
   └─> Guarda en postgres-products
   └─> Retorna producto creado
```

### Agregar Item al Carrito

```
1. POST /api/v1/carts/{id}/items (cart-service:8083)
   └─> Valida carrito existe
   └─> Valida producto existe (HTTP → product-service:8081)
   └─> Valida stock disponible
   └─> Guarda en postgres-carts
   └─> Retorna carrito actualizado
```

### Crear Orden desde Carrito

```
1. POST /api/v1/orders (order-service:8082)
   └─> Valida carrito confirmado
   └─> Crea orden en postgres-orders
   └─> PUBLICA evento: "order.created" → RabbitMQ

2. product-service CONSUME evento
   └─> Recibe "order.created"
   └─> Decrementa stock en postgres-products
   └─> PUBLICA evento: "stock.decremented" → RabbitMQ

3. order-service CONSUME evento (opcional)
   └─> Recibe "stock.decremented"
   └─> Actualiza estado de orden
```

---

## 🧪 Testing Rápido

```bash
# Test que todo está en pie
docker-compose ps | grep -E "healthy|up"

# Test de conectividad entre contenedores
docker-compose exec cart-service curl http://product-service:8081/api/v1/products

# Test de healthcheck de BD
docker-compose exec postgres-users pg_isready

# Test de RabbitMQ
docker-compose exec rabbitmq rabbitmq-diagnostics ping_alarms

# Test de latencia
time curl http://localhost:8081/api/v1/products

# Test de carga
for i in {1..100}; do curl -s http://localhost:8081/api/v1/products > /dev/null; done && echo "OK"
```

---

## 📈 Monitoreo

### En Tiempo Real
```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs filtrados por palabra clave
docker-compose logs -f | grep -i error

# Ver métricas de recursos
docker stats

# Ver estado del network
docker network inspect sofkify-network
```

### Dashboards
- RabbitMQ: `http://localhost:15672` (guest/guest)
- Prometheus: `http://localhost:9090` (si está habilitado)
- Grafana: `http://localhost:3001` (si está habilitado)

---

## 🎓 Conceptos Clave

**Bridge Network**: RED PRIVADA que conecta todos los contenedores. 
- Internamente: `postgres-products:5432` (funciona)
- Desde host: `localhost:5433` (puerto mapeado)

**Volúmenes**: ALMACENAMIENTO PERSISTENTE entre reinicios.
- Los datos se pierden si haces `docker-compose down -v`
- Se mantienen si haces `docker-compose down` (sin `-v`)

**Health Checks**: VALIDACIONES para saber si un servicio está listo.
- PostgreSQL usa `pg_isready`
- RabbitMQ usa `rabbitmq-diagnostics ping_alarms`
- Spring Boot puede usar `/actuator/health`

**Depends On**: DEPENDENCIAS entre servicios.
- `product-service` espera que `postgres-products` esté healthy ANTES de iniciar
- Sin `condition: service_healthy`, no espera

---

## 📖 Para Aprender Más

- 📚 [Docker Compose Official Docs](https://docs.docker.com/compose/)
- 📚 [PostgreSQL Docker Best Practices](https://hub.docker.com/_/postgres)
- 📚 [RabbitMQ Docker Guide](https://hub.docker.com/_/rabbitmq)
- 📚 [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- 📚 [Microservices Patterns](https://microservices.io/)

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Mantenido por**: Equipo Sofkify Backend

