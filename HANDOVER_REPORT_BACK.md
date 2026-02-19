# HANDOVER REPORT - Sofkify (Backend)

## Resumen rápido
Repositorio: `Sofkify_BE`
Arquitectura: Microservicios Java Spring Boot (product-service, order-service, cart-service, user-service) con enfoque Hexagonal (ports & adapters). Persistencia: PostgreSQL (DB por servicio). Mensajería: RabbitMQ para eventos (OrderCreated → ProductService). Build: Gradle por servicio. Local: `docker-compose.yml` orquesta DB + RabbitMQ + servicios.

## Cómo correr localmente (rápido)
Desde la raíz `Sofkify_BE/`:

```bash
# Levantar toda la pila local (DBs, RabbitMQ, servicios si están en docker)
docker-compose up --build

# O ejecutar un servicio individual (ej. product-service)
cd product-service
./gradlew bootRun
```

## Componentes clave y ubicación
- Product consumer: `product-service/src/main/java/.../RabbitMQOrderCreatedConsumer.java`
- Stock decrement logic: `product-service/.../StockDecrementService.java`
- Product persistence adapter: `product-service/.../ProductPersistenceAdapter.java`
- Order event publisher: `order-service/.../RabbitMQEventPublisherAdapter.java`
- RestTemplate adapters / sync calls: `*/.../ProductServiceAdapter.java`, `CartServiceAdapter.java`
- Auth & users: `user-service/.../AuthenticationService.java`, `UserService.java`
- Configs: cada servicio tiene `application.yml`/`application.properties` en `src/main/resources` o en `bin/main` durante build.

## Problemas observados (prioridad alta → baja)
- RabbitMQ consumers no tienen DLQ ni políticas de retry estructuradas → riesgo de requeue infinito.
- StockDecrement requiere idempotencia (reprocessing de eventos) y manejo transaccional claro.
- Publicador de eventos puede beneficiarse de publisher confirms o patrón outbox para garantizar entrega.
- RestTemplate sync calls sin resiliencia (no hay circuit-breaker/retry)
- Falta de pruebas end-to-end para flujos de mensajería.

## Recomendaciones inmediatas
1. Añadir DLQ y retry policy para consumidores (product-service).  
2. Implementar idempotencia en `StockDecrementService` (persistir orderId procesado).  
3. Evaluar Outbox pattern o RabbitMQ publisher confirms en `RabbitMQEventPublisherAdapter`.  
4. Añadir `spring-retry` o `resilience4j` para llamadas sync entre servicios.  
5. Centralizar variables (URLs, creds) en `application-*.yml` y usar `VITE_API_BASE_URL`-like env vars para despliegues.

## Cómo generar documentación (Javadoc)
En cada servicio:

```bash
cd product-service
./gradlew javadoc
# o usar IntelliJ: Tools → Generate JavaDoc
```

Si prefieres, puedo inyectar bloques `/** ... */` en clases críticas (p.ej. consumers, use-cases).

## Tareas sugeridas (próximos PR)
- PR: agregar DLQ + retry en consumer (skeleton + tests).  
- PR: idempotency table + check in `StockDecrementService`.  
- PR: agregar `resilience4j` a adapters HTTP.  
- PR: integración de publisher confirms o outbox en order-service.

## Referencias rápidas en repo
- README general: `Sofkify_BE/README.md`  
- Docker compose (local): `Sofkify_BE/docker-compose.yml`  
- Deuda técnica y arquitectura: `Sofkify_BE/docs_IA/architecture.md`, `docs_IA/DEUDA_TECNICA.md`

---

Si quieres, procedo a: 1) crear un PR con DLQ skeleton en `product-service`, 2) agregar idempotency scaffold, 3) insertar Javadoc en clases críticas. ¿Cuál prefieres?
## Documentacion tecnica consolidada (desde /doc/backend)

### AuthenticationService
- Ubicacion: `Sofkify_BE/user-service/src/main/java/com/sofkify/userservice/application/service/AuthenticationService.java`
- Proposito: Encapsula la autenticacion validando credenciales contra repositorio y retornando el `User` autenticado.
- Responsabilidades:
  - Verificar existencia de usuario.
  - Comparar password almacenado y validar estado de la cuenta.
  - Lanzar `InvalidCredentialsException` o `AccountDisabledException` ante fallo.
- Metodo clave: `authenticate(String email, String password)`.
- Observaciones:
  - No genera JWT actualmente.
- Mejoras sugeridas:
  - Integrar Spring Security + JWT.
  - Hashing de password con BCrypt (si aun no aplica).
  - Rate limiting en endpoints de autenticacion.

