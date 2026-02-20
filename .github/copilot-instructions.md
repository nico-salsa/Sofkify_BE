# Copilot Instructions - Sofkify Backend (Microservices)

## 🎯 Visión General del Proyecto

**Sofkify_BE** es una plataforma de e-commerce implementada con **microservicios** siguiendo **Arquitectura Hexagonal (Ports & Adapters)**, **Clean Architecture** y **DDD Táctico**. El sistema utiliza comunicación asíncrona mediante eventos de dominio para lograr bajo acoplamiento y alta cohesión entre servicios.

### Repositorio
- **Local**: `C:\Sofkify\Sofkify_BE`
- **Remoto**: `https://github.com/nico-salsa/Sofkify_BE.git`

---

## 🏗️ Arquitectura del Sistema

### Bounded Contexts (Microservicios)

#### **user-service** (Puerto 8080)
- Gestión de usuarios, autenticación y roles (Cliente, Admin)  
- Base de datos: `sofkify_users`
- Java 21

#### **product-service** (Puerto 8081)
- Catálogo de productos, gestión de stock e inventario
- Consumer de eventos de órdenes, Producer de eventos de stock
- Base de datos: `sofkify_products_bd`
- Java 17

#### **cart-service** (Puerto 8083)
- Gestión de carritos de compra
- Validación de disponibilidad de productos
- Base de datos: `sofkify_cars_bd`
- Java 17

#### **order-service** (Puerto 8082)
- Gestión del ciclo de vida de órdenes
- Producer de eventos de órdenes, Consumer de eventos de stock
- Base de datos: `sofkify_orders_bd`
- Java 17

---

## 🛠️ Stack Tecnológico

- **Java**: 17-21 (según microservicio)
- **Spring Boot**: 4.0.2
- **PostgreSQL**: Base de datos relacional para cada microservicio
- **RabbitMQ**: Message broker para comunicación asíncrona
- **Gradle**: Gestión de dependencias y build
- **JUnit 5**: Testing framework
- **Lombok**: Reducción de boilerplate (opcional)
- **Jackson**: Serialización JSON
- **Flyway**: Migraciones de base de datos
- **Docker**: Contenerización
- **OpenSpec**: Definición de contratos de API

---

## 📐 Principios Arquitectónicos (OBLIGATORIOS)

### 1. Arquitectura Hexagonal (Ports & Adapters)
- **Domain** no depende de frameworks
- **Application** coordina casos de uso
- **Infrastructure** implementa adaptadores técnicos
- Dependencias siempre apuntan **hacia el dominio**

### 2. Clean Architecture
- Separación clara de responsabilidades por capas
- Reglas de negocio encapsuladas en el dominio
- Inversión de dependencias mediante interfaces (Ports)

### 3. DDD Táctico
- **Agregados**: Cart, Order, Product, User
- **Entidades**: Con identidad única
- **Value Objects**: Objetos inmutables sin identidad
- **Domain Events**: Hechos de negocio que ya ocurrieron
- **Ubiquitous Language**: Lenguaje compartido entre negocio y código

### 4. SOLID
- **SRP**: Una clase, una responsabilidad
- **OCP**: Abierto para extensión, cerrado para modificación
- **LSP**: Sustitución de tipos
- **ISP**: Interfaces segregadas
- **DIP**: Depender de abstracciones, no de implementaciones

### 5. Bajo Acoplamiento / Alta Cohesión
- Microservicios independientes con su propia base de datos
- Comunicación mediante APIs REST (síncrona) y eventos (asíncrona)
- No compartir estado entre servicios

---

## 📂 Estructura Canónica de un Microservicio

