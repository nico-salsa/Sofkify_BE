# Sofkify Backend E-commerce

## 📋 Descripción del Proyecto

**Sofkify_BE** es una plataforma de e-commerce implementada con microservicios siguiendo patrones de arquitectura hexagonal. El proyecto demuestra mejores prácticas de desarrollo de software con Spring Boot, comunicación asíncrona con RabbitMQ, y contenerización con Docker.

## 🎯 MVP (Producto Mínimo Viable)

### **Funcionalidades Principales:**
- ✅ **Gestión de Usuarios** - Registro, login, perfiles
- ✅ **Catálogo de Productos** - CRUD completo con gestión de stock
- ✅ **Carritos de Compra** - Agregar/actualizar/eliminar items
- ✅ **Gestión de Órdenes** - Creación desde carrito con estados
- ✅ **Comunicación Asíncrona** - Decremento automático de stock
- ✅ **Arquitectura Escalable** - Microservicios desacoplados

### **Flujo de Usuario Completo:**
1. **Registro/Login** → User Service valida credenciales
2. **Navegación Productos** → Product Service muestra catálogo
3. **Agrega al Carrito** → Cart Service gestiona items
4. **Confirma Compra** → Order Service crea orden
5. **Procesamiento Automático** → Product Service decrementa stock
6. **Seguimiento** → Order Service actualiza estados

## 🏗️ Arquitectura General

### **Microservicios Implementados:**

#### **🔐 User Service** (Puerto 8080)
- **Propósito**: Gestión de identidad y autenticación
- **Endpoints**: 7 endpoints (CRUD + login + promoción)
- **Base de Datos**: `sofkify_users`
- **Tecnologías**: Java 21, Spring Boot, PostgreSQL, Lombok

#### **🛒 Cart Service** (Puerto 8083)
- **Propósito**: Gestión de carritos de compra
- **Endpoints**: 5 endpoints (CRUD completo)
- **Base de Datos**: `sofkify_cars_bd`
- **Tecnologías**: Java 17, Spring Boot, PostgreSQL, Flyway

#### **📦 Product Service** (Puerto 8081)
- **Propósito**: Catálogo de productos y gestión de inventario
- **Endpoints**: 3 endpoints (CRUD básico)
- **Base de Datos**: `sofkify_products_bd`
- **Tecnologías**: Java 17, Spring Boot, PostgreSQL, RabbitMQ

#### **📋 Order Service** (Puerto 8082)
- **Propósito**: Gestión del ciclo de vida de órdenes
- **Endpoints**: 4 endpoints (CRUD + creación desde carrito)
- **Base de Datos**: `sofkify_orders_bd`
- **Tecnologías**: Java 17, Spring Boot, PostgreSQL, RabbitMQ

### **🔄 Comunicación entre Servicios:**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   User Service   │    │  Cart Service   │    │ Product Service  │
│   (8080)       │    │   (8083)       │    │   (8081)       │
│                 │    │                 │    │                 │
│  Autenticación   │    │  Gestión       │    │  Catálogo       │
│  Perfiles       │◄──►│  Carritos       │◄──►│  Inventario      │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         │                        │                        │
         │                        ▼                        ▼
         │              ┌─────────────────┐    ┌─────────────────┐
         │              │  Order Service  │    │   RabbitMQ     │
         │              │   (8082)       │    │  Message Broker │
         │              │                 │    │                 │
         └──────────────►│  Creación       │◄──►│  Eventos       │
                        │  Órdenes       │    │  Asíncronos     │
                        │                 │    │                 │
                        └─────────────────┘    └─────────────────┘
