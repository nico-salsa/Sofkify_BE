# HANDOVER_REPORT

> Documento generado con apoyo de GitHub Copilot y validado manualmente contra el codigo del repositorio.

## 1. Resumen ejecutivo

`Sofkify_BE` es el backend de un MVP e-commerce basado en microservicios Spring Boot con arquitectura hexagonal (Ports and Adapters). El sistema cubre:

- Gestion de usuarios y autenticacion basica.
- Catalogo de productos.
- Gestion de carrito.
- Creacion y seguimiento de ordenes.
- Integracion asincrona por RabbitMQ para decremento de stock al crear orden.

El backend esta dividido en 4 servicios desplegables de forma independiente:

- `user-service` (puerto `8080`)
- `product-service` (puerto `8081`)
- `order-service` (puerto `8082`)
- `cart-service` (puerto `8083`)

## 2. Que hace el sistema (funcional)

## 2.1 Flujo funcional principal

1. Usuario se registra/inicia sesion en `user-service`.
2. Cliente consulta productos en `product-service`.
3. Cliente agrega productos al carrito en `cart-service`.
4. `order-service` crea orden a partir de un carrito.
5. `order-service` publica evento `OrderCreatedEvent` en RabbitMQ.
6. `product-service` consume el evento y descuenta stock.

## 2.2 Endpoints principales por servicio

`user-service` (`/api/users`)
- `POST /api/users`
- `POST /api/users/auth/login`
- `GET /api/users/{id}`
- `GET /api/users/email/{email}`
- `PUT /api/users/{id}`
- `POST /api/users/{id}/promote`
- `DELETE /api/users/{id}`

`product-service` (`/api/products`)
- `POST /api/products`
- `GET /api/products`
- `GET /api/products/{id}`

`cart-service` (`/api/carts`)
- `POST /api/carts/items`
- `GET /api/carts`
- `GET /api/carts/{cartId}`
- `PUT /api/carts/items/{cartItemId}`
- `DELETE /api/carts/items/{cartItemId}`

`order-service` (`/api/orders`)
- `POST /api/orders/from-cart/{cartId}`
- `GET /api/orders/{orderId}`
- `GET /api/orders/customer/{customerId}`
- `PUT /api/orders/{orderId}/status`

## 3. Como esta construido (arquitectura)

## 3.1 Estilo arquitectonico

- Arquitectura hexagonal por microservicio:
  - `domain`: entidades, reglas de negocio, puertos.
  - `application`: casos de uso, DTOs, servicios de aplicacion.
  - `infrastructure`: REST controllers, persistencia JPA, mensajeria RabbitMQ, clientes HTTP.
- DDD tactico basico con capas bien separadas.
- Comunicacion mixta:
  - Sincrona REST entre servicios (por ejemplo cart -> user/product, order -> cart).
  - Asincrona con RabbitMQ (order -> product).

## 3.2 Persistencia y mensajeria

- Base de datos: PostgreSQL.
- Esquema por servicio (segun practica actual del repo):
  - `sofkify_users`
  - `sofkify_products_bd`
  - `sofkify_cars_bd`
  - `sofkify_orders_bd`
- Mensajeria:
  - Exchange: `order.exchange`
  - Routing key principal: `order.created`
  - Cola principal de stock: `product.stock.decrement.queue`

## 3.3 Stack tecnico

- Java 17/21 (user-service usa toolchain 21; otros 17)
- Spring Boot 4.0.2
- Spring Data JPA
- PostgreSQL driver
- RabbitMQ (spring-amqp en product/order)
- Gradle Wrapper por servicio

## 4. Estructura del repositorio

Raiz:
- `user-service/`
- `product-service/`
- `cart-service/`
- `order-service/`
- `init-db.sql` (creacion parcial de bases)
- `README.md`

Cada microservicio contiene:
- `src/main/java/...`
- `src/main/resources/application.yml`
- `build.gradle`
- `gradlew`/`gradlew.bat`
- `Dockerfile`

## 5. Operacion local (estado actual heredado)

## 5.1 Dependencias externas

- PostgreSQL escuchando en `localhost:5432`
- RabbitMQ escuchando en `localhost:5672` (UI en `15672`)

## 5.2 Variables de entorno requeridas por servicio

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`

## 5.3 Arranque manual recomendado

Desde cada carpeta de servicio:

```bash
./gradlew bootRun
```

Nota: no hay `docker-compose.yml` versionado actualmente en el repo, por lo que infraestructura y servicios se levantan manualmente o con scripts locales.

## 6. Calidad, pruebas y observabilidad

- Testing: JUnit por microservicio (cobertura basica existente).
- Logs en nivel DEBUG en varios servicios.
- Manejo de excepciones con `GlobalExceptionHandler` por servicio.

## 7. Riesgos y deuda tecnica observada

- Seguridad:
  - Login basico sin JWT ni Spring Security completo.
- Integracion:
  - Dependencias entre servicios via URLs directas (sin service discovery/gateway).
- Operacion:
  - Sin compose estandar en repo para levantar stack completo.
- Funcional:
  - Flujos de pago/notificacion aun no implementados.

## 8. Checklist de handover para nuevo equipo

1. Confirmar version de Java por servicio (21 para user, 17 para los demas).
2. Levantar PostgreSQL y RabbitMQ locales.
3. Crear las 4 bases requeridas.
4. Exportar variables `DB_*` por terminal de cada servicio.
5. Levantar servicios en orden sugerido: user -> product -> cart -> order.
6. Probar flujo E2E minimo: crear usuario, crear producto, agregar al carrito, crear orden.

## 9. Estado del repo al entregar

- Proyecto funcional en modo MVP con 4 microservicios.
- Arquitectura y capas claramente separadas.
- Base lista para evolucionar a seguridad robusta, compose oficial y capacidades de produccion.

