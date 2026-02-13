# Arquitectura del Sistema – Ecommerce Asíncrono

## 1. Visión General

El sistema se concibe como un **ecosistema de microservicios** independientes, orientados al dominio,
que se comunican mediante **APIs síncronas** cuando es estrictamente necesario y **eventos asíncronos**
para desacoplar procesos de negocio y lograr **consistencia eventual**.

El desarrollo del sistema es **incremental**.  
Cada bounded context se implementa cuando existe una historia de usuario que lo requiera.

Cada microservicio es **autónomo**, posee su **base de datos**, y encapsula su **modelo de dominio**
sin compartir estado con otros servicios.

### Alcance técnico del documento

Este documento define:

* La arquitectura interna canónica de los microservicios
* Los patrones de comunicación
* Las reglas técnicas obligatorias

### Fuera de alcance técnico actual

* Service Discovery (Eureka)
* Config Server / Config Repo
* Spring Cloud Bus

La comunicación síncrona entre microservicios se realiza mediante endpoints REST conocidos (URLs directas),
definidos como **Ports Out** en la capa Application.

---

## 2. Bounded Contexts del Sistema

El sistema Ecommerce Asíncrono se organiza en los siguientes bounded contexts:

### **user-service**
* Gestión del agregado User
* Manejo de autenticación y roles (Cliente, Admin)
* Persistencia de información de usuarios

### **product-service**
* Gestión del agregado Product
* Administración de categorías
* Gestión de stock
* Consumer de eventos relacionados con órdenes
* Producer de eventos relacionados con stock

### **cart-service**
* Gestión del agregado Cart
* Validación de disponibilidad de productos
* Coordinación de la construcción de una compra

### **order-service**
* Gestión del agregado Order
* Producer de eventos relacionados con órdenes
* Consumer de eventos relacionados con stock

> **Nota sobre implementación:**  
> Cada bounded context se implementa cuando existe una historia de usuario que lo requiera.  
> La ausencia de un servicio en el código no implica que no forme parte del dominio conceptual.

---

## 3. Principios Arquitectónicos

Los siguientes principios son obligatorios para todos los microservicios:

* **Arquitectura Hexagonal (Ports & Adapters)**
* **Clean Architecture**
* **DDD táctico** (agregados, entidades, reglas)
* **Principios SOLID**
* **Bajo acoplamiento / Alta cohesión**
* **Comunicación explícita mediante contratos** (APIs y eventos)

---

## 4. Arquitectura Interna de un Microservicio

Esta sección define la **estructura canónica** que debe seguir **cada microservicio** del sistema.

> Esta estructura es la referencia obligatoria para la IA y para los desarrolladores humanos.

---

### 4.1 Capas del Microservicio

Cada microservicio se divide en las siguientes capas:
```
┌──────────────────────────────┐ 
│       Infrastructure         │ ← Frameworks, Web, Persistence, Messaging 
├──────────────────────────────┤ 
│         Application          │ ← Casos de uso / Orquestación 
├──────────────────────────────┤ 
│           Domain             │ ← Núcleo del negocio 
└──────────────────────────────┘
```

Las **dependencias siempre apuntan hacia el dominio**.

---

### 4.2 Capa Domain

La capa **Domain** representa el núcleo del negocio.

Contiene:

* Entidades y Agregados
* Value Objects (cuando aplique)
* Enumeraciones del dominio
* Excepciones de negocio
* Reglas e invariantes
* Domain Events

#### Reglas clave

* El dominio **no depende** de Spring, JPA, RabbitMQ ni frameworks.
* Un agregado **no modifica** directamente otro agregado.
* Las reglas de negocio viven exclusivamente en esta capa.

Ejemplo de estructura:

```
/domain
├── model
├── enums
├── exception
└── event
```

---

### 4.3 Domain Events

Los **Domain Events** representan hechos de negocio que **ya ocurrieron**.

Ejemplos:

* `OrderCreatedEvent`
* `OrderCancelledEvent`
* `StockDecrementedEvent`
* `OrderFailedEvent`

Características:

* Se generan **dentro del dominio**
* No conocen quién los consume
* No contienen lógica técnica ni dependencias externas

#### Domain Events vs Integration Events

* El dominio **no publica eventos técnicos**
* La infraestructura puede mapear un Domain Event a un Integration Event

El flujo es:

1. El dominio genera un Domain Event
2. El Use Case lo recoge
3. El Use Case invoca un **EventPublisher Port**
4. La infraestructura transforma (si es necesario) y publica el Integration Event

Esta separación permite:
* Dominio puro sin dependencias técnicas
* Evolución independiente de contratos de integración
* Mayor testabilidad

