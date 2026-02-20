# Docker Architecture - Sofkify Backend E-commerce

## 📋 Descripción General

La arquitectura de Docker para Sofkify_BE implementa una infraestructura containerizada completa con:
- **4 Bases de Datos PostgreSQL** independientes (una por microservicio)
- **1 Message Broker RabbitMQ** para comunicación asíncrona
- **4 Microservicios Spring Boot** desacoplados
- **1 Red interna** para comunicación segura entre contenedores
- **Healthchecks** para garantizar disponibilidad
- **Variables de entorno** para configuración flexible

---

## 🏗️ Topología de Servicios

```
┌─────────────────────────────────────────────────────────────────┐
│                     SOFKIFY-NETWORK (Bridge)                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐  │
│  │  USER-SERVICE    │  │ PRODUCT-SERVICE  │  │ ORDER-SERVICE│  │
│  │    (8080)        │  │    (8081)        │  │  (8082)      │  │
│  │   Java 21        │  │   Java 17        │  │  Java 17     │  │
│  └────────┬─────────┘  └────────┬─────────┘  └──────┬───────┘  │
│           │                    │                     │          │
│           │                    │                     │          │
│  ┌────────▼─────────┐  ┌───────▼──────────┐  ┌──────▼────────┐ │
│  │ POSTGRES-USERS   │  │POSTGRES-PRODUCTS │  │POSTGRES-ORDERS│ │
│  │   (5432 local)   │  │  (5433 local)    │  │ (5435 local)  │ │
│  │ sofkify_users    │  │sofkify_products_ │  │sofkify_orders │ │
│  │                  │  │      bd          │  │     _bd       │ │
│  └────────────────── ┘  └──────────────────┘  └───────────────┘ │
│                                                                 │
│  ┌──────────────────┐                                          │
│  │  CART-SERVICE    │                                          │
│  │    (8083)        │                                          │
│  │   Java 17        │                                          │
│  └────────┬─────────┘                                          │
│           │                                                    │
│  ┌────────▼─────────┐                                          │
│  │ POSTGRES-CARTS   │                                          │
│  │   (5434 local)   │                                          │
│  │ sofkify_cars_bd  │                                          │
│  └──────────────────┘                                          │
│                                                                 │
│  ┌────────────────────────────────────┐                        │
│  │    RABBITMQ (Message Broker)       │                        │
│  │    AMQP: 5672                      │                        │
│  │    Management UI: 15672            │                        │
│  │    User: guest / Pass: guest       │                        │
│  └────────────────────────────────────┘                        │
│                                                                 │
│  ┌────────────────────────────────────┐                        │
│  │   FRONTEND (Futuro)                │                        │
│  │    Puerto 3000                     │                        │
│  │    Reactjs / Nextjs                │                        │
│  └────────────────────────────────────┘                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

        ▼ Puertos Expuestos al Host
        
    5432 ─────> postgres-users
    5433 ─────> postgres-products  
    5434 ─────> postgres-carts
    5435 ─────> postgres-orders
    5672 ─────> RabbitMQ AMQP
    15672 ───> RabbitMQ Management UI
    8080 ─────> User Service
    8081 ─────> Product Service
    8082 ─────> Order Service
    8083 ─────> Cart Service
    3000 ─────> Frontend (Futuro)
```

---

## 📊 Servicios y Configuración

### 1. PostgreSQL Instances

Cada microservicio tiene su propia instancia de PostgreSQL independiente para garantizar desacoplamiento de datos.

#### postgres-users
- **Puerto Local**: 5432
- **Base de Datos**: `sofkify_users`
- **Usuario**: `postgres` (configurable)
- **Contraseña**: `postgres` (configurable)
- **Volumen**: `postgres-users-data`
- **Consumidor**: `user-service`
- **Función**: Gestión de usuarios, autenticación, roles