```
src/main/java/com/sofkify/{service-name}/
├── domain/                          # Núcleo del negocio (sin dependencias externas)
│   ├── model/                       # Agregados, entidades
│   │   ├── Cart.java               # Ejemplo de agregado
│   │   └── CartItem.java           # Ejemplo de entidad hija
│   ├── enums/                       # CartStatus, OrderStatus, Role
│   ├── exception/                   # CartException, OrderException (excepciones de negocio)
│   ├── event/                       # OrderCreatedEvent, StockDecrementedEvent
│   └── ports/                       # Interfaces de entrada y salida
│       ├── in/                      # Use Cases (interfaces)
│       │   └── AddItemToCartUseCase.java
│       └── out/                     # Interfaces hacia infraestructura
│           └── CartRepository.java
│
├── application/                     # Coordinación y casos de uso
│   ├── service/                     # Implementaciones de Use Cases
│   │   └── AddItemToCartService.java
│   └── dto/                         # DTOs para comunicación entre capas
│       ├── AddItemRequest.java
│       └── CartResponse.java
│
└── infrastructure/                  # Frameworks y adaptadores técnicos
    ├── adapters/
    │   ├── in/                      # Entrada (REST, Messaging)
    │   │   └── rest/
    │   │       ├── CartRestController.java
    │   │       └── GlobalExceptionHandler.java
    │   └── out/                     # Salida (Persistencia, HTTP, Messaging)
    │       ├── persistence/
    │       │   ├── CartJpaEntity.java
    │       │   ├── CartJpaRepository.java
    │       │   └── CartRepositoryAdapter.java
    │       ├── http/                # Clientes HTTP hacia otros servicios
    │       │   └── ProductServiceClient.java
    │       └── messaging/           # RabbitMQ Publishers/Consumers
    │           └── RabbitMQEventPublisher.java
    ├── config/                      # Configuración de Spring
    │   ├── RabbitMQConfig.java
    │   └── BeanConfig.java
    └── mapper/                      # Mappers entre Domain y DTOs/Entities
        └── CartMapper.java
```

---

## 🔄 Comunicación Entre Servicios

### Comunicación Síncrona (REST)
- Cuando se necesita respuesta inmediata
- Ejemplo: Cart Service consulta Product Service para validar stock
- Definir como **Port Out** en Application

### Comunicación Asíncrona (RabbitMQ)
- Para procesos que no requieren respuesta inmediata
- Ejemplo: Order Service publica `OrderCreatedEvent` → Product Service decrementa stock
- Usar **Domain Events** en el dominio
- Transformar a **Integration Events** en Infrastructure

#### Flujo de Eventos

```
Domain → genera → Domain Event
    ↓
Application → recoge → Domain Event
    ↓
Application → invoca → EventPublisher Port (interface)
    ↓
Infrastructure → transforma → Integration Event
    ↓
RabbitMQ → publica → Integration Event
```

#### Contratos RabbitMQ Actuales
- **Exchange**: `order.exchange`
- **Routing Key**: `order.created`
- **Queue**: `product.stock.decrement.queue`

---

## 💻 Convenciones de Código

### Nomenclatura

#### Paquetes
- `domain.model`: Agregados y entidades
- `domain.ports.in`: Interfaces de casos de uso
- `domain.ports.out`: Interfaces hacia infraestructura
- `application.service`: Implementaciones de casos de uso
- `application.dto`: Data Transfer Objects
- `infrastructure.adapters.in.rest`: Controladores REST
- `infrastructure.adapters.out.persistence`: Persistencia JPA

#### Clases
- **Agregados**: Sustantivo singular (`Cart`, `Order`, `Product`)
- **Use Cases (Interfaces)**: `{Action}{Entity}UseCase` (`CreateOrderUseCase`)
- **Services (Impl)**: `{Action}{Entity}Service` (`CreateOrderService`)
- **Controllers**: `{Entity}RestController` (`CartRestController`)
- **Repositories**: `{Entity}Repository` (interface), `{Entity}RepositoryAdapter` (impl)
- **DTOs**: `{Entity}Request`, `{Entity}Response`, `{Entity}DTO`
- **Events**: `{Action}{Entity}Event` (`OrderCreatedEvent`)
- **Exceptions**: `{Entity}Exception` (`CartException`)

#### Variables y Métodos
- **camelCase** para métodos y variables
- Métodos: Verbos en imperativo (`addItem`, `removeItem`, `calculateTotal`)
- Variables: Sustantivos descriptivos (`customerId`, `productPrice`, `cartItems`)
- Constantes: `UPPER_SNAKE_CASE`

#### Anotaciones Spring
- `@RestController` + `@RequestMapping`: Controladores REST
- `@Service`: Servicios de aplicación
- `@Component`: Adaptadores y componentes genéricos
- `@Repository`: Repositorios JPA
- `@RestControllerAdvice`: Manejo global de excepciones

