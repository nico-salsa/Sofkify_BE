# AUDITORÍA TÉCNICA DEL SISTEMA | product-service

## 1. Información General

**Proyecto:** Softkify  
**Repositorio:** https://github.com/softkify/Sofkify_BE  
**Rama Evaluada:** develop  
**Commit Base (Snapshot):** audit: snapshot post-mvp  
**Fecha de Auditoría:** 12/02/2026  
**Equipo Auditor:** Backend  
**Microservicio Evaluado:** product-service

---

## 2. Contexto y Alcance

Documento correspondiente a la **Fase 1 del reto técnico**: Diagnóstico y Snapshot Arquitectónico.

El objetivo es evaluar el estado estructural del microservicio **product-service** posterior al MVP, identificando:

- Violaciones a principios SOLID.
- Antipatrones y code smells relevantes.
- Riesgos arquitectónicos.
- Impacto en escalabilidad, mantenibilidad y testabilidad.
- Validación de la implementación de Arquitectura Hexagonal.

Este documento no contempla refactorización, sino **diagnóstico técnico fundamentado**.

---

## 3. Observación General de Arquitectura

El sistema declara implementar **Arquitectura Hexagonal (Puertos y Adaptadores)** con separación clara en las siguientes capas:

```
📁 product-service/
├── 📁 domain/              → Lógica de negocio pura
│   ├── model/              → Entidades de dominio
│   ├── enums/              → Enumeraciones
│   ├── exception/          → Excepciones de negocio
│   └── ports/              → Contratos del dominio
├── 📁 application/         → Casos de uso y orquestación
│   ├── service/            → Implementación de casos de uso
│   ├── port/               → Puertos de aplicación
│   └── dto/                → Eventos de integración
└── 📁 infrastructure/      → Adaptadores externos
    ├── web/                → REST API (entrada)
    ├── messaging/          → RabbitMQ (entrada)
    └── persistence/        → JPA/PostgreSQL (salida)
```

### Observaciones Generales:

✅ **Fortalezas:**
- El dominio (`Product.java`) está libre de anotaciones de infraestructura (JPA, Spring).
- Existe separación clara entre entidad de dominio (`Product`) y entidad de persistencia (`ProductEntity`).
- Se utilizan puertos e interfaces para abstraer dependencias externas.
- El modelo de dominio contiene comportamiento y validaciones (no es anémico).
- Uso correcto de inyección de dependencias por constructor.

⚠️ **Inconsistencias Detectadas:**
- **Duplicación de puertos de salida**: Existen dos interfaces prácticamente idénticas (`ProductRepositoryPort` en dominio y `ProductPersistencePort` en aplicación).
- **Inconsistencia en el uso de puertos**: `StockDecrementService` usa un puerto del dominio cuando debería usar uno de aplicación.
- **Conversión de tipos redundante**: Un puerto recibe `UUID` y otro `String` para el mismo propósito.

**Conclusión preliminar:**  
La arquitectura está **bien intencionada** pero presenta **inconsistencias** que generan confusión y violan la separación de responsabilidades entre capas.

---

## 4. Metodología de Evaluación

### 4.1 Criterios de Análisis

1. Revisión estructural por capas (dominio, aplicación, infraestructura).
2. Cumplimiento de principios SOLID.
3. Evaluación de cohesión y acoplamiento.
4. Identificación de duplicación de lógica.
5. Análisis de dirección de dependencias.
6. Validación de implementación de Arquitectura Hexagonal.
7. Identificación de riesgos de escalabilidad y mantenibilidad.

### 4.2 Formato de Registro de Hallazgos

Cada hallazgo incluye:

- **Archivo y Línea(s)**
- **Principio Vulnerado**
- **Descripción del Problema**
- **Fragmento de Código**
- **Impacto Técnico**
- **Riesgo Arquitectónico**
- **Recomendación Técnica**

---

## 5. Mapa de Riesgo Técnico

| ID | Categoría | Hallazgo | Severidad | Impacto | Probabilidad | Prioridad |
|----|-----------|----------|-----------|---------|--------------|-----------|
| ISP-01 | Arquitectura | Duplicación de puertos de salida | **Alta** | **Alta** | **Alta** | **🔴 Crítica** |
| DIP-01 | Arquitectura | Inconsistencia en uso de puertos | **Alta** | **Alta** | **Alta** | **🔴 Crítica** |
| SRP-01 | Diseño | Validación duplicada en servicio y dominio | **Media** | **Media** | **Alta** | **🟡 Alta** |
| OCP-01 | Diseño | Lógica de transición de estado ausente | **Media** | **Alta** | **Media** | **🟡 Alta** |
| SRP-02 | Diseño | Producto no valida su propio estado antes de operar | **Media** | **Media** | **Media** | **🟡 Media** |
| SMELL-01 | Calidad | Excepciones sin contexto estructurado | **Baja** | **Media** | **Alta** | **🟢 Media** |
| SMELL-02 | Calidad | Manejo de errores con RuntimeException genérica | **Media** | **Media** | **Media** | **🟡 Media** |

**Leyenda:**
- **Severidad**: Magnitud estructural del problema.
- **Impacto**: Consecuencia operativa o evolutiva.
- **Probabilidad**: Frecuencia esperada de manifestación.
- **Prioridad**: Urgencia de intervención técnica.

---

## 6. Hallazgos Detallados

### 6.1 Interface Segregation Principle (ISP)

#### **Hallazgo ISP-01** 🔴 **CRÍTICO**

**Archivos:**
- `ProductRepositoryPort.java` (dominio)
- `ProductPersistencePort.java` (aplicación)