#### postgres-products
- **Puerto Local**: 5433
- **Base de Datos**: `sofkify_products_bd`
- **Usuario**: `postgres` (configurable)
- **Contraseña**: `postgres` (configurable)
- **Volumen**: `postgres-products-data`
- **Consumidor**: `product-service`
- **Función**: Catálogo de productos, inventario, stock
- **Eventos Consumidos**: `order.created` → decrementa stock

#### postgres-carts
- **Puerto Local**: 5434
- **Base de Datos**: `sofkify_cars_bd`
- **Usuario**: `postgres` (configurable)
- **Contraseña**: `postgres` (configurable)
- **Volumen**: `postgres-carts-data`
- **Consumidor**: `cart-service`
- **Función**: Carritos de compra, items, confirmación

#### postgres-orders
- **Puerto Local**: 5435
- **Base de Datos**: `sofkify_orders_bd`
- **Usuario**: `postgres` (configurable)
- **Contraseña**: `postgres` (configurable)
- **Volumen**: `postgres-orders-data`
- **Consumidor**: `order-service`
- **Función**: Gestión de órdenes, states, historial
- **Eventos Producidos**: `order.created` → publica a RabbitMQ

#### Script de Inicialización
- **Archivo**: `init-db.sql`
- **Ubicación en Contenedor**: `/docker-entrypoint-initdb.d/01-init.sql`
- **Ejecución**: Automática al crear el contenedor por primera vez
- **Función**: Crear bases de datos, tablas, usuarios init

### 2. RabbitMQ - Message Broker

Intermediario asíncrono para comunicación entre microservicios desacoplados.

#### Configuración
- **Imagen**: `rabbitmq:3.13-management-alpine`
- **Puerto AMQP**: 5672 (interno y expuesto)
- **Puerto Management UI**: 15672 (acceso web)
- **Usuario por defecto**: `guest`
- **Contraseña por defecto**: `guest`
- **Virtual Host**: `/` (por defecto)

#### Volúmenes
- **rabbitmq-data**: Persistencia de mensajes y configuración
- **rabbitmq-logs**: Logs de RabbitMQ

#### Management UI
- **URL**: `http://localhost:15672`
- **Usuario**: `guest`
- **Contraseña**: `guest`
- **Funciones**:
  - Monitorear queues
  - Ver exchanges
  - Inspeccionar mensajes
  - Configurar usuarios y permisos

#### Exchanges y Queues (Según especificación)
```
Exchange: order.exchange
  ├─ Routing Key: order.created
  └─ Queue: product.stock.decrement.queue (product-service)

Exchange: product.exchange
  ├─ Routing Key: product.stock.decremented
  └─ Queue: order.stock.update.queue (order-service)
```

### 3. Spring Boot Microservices

#### user-service
```
Puerto:             8080
Java Version:       21
Base de Datos:      postgres-users (5432)
Perfil Spring:      docker
Healthcheck:        /actuator/health
DDL Auto:           validate
Rol en Sistema:     Gestión de identidad
Dependencias:       postgres-users
```

**Variables de Entorno**:
```
JAVA_TOOL_OPTIONS=-Dspring.profiles.active=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-users:5432/sofkify_users
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

#### product-service
```
Puerto:             8081
Java Version:       17
Base de Datos:      postgres-products (5433)
Message Broker:     rabbitmq:5672
Perfil Spring:      docker
Healthcheck:        /actuator/health
DDL Auto:           validate
Rol en Sistema:     Catálogo y inventario
Dependencias:       postgres-products, rabbitmq
Consumer:           order.created → decrementa stock
```

**Variables de Entorno**:
```
JAVA_TOOL_OPTIONS=-Dspring.profiles.active=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-products:5433/sofkify_products_bd
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

#### order-service
```
Puerto:             8082
Java Version:       17
Base de Datos:      postgres-orders (5435)
Message Broker:     rabbitmq:5672
Perfil Spring:      docker
Healthcheck:        /actuator/health
DDL Auto:           validate
Rol en Sistema:     Gestión de órdenes
Dependencias:       postgres-orders, rabbitmq
Producer:           order.created → publica evento
Consumer:           product.stock.decremented → actualiza orden
```