---

### 4.4 Capa Application

La capa **Application** coordina el flujo del sistema mediante **casos de uso explícitos**.

#### Casos de uso

* Cada Use Case es una **clase explícita**
* Representa una **intención del negocio**
* Puede interactuar con múltiples agregados

Ejemplos:

* `CreateUserUseCase`
* `AddProductToCartUseCase`
* `CreateOrderUseCase`
* `DecrementStockUseCase`

#### Reglas clave

* Un use case puede modificar más de un agregado
* El agregado **no coordina** otros agregados
* La coordinación pertenece a Application

#### Puertos

* **Ports In**: interfaces de entrada (casos de uso)
* **Ports Out**: interfaces hacia infraestructura externa

Ejemplo:

```
/application
├── port
│   ├── in
│   │   ├── CreateOrderUseCase
│   │   ├── AddProductToCartUseCase
│   │   └── ...
│   └── out
│       ├── OrderRepositoryPort
│       ├── ProductClientPort
│       ├── EventPublisherPort
│       └── ...
└── service
    ├── CreateOrderService (implements CreateOrderUseCase)
    ├── AddProductToCartService
    └── ...
```

---

### 4.5 Publicación de Eventos

El flujo correcto es:

1. El dominio genera un Domain Event
2. El Use Case lo recoge
3. El Use Case invoca un **EventPublisher Port**
4. La infraestructura publica el evento mediante mensajería (ej. RabbitMQ)

Esto garantiza:

* Dominio puro
* Infraestructura desacoplada
* Alta testabilidad

Ejemplo:

```java
// Domain Event (puro)
public class OrderCreatedEvent {
    private final String orderId;
    private final String userId;
    private final LocalDateTime createdAt;
    // ...
}

// Port Out
public interface EventPublisherPort {
    void publish(DomainEvent event);
}

// Use Case
public class CreateOrderService implements CreateOrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;
    
    @Override
    public OrderResponse execute(CreateOrderCommand command) {
        Order order = Order.create(command);
        orderRepository.save(order);
        
        // Publicar evento de dominio
        eventPublisher.publish(new OrderCreatedEvent(order.getId(), ...));
        
        return OrderResponse.from(order);
    }
}
```

---

### 4.6 Consumo de Eventos

Los microservicios pueden actuar como **consumers** de eventos emitidos por otros servicios.

El flujo es:

1. La infraestructura recibe el mensaje del broker (ej. RabbitMQ)
2. Un **Listener/Consumer** en la capa Infrastructure deserializa el evento
3. El Consumer invoca un **Use Case** (Port In)
4. El Use Case ejecuta la lógica de negocio
5. Opcionalmente, puede emitir nuevos eventos

Ejemplo:

```java
// Infrastructure - Consumer
@Component
public class OrderCreatedConsumer {
    private final DecrementStockUseCase decrementStockUseCase;
    
    @RabbitListener(queues = "${rabbitmq.queues.stock-decrement}")
    public void consume(OrderCreatedEvent event) {
        decrementStockUseCase.execute(
            new DecrementStockCommand(event.getOrderItems())
        );
    }
}
```

Esto garantiza:

* Separación entre infraestructura y lógica de negocio
* Reutilización de Use Cases
* Testabilidad

---

### 4.7 Capa Infrastructure

La capa **Infrastructure** contiene implementaciones técnicas y no define reglas de negocio.

Incluye:

* Controladores REST
* DTOs por agregado
* Mapeadores
* Excepciones técnicas
* Adaptadores de persistencia
* Implementaciones de mensajería (producers y consumers)
* Configuración técnica

Ejemplo:

```
/infrastructure
├── config
│   ├── RabbitMQConfig
│   ├── SecurityConfig
│   └── ...
├── exception
├── persistence
│   ├── entity
│   ├── repository
│   └── adapter
├── web
│   ├── controller
│   ├── dto
│   │   ├── request
│   │   └── response
│   └── mapper
└── messaging
    ├── producer
    ├── consumer
    └── event (Integration Events)
```

#### DTOs

* Los DTOs se organizan **por agregado**
* Separación clara entre:
    * `request` (entrada)
    * `response` (salida)

---

### 4.8 Persistencia

* Cada microservicio **posee su propia base de datos**
* No existen joins entre servicios
* La persistencia implementa **Ports Out**
* Se utiliza el patrón Repository

---

### 4.9 Comunicación entre Microservicios

El sistema utiliza dos patrones de comunicación:

#### Comunicación Síncrona (REST)

* Solo cuando es **estrictamente necesario**
* Desde Application hacia **Ports Out** que representan clientes externos
* URLs directas configuradas