**Líneas:** Definición completa de ambas interfaces

**Principio Vulnerado:** ISP (Interface Segregation Principle)

---

**Descripción del Problema:**

Existen **dos puertos de salida** que exponen funcionalidades prácticamente idénticas pero en diferentes capas:

**Puerto 1 - Dominio (`domain/ports/out/ProductRepositoryPort.java`):**
```java
public interface ProductRepositoryPort {
    Optional<Product> findById(UUID id);      // ← Recibe UUID
    Product save(Product product);
}
```

**Puerto 2 - Aplicación (`application/port/out/ProductPersistencePort.java`):**
```java
public interface ProductPersistencePort {
    Product save(Product product);
    Optional<Product> findById(String id);    // ← Recibe String
    List<Product> findAll();
    List<Product> findByStatus(String status);
}
```

**Problemas detectados:**

1. **Redundancia funcional**: Ambos tienen `save()` y `findById()` con semánticas idénticas.
2. **Inconsistencia de tipos**: Uno recibe `UUID`, el otro `String` (conversión manual innecesaria).
3. **Violación arquitectónica**: El dominio **NO debe exponer puertos de salida** en arquitectura hexagonal pura.
4. **Confusión en el equipo**: No hay claridad sobre cuál puerto usar.

---

**Evidencia de uso inconsistente:**

**Caso 1 - `StockDecrementService` usa el puerto del DOMINIO:**
```java
@Service
public class StockDecrementService implements HandleOrderCreatedUseCase {
    private final ProductRepositoryPort productRepositoryPort; // ❌ Puerto del DOMINIO
    
    var product = productRepositoryPort.findById(productId); // ← Recibe UUID directamente
}
```

**Caso 2 - `CreateProductService` usa el puerto de APLICACIÓN:**
```java
@Service
public class CreateProductService implements CreateProductUseCase {
    private final ProductPersistencePort productPersistencePort; // ✅ Puerto de APLICACIÓN
    
    return productPersistencePort.save(product);
}
```

---

**Impacto Técnico:**

- **Inconsistencia arquitectónica**: No hay consenso sobre qué puerto usar.
- **Confusión en nuevos desarrolladores**: ¿Cuál importar?
- **Duplicación de contratos**: Cambios deben replicarse en dos lugares.
- **Violación de responsabilidades**: El dominio está exponiendo infraestructura.

---

**Riesgo Arquitectónico:**

🔴 **Crítico**: La arquitectura hexagonal pierde su propósito si no hay claridad en la dirección de dependencias.

---

**Recomendación Técnica:**

1. **Eliminar `ProductRepositoryPort` del dominio**.
2. **Unificar en un solo puerto de aplicación**: `ProductPersistencePort`.
3. **Estandarizar el tipo de ID**: Usar `UUID` consistentemente (es el tipo del dominio).
4. **Actualizar `StockDecrementService`** para usar `ProductPersistencePort`.

**Puerto único propuesto:**
```java
// application/port/out/ProductPersistencePort.java
public interface ProductPersistencePort {
    Product save(Product product);
    Optional<Product> findById(UUID id);               // ← UUID (tipo del dominio)
    List<Product> findAll();
    List<Product> findByStatus(ProductStatus status);  // ← Enum del dominio
}
```

---

### 6.2 Dependency Inversion Principle (DIP)

#### **Hallazgo DIP-01** 🔴 **CRÍTICO**

**Archivo:** `StockDecrementService.java`  
**Línea:** 24  
**Principio Vulnerado:** DIP (Dependency Inversion Principle)

---

**Descripción del Problema:**

El servicio de aplicación está inyectando un puerto del **dominio** en lugar de uno de **aplicación**:

```java
@Service
@Transactional
public class StockDecrementService implements HandleOrderCreatedUseCase {

    private final ProductRepositoryPort productRepositoryPort; // ❌ PUERTO DEL DOMINIO

    public StockDecrementService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }
    
    // ...
    
    var product = productRepositoryPort.findById(productId)  // ← Usa puerto del dominio
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
}
```

---

**Análisis del problema:**

En arquitectura hexagonal:

- **El DOMINIO** define **puertos de entrada** (interfaces que él implementa).
- **La APLICACIÓN** define **puertos de salida** (interfaces que la infraestructura implementa).
- **El DOMINIO NO debe exponer puertos de salida**.

Al colocar `ProductRepositoryPort` en `domain/ports/out/`, se está violando el principio de dirección de dependencias:

```
❌ MAL:
Aplicación → Dominio (puerto) → Infraestructura

✅ BIEN:
Aplicación (puerto) ← Infraestructura
Dominio (entidades) ← Aplicación
```

---

**Comparación con otros servicios:**

**❌ Inconsistente (`StockDecrementService`):**
```java
private final ProductRepositoryPort productRepositoryPort; // domain/ports/out/
```

**✅ Correcto (`CreateProductService`):**
```java
private final ProductPersistencePort productPersistencePort; // application/port/out/
```

---

**Impacto Técnico:**

- **Violación de la arquitectura hexagonal**: El dominio está acoplado a infraestructura indirectamente.
- **Confusión de responsabilidades**: No hay claridad sobre qué capa define contratos.
- **Dificultad para testear**: Se pierde la abstracción correcta.

---

**Riesgo Arquitectónico:**

🔴 **Crítico**: La arquitectura se degrada progresivamente si no se respeta la dirección de dependencias.

---

**Recomendación Técnica:**

1. **Mover todos los puertos de salida a `application/port/out/`**.
2. **Eliminar `domain/ports/out/`** (el dominio no debe definir contratos de infraestructura).
3. **Actualizar `StockDecrementService`**:

```java
@Service
public class StockDecrementService implements HandleOrderCreatedUseCase {
    
    private final ProductPersistencePort productPersistencePort; // �� Puerto de APLICACIÓN
    
    public StockDecrementService(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }
}
```

---

### 6.3 Single Responsibility Principle (SRP)

#### **Hallazgo SRP-01** 🟡 **MEDIA**

**Archivo:** `StockDecrementService.java`  
**Líneas:** 54-62  
**Principio Vulnerado:** SRP (Single Responsibility Principle)

---

**Descripción del Problema:**

El servicio de aplicación está **duplicando validaciones de negocio** que ya existen en el dominio:

```java
private void decrementStock(UUID productId, int quantity, UUID orderId) {
    var product = productRepositoryPort.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

    // ❌ VALIDACIÓN EN EL SERVICIO (capa de aplicación)
    if (product.getStock() < quantity) {
        throw new InsufficientStockException(
            String.format("Insufficient stock for product %s. Available: %d, Required: %d", 
                    productId, product.getStock(), quantity)
        );
    }

    product.decrementStock(quantity); // ← Esta llamada TAMBIÉN valida stock (línea 64 de Product.java)
    productRepositoryPort.save(product);
}
```

**Validación duplicada en el dominio (`Product.java`, línea 64-68):**
```java
public void decrementStock(int quantity) {
    if (quantity <= 0) {
        throw new InvalidProductStockException("Quantity to decrement must be greater than zero");
    }
    
    // ❌ MISMA VALIDACIÓN que en el servicio
    if (this.stock < quantity) {
        throw new InvalidProductStockException(
            String.format("Insufficient stock. Available: %d, Required: %d", this.stock, quantity)
        );
    }
    
    this.stock -= quantity;
}
```

---

**Análisis del problema:**

La lógica de negocio "verificar stock suficiente" está implementada en **dos lugares**:

1. **Servicio de aplicación** (`StockDecrementService`, línea 54)
2. **Modelo de dominio** (`Product.decrementStock()`, línea 64)

Esto viola SRP porque:

- **El servicio no debería saber** las reglas de negocio del dominio.
- **El dominio ya valida correctamente**: Delegar es suficiente.
- **Duplicación innecesaria**: Si la regla cambia, hay que modificar dos lugares.

---

**Impacto Técnico:**

- **Mantenibilidad reducida**: Cambios en reglas de negocio requieren modificar múltiples archivos.
- **Riesgo de inconsistencia**: Si se modifica una validación y no la otra, el comportamiento es impredecible.
- **Violación de "Tell, don't ask"**: El servicio está interrogando al dominio en lugar de comandarlo.

---

**Riesgo Arquitectónico:**

🟡 **Medio**: La lógica de negocio empieza a "escapar" del dominio hacia los servicios de aplicación.

---

**Recomendación Técnica:**

**Eliminar la validación del servicio** y confiar en el dominio:

```java
private void decrementStock(UUID productId, int quantity, UUID orderId) {
    logger.debug("Decrementing {} units for product: {} (order: {})", quantity, productId, orderId);

    var product = productPersistencePort.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

    // ✅ Delegar directamente al dominio (él ya valida)
    product.decrementStock(quantity);
    productPersistencePort.save(product);

    logger.debug("Stock decremented successfully for product: {}. New stock: {}", 
            productId, product.getStock());
}
```

El dominio ya lanza `InvalidProductStockException` si hay problemas. El servicio solo debe **orquestar**, no **validar**.

---

#### **Hallazgo SRP-02** 🟡 **MEDIA**

**Archivo:** `Product.java`  
**Líneas:** 56-69  
**Principio Vulnerado:** SRP (Single Responsibility Principle)

---

**Descripción del Problema:**

El método `decrementStock()` **NO valida el estado del producto** antes de permitir la operación:

```java
public void decrementStock(int quantity) {
    if (quantity <= 0) {
        throw new InvalidProductStockException("Quantity to decrement must be greater than zero");
    }
    
    if (this.stock < quantity) {
        throw new InvalidProductStockException(
            String.format("Insufficient stock. Available: %d, Required: %d", this.stock, quantity)
        );
    }
    
    this.stock -= quantity; // ❌ ¿Y si el producto está INACTIVE o DELETED?
}
```

---

**Análisis del problema:**

Actualmente, un producto en estado `INACTIVE` o `DELETED` puede decrementar stock:

```java
// Caso problemático:
Product product = Product.restore(uuid, "Laptop", "...", price, 100, ProductStatus.DELETED);
product.decrementStock(10); // ✅ ¡Funciona! Pero no debería.
```

**Reglas de negocio faltantes:**

- Un producto **eliminado** no debería permitir operaciones de stock.
- Un producto **inactivo** no debería permitir decrementos (dependiendo de la lógica de negocio).

---

**Impacto Técnico:**

- **Inconsistencia de estado**: Se pueden realizar operaciones sobre productos que no deberían estar operativos.
- **Falta de invariantes de dominio**: El modelo no protege su integridad.
- **Lógica de negocio incompleta**: Las reglas están implícitas pero no implementadas.

---

**Riesgo Arquitectónico:**

🟡 **Medio**: El dominio no está garantizando sus propias invariantes.

---

**Recomendación Técnica:**

Agregar validación de estado en el método:

```java
public void decrementStock(int quantity) {
    // ✅ Validar estado antes de operar
    if (!this.isActive()) {
        throw new InvalidProductOperationException(
            "Cannot decrement stock for a product that is not active. Current status: " + this.status
        );
    }
    
    if (quantity <= 0) {
        throw new InvalidProductStockException("Quantity to decrement must be greater than zero");
    }
    
    if (this.stock < quantity) {
        throw new InvalidProductStockException(
            String.format("Insufficient stock. Available: %d, Required: %d", this.stock, quantity)
        );
    }
    
    this.stock -= quantity;
}
```

Crear la nueva excepción de dominio:
```java
public class InvalidProductOperationException extends RuntimeException {
    public InvalidProductOperationException(String message) {
        super(message);
    }
}
```

---

### 6.4 Open/Closed Principle (OCP)

#### **Hallazgo OCP-01** 🟡 **ALTA**

**Archivo:** `Product.java`  
**Líneas:** Definición completa de la clase  
**Principio Vulnerado:** OCP (Open/Closed Principle)

---

**Descripción del Problema:**

El modelo de dominio tiene un campo `status` **inmutable** sin comportamiento asociado:

```java
public class Product {
    // ...
    private final ProductStatus status; // ❌ Inmutable (final)
    
    // ✅ Tiene consultor
    public boolean isActive() {
        return ProductStatus.ACTIVE.equals(status);
    }
    
    // ❌ NO hay métodos para cambiar estado:
    // - activate()
    // - deactivate()
    // - delete()
}
```

---

**Análisis del problema:**

1. **Imposibilidad de transición de estado**: Una vez creado, un producto no puede cambiar de `ACTIVE` a `INACTIVE` o `DELETED`.
2. **Lógica de negocio ausente**: No hay lugar para implementar reglas como:
    - "No se puede activar un producto sin stock"
    - "No se puede eliminar un producto con órdenes pendientes"
3. **Violación de "Tell, don't ask"**: Los servicios tendrían que consultar el estado y crear nuevos objetos en lugar de comandar la transición.

---

**Caso de uso bloqueado:**

```java
// ❌ No se puede hacer:
product.deactivate();

// ❌ Habría que hacer:
Product updatedProduct = Product.restore(
    product.getId(),
    product.getName(),
    product.getDescription(),
    product.getPrice(),
    product.getStock(),
    ProductStatus.INACTIVE  // ← Violación de encapsulación
);
```

---

**Impacto Técnico:**

- **Extensibilidad limitada**: No se pueden agregar reglas de negocio para transiciones de estado.
- **Violación de encapsulación**: Los servicios deben conocer la estructura interna del dominio.
- **Modelo anémico parcial**: El dominio tiene datos pero no comportamiento completo.

---

**Riesgo Arquitectónico:**

🟡 **Alto**: A medida que el negocio crezca, será necesario gestionar estados, y no hay infraestructura para hacerlo correctamente.

---

**Recomendación Técnica:**

Agregar métodos de comportamiento para transiciones de estado:

```java
public class Product {
    // Cambiar a mutable
    private ProductStatus status; // ← Quitar 'final'
    
    // Métodos de transición con reglas de negocio
    public void activate() {
        if (this.status == ProductStatus.ACTIVE) {
            throw new InvalidProductOperationException("Product is already active");
        }
        if (this.stock <= 0) {
            throw new InvalidProductOperationException("Cannot activate a product with no stock");
        }
        this.status = ProductStatus.ACTIVE;
    }
    
    public void deactivate() {
        if (this.status == ProductStatus.INACTIVE) {
            throw new InvalidProductOperationException("Product is already inactive");
        }
        this.status = ProductStatus.INACTIVE;
    }
    
    public void delete() {
        if (this.status == ProductStatus.DELETED) {
            throw new InvalidProductOperationException("Product is already deleted");
        }
        this.status = ProductStatus.DELETED;
    }
}
```

**Beneficios:**
- ✅ Reglas de negocio en el dominio
- ✅ Extensible a futuras transiciones
- ✅ Encapsulación correcta

---

## 7. Code Smells Relevantes

### 7.1 Excepciones Simplistas sin Contexto Estructurado

**Hallazgo SMELL-01** 🟢 **MEDIA**

**Archivos:**
- `InsufficientStockException.java`
- `ProductNotFoundException.java`
- `InvalidProductPriceException.java`
- `InvalidProductStockException.java`

---

**Descripción del Problema:**

Las excepciones de dominio solo contienen un mensaje `String` sin contexto estructurado:

```java
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
```

**Problema:**
- ❌ No incluyen campos como `productId`, `availableStock`, `requiredStock`.
- ❌ Dificultan el manejo granular de errores en capas superiores.
- ❌ Obligan a parsear mensajes de texto para extraer datos.

---

**Uso actual:**
```java
throw new InsufficientStockException(
    String.format("Insufficient stock for product %s. Available: %d, Required: %d", 
            productId, product.getStock(), quantity)
);
```

El controlador recibe solo un `String`, no los datos estructurados.

---

**Impacto Técnico:**

- **Pérdida de información**: No se puede acceder programáticamente a los valores.
- **Respuestas HTTP pobres**: No se puede devolver JSON estructurado con los detalles del error.
- **Dificultad para logging**: Los sistemas de monitoreo no pueden extraer métricas.

---

**Recomendación Técnica:**

**Enriquecer las excepciones con campos:**

```java
public class InsufficientStockException extends RuntimeException {
    private final UUID productId;
    private final int availableStock;
    private final int requiredStock;
    
    public InsufficientStockException(UUID productId, int availableStock, int requiredStock) {
        super(String.format("Insufficient stock for product %s. Available: %d, Required: %d", 
                productId, availableStock, requiredStock));
        this.productId = productId;
        this.availableStock = availableStock;
        this.requiredStock = requiredStock;
    }
    
    // Getters
    public UUID getProductId() { return productId; }
    public int getAvailableStock() { return availableStock; }
    public int getRequiredStock() { return requiredStock; }
}
```