**Variables de Entorno**:
```
JAVA_TOOL_OPTIONS=-Dspring.profiles.active=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-orders:5435/sofkify_orders_bd
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

#### cart-service
```
Puerto:             8083
Java Version:       17
Base de Datos:      postgres-carts (5434)
Perfil Spring:      docker
Healthcheck:        /actuator/health
DDL Auto:           validate
Rol en Sistema:     Gestión de carritos
Dependencias:       postgres-carts
Comunicación:       REST hacia Product Service (validación de stock)
```

**Variables de Entorno**:
```
JAVA_TOOL_OPTIONS=-Dspring.profiles.active=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-carts:5434/sofkify_cars_bd
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

---

## 🔄 Flujo de Comunicación

### Comunicación Síncrona (REST)

```
┌─────────────────┐  HTTP  ┌──────────────────┐
│ cart-service    │─────>  │ product-service  │
│ (Validar stock) │<─────  │ (Consultar stock)│
└─────────────────┘        └──────────────────┘

Puerto interno de la red: product-service:8081
Endpoint: GET /api/v1/products/{id}
```

### Comunicación Asíncrona (RabbitMQ)

```
┌─────────────────┐      order.created      ┌──────────────────┐
│ order-service   │─────────────────────>   │ product-service  │
│ (Publisher)     │                         │ (Consumer)       │
└─────────────────┘      Decrementa Stock   └──────────────────┘
      │
      │
  Exchange: order.exchange
  Routing Key: order.created
  Queue: product.stock.decrement.queue


┌──────────────────┐    product.stock      ┌─────────────────┐
│ product-service  │    .decremented       │ order-service   │
│ (Publisher)      │─────────────────────> │ (Consumer)      │
└──────────────────┘  Actualiza estado     └─────────────────┘
```

---

## 🏥 Health Checks

Los healthchecks garantizan que los servicios estén disponibles antes de iniciar dependientes.

### PostgreSQL Healthcheck
```bash
pg_isready -U postgres -d sofkify_users
```

**Configuración**:
- **Intervalo**: 10 segundos
- **Timeout**: 5 segundos
- **Reintentos**: 5
- **Período de inicio**: 10 segundos

### RabbitMQ Healthcheck
```bash
rabbitmq-diagnostics ping_alarms
```

**Configuración**:
- **Intervalo**: 10 segundos
- **Timeout**: 5 segundos
- **Reintentos**: 5
- **Período de inicio**: 10 segundos

### Estrategia de Dependencias
```yaml
user-service:
  - depends_on: [postgres-users]
    condition: service_healthy

product-service:
  - depends_on: [postgres-products, rabbitmq]
    condition: service_healthy

order-service:
  - depends_on: [postgres-orders, rabbitmq]
    condition: service_healthy

cart-service:
  - depends_on: [postgres-carts]
    condition: service_healthy
```

---

## 📝 Variables de Entorno

