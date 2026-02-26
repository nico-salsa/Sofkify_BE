# Stack Tecnológico — Sofkify Backend

## Lenguaje y Plataforma

| Microservicio   | Java | JRE Runtime              |
|-----------------|------|--------------------------|
| user-service    | 21   | eclipse-temurin:21-jre   |
| product-service | 17   | eclipse-temurin:17-jre   |
| cart-service    | 17   | eclipse-temurin:17-jre   |
| order-service   | 17   | eclipse-temurin:17-jre   |

---

## Framework Principal

| Tecnología                           | Versión |
|--------------------------------------|---------|
| Spring Boot                          | 4.0.2   |
| Spring Dependency Management Plugin  | 1.1.7   |

### Módulos Spring Boot utilizados

| Módulo                            | Servicios                                 |
|-----------------------------------|-------------------------------------------|
| spring-boot-starter-webmvc        | Todos (REST controllers)                  |
| spring-boot-starter-data-jpa      | Todos (persistencia)                      |
| spring-boot-starter-validation    | Todos (validación de DTOs)                |
| spring-boot-starter-amqp          | product-service, order-service            |
| spring-boot-starter-test          | product-service, order-service            |

---

## Herramienta de Build

| Tecnología | Versión |
|------------|---------|
| Gradle     | 8.x     |

Plugins Gradle aplicados en todos los servicios:
- `org.springframework.boot`
- `io.spring.dependency-management`
- `java`

---

## Base de Datos

| Tecnología          | Versión         | Rol                                      |
|---------------------|-----------------|------------------------------------------|
| PostgreSQL          | 15-alpine       | Base de datos relacional                 |
| Spring Data JPA     | (via Boot 4.0.2)| ORM / repositorios                       |
| Hibernate           | (via Boot 4.0.2)| Implementación JPA                       |
| Flyway              | (via Boot 4.0.2)| Migraciones de base de datos             |

Cada microservicio tiene su propia instancia de PostgreSQL (base de datos independiente):

| Microservicio   | Base de datos          | Puerto externo |
|-----------------|------------------------|----------------|
| user-service    | `sofkify_users`        | 5432           |
| product-service | `sofkify_products_bd`  | 5433           |
| cart-service    | `sofkify_cars_bd`      | 5434           |
| order-service   | `sofkify_orders_bd`    | 5435           |

---

## Message Broker

| Tecnología                | Versión                   | Rol                                  |
|---------------------------|---------------------------|--------------------------------------|
| RabbitMQ                  | 3.12-management-alpine    | Broker de mensajería asíncrona       |
| Spring AMQP               | (via Boot 4.0.2)          | Integración con RabbitMQ             |
| spring-rabbit-test        | (via Boot 4.0.2)          | Testing de mensajería                |

---

## Serialización

| Tecnología                      | Servicios                         | Uso                              |
|---------------------------------|-----------------------------------|----------------------------------|
| jackson-databind                | product-service, order-service    | Serialización/deserialización JSON|
| jackson-datatype-jsr310         | product-service, order-service    | Soporte para tipos `java.time`   |

---

## Reducción de Boilerplate

| Tecnología | Versión              | Servicios                          |
|------------|----------------------|------------------------------------|
| Lombok     | (via Boot 4.0.2)     | user-service, product-service      |

---

## Testing

| Tecnología                  | Servicios                                  |
|-----------------------------|--------------------------------------------|
| JUnit 5 (JUnit Platform)    | Todos                                      |
| Spring Boot Test            | product-service, order-service             |
| Spring AMQP Test            | product-service, order-service             |

---

## Contenerización e Infraestructura

| Tecnología      | Versión          | Rol                                   |
|-----------------|------------------|---------------------------------------|
| Docker          | (multi-stage)    | Contenerización de microservicios      |
| Docker Compose  | 3.8              | Orquestación local del stack completo |

Imágenes base utilizadas:
- **Build stage**: `gradle:8-jdk17` / `gradle:8-jdk21`
- **Runtime stage**: `eclipse-temurin:17-jre-jammy` / `eclipse-temurin:21-jre-jammy`

---

## Contratos de API

| Tecnología | Rol                                     |
|------------|-----------------------------------------|
| OpenSpec   | Definición y validación de contratos REST |

---

## Arquitectura y Patrones

| Patrón / Principio         | Descripción                                                    |
|----------------------------|----------------------------------------------------------------|
| Arquitectura Hexagonal     | Ports & Adapters: domain sin dependencias de frameworks        |
| Clean Architecture         | Separación en capas: domain → application → infrastructure     |
| DDD Táctico                | Agregados, Entidades, Value Objects, Domain Events             |
| SOLID                      | Aplicado en todas las capas                                    |
| Microservicios             | Servicios independientes con base de datos y despliegue propios |
| Domain Events              | Comunicación asíncrona mediante eventos de dominio             |

---

## Puertos de los Servicios

| Microservicio   | Puerto |
|-----------------|--------|
| user-service    | 8080   |
| product-service | 8081   |
| order-service   | 8082   |
| cart-service    | 8083   |
| RabbitMQ AMQP   | 5672   |
| RabbitMQ UI     | 15672  |