**Uso mejorado:**
```java
throw new InsufficientStockException(productId, product.getStock(), quantity);
```

**Beneficio en el controlador:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ErrorResponse(
                "INSUFFICIENT_STOCK",
                ex.getMessage(),
                Map.of(
                    "productId", ex.getProductId(),
                    "availableStock", ex.getAvailableStock(),
                    "requiredStock", ex.getRequiredStock()
                )
            )
        );
    }
}
```

---

### 7.2 Manejo de Errores con RuntimeException Genérica

**Hallazgo SMELL-02** 🟡 **MEDIA**

**Archivo:** `RabbitMQOrderCreatedConsumer.java`  
**Líneas:** 37-43

---

**Descripción del Problema:**

El listener de mensajería captura excepciones específicas solo para lanzar `RuntimeException` genérica:

```java
@RabbitListener(queues = "${rabbitmq.queues.stock-decrement}")
public void handleOrderCreated(String message) {
    try {
        OrderCreatedEventDTO event = objectMapper.readValue(message, OrderCreatedEventDTO.class);
        handleOrderCreatedUseCase.handleOrderCreated(event);
        
    } catch (JsonProcessingException e) {
        logger.error("Error parsing OrderCreatedEvent message: {}", message, e);
        // ❌ Captura excepción específica para lanzar genérica
        throw new RuntimeException("Failed to parse OrderCreatedEvent", e);
        
    } catch (Exception e) {
        logger.error("Error processing OrderCreatedEvent", e);
        // ❌ Captura TODO para lanzar genérica
        throw new RuntimeException("Failed to process OrderCreatedEvent", e);
    }
}
```

---

**Problemas detectados:**

1. **Pérdida de información**: Se reemplaza una excepción específica (`JsonProcessingException`) por una genérica.
2. **No hay Dead Letter Queue (DLQ)**: Los mensajes fallidos se pierden o quedan en la cola indefinidamente.
3. **Comentario sin implementación**: "Aquí podríamos mover el mensaje a una Dead Letter Queue" (línea 38).
4. **Reintento indefinido**: Sin configuración de DLQ, RabbitMQ reintentará infinitamente.

---

**Impacto Técnico:**

- **Degradación del servicio**: Mensajes mal formados bloquean la cola.
- **Pérdida de mensajes**: Sin DLQ, los mensajes fallidos desaparecen.
- **Dificultad para debugging**: La causa raíz se enmascara con `RuntimeException`.

---

**Recomendación Técnica:**

**1. Crear excepciones específicas:**

```java
public class MessageParsingException extends RuntimeException {
    private final String rawMessage;
    
    public MessageParsingException(String message, String rawMessage, Throwable cause) {
        super(message, cause);
        this.rawMessage = rawMessage;
    }
    
    public String getRawMessage() { return rawMessage; }
}
```

**2. Configurar Dead Letter Queue:**

```java
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Queue stockDecrementQueue() {
        return QueueBuilder.durable("product.stock.decrement.queue")
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "failed.stock.decrement")
                .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("failed.stock.decrement.queue", true);
    }
}
```

**3. Mejorar el manejo:**

```java
@RabbitListener(queues = "${rabbitmq.queues.stock-decrement}")
public void handleOrderCreated(String message) {
    try {
        OrderCreatedEventDTO event = objectMapper.readValue(message, OrderCreatedEventDTO.class);
        handleOrderCreatedUseCase.handleOrderCreated(event);
        
    } catch (JsonProcessingException e) {
        logger.error("Invalid message format, moving to DLQ: {}", message, e);
        throw new MessageParsingException("Failed to parse OrderCreatedEvent", message, e);
        
    } catch (InsufficientStockException e) {
        logger.warn("Insufficient stock for order, moving to DLQ: {}", e.getMessage());
        // Publicar evento de fallo de orden
        throw e; // Mover a DLQ
        
    } catch (Exception e) {
        logger.error("Unexpected error processing OrderCreatedEvent", e);
        throw e;
    }
}
```

---

### 7.3 Ausencia de Manejo Global de Excepciones

**Hallazgo SMELL-03** 🟢 **BAJA**

**Archivo:** `ProductController.java`

---

**Descripción del Problema:**

No existe un `@ControllerAdvice` para manejar excepciones de forma centralizada:

```java
@GetMapping("/{id}")
public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
    return getProductUseCase.getProductById(id)
            .map(product -> ResponseEntity.ok(ProductMapper.toResponse(product)))
            .orElse(ResponseEntity.notFound().build()); // ← Manejo manual
}
```

**Problemas:**

- ❌ Si `getProductById()` lanza `InvalidProductPriceException`, se devuelve un error 500 genérico.
- ❌ No hay estructura de respuesta de error consistente.
- ❌ Los logs no capturan el contexto completo.

---

**Recomendación Técnica:**

**Crear `@ControllerAdvice` global:**

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("PRODUCT_NOT_FOUND", ex.getMessage(), null)
        );
    }
    
    @ExceptionHandler(InvalidProductPriceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPrice(InvalidProductPriceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("INVALID_PRICE", ex.getMessage(), null)
        );
    }
    
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ErrorResponse("INSUFFICIENT_STOCK", ex.getMessage(), 
                Map.of(
                    "productId", ex.getProductId(),
                    "availableStock", ex.getAvailableStock(),
                    "requiredStock", ex.getRequiredStock()
                )
            )
        );
    }
}
```