### Base de Datos
| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_USER` | `postgres` | Usuario PostgreSQL |
| `DB_PASSWORD` | `postgres` | Contraseña PostgreSQL |
| `USERS_DB_NAME` | `sofkify_users` | Nombre BD de usuarios |
| `PRODUCTS_DB_NAME` | `sofkify_products_bd` | Nombre BD de productos |
| `CARTS_DB_NAME` | `sofkify_cars_bd` | Nombre BD de carritos |
| `ORDERS_DB_NAME` | `sofkify_orders_bd` | Nombre BD de órdenes |
| `DB_HOST_USERS` | `postgres-users` | Host del contenedor BD de usuarios |
| `DB_HOST_PRODUCTS` | `postgres-products` | Host del contenedor BD de productos |
| `DB_HOST_CARTS` | `postgres-carts` | Host del contenedor BD de carritos |
| `DB_HOST_ORDERS` | `postgres-orders` | Host del contenedor BD de órdenes |
| `POSTGRES_USERS_PORT` | `5432` | Puerto expuesto BD de usuarios |
| `POSTGRES_PRODUCTS_PORT` | `5433` | Puerto expuesto BD de productos |
| `POSTGRES_CARTS_PORT` | `5434` | Puerto expuesto BD de carritos |
| `POSTGRES_ORDERS_PORT` | `5435` | Puerto expuesto BD de órdenes |

### RabbitMQ
| Variable | Default | Descripción |
|----------|---------|-------------|
| `RABBITMQ_HOST` | `rabbitmq` | Host del contenedor RabbitMQ |
| `RABBITMQ_PORT` | `5672` | Puerto AMQP de RabbitMQ |
| `RABBITMQ_MANAGEMENT_PORT` | `15672` | Puerto UI de gestión de RabbitMQ |
| `RABBITMQ_USER` | `guest` | Usuario RabbitMQ |
| `RABBITMQ_PASSWORD` | `guest` | Contraseña RabbitMQ |

### Microservicios
| Variable | Default | Descripción |
|----------|---------|-------------|
| `USER_SERVICE_PORT` | `8080` | Puerto expuesto user-service |
| `PRODUCT_SERVICE_PORT` | `8081` | Puerto expuesto product-service |
| `ORDER_SERVICE_PORT` | `8082` | Puerto expuesto order-service |
| `CART_SERVICE_PORT` | `8083` | Puerto expuesto cart-service |

---

## 🚀 Guía de Uso

### Configuración Inicial

1. **Crear archivo `.env`** desde template:
```bash
cp .env.example .env
```

2. **Personalizar variables** si es necesario (generalmente no necesario):
```bash
# .env
DB_USER=postgres
DB_PASSWORD=mi-contraseña-segura
RABBITMQ_PASSWORD=mi-contraseña-rabbitmq
```

### Levantar los Servicios

```bash
# Construir imágenes y levantar todos los contenedores
docker-compose up --build

# Levantar en background
docker-compose up -d --build

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f product-service
```

### Verificar Estado de Servicios

```bash
# Estado de todos los contenedores
docker-compose ps

# Healthcheck detallado
docker-compose ps --format "table {{.Service}}\t{{.Status}}"
```

### Acceder a RabbitMQ Management UI

```
URL: http://localhost:15672
Usuario: guest
Contraseña: guest
```

### Conectar a las Bases de Datos

```bash
# Usuarios
psql -h localhost -p 5432 -U postgres -d sofkify_users

# Productos
psql -h localhost -p 5433 -U postgres -d sofkify_products_bd

# Carritos
psql -h localhost -p 5434 -U postgres -d sofkify_cars_bd

# Órdenes
psql -h localhost -p 5435 -U postgres -d sofkify_orders_bd
```

### Detener los Servicios

```bash
# Detener sin eliminar volúmenes (datos persisten)
docker-compose down

# Detener y eliminar volúmenes (CUIDADO: se eliminan datos)
docker-compose down -v

# Detener servicios específicos
docker-compose stop cart-service product-service
```

### Rebuild Selectivo

```bash
# Recompilar solo product-service
docker-compose build product-service

# Recompilar y levantar solo product-service
docker-compose up -d --build product-service
```

---

## 🔧 Configuración de Spring Boot

### Archivo `application.yml` (ejemplo para product-service)

```yaml
spring:
  application:
    name: product-service
  
  datasource:
    url: jdbc:postgresql://${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
  
  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:validate}
    database-platform: ${SPRING_JPA_DATABASE_PLATFORM}
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20
          fetch_size: 50
  
  rabbitmq:
    host: ${SPRING_RABBITMQ_HOST:localhost}
    port: ${SPRING_RABBITMQ_PORT:5672}
    username: ${SPRING_RABBITMQ_USERNAME:guest}
    password: ${SPRING_RABBITMQ_PASSWORD:guest}
    virtual-host: /

