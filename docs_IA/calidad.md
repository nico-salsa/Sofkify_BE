# CALIDAD.md

---

# 1. ANATOMÍA DE UN INCIDENTE

Este documento recoge incidentes críticos ocurridos durante el desarrollo del proyecto **Softkify**, enfocados en puntos de integración del backend bajo arquitectura hexagonal y entorno de microservicios.

---

## INCIDENTE: Configuración CORS – Backend

### Contexto

Durante la integración entre frontend y backend, las peticiones realizadas desde el navegador eran bloqueadas por la política CORS, aunque el backend respondía correctamente a herramientas como Postman e Insomnia.

El backend es un microservicio construido con Spring Boot bajo arquitectura hexagonal.

---

### Error (Causa Humana)

La configuración CORS fue aplicada directamente en el controlador mediante `@CrossOrigin`, sin una estrategia centralizada ni validación mediante pruebas de integración.

---

### Defecto (Código)

Configuración localizada en el controller:

```java
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {
    ...
}
```

Esto generó:

- Configuración dispersa
- Falta de validación del preflight (OPTIONS)
- Dificultad para escalar la política CORS en microservicios

---

### Solución Aplicada

Se migró la configuración a una clase centralizada:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

### Fallo (Impacto en Usuario)

El navegador bloqueaba la petición con error:

```
No 'Access-Control-Allow-Origin' header is present
```

Postman no detectaba el problema porque no aplica políticas CORS.

---

### Aprendizaje Técnico

- CORS debe configurarse centralmente.
- Las peticiones `OPTIONS` deben validarse con pruebas de integración.
- Postman no es suficiente para validar comportamiento real del navegador.

---

# 2. ANÁLISIS DE PIRÁMIDE DE PRUEBAS – BACKEND

## Contexto Arquitectónico

Softkify backend está construido bajo:

- Arquitectura Hexagonal (Ports & Adapters)
- Microservicios independientes
- Comunicación asíncrona con RabbitMQ (implementado)
- Roles de dominio: Cliente y Admin
- `@ControllerAdvice` global para manejo de errores
- Sin Spring Security actualmente

---

## Estrategia de Testing Adoptada

Dado que se usa arquitectura hexagonal, las pruebas priorizan dominio y casos de uso sobre infraestructura.

### Distribución Recomendada

```
Unit (Dominio + UseCases)        60%
Integration (Adapters + Spring)  30%
API Schema Validation            10%
```

---

## 2.1 Pruebas Unitarias

Enfocadas en:

- Casos de uso (Application layer)
- Reglas de negocio (Domain)
- Validaciones
- Invariantes
- Excepciones de dominio

Características:

- Uso de JUnit 5
- Uso de Mockito
- Se mockean puertos, no repositorios concretos
- No se levanta contexto Spring

Objetivo: Validar lógica pura independiente de infraestructura.

---

## 2.2 Pruebas de Integración

**Estado actual:**  
Aún no implementadas completamente. Se planifica realizarlas posteriormente.

Alcance previsto:

- Controllers con MockMvc
- Validación de configuración CORS (preflight OPTIONS)
- Validación de `@ControllerAdvice`
- Persistencia con base de datos real
- Validación de serialización JSON

Nota:

Actualmente no se usan Testcontainers.  
Se evaluará su incorporación cuando se implementen pruebas con PostgreSQL real.

---

## 2.3 Validación de API (Schema)

Actualmente se realizan pruebas con RestAssured para:

- Validar estructura de response
- Validar códigos HTTP
- Verificar que no se expongan campos sensibles (ej. password)
- Validar formato uniforme de error

Importante:

Estas pruebas validan comportamiento de API,  
pero no constituyen Contract Testing Consumer-Driven.

Futuro objetivo:

- Evaluar Spring Cloud Contract o Pact en entorno multi-microservicio.

---

# 3. MENSAJERÍA ASÍNCRONA (RabbitMQ)

## Estado Actual

RabbitMQ está implementado para comunicación entre microservicios.

Actualmente:

- Se publican eventos desde el microservicio.
- Existen consumidores con `@RabbitListener`.

Pero:

- No se han implementado aún pruebas automatizadas de mensajería.

---

## Estrategia de Testing Planificada

Se planifica implementar:

- Pruebas de publicación de eventos tras casos de uso exitosos.
- Pruebas de serialización del mensaje.
- Pruebas de consumo con broker embebido o Testcontainers.
- Validación de idempotencia del consumidor.
- Validación de manejo de errores y reintentos.

Estas pruebas aún no están implementadas y forman parte del roadmap de calidad.

---

# 4. MANEJO GLOBAL DE ERRORES

El backend implementa `@ControllerAdvice` para:

- Manejar excepciones de dominio.
- Estandarizar estructura de error.
- Retornar códigos HTTP adecuados (400, 409, 404, etc.).

Estado actual:

- Implementado.
- Pruebas automatizadas específicas aún pendientes.

Formato esperado de error:

```json
{
  "timestamp": "...",
  "status": 400,
  "message": "Descripción del error"
}
```

---

# 5. ROLES Y CONTROL DE ACCESO

El dominio define dos roles:

- Cliente
- Admin

Actualmente:

- Los roles existen a nivel de modelo.
- No se ha implementado Spring Security.
- No existen pruebas de autorización aún.

Futuro objetivo:

- Implementar Spring Security.
- Agregar pruebas de autorización por rol.
- Validar endpoints restringidos.

---

# 6. COBERTURA Y MÉTRICAS

## Objetivos Backend

```
Líneas:     ≥ 80%
Ramas:      ≥ 75%
Funciones:  ≥ 85%
Casos de uso críticos: 100%
```

Crítico incluye:

- Casos de uso principales
- Validaciones de dominio
- Configuración CORS
- Manejo global de errores

Cobertura no es el único indicador de calidad.  
Se prioriza cobertura de reglas de negocio sobre cobertura superficial.

---

# 7. CHECKLIST DE CALIDAD – BACKEND

Antes de cada commit:

- [ ] Casos de uso tienen pruebas unitarias
- [ ] No existen dependencias directas de dominio a infraestructura
- [ ] Puertos correctamente mockeados en unit tests
- [ ] CORS configurado centralmente
- [ ] `@ControllerAdvice` mantiene estructura uniforme
- [ ] No se exponen datos sensibles en responses
- [ ] Eventos publicados correctamente desde el caso de uso
- [ ] Build pasa correctamente (`mvn clean install`)
- [ ] Tests pasan (`mvn test`)
- [ ] No existen warnings críticos de compilación

---

# 8. ROADMAP DE MADUREZ DE CALIDAD (BACKEND)

Fase actual:

- Unit tests en dominio
- API validation básica
- Manejo global de errores
- CORS centralizado

Fase siguiente:

- Pruebas de integración completas
- Incorporación de Testcontainers
- Pruebas de RabbitMQ
- Contract Testing real
- Implementación de Spring Security
- Pruebas de autorización por rol

---

# 9. LECCIONES APRENDIDAS (BACKEND)

1. La integración real ocurre en bordes del sistema (HTTP y mensajería).
2. Las pruebas unitarias no detectan errores de configuración.
3. La mensajería asíncrona requiere estrategia de testing dedicada.
4. La configuración debe centralizarse.
5. La calidad debe evolucionar junto con la arquitectura.

---

**Última actualización**: 2026-02-15  
**Mantenido por**: Equipo Backend Softkify  
**Versión**: 1.2 – Refinado para arquitectura hexagonal y microservicios