### Formato de Código Java
```java
// ✅ BUENO: Agregado puro sin dependencias de frameworks
package com.sofkify.cartservice.domain.model;

import java.util.UUID;
import java.util.Objects;

public class Cart {
    private final UUID id;
    private final UUID customerId;
    private CartStatus status;
    private final List<CartItem> items;

    public Cart(UUID id, UUID customerId) {
        this.id = Objects.requireNonNull(id, "Cart ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.status = CartStatus.ACTIVE;
        this.items = new ArrayList<>();
    }

    // Lógica de negocio encapsulada
    public void addItem(UUID productId, String productName, BigDecimal price, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        // Validaciones y reglas de negocio...
    }
}

// ✅ BUENO: Use Case como interface (Port In)
package com.sofkify.cartservice.domain.ports.in;

public interface AddItemToCartUseCase {
    CartResponse execute(UUID cartId, AddItemRequest request);
}

// ✅ BUENO: Servicio implementa Use Case
package com.sofkify.cartservice.application.service;

@Service
public class AddItemToCartService implements AddItemToCartUseCase {
    
    private final CartRepository cartRepository;
    
    public AddItemToCartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }
    
    @Override
    public CartResponse execute(UUID cartId, AddItemRequest request) {
        // Coordina la lógica del caso de uso
    }
}

// ✅ BUENO: Controlador REST delgado
package com.sofkify.cartservice.infrastructure.adapters.in.rest;

@RestController
@RequestMapping("/api/v1/carts")
public class CartRestController {
    
    private final AddItemToCartUseCase addItemToCartUseCase;
    
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable UUID cartId,
            @RequestBody @Valid AddItemRequest request) {
        CartResponse response = addItemToCartUseCase.execute(cartId, request);
        return ResponseEntity.ok(response);
    }
}
```

---

## 🚫 Antipatrones (EVITAR)

### ❌ Lógica de negocio en Infrastructure
```java
// ❌ MAL: Lógica de negocio en el controlador
@PostMapping
public ResponseEntity<CartResponse> createCart(@RequestBody CartRequest request) {
    if (request.quantity <= 0) {  // ❌ Validación en controlador
        throw new BadRequestException("Invalid quantity");
    }
    // ...
}
```

### ❌ Domain dependiendo de frameworks
```java
// ❌ MAL: Agregado con anotaciones JPA
@Entity  // ❌ Domain no debe conocer JPA
@Table(name = "carts")
public class Cart {
    @Id
    private UUID id;
    // ...
}
```

### ❌ Acoplamiento directo entre servicios
```java
// ❌ MAL: Servicio A inyectando directamente servicio B
@Service
public class OrderService {
    @Autowired
    private ProductService productService;  // ❌ Acoplamiento directo
}
```

### ❌ Use Cases monolíticos
```java
// ❌ MAL: Un servicio con múltiples responsabilidades
@Service
public class CartService {
    public void addItem() { }
    public void removeItem() { }
    public void clearCart() { }
    public void checkout() { }
    // ❌ Demasiadas responsabilidades en una clase
}
```

---

## 📋 Reglas de Negocio

### User
- **Role por defecto**: Cliente
- **Role Admin**: Puede gestionar productos y stock
- Users eliminados (soft delete) no pueden crear carritos ni órdenes

### Cart
- Un cart pertenece a un único User (customerId)
- Status posibles: `ACTIVE`, `CONFIRMED`, `EXPIRED`
- Al agregar un producto existente, se incrementa la cantidad
- No se puede modificar un cart `CONFIRMED`

### Product
- Stock debe ser >= 0
- Precio debe ser > 0
- Al crear una orden, decrementar stock automáticamente (evento asíncrono)
- Validar disponibilidad antes de agregar al cart

### Order
- Se crea desde un Cart confirmado
- Status: `PENDING`, `CONFIRMED`, `CANCELLED`
- Al crear orden, publicar `OrderCreatedEvent`
- Product Service consume el evento y decrementa stock

### Validaciones Generales
- **UUIDs** para identificadores
- **BigDecimal** para precios (nunca float/double)
- **LocalDateTime** para timestamps
- Validar null con `Objects.requireNonNull()`
- Usar `@Valid` en request DTOs

---

## 🧪 Testing

### Principios
- **Unit Tests**: Domain (sin dependencias)
- **Integration Tests**: Application + Infrastructure
- **Contract Tests**: APIs REST (OpenSpec)
- Cobertura mínima: 80%