server:
  port: 8081
  servlet:
    context-path: /api/v1

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

---

## 📦 Volúmenes Persistentes

Los volúmenes permiten que los datos persistan entre reinicios de contenedores.

| Volumen | Contenedor | Propósito |
|---------|-----------|----------|
| `postgres-users-data` | postgres-users | Datos de BD de usuarios |
| `postgres-products-data` | postgres-products | Datos de BD de productos |
| `postgres-carts-data` | postgres-carts | Datos de BD de carritos |
| `postgres-orders-data` | postgres-orders | Datos de BD de órdenes |
| `rabbitmq-data` | rabbitmq | Datos de mensajes RabbitMQ |
| `rabbitmq-logs` | rabbitmq | Logs de RabbitMQ |

### Limpieza de Volúmenes

```bash
# Listar volúmenes
docker volume ls

# Eliminar volumen específico (servicio detenido)
docker volume rm sofkify_postgres-users-data

# Eliminar todos los volúmenes no usados
docker volume prune
```

---

## 🌐 Red Docker

### sofkify-network (Bridge Network)

Red interna que permite comunicación entre contenedores usando nombres de servicio.

**Características**:
- Aislamiento: Contenedores en la red pueden comunicarse
- DNS interno: Usa nombres de servicio como hosts
- Seguridad: No expone puertos internamente a menos que se especifique

**Comunicación Interna**:
- product-service → postgres-products (puerto 5432, no expuesto)
- cart-service → product-service (puerto 8081, no expuesto)
- order-service → rabbitmq (puerto 5672, no expuesto)

**Ventaja**: Los microservicios se comunican internamente sin exponer puertos, solo los puertos finales están expuestos al host.

---

## 🔐 Seguridad

### Buenas Prácticas Implementadas

1. **Red Aislada**: Sofkify-network aisla contenedores del exterior
2. **Credenciales en Env**: Contraseñas en `.env`, no en código
3. **Usuarios BD**: Cada servicio puede usar el mismo usuario (postgres) compartido
4. **RabbitMQ Auth**: Usuario y contraseña configurables
5. **Restart Policy**: `unless-stopped` para recuperación automática

### Mejoras de Seguridad (Producción)

1. **Usuarios BD Segregados**:
```yaml
# Crear usuarios separados por servicio
postgres-users:
  environment:
    POSTGRES_USER: user_svc
    POSTGRES_PASSWORD: ${USER_SVC_PASSWORD}

postgres-products:
  environment:
    POSTGRES_USER: product_svc
    POSTGRES_PASSWORD: ${PRODUCT_SVC_PASSWORD}
```

2. **RabbitMQ Seguro**:
```yaml
rabbitmq:
  environment:
    RABBITMQ_DEFAULT_USER: ${RABBITMQ_ADMIN_USER}
    RABBITMQ_DEFAULT_PASS: ${RABBITMQ_ADMIN_PASS}
    # Crear usuarios por servicio con permisos limitados
```

3. **SSL/TLS**:
- Habilitar SSL en PostgreSQL (conexiones cifradas)
- Habilitar AMQPS en RabbitMQ

4. **Secretos en Producción**:
- Usar Docker Secrets en Swarm
- Usar Docker Compose overrides para env segura
- Usar gestores de secretos (Vault, AWS Secrets Manager)

---

## 📈 Escalabilidad

### Agregar Frontend (Puerto 3000)

El docker-compose incluye una sección comentada para el frontend. Para habilitarla:

1. **Descomentar servicio frontend**:
```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
  container_name: frontend
  ports:
    - "${FRONTEND_PORT:-3000}:3000"
  environment:
    REACT_APP_API_URL: http://localhost:8000
  depends_on:
    - user-service
    - product-service
    - cart-service
    - order-service
  networks:
    - sofkify-network
```