```

## 🛠️ Stack Tecnológico

### **Backend:**
- **Java**: 17-21 (dependiendo del servicio)
- **Spring Boot**: 4.0.2 (framework principal)
- **PostgreSQL**: Base de datos relacional
- **RabbitMQ**: Message broker para comunicación asíncrona
- **Gradle**: Gestión de dependencias y build
- **JUnit 5**: Framework de testing
- **Lombok**: Reducción de código boilerplate
- **Jackson**: Serialización/deserialización JSON
- **Flyway**: Migraciones de base de datos

### **Infraestructura:**
- **Docker**: Contenerización de todos los servicios
- **Docker Compose**: Orquestación completa
- **GitHub**: Control de versiones y CI/CD

## 🐳 Docker Architecture - Orquestación Completa ✅

### **INICIO RÁPIDO - Fase 1 Completada:**

> 🚀 **La orquestación completa está lista.** Ve a [DOCKER_MAESTRO.md](DOCKER_MAESTRO.md) para el punto de entrada.
> 
> ```bash
> cp .env.example .env
> docker-compose up -d --build
> docker-compose ps  # Verificar
> ```
> **Tiempo**: ~60 segundos hasta que todo esté operativo

**Documentación relacionada:**
- 📍 Punto de entrada: [DOCKER_MAESTRO.md](DOCKER_MAESTRO.md)
- 📋 Resumen Fase 1: [FASE_1_RESUMEN.md](FASE_1_RESUMEN.md)
- ✅ Validación Final: [FASE_1_VALIDACION_FINAL.md](FASE_1_VALIDACION_FINAL.md)
- 📖 Plan Fase 2 (Frontend): [FASE_2_PLAN.md](FASE_2_PLAN.md)
- 🔧 Guía Rápida: [DOCKER.md](DOCKER.md)

### **Arquitectura Containerizada Completa:**

Sofkify_BE incluye una arquitectura completa de Docker Compose con:

#### **Bases de Datos Independientes:**
- **postgres-users** (Puerto 5432): `sofkify_users` - Gestión de usuarios
- **postgres-products** (Puerto 5433): `sofkify_products_bd` - Catálogo y inventario
- **postgres-carts** (Puerto 5434): `sofkify_cars_bd` - Carritos de compra
- **postgres-orders** (Puerto 5435): `sofkify_orders_bd` - Gestión de órdenes

#### **Message Broker:**
- **RabbitMQ** (Puerto 5672 AMQP, 15672 Management UI)
- Management Console: `http://localhost:15672` (guest/guest)
- Gestión de eventos asíncronos entre servicios

#### **Microservicios (ya containerizados):**
- **user-service** (8080) - Java 21
- **product-service** (8081) - Java 17 + RabbitMQ Consumer
- **order-service** (8082) - Java 17 + RabbitMQ Producer
- **cart-service** (8083) - Java 17

#### **Red Interna:**
- **sofkify-network**: Red bridge privada para comunicación segura entre contenedores
- Health checks para cada servicio
- Dependencias declaradas para startup ordenado

### **Levantamiento Rápido:**

```bash
# 1. Clonar el repositorio
git clone https://github.com/nico-salsa/Sofkify_BE.git
cd Sofkify_BE

# 2. Configurar variables de entorno (opcional)
cp .env.example .env

# 3. Levantar todo
docker-compose up -d --build

# 4. Verificar estado
docker-compose ps

# 5. Acceder a servicios
# User: http://localhost:8080
# Products: http://localhost:8081
# Orders: http://localhost:8082
# Cart: http://localhost:8083
# RabbitMQ UI: http://localhost:15672
```

**Tiempo aproximado**: 60 segundos (primer levantamiento)

### **Documentación Docker Completa:**

Para documentación detallada sobre Docker, consultar:

| Documento | Propósito |
|-----------|----------|
| [DOCKER.md](DOCKER.md) | Guía de inicio rápido y uso diario |
| [docs_IA/DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md) | Arquitectura detallada de todos los servicios |
| [docs_IA/DOCKER_QUICK_REFERENCE.md](docs_IA/DOCKER_QUICK_REFERENCE.md) | Referencia rápida, diagramas, comandos |
| [docs_IA/DOCKER_EXTENSIBILITY.md](docs_IA/DOCKER_EXTENSIBILITY.md) | Cómo agregar nuevos servicios, Frontend, API Gateway, Monitoreo |
| [docs_IA/DOCKER_TROUBLESHOOTING.md](docs_IA/DOCKER_TROUBLESHOOTING.md) | Solución de problemas y debugging |
| [docs_IA/DOCKER_INDEX.md](docs_IA/DOCKER_INDEX.md) | Índice completo y learning path |

**📍 Para comenzar**: Ver [DOCKER.md](DOCKER.md)

---

## 📊 Alcance del MVP

### **Características Implementadas:**
- ✅ **Autenticación y Autorización** básica
- ✅ **Gestión de Catálogo de Productos** completa
- ✅ **Carritos de Compra** funcionales
- ✅ **Procesamiento de Órdenes** end-to-end
- ✅ **Comunicación Asíncrona** entre servicios (RabbitMQ)
- ✅ **Validaciones de Negocio** robustas
- ✅ **Manejo de Errores** consistente
- ✅ **Logging** y monitoreo básico
- ✅ **Dockerización Completa** con Docker Compose
- ✅ **Health Checks** para todos los servicios
- ✅ **Volúmenes Persistentes** para datos