**DTO de respuesta:**
```java
public record ErrorResponse(
    String code,
    String message,
    Map<String, Object> details
) {}
```

---

### 7.4 Endpoints REST Incompletos

**Hallazgo SMELL-04** 🟢 **BAJA**

**Archivo:** `ProductController.java`  
**Líneas:** 68-71

---

**Descripción del Problema:**

Existen TODOs para funcionalidades críticas:

```java
// TODO: PUT /products/{id} - Update product details
// TODO: PATCH /products/{id}/stock - Add stock to product
// TODO: DELETE /products/{id} - Soft delete product
// TODO: PATCH /products/{id}/status - Change product status
```

**Funcionalidades faltantes:**

1. **Actualizar productos**: No se puede modificar nombre, descripción o precio.
2. **Añadir stock**: Solo se puede decrementar, no incrementar.
3. **Soft delete**: No se puede eliminar (marcar como `DELETED`).
4. **Cambiar estado**: No se puede activar/desactivar productos.

---

**Impacto Técnico:**

- **Funcionalidad limitada**: El sistema no es completo.
- **Dependencia de base de datos**: Cambios requieren acceso directo a BD.
- **Experiencia de usuario degradada**: Faltan operaciones CRUD básicas.

---

**Recomendación Técnica:**

Implementar los endpoints faltantes siguiendo los casos de uso del dominio (una vez implementados los métodos de transición de estado):

```java
@PatchMapping("/{id}/status")
public ResponseEntity<ProductResponse> changeStatus(
        @PathVariable String id, 
        @RequestBody ChangeStatusRequest request) {
    // Caso de uso: ChangeProductStatusUseCase
}

@PatchMapping("/{id}/stock")
public ResponseEntity<ProductResponse> addStock(
        @PathVariable String id, 
        @RequestBody AddStockRequest request) {
    // Caso de uso: AddProductStockUseCase
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
    // Caso de uso: DeleteProductUseCase (soft delete)
}
```

---

## 8. Aciertos Arquitectónicos

### 8.1 Dominio Puro sin Dependencias de Infraestructura ✅

**Archivo:** `Product.java`

**Descripción:**

El modelo de dominio **NO tiene anotaciones JPA ni dependencias de Spring**:

```java
public class Product {
    private final UUID id;
    private final String name;
    // ... sin @Entity, @Column, @Table
}
```

**Separación correcta:**

- **Dominio:** `Product.java` (lógica pura)
- **Infraestructura:** `ProductEntity.java` (mapeo JPA)

**Beneficio:**

✅ El dominio es testable sin necesidad de contexto Spring ni base de datos.  
✅ Cumple con el principio de independencia de frameworks.  
✅ Arquitectura hexagonal correctamente implementada en este aspecto.

---

### 8.2 Uso de Factory Methods en el Dominio ✅

**Archivo:** `Product.java`  
**Líneas:** 28-42, 44-46

**Descripción:**

El dominio utiliza **factory methods** en lugar de constructores públicos:

```java
// ✅ Para crear nuevos productos
public static Product create(String name, String description, BigDecimal price, int stock) {
    validatePrice(price);
    validateStock(stock);
    return new Product(UUID.randomUUID(), name, description, price, stock, ProductStatus.ACTIVE);
}

// ✅ Para reconstruir desde persistencia
public static Product restore(UUID id, String name, String description, BigDecimal price, 
                             int stock, ProductStatus status) {
    return new Product(id, name, description, price, stock, status);
}
```

**Beneficios:**

✅ **Intención clara**: `create()` vs `restore()` tienen semánticas diferentes.  
✅ **Validaciones centralizadas**: Solo `create()` valida (porque `restore()` viene de BD ya validada).  
✅ **Constructor privado**: Evita construcción incorrecta.  
✅ **Patrón Named Constructor**: Mejora la legibilidad.

---

### 8.3 Validaciones en el Dominio ✅

**Archivo:** `Product.java`  
**Líneas:** 48-60

**Descripción:**

Las reglas de negocio están **encapsuladas en el dominio**:

```java
private static void validatePrice(BigDecimal price) {
    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidProductPriceException("Product price must be greater than zero");
    }
}

private static void validateStock(int stock) {
    if (stock < 0) {
        throw new InvalidProductStockException("Product stock cannot be negative");
    }
}
```

**Beneficio:**

✅ **Single Source of Truth**: Las reglas están en un solo lugar.  
✅ **Fail-fast**: Errores detectados en construcción, no en persistencia.  
✅ **Testabilidad**: Se pueden testear las validaciones aisladamente.

---

### 8.4 Separación de Validaciones de Formato vs Negocio ✅

**Archivos:** `CreateProductRequest.java` vs `Product.java`

**Descripción:**

Las validaciones están correctamente separadas por responsabilidad:

**Validaciones de formato (HTTP/API):**
```java
@NotBlank(message = "Product name is required")
@Size(max = 200, message = "Product name must not exceed 200 characters")
private String name;

@DecimalMin(value = "0.01", message = "Product price must be greater than zero")
private BigDecimal price;
```

**Validaciones de negocio (Dominio):**
```java
private static void validatePrice(BigDecimal price) {
    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidProductPriceException(...);
    }
}
```

**Análisis:**

✅ **No hay duplicación real**: Las validaciones de `@DecimalMin` son de *formato de entrada HTTP*, no de lógica de negocio.  
✅ **Responsabilidades claras**: DTO valida formato, Dominio valida semántica.  
✅ **Múltiples puntos de entrada**: Si se crea un producto desde CLI o mensaje, el dominio sigue validando.

---