2. **Crear estructura del frontend**:
```
frontend/
├── package.json
├── Dockerfile
├── src/
│   ├── App.tsx
│   └── ...
└── public/
```

3. **Ejemplo Dockerfile para React**:
```dockerfile
FROM node:20-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
```

### Agregar API Gateway (Puerto 8000)

Para gestionar rutas de los microservicios:

```yaml
api-gateway:
  image: nginx:alpine
  container_name: api-gateway
  ports:
    - "8000:80"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf:ro
  depends_on:
    - user-service
    - product-service
    - order-service
    - cart-service
  networks:
    - sofkify-network
```

### Agregar Monitoreo (Prometheus + Grafana)

```yaml
prometheus:
  image: prom/prometheus:latest
  ports:
    - "9090:9090"
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
  networks:
    - sofkify-network

grafana:
  image: grafana/grafana:latest
  ports:
    - "3001:3000"
  environment:
    GF_SECURITY_ADMIN_PASSWORD: admin
  depends_on:
    - prometheus
  networks:
    - sofkify-network
```

### Agregar Logging (ELK Stack)

```yaml
elasticsearch:
  image: docker.elastic.co/elasticsearch/elasticsearch:8.0.0
  environment:
    - discovery.type=single-node
  ports:
    - "9200:9200"
  networks:
    - sofkify-network

kibana:
  image: docker.elastic.co/kibana/kibana:8.0.0
  ports:
    - "5601:5601"
  depends_on:
    - elasticsearch
  networks:
    - sofkify-network
```

---

## 🐛 Troubleshooting

### El contenedor no inicia

```bash
# Ver logs detallados
docker-compose logs -f <service-name>

# Ver estado del contenedor
docker inspect <container-name>

# Recrear el contenedor
docker-compose up -d --force-recreate <service-name>
```

### La base de datos no acepta conexiones

```bash
# Verificar que el contenedor PostgreSQL esté corriendo
docker-compose ps postgres-users

# Verificar healthcheck
docker-compose ps --format "table {{.Service}}\t{{.Status}}"

# Conectar manualmente
docker exec -it postgres-users psql -U postgres -d sofkify_users
```

### RabbitMQ no se conecta

```bash
# Verificar que RabbitMQ esté corriendo y accesible
docker-compose exec rabbitmq rabbitmq-diagnostics ping_alarms

# Ver logs de RabbitMQ
docker-compose logs rabbitmq

# Acceder a Management UI
# http://localhost:15672 (guest/guest)
```

### Microservicio no conecta a base de datos

```bash
# Verificar URL de conexión
echo $SPRING_DATASOURCE_URL

# Probar conectividad desde contenedor
docker-compose exec product-service pg_isready -h postgres-products -p 5432

# Ver logs del servicio
docker-compose logs product-service
```

### Eliminar datos y empezar de cero

```bash
# Detener y eliminar TODO
docker-compose down -v

# Limpiar imágenes no usadas
docker image prune -a

# Reconstruir desde cero
docker-compose up -d --build
```

---

## 📊 Monitoreo Básico

### Comando para ver métricas de contenedores

```bash
# Ver uso de recursos (CPU, memoria, IO)
docker stats

# Ver estadísticas de red
docker network ls
docker network inspect sofkify-network
```

### Acceder a logs

```bash
# Logs de todo el stack
docker-compose logs

# Solo errores de los últimos 10 minutos
docker-compose logs --since 10m | grep -i error

# Logs en vivo de un servicio
docker-compose logs -f product-service

# Últimas 100 líneas
docker-compose logs --tail=100 order-service
```

---

## 📚 Referencias

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [RabbitMQ Docker Image](https://hub.docker.com/_/rabbitmq)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker Networking](https://docs.docker.com/network/)

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Mantenido por**: Equipo Sofkify Backend