### **Limitaciones Actuales:**
- ⚠️ **Autenticación sin JWT** (solo login básico)
- ⚠️ **Sin integración con pasarelas de pago**
- ⚠️ **Sin notificaciones por email/SMS**
- ⚠️ **Sin panel de administración**
- ⚠️ **Sin analytics o reportes**
- ⚠️ **Carritos no se limpian automáticamente**

## 🚀 Próximas Implementaciones

1. **🔐 Mejoras de Seguridad**
   - Implementar JWT para autenticación stateless
   - Integrar Spring Security
   - Agregar refresh tokens
   - Implementar 2FA opcional

2. **🛒 Limpieza Automática de Carritos**
   - Event-driven cleanup con RabbitMQ
   - Carritos abandonados por tiempo
   - Política de retención

3. **📋 Mejoras en Órdenes**
   - Integración con pasarelas de pago (Stripe, PayPal)
   - Notificaciones de estado por email
   - Cancelación automática por tiempo

### **Mediano Plazo**
4. **📦 Gestión Avanzada de Inventario**
   - Categorías de productos
   - Búsqueda y filtrado avanzado
   - Gestión de proveedores
   - Alertas de stock bajo

5. **📊 Analytics y Reportes**
   - Dashboard de ventas
   - Reportes de productos más vendidos
   - Métricas de usuario
   - Exportación de datos

6. **🔔 Sistema de Notificaciones**
   - Email transaccional
   - Notificaciones push (WebSocket)
   - Preferencias de usuario
   - Historial de notificaciones

### **Largo Plazo:**
7. **🛒 Carritos Avanzados**
   - Listas de deseos (wishlists)
   - Carritos compartidos
   - Descuentos y promociones
   - Recomendaciones de productos

8. **🌐 Expansión Multi-tenant**
   - Soporte para múltiples tiendas
   - Configuración por tenant
   - Aislamiento de datos
   - White-labeling

## 🐳 Dockerización Completa

### **Todos los servicios son Dockerizables:**
```bash
# Construir todos los servicios
./order-service/gradlew build
./product-service/gradlew build
./cart-service/gradlew build
./user-service/gradlew build

# Construir imágenes Docker
docker build -t order-service ./order-service
docker build -t product-service ./product-service
docker build -t cart-service ./cart-service
docker build -t user-service ./user-service
```

### **Docker Compose para Desarrollo:**
```yaml
version: '3.8'
services:
  # Base de datos
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: sofkify_ecommerce
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  # Message broker
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

  # Microservicios
  user-service:
    build: ./user-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_users
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
    depends_on:
      - postgres

  product-service:
    build: ./product-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_products_bd
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
      - SPRING_RABBITMQ_HOST=rabbitmq
    depends_on:
      - postgres
      - rabbitmq

  cart-service:
    build: ./cart-service
    ports:
      - "8083:8083"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_cars_bd
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
      - PRODUCT_SERVICE_URL=http://product-service:8081/api
      - USER_SERVICE_URL=http://user-service:8080/api
    depends_on:
      - postgres
      - product-service
      - user-service

  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_orders_bd
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
      - SPRING_RABBITMQ_HOST=rabbitmq
      - CART_SERVICE_URL=http://cart-service:8083/api
    depends_on:
      - postgres
      - rabbitmq
      - cart-service

volumes:
  postgres_data:
  rabbitmq_data:
```

## 🚀 Instalación y Ejecución

### **Prerrequisitos:**
- Java 17+ (para user-service se necesita Java 21)
- Docker y Docker Compose
- PostgreSQL 13+ (si se ejecuta sin Docker)
- RabbitMQ 3.8+ (si se ejecuta sin Docker)

