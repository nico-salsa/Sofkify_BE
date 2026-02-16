# AUDITORIA.md

# Auditoría Técnica del Sistema - Cart Service 

## 1. Información General

Proyecto: Softkify

Repositorio: https://github.com/softkify/Sofkify_BE

Rama Evaluada:  

Commit Base (Snapshot): audit: snapshot post-mvp  

Fecha de Auditoría: 12/02/2026

Equipo Auditor: Backend

---

## 2. Contexto y Alcance

Documento correspondiente a la Fase 1 del reto técnico: Diagnóstico y Snapshot Arquitectónico.

El objetivo es evaluar el estado estructural del sistema posterior al MVP, identificando:

- Violaciones a principios SOLID.
- Antipatrones y code smells relevantes.
- Riesgos arquitectónicos.
- Impacto en escalabilidad, mantenibilidad y testabilidad.

Este documento no contempla refactorización, sino diagnóstico técnico fundamentado.

---

## 3. Observación General de Arquitectura

El cart-service presenta una arquitectura hexagonal bien estructurada con clara separación de responsabilidades:

- **Organización por capas:** Estructura limpia con domain, application, e infrastructure claramente separados
- **Ports and Adapters:** Implementación correcta del patrón con use cases (input) y repository ports (output)
- **Dominio rico:** Los modelos Cart y CartItem contienen lógica de negocio y validaciones adecuadas
- **Inyección de dependencias:** Uso correcto de Spring para gestión de dependencias
- **Separación de concerns:** Controller, services y repository bien delimitados

El servicio sigue principios de DDD con agregados bien definidos y lógica de negocio contenida en el dominio.

---

## 4. Metodología de Evaluación

### 4.1 Criterios de Análisis

1. Revisión estructural por capas.
2. Cumplimiento de principios SOLID.
3. Evaluación de cohesión y acoplamiento.
4. Identificación de duplicación de lógica.
5. Análisis de dirección de dependencias.
6. Identificación de riesgos de escalabilidad.

### 4.2 Formato de Registro de Hallazgos

Cada hallazgo incluye:

- Archivo
- Línea(s)
- Principio vulnerado
- Descripción del problema
- Impacto técnico
- Riesgo arquitectónico
- Recomendación técnica

---

## 5. Mapa de Riesgo Técnico

| Hallazgo | Categoría | Severidad | Impacto | Probabilidad | Prioridad |
|----------|------------|-----------|----------|--------------|------------|
| SRP-01   | Diseño     | Media     | Media    | Alta         | Media      |
| SRP-02   | Datos      | Alta      | Alta     | Alta         | Alta       |
| SRP-03   | Datos      | Media     | Media    | Media        | Media      |
| OCP-01   | Diseño     | Media     | Media    | Alta         | Media      |
| ISP-01   | Diseño     | Baja      | Baja     | Baja         | Baja       |
| DIP-01   | Arquitectura | Media   | Media    | Media        | Media      |

---

Criterios:

- Severidad: Magnitud estructural.
- Impacto: Consecuencia operativa o evolutiva.
- Probabilidad: Frecuencia esperada.
- Prioridad: Urgencia de intervención.

---

## 6. Hallazgos

### 6.1 Single Responsibility Principle (SRP)

#### Hallazgo SRP-01

Archivo: CartController.java  
Línea(s): 153-179  
Principio vulnerado: SRP (Single Responsibility Principle)  

Descripción del problema: El controller contiene dos métodos privados de transformación (toCartResponse y toCartItemResponse) que mezclan responsabilidad de presentación con transformación de datos. Además, el método toCartResponse realiza cálculos de negocio (cálculo de total en líneas 161-163).

Impacto técnico: Dificulta el testing unitario, acopla la capa de presentación con lógica de negocio y transformación de datos.

Riesgo arquitectónico: Violación de separación de concerns, posible introducción de errores de cálculo en la capa incorrecta.

Recomendación técnica: Extraer la lógica de mapeo a una clase CartDtoMapper separada y mover los cálculos de negocio al dominio o servicios de aplicación.

#### Hallazgo SRP-02

Archivo: UserServiceAdapter.java y ProductServiceAdapter.java  
Línea(s): 40-59 (UserServiceAdapter) y 56-77 (ProductServiceAdapter)  
Principio vulnerado: SRP (Single Responsibility Principle)  

Descripción del problema: Los adapters de mensajería contienen DTOs internos (UserResponse líneas 40-59 y ProductResponse líneas 56-77) que no deberían estar definidos en la capa de infraestructura. Estos DTOs representan contratos con otros servicios y deberían estar en un paquete compartido o de dominio.