### Ejemplo de Test de Dominio
```java
@Test
void shouldAddItemToCart() {
    // Given
    Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID());
    UUID productId = UUID.randomUUID();
    
    // When
    cart.addItem(productId, "Product A", new BigDecimal("19.99"), 2);
    
    // Then
    assertThat(cart.getItems()).hasSize(1);
    assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
}
```

---

## 🔧 Herramientas y Configuración

### Gradle
- Usar `build.gradle` para dependencias
- Plugins: Spring Boot, Spring Dependency Management
- Java toolchain configurado por versión de servicio

### Base de Datos
- Un PostgreSQL independiente por microservicio
- Flyway para migraciones versionadas
- No acceso directo entre bases de datos

### Docker
- Dockerfile por microservicio
- Docker Compose para orquestación local
- Exponer puertos según microservicio

### OpenSpec
- Definir contratos de API en `openspec/config.yaml`
- Usar para validación de contratos
- Mantener sincronizado con código

---

## 🚀 Workflow de Desarrollo

### Metodología: Desarrollo Guiado por Arquitectura con IA

1. **Planificación**: Arquitecto + IA definen historia de usuario
2. **Desarrollo**: IA implementa siguiendo arquitectura
3. **Validación**: Arquitecto revisa diseño y calidad
4. **Ajuste**: IA refactoriza según feedback
5. **Aprobación**: Arquitecto aprueba e integra

### Fase 1: Auditoría (AI_WORKFLOW_v2.0)
- Identificar violaciones SOLID
- Detectar code smells y antipatrones
- Documentar en `AUDITORIA.md`
- **NO modificar código**, solo reportar

### Fase 2: Refactorización
- Corregir violaciones identificadas
- Aplicar mejoras estructurales
- Ejecutar tests para validar comportamiento

---

## 📚 Documentación de Referencia

### Documentos Clave
- `docs_IA/architecture.md`: Arquitectura técnica completa
- `docs_IA/context.md`: Contexto de negocio y dominio
- `docs_IA/AI_WORKFLOW_v2.0.md`: Metodología de trabajo con IA
- `docs_IA/events.md`: Especificación de eventos de dominio
- `DEUDA_TECNICA.md`: Deuda técnica identificada

### README por Servicio
- `cart-service/README.md`
- `order-service/README.md`
- `product-service/README.md`
- `user-service/README.md`

---

## 🎯 Cuando generes código:

1. **Siempre empieza por el Domain**: Crea agregados, entidades y reglas de negocio sin dependencias externas
2. **Define Ports (interfaces)**: En `domain.ports.in` (use cases) y `domain.ports.out` (repositorios, clients)
3. **Implementa Application**: Servicios que coordinan casos de uso
4. **Adapta en Infrastructure**: Controladores REST, persistencia JPA, mensajería RabbitMQ
5. **Valida con Tests**: Unitarios para domain, integración para flujos completos
6. **Documenta eventos**: Si el caso de uso genera eventos, especificar en `events.md`
7. **Usa OpenSpec**: Mantener contratos de API actualizados
8. **Sigue SOLID**: Revisa cada clase para cumplir principios
9. **Verifica arquitectura**: Dependencias siempre hacia el dominio
10. **Nomenclatura consistente**: Seguir convenciones del proyecto

---

## ⚠️ Restricciones Críticas

- **NUNCA** poner lógica de negocio en controladores o adaptadores
- **NUNCA** inyectar un microservicio dentro de otro directamente
- **NUNCA** compartir bases de datos entre microservicios
- **NUNCA** usar anotaciones de frameworks en el dominio (`@Entity`, `@Service`, etc.)
- **SIEMPRE** validar inputs en los DTOs con `@Valid`
- **SIEMPRE** usar UUIDs para identificadores
- **SIEMPRE** manejar excepciones con `GlobalExceptionHandler`
- **SIEMPRE** publicar eventos de dominio para comunicación asíncrona

---

## 🚫 Fuera de Alcance

- No introducir lógica de pagos, envíos o facturación a menos que se solicite explícitamente
- No agregar Service Discovery/Config Server a menos que se solicite explícitamente
- No cambiar contratos de API o mensajes como parte de tareas de solo contexto

---

**Fecha de última actualización**: Febrero 2026  
**Versión**: 2.0  
**Mantenido por**: Equipo Sofkify Backend