### **Ejecución con Docker Compose (Recomendado):**
```bash
# Clonar repositorio
git clone <repository-url>
cd Sofkify_BE

# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### **Ejecución Local:**
```bash
# Cada servicio en su propia terminal
cd user-service && ./gradlew bootRun &
cd product-service && ./gradlew bootRun &
cd cart-service && ./gradlew bootRun &
cd order-service && ./gradlew bootRun &
```

## 📊 Arquitectura y Patrones

### **Patrones Implementados:**
- ✅ **Arquitectura Hexagonal** - Desacoplamiento de negocio
- ✅ **Domain-Driven Design** - Lógica de negocio centralizada
- ✅ **CQRS** (parcial) - Separación de lectura/escritura
- ✅ **Event-Driven Architecture** - Comunicación asíncrona
- ✅ **Repository Pattern** - Abstracción de persistencia
- ✅ **Dependency Injection** - Inversión de control
- ✅ **DTO Pattern** - Transferencia de datos limpia

### **Principios SOLID:**
- ✅ **S** - Responsabilidad única
- ✅ **O** - Abierto a extensión
- ✅ **L** - Sustitución de Liskov
- ✅ **I** - Segregación de interfaces
- ✅ **D** - Inversión de dependencias

## 🧪 Testing

### **Estrategia de Testing:**
- **Unit Tests**: Pruebas de lógica de negocio
- **Integration Tests**: Pruebas de integración entre capas
- **API Tests**: Pruebas de endpoints REST
- **Contract Tests**: Pruebas de contratos entre servicios

### **Comandos de Testing:**
```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con cobertura
./gradlew test jacocoTestReport

# Ejecutar tests específicos
./order-service/gradlew test
./product-service/gradlew test
./cart-service/gradlew test
./user-service/gradlew test
```

## 🔗 Documentación de APIs

### **Documentación por Servicio:**
- **[User Service](./user-service/README.md)** - Gestión de usuarios y autenticación
- **[Product Service](./product-service/README.md)** - Catálogo e inventario
- **[Cart Service](./cart-service/README.md)** - Carritos de compra
- **[Order Service](./order-service/README.md)** - Gestión de órdenes

### **📚 Documentación de Arquitectura y Desarrollo:**
- **[IA Workflow](./docs%20IA/AI_WORKFLOW.md)** - Estrategia de trabajo AI-first y flujo de desarrollo
- **[Arquitectura del Sistema](./docs%20IA/architecture.md)** - Principios arquitectónicos y patrones de diseño
- **[Contexto del Dominio](./docs%20IA/context.md)** - Reglas de negocio y modelo de dominio

### **🐳 Documentación Docker Completa (Fase 1 - Completada):**
- **[DOCKER_MAESTRO.md](DOCKER_MAESTRO.md)** ⭐ **PUNTO DE ENTRADA** - Visión general + guía de fases
- **[DOCKER.md](DOCKER.md)** - Guía rápida de inicio
- **[FASE_1_RESUMEN.md](FASE_1_RESUMEN.md)** - Lo que se completó en Fase 1
- **[FASE_1_VALIDACION_FINAL.md](FASE_1_VALIDACION_FINAL.md)** - Validaciones y checklists
- **[FASE_2_PLAN.md](FASE_2_PLAN.md)** - Plan para integración frontend (cuando Raúl termine)
- **[docs_IA/DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md)** - Arquitectura técnica detallada
- **[docs_IA/DOCKER_QUICK_REFERENCE.md](docs_IA/DOCKER_QUICK_REFERENCE.md)** - Referencia rápida y comandos
- **[docs_IA/DOCKER_EXTENSIBILITY.md](docs_IA/DOCKER_EXTENSIBILITY.md)** - Cómo extender con frontend, gateway, etc
- **[docs_IA/DOCKER_TROUBLESHOOTING.md](docs_IA/DOCKER_TROUBLESHOOTING.md)** - Solución de problemas
- **[docs_IA/DOCKER_VISUAL_FLOWS.md](docs_IA/DOCKER_VISUAL_FLOWS.md)** - Diagramas ASCII de arquitectura
- **[docs_IA/DOCKER_INDEX.md](docs_IA/DOCKER_INDEX.md)** - Índice maestro de documentación

#### **Archivos Generados por Agentes:**
- **docker-compose.yml** - Orquestación con 4 BDs, RabbitMQ, 4 microservicios (233 líneas)
- **.env.example** - Template de variables de entorno (60 líneas)
- **.dockerignore** - Exclusiones de build
- **docker-helper.sh** - Script interactivo con 12+ comandos (398 líneas)

### **API Gateway (Futuro):**
- **Endpoint Unificado**: `http://localhost:8080/api-gateway`
- **Documentación Swagger**: `/swagger-ui.html`
- **Rate Limiting** por cliente
- **Circuit Breaker** para resiliencia

## 🎉 Estado Actual del Proyecto

**Sofkify_BE es un MVP funcional con arquitectura enterprise-ready:**

- ✅ **4 microservicios** completamente funcionales
- ✅ **Comunicación asíncrona** implementada
- ✅ **Dockerización** completa
- ✅ **Documentación exhaustiva**
- ✅ **Testing integrado**
- ✅ **Base para escalar** y evolucionar

**¡Listo para producción y próximos desarrollos!** 🚀