Impacto técnico: Acoplamiento de contratos de servicios con implementaciones específicas, dificulta la reutilización y testing de los contratos.

Riesgo arquitectónico: Violación de separación de concerns, posible inconsistencia en los contratos si se modifican en diferentes lugares.

Recomendación técnica: Extraer los DTOs a un paquete shared o domain contracts, y mantener los adapters solo con la lógica de comunicación HTTP.

#### Hallazgo SRP-03

Archivo: ConfirmCartService.java  
Línea(s): 1  
Principio vulnerado: SRP (Single Responsibility Principle)  

Descripción del problema: El archivo ConfirmCartService está completamente vacío, sin implementación. Esto viola el principio de responsabilidad al no cumplir su propósito declarado.

Impacto técnico: Código incompleto, posible error de compilación, pérdida de funcionalidad en el sistema.

Riesgo arquitectónico: Funcionalidad faltante, posible inconsistencia en el diseño del sistema.

Recomendación técnica: Implementar la funcionalidad de confirmación de carrito o eliminar el archivo si no es necesario.

---

### 6.2 Open/Closed Principle (OCP)

#### Hallazgo OCP-01

Archivo: CartController.java  
Línea(s): 153-179  
Principio vulnerado: OCP (Open/Closed Principle)  

Descripción del problema: Los métodos de transformación toCartResponse (líneas 153-167) y toCartItemResponse (líneas 169-179) están definidos directamente en el controller. Si se necesitan nuevas representaciones o modificar los cálculos, se debe modificar el controller.

Impacto técnico: Acopla la capa de presentación con la transformación de DTOs, dificulta la reutilización y testing de las transformaciones.

Riesgo arquitectónico: Violación de separación de responsabilidades, posible duplicación de lógica de mapeo en otros controllers.

Recomendación técnica: Extraer la lógica de mapeo a una clase CartDtoMapper separada con métodos específicos para cada transformación.

---

### 6.3 Liskov Substitution Principle (LSP)

No se encontraron violaciones significativas al principio LSP en el código analizado. Las jerarquías existentes son simples y bien definidas.

---

### 6.4 Interface Segregation Principle (ISP)

#### Hallazgo ISP-01

Archivo: CartRepositoryPort.java  
Línea(s): 8-15  
Principio vulnerado: ISP (Interface Segregation Principle)  

Descripción del problema: La interfaz CartRepositoryPort contiene métodos de consulta y persistencia en la misma interfaz. Clientes que solo necesitan consultar carritos no deberían depender de métodos de persistencia.

Impacto técnico: Acoplamiento innecesario, violación del principio de mínima dependencia, posible confusión en la implementación.

Riesgo arquitectónico: Bajo - no afecta funcionalmente pero reduce la claridad del diseño.

Recomendación técnica: Segregar la interfaz en múltiples interfaces especializadas: CartQueryPort y CartCommandPort.

---

### 6.5 Dependency Inversion Principle (DIP)

#### Hallazgo DIP-01

Archivo: AddItemToCartService.java  
Línea(s): 3-8  
Principio vulnerado: DIP (Dependency Inversion Principle)  

Descripción del problema: El servicio de aplicación depende directamente de puertos de otros dominios (ProductServicePort, UserServicePort) creando acoplamiento entre bounded contexts. Esto viola la autonomía de los microservicios.

Impacto técnico: Acoplamiento directo entre servicios, dificultad para testear y reemplazar implementaciones, posible cascada de fallos.

Riesgo arquitectónico: Rompe la autonomía de microservicios, crea dependencias directas entre bounded contexts.

Recomendación técnica: Utilizar eventos de dominio o mensajería asíncrona para comunicarse con otros servicios en lugar de dependencias directas.

---

## 7. Code Smells Relevantes

### 7.1 Complejidad elevada

No se detectaron métodos con complejidad ciclomática elevada. Los métodos mantienen un nivel de complejidad adecuado.

### 7.2 Duplicación de lógica

**Archivo:** CartController.java  
**Línea(s):** 153-180  
**Problema:** El controller contiene métodos de transformación manual que deberían estar centralizados en una clase mapper dedicada.

**Impacto:** Violación de separación de responsabilidades, el controller no debe realizar transformaciones de datos. Dificulta la reutilización y testing de la lógica de mapeo.

### 7.3 Lógica de negocio en capa incorrecta