### 8.5 Uso de Inyección de Dependencias por Constructor ✅

**Archivo:** `CreateProductService.java`  
**Líneas:** 13-16

**Descripción:**

Todos los servicios usan **constructor injection**:

```java
@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductPersistencePort productPersistencePort;

    public CreateProductService(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }
}
```

**Beneficios:**

✅ **Inmutabilidad**: Dependencias son `final`.  
✅ **Testabilidad**: Fácil de mockear sin Spring.  
✅ **Explícito**: Las dependencias son visibles en la firma del constructor.  
✅ **No usa `@Autowired` en campos** (antipatrón).

---

### 8.6 Uso de Records para DTOs ✅

**Archivo:** `OrderCreatedEventDTO.java` (inferido del código)

**Descripción:**

Los eventos usan **Java Records**:

```java
public record OrderCreatedEventDTO(
    UUID orderId,
    UUID userId,
    List<OrderItemEventDTO> items
) {
    public record OrderItemEventDTO(
        UUID productId,
        int quantity
    ) {}
}
```

**Beneficios:**

✅ **Inmutabilidad**: Los records son `final` por defecto.  
✅ **Concisión**: Sin boilerplate de getters/setters.  
✅ **Semántica clara**: Los DTOs son valores, no entidades.  
✅ **Pattern matching**: Compatible con futuras features de Java.

---

### 8.7 Logging Adecuado en Adaptadores ✅

**Archivo:** `StockDecrementService.java`  
**Líneas:** 30, 38, 54, 67

**Descripción:**

Los adaptadores registran eventos importantes:

```java
logger.info("Handling OrderCreatedEvent for order: {}", event.orderId());
logger.info("Successfully decremented stock for order: {}", event.orderId());
logger.debug("Decrementing {} units for product: {}", quantity, productId);
logger.error("Error handling OrderCreatedEvent for order: {}", event.orderId(), e);
```

**Beneficios:**

✅ **Trazabilidad**: Se puede seguir el flujo de un mensaje.  
✅ **Niveles correctos**: `info` para eventos, `debug` para detalles, `error` para fallos.  
✅ **Contexto incluido**: Siempre incluye IDs relevantes (`orderId`, `productId`).  
✅ **No logea en el dominio**: El dominio está libre de infraestructura.

---

### 8.8 Transaccionalidad en Operaciones Críticas ✅

**Archivo:** `StockDecrementService.java`  
**Línea:** 22

**Descripción:**

El servicio que modifica múltiples productos está marcado como transaccional:

```java
@Service
@Transactional
public class StockDecrementService implements HandleOrderCreatedUseCase {
    // ...
}
```

**Beneficio:**

✅ **Consistencia**: Si falla el decremento de un producto, se hace rollback de todos.  
✅ **Atomicidad**: La operación es "todo o nada".  
✅ **Protección ante concurrencia**: Evita condiciones de carrera.

---

## 9. Conclusión y Evaluación Final

### 9.1 Estado General del Microservicio

El microservicio **product-service** presenta una **arquitectura bien intencionada** con implementación parcial de **Arquitectura Hexagonal**.

**Fortalezas principales:**

✅ **Dominio puro**: Sin dependencias de infraestructura (JPA, Spring).  
✅ **Separación de capas**: Clara distinción entre dominio, aplicación e infraestructura.  
✅ **Modelo rico**: `Product` tiene comportamiento y validaciones.  
✅ **Uso de puertos**: Interfaces para desacoplar capas.  
✅ **Factory methods**: Construcción controlada de entidades.  
✅ **Validaciones en capas correctas**: Formato en DTOs, negocio en dominio.  
✅ **Inyección de dependencias**: Constructor injection consistente.  
✅ **Transaccionalidad**: Operaciones críticas protegidas.

---

### 9.2 Violaciones Críticas Detectadas

🔴 **Críticas (Prioridad Inmediata):**

1. **ISP-01**: Duplicación de puertos (`ProductRepositoryPort` vs `ProductPersistencePort`)
    - **Impacto**: Confusión arquitectónica, inconsistencia.
    - **Acción**: Unificar en un solo puerto de aplicación.

2. **DIP-01**: Servicio usa puerto del dominio en lugar de aplicación
    - **Impacto**: Violación de dirección de dependencias.
    - **Acción**: Mover puertos de salida a `application/port/out/`.

---

🟡 **Altas (Prioridad Media):**

3. **SRP-01**: Validación duplicada en servicio y dominio
    - **Impacto**: Mantenibilidad reducida, riesgo de inconsistencia.
    - **Acción**: Eliminar validación del servicio, confiar en el dominio.

4. **OCP-01**: Lógica de transición de estado ausente
    - **Impacto**: Extensibilidad limitada, endpoints faltantes.
    - **Acción**: Implementar métodos `activate()`, `deactivate()`, `delete()`.

5. **SRP-02**: Producto no valida su propio estado antes de operar
    - **Impacto**: Inconsistencia de estado, invariantes no garantizadas.
    - **Acción**: Validar estado en `decrementStock()`.

---

🟢 **Medias/Bajas (Prioridad Técnica de Deuda):**

6. **SMELL-01**: Excepciones sin contexto estructurado
7. **SMELL-02**: Manejo de errores con `RuntimeException` genérica
8. **SMELL-03**: Ausencia de `@ControllerAdvice`
9. **SMELL-04**: Endpoints REST incompletos (TODOs)

---

### 9.3 Comparación con user-service (según anterior.md)