### ProductPersistenceAdapter
- Ubicacion: `Sofkify_BE/product-service/src/main/java/com/sofkify/productservice/infrastructure/persistence/adapter/ProductPersistenceAdapter.java`
- Proposito: Implementa `ProductPersistencePort` para operaciones de BD de productos.
- Responsabilidades:
  - Mapear entre entidades JPA y modelo de dominio.
  - Ejecutar CRUD (save/find/findAll/actualizacion de stock).
- Consideraciones:
  - Evitar filtrar entidades JPA fuera del adapter.
  - Manejar `Optional` y traducir errores de infraestructura correctamente.
- Mejoras sugeridas:
  - Checks defensivos y mapeo de excepciones de BD.
  - Tests de mapeo y concurrencia en update de stock.

### ProductServiceAdapter (HTTP sincrono)
- Ubicacion de referencia: `Sofkify_BE/*/infrastructure/adapters/out/http/ProductServiceAdapter.java`
- Proposito: Consumir Product Service de forma sincrona via REST (ej. desde Cart Service).
- Responsabilidades:
  - Construir URL al servicio de productos.
  - Manejar errores HTTP y traducirlos a excepciones de dominio/infraestructura.
- Metodo clave: `getProductById(String id)`.
- Riesgos detectados:
  - URL potencialmente hardcodeada.
  - Sin resiliencia (retry/circuit-breaker/timeouts robustos).
- Mejoras sugeridas:
  - Externalizar base URL en config.
  - Agregar `resilience4j` o `spring-retry` + timeouts.
  - Tests de integracion con respuestas mockeadas.

### RabbitMQEventPublisherAdapter
- Ubicacion: `Sofkify_BE/order-service/src/main/java/com/sofkify/orderservice/infrastructure/adapters/out/messaging/RabbitMQEventPublisherAdapter.java`
- Proposito: Publicar eventos de dominio (`OrderCreatedEvent`) en RabbitMQ.
- Responsabilidades:
  - Resolver exchange/routing key desde `application.yml`.
  - Serializar evento a JSON.
  - Publicar con `rabbitTemplate.convertAndSend(...)`.
- Metodo clave: `publishOrderCreated(OrderCreatedEvent event)`.
- Consideraciones de falla:
  - Errores de serializacion/publicacion lanzan excepcion de infraestructura.
- Mejoras sugeridas:
  - Publisher confirms o patron outbox para mayor garantia de entrega.
  - Metricas de fallos/latencia de publicacion.

### RabbitMQOrderCreatedConsumer
- Ubicacion: `Sofkify_BE/product-service/src/main/java/com/sofkify/productservice/infrastructure/messaging/adapter/RabbitMQOrderCreatedConsumer.java`
- Proposito: Escuchar cola de stock-decrement, deserializar mensaje y delegar al caso de uso.
- Responsabilidades:
  - Deserializacion JSON a `OrderCreatedEventDTO`.
  - Logging de recepcion/proceso/error.
  - Delegacion a `HandleOrderCreatedUseCase`.
- Metodo importante: `handleOrderCreated(String message)` con `@RabbitListener`.
- Riesgos detectados:
  - `RuntimeException` en error puede causar reintentos/requeue indefinidos sin DLQ.
- Mejoras sugeridas:
  - Configurar DLQ y politicas de retry/backoff.
  - Canal para mensajes malformados.
  - Tests unit/integration de mensajeria.

### StockDecrementService
- Ubicacion: `Sofkify_BE/product-service/src/main/java/com/sofkify/productservice/application/service/StockDecrementService.java`
- Proposito: Implementar `HandleOrderCreatedUseCase` para descontar inventario al crear orden.
- Responsabilidades:
  - Recorrer items del evento y decrementar stock via `ProductPersistencePort`.
  - Validar existencia de producto y stock suficiente.
  - Registrar operaciones y errores.
- Metodo clave: `handleOrderCreated(OrderCreatedEventDTO event)`.
- Riesgos detectados:
  - Reintentos pueden duplicar decrementos si no hay idempotencia.
- Mejoras sugeridas:
  - Idempotencia por `orderId`.
  - Retry/DLQ para fallos transitorios.
  - Tests para stock insuficiente y fallas parciales.

### UserService
- Ubicacion: `Sofkify_BE/user-service/src/main/java/com/sofkify/userservice/application/service/UserService.java`
- Proposito: Servicio de aplicacion que implementa `UserServicePort`.
- Responsabilidades:
  - Validar precondiciones (ej. unicidad de email).
  - Crear y persistir usuarios via `UserRepositoryPort`.
  - Delegar autenticacion a `AuthenticationService`.
- Metodos clave:
  - `createUser(String email, String password, String name)`.
  - `findByEmail(String email)`.
  - `updateProfile(String userId, String newName, String newEmail)`.
- Mejoras sugeridas:
  - Mayor cobertura de pruebas de edge cases.
  - Mejor traduccion/manejo de excepciones de adapters.