**Archivo:** CartController.java  
**Línea(s):** 161-163  
**Problema:** El cálculo del total del carrito (sum de subtotales) se realiza en el controller en lugar de en el dominio o servicio de aplicación.

**Impacto:** Lógica de negocio dispersa en capa de presentación, posible inconsistencia en cálculos, dificultad para testing unitario.

### 7.4 Validaciones duplicadas

**Archivo:** CartItem.java  
**Línea(s):** 33-35 (validate()) y 47-49 (setQuantity())  
**Problema:** La validación de quantity > 0 está duplicada en el método validate() (líneas 33-35) y en el método setQuantity() (líneas 47-49).

**Impacto:** Duplicación de reglas de validación, posible inconsistencia si las reglas cambian en un solo lugar, mantenimiento más complejo.

### 7.5 Falta de especialización en excepciones

**Archivo:** CartException.java  
**Línea(s):** 3-11  
**Problema:** La excepción personalizada extiende directamente RuntimeException sin seguir una jerarquía o especialización adecuada. No hay diferenciación entre tipos de errores del dominio.

**Impacto:** Dificultad en el manejo centralizado de errores, falta de semántica en el tratamiento de excepciones, posible pérdida de contexto del error.

**Recomendación:** Crear excepciones especializadas por tipo de error (CartNotFoundException, InsufficientStockException, InvalidProductException).

### 7.6 Manejo inadecuado de excepciones en infraestructura

**Archivo:** ProductServiceAdapter.java  
**Línea(s):** 30 y 41  
**Problema:** El adapter lanza RuntimeException genérica en lugar de excepciones específicas del dominio, perdiendo contexto del error y dificultando el manejo adecuado.

**Impacto:** Pérdida de contexto del error, manejo genérico de excepciones, dificultad para debugging y tracing.

**Recomendación:** Lanzar excepciones específicas del dominio o crear excepciones de infraestructura apropiadas.

### 7.7 Código incompleto

**Archivo:** ConfirmCartService.java  
**Línea(s):** 1  
**Problema:** El archivo está completamente vacío sin ninguna implementación.

**Impacto:** Funcionalidad faltante, posible error de compilación, pérdida de capacidad de confirmación de carritos.

**Recomendación:** Implementar la funcionalidad de confirmación o eliminar el archivo si no es requerida.

---

## 8. Aciertos Arquitectónicos

### 8.1 Buen uso de abstracciones

- **Ports and Adapters:** Implementación correcta del patrón hexagonal con interfaces bien definidas
- **Dominio rico:** Los modelos Cart y CartItem contienen validaciones y comportamiento de negocio adecuado
- **Inyección de dependencias:** Uso correcto de constructor injection en todas las clases

### 8.2 Separación de capas

- **Estructura limpia:** Separación clara entre domain, application e infrastructure
- **DTOs bien definidos:** Separación adecuada entre modelos de dominio y de transporte
- **Use Cases:** Implementación correcta de patrones de casos de uso en el dominio

### 8.3 Buenas prácticas observadas

- **Inmutabilidad:** Uso correcto de campos finales en modelos de dominio
- **Validaciones:** Separación adecuada entre validaciones de dominio y de aplicación
- **Transaccionalidad:** Uso correcto de @Transactional en servicios de aplicación

---

## 9. Conclusión

El cart-service presenta una arquitectura sólida con una base hexagonal bien implementada y clara separación de responsabilidades. La mayoría de los principios SOLID se cumplen adecuadamente y el código sigue buenas prácticas de DDD.

**Hallazgos principales:**
- **Arquitectura robusta:** Estructura hexagonal correctamente implementada
- **Dominio bien modelado:** Lógica de negocio contenida en los modelos Cart y CartItem
- **Bajo acoplamiento:** Uso correcto de interfaces e inyección de dependencias

**Oportunidades de mejora identificadas:**
- **Diseño:** Extraer lógica de mapeo a clases especializadas
- **Arquitectura:** Revisar acoplamiento entre microservicios y ubicación de DTOs
- **Consistencia:** Unificar validaciones duplicadas y centralizar cálculos de negocio
- **Manejo de errores:** Especializar excepciones y mejorar manejo en infraestructura

**Impacto general:** Los hallazgos detectados son de baja a media severidad y no comprometen la funcionalidad actual. Las mejoras sugeridas incrementarán la mantenibilidad y testabilidad del sistema a largo plazo.

Este documento constituye la línea base para la fase de refactorización dirigida del cart-service.