Ejemplos:
* `cart-service` → `product-service` (validar disponibilidad)
* `order-service` → `user-service` (validar usuario)

Implementación:

```java
// Port Out
public interface ProductClientPort {
    ProductDto getProduct(String productId);
    boolean hasStock(String productId, int quantity);
}

// Infrastructure
@Component
public class ProductRestClient implements ProductClientPort {
    private final RestTemplate restTemplate;
    private final String productServiceUrl;
    
    @Override
    public ProductDto getProduct(String productId) {
        return restTemplate.getForObject(
            productServiceUrl + "/products/" + productId,
            ProductDto.class
        );
    }
}
```

#### Comunicación Asíncrona (Eventos)

* Procesos derivados
* Notificaciones
* Desacoplamiento
* Consistencia eventual

Ejemplos:
* `order-service` emite `OrderCreated` → `product-service` lo consume
* `product-service` emite `StockDecremented` → `order-service` lo consume

Configuración típica (RabbitMQ):

```java
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchanges.order}")
    private String orderExchange;
    
    @Value("${rabbitmq.queues.stock-decrement}")
    private String stockDecrementQueue;
    
    @Value("${rabbitmq.routing-keys.order-created}")
    private String orderCreatedRoutingKey;
    
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(orderExchange);
    }
    
    @Bean
    public Queue stockDecrementQueue() {
        return QueueBuilder.durable(stockDecrementQueue).build();
    }
    
    @Bean
    public Binding stockDecrementBinding() {
        return BindingBuilder
                .bind(stockDecrementQueue())
                .to(orderExchange())
                .with(orderCreatedRoutingKey);
    }
}
```

---

### 4.10 Consistencia

El sistema opera bajo **consistencia eventual**:

* La operación principal es inmediata y consistente
* Los efectos secundarios son asíncronos

Ejemplo:

1. **Consistencia inmediata**: Order se crea y persiste
2. **Consistencia eventual**: Stock se decrementa de forma asíncrona
3. **Compensación**: Si falla, se emite `OrderFailed` para revertir

---

## 5. Convenciones de Nombres

### Use Cases
* Formato: `Verb + Noun + UseCase`
* Ejemplos: `CreateOrderUseCase`, `AddProductToCartUseCase`

### Services
* Formato: `Verb + Noun + Service`
* Ejemplos: `CreateOrderService`, `AddProductToCartService`

### Ports In
* Formato: `<Action><Entity>UseCase`
* Ejemplos: `CreateOrderUseCase`, `GetUserUseCase`

### Ports Out
* Formato: `<Entity><Purpose>Port`
* Ejemplos: `OrderRepositoryPort`, `ProductClientPort`, `EventPublisherPort`

### Domain Events
* Formato: `<Entity><Action>Event`
* Ejemplos: `OrderCreatedEvent`, `StockDecrementedEvent`

### Integration Events
* Formato: Puede coincidir con Domain Events o usar un contrato versionado
* Ejemplos: `OrderCreatedEvent`, `OrderCreatedEventV1`

---

## 6. Patrones de Diseño Aplicados

### Arquitectura Hexagonal
* Dominio en el centro
* Puertos como abstracciones
* Adaptadores como implementaciones

### Repository Pattern
* Abstracción de persistencia
* Definido como Port Out
* Implementado en Infrastructure

### Command Pattern
* Commands como DTOs de entrada a Use Cases
* Ejemplos: `CreateOrderCommand`, `AddItemToCartCommand`

### Event-Driven Architecture
* Domain Events para hechos de negocio
* Integration Events para comunicación entre servicios
* Consumers reactivos

---

## 7. Relación con Otros Documentos

* `context.md`: reglas y lenguaje del dominio
* `events.md`: contratos de eventos de integración
* `HU-*.md`: historias técnicas que definen implementación específica
* `AI_WORKFLOW.md`: uso controlado de IA

Este archivo define el **cómo técnico**; el dominio define el **qué**.

---

## 8. Documentación por Microservicio

Cada microservicio:

* Comparte este `architecture.md` como referencia estructural
* No define una arquitectura propia
* Se documenta mediante:
    * Historias de usuario (`HU-*.md`)
    * Contratos de eventos (`events.md` cuando aplique)

Esto evita duplicación y divergencia arquitectónica.

---

## 9. Evolución del Documento

Este documento se modifica cuando:

* Se definen nuevos patrones arquitectónicos obligatorios
* Se agregan nuevos bounded contexts al sistema
* Se establecen nuevas convenciones técnicas
* Se incorporan nuevas tecnologías transversales

Los cambios deben ser aprobados por el arquitecto del sistema y comunicados a todo el equipo.