| Aspecto | user-service | product-service |
|---------|--------------|-----------------|
| **Arquitectura declarada** | Capas tradicional | Hexagonal |
| **Dominio puro** | ❌ Acoplado a JPA | ✅ Libre de infraestructura |
| **Modelo de dominio** | ⚠️ Casi anémico | ✅ Rico (con comportamiento) |
| **Puertos e interfaces** | ❌ No implementados | ✅ Implementados (con inconsistencias) |
| **Validaciones** | ❌ En servicios | ✅ En dominio (con duplicación menor) |
| **Separación capas** | ❌ Difusa | ✅ Clara |
| **Dirección dependencias** | ❌ Hacia frameworks | ⚠️ Mayormente correcta |

**Conclusión comparativa:**

El **product-service** está **significativamente mejor diseñado** que el **user-service**. Las violaciones detectadas son **menores y corregibles** sin refactorización estructural profunda.

---

### 9.4 Nivel de Madurez Arquitectónica

Según el modelo de madurez DDD/Hexagonal:

| Nivel | Descripción | Estado |
|-------|-------------|--------|
| 0 - Caótico | Sin separación de capas | ❌ |
| 1 - Básico | Capas básicas (Controller-Service-Repository) | ❌ |
| 2 - Intermedio | Puertos definidos, dominio parcialmente puro | ✅ **Aquí** |
| 3 - Avanzado | Hexagonal completa, dominio rico, eventos | ⚠️ **Objetivo** |
| 4 - Maduro | DDD completo, CQRS, Event Sourcing | ❌ |

**Evaluación: Nivel 2 (Intermedio) con oportunidades de avanzar a Nivel 3.**

---

### 9.5 Riesgos para Escalabilidad

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| **Duplicación de puertos confunde a nuevos desarrolladores** | Alta | Alto | Unificar puertos inmediatamente |
| **Imposibilidad de gestionar estados bloquea features** | Media | Alto | Implementar métodos de transición |
| **Excepciones simplistas dificultan debugging** | Media | Medio | Enriquecer con campos estructurados |
| **Falta de DLQ causa pérdida de mensajes** | Alta | Alto | Configurar Dead Letter Queue |
| **Endpoints incompletos limitan operatividad** | Alta | Medio | Implementar TODOs críticos |

---

### 9.6 Recomendaciones Priorizadas

#### **Fase 1: Correcciones Arquitectónicas (Sprint 1)**

1. ✅ **Unificar puertos de salida**
    - Eliminar `ProductRepositoryPort` del dominio.
    - Usar solo `ProductPersistencePort` en aplicación.
    - Actualizar `StockDecrementService`.

2. ✅ **Eliminar validación duplicada**
    - Remover validación de stock en `StockDecrementService`.
    - Confiar en el dominio.

3. ✅ **Configurar Dead Letter Queue**
    - Evitar pérdida de mensajes fallidos.

---

#### **Fase 2: Enriquecimiento del Dominio (Sprint 2)**

4. ✅ **Implementar transiciones de estado**
    - Métodos: `activate()`, `deactivate()`, `delete()`.
    - Reglas de negocio para cada transición.

5. ✅ **Validar estado antes de operar**
    - `decrementStock()` solo si el producto está `ACTIVE`.

6. ✅ **Enriquecer excepciones de dominio**
    - Agregar campos estructurados (`productId`, `availableStock`, etc.).

---

#### **Fase 3: Completitud Funcional (Sprint 3)**

7. ✅ **Implementar endpoints REST faltantes**
    - `PUT /products/{id}`: Actualizar producto.
    - `PATCH /products/{id}/stock`: Añadir stock.
    - `DELETE /products/{id}`: Soft delete.
    - `PATCH /products/{id}/status`: Cambiar estado.

8. ✅ **Agregar `@ControllerAdvice` global**
    - Manejo centralizado de excepciones.
    - Respuestas HTTP consistentes.

---

#### **Fase 4: Calidad y Observabilidad (Sprint 4)**

9. ✅ **Implementar tests unitarios**
    - Tests del dominio (sin Spring).
    - Tests de servicios (con mocks).

10. ✅ **Implementar tests de integración**
    - Tests de adaptadores (con Testcontainers).
    - Tests de mensajería (RabbitMQ).

11. ✅ **Agregar documentación OpenAPI/Swagger**
    - Especificación de la API REST.

---

### 9.7 Conclusión Final

El microservicio **product-service** demuestra un **buen entendimiento de Arquitectura Hexagonal** con una implementación **mayormente correcta**.

Las violaciones detectadas son **específicas y corregibles** sin requerir una reescritura estructural.

**Puntuación técnica:** 7.5/10

**Desglose:**
- ✅ Arquitectura: 8/10 (buena separación, con inconsistencias menores)
- ✅ Dominio: 7/10 (rico pero con lógica de estado faltante)
- ✅ SOLID: 7/10 (SRP e ISP vulnerados puntualmente)
- ✅ Testabilidad: 6/10 (diseño testable, pero sin tests)
- ✅ Completitud: 7/10 (funcionalidades críticas faltantes)

**Recomendación final:**  
Implementar las correcciones de **Fase 1** inmediatamente (críticas) y planificar las siguientes fases en el próximo trimestre.

Este microservicio es una **base sólida** para evolucionar hacia un sistema de grado profesional.

---

**Auditoría completada.**  
**Próximo paso:** Fase 2 del reto (Investigación y Arquitectura - Patrones de Diseño).

---

**Elaborado por:** Equipo Backend  
**Revisado con:** GitHub Copilot  
**Metodología:** Auditoría técnica guiada por SOLID, DDD y Arquitectura Hexagonal  
**Herramientas:** Análisis estático de código, revisión arquitectónica manual  