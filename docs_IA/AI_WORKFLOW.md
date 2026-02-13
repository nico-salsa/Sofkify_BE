# AI_WORKFLOW – Estrategia de Trabajo AI-First

## 1. Propósito del Documento

Este documento define **cómo se utiliza la Inteligencia Artificial durante el desarrollo del proyecto Ecommerce Asíncrono**.

Establece un **flujo de trabajo controlado**, donde la IA actúa como **asistente técnico** bajo la dirección del arquitecto del proyecto.

Este archivo **no define reglas de negocio ni arquitectura**.  
Su objetivo es garantizar **coherencia, calidad y control** en el uso de IA durante todo el ciclo de vida del sistema.

El proyecto se concibe como un **e-commerce orientado a eventos**, desarrollado de forma **incremental**.

---

## 2. Rol de la IA en el Proyecto

La IA actúa como:

* Desarrollador técnico asistido
* Generador de propuestas de diseño
* Acelerador de implementación y pruebas
* Soporte para documentación técnica
* Asistente en refactoring y mantenimiento

La IA **NO** actúa como:

* Arquitecto del sistema
* Dueño del dominio
* Autoridad de decisiones técnicas finales
* Sustituto de validación humana

Toda decisión final es responsabilidad del arquitecto.

---

## 3. Fuentes de Verdad del Proyecto

Antes de cualquier interacción con la IA, deben existir y usarse como contexto obligatorio:

1. `context.md`  
   → Fuente de verdad del dominio y reglas de negocio.

2. `architecture.md`  
   → Fuente de verdad técnica y estructural.

3. `events.md`  
   → Fuente de verdad de contratos de eventos de integración.

4. `HU-*.md`  
   → Historias de usuario técnicas que delimitan el alcance.

La IA **no puede contradecir** estos documentos.

### Precedencia en caso de conflicto

En caso de conflicto entre documentos, se aplica el siguiente orden
de precedencia obligatoria:

1. `context.md` – reglas de negocio y dominio
2. `architecture.md` – estructura y decisiones técnicas
3. `events.md` – contratos de eventos de integración
4. `HU-*.md` – alcance específico de implementación
5. Código existente aprobado

La IA debe **detenerse y pedir aclaración** si detecta inconsistencias.

---

## 4. Principios de Uso de IA

* La IA **no trabaja sin contexto**.
* La IA **no implementa sin una historia de usuario**.
* La IA **no redefine arquitectura**.
* La IA **no introduce lógica de negocio fuera del dominio**.
* La IA **no implementa bounded contexts sin autorización explícita**.
* La IA **siempre justifica su propuesta** cuando diseña.
* La IA **respeta el código existente** y solo lo modifica bajo instrucción explícita.

El uso de IA es **guiado, iterativo y validado**.

---

## 5. Flujo de Trabajo AI-First

### 5.1 Planificación

Objetivo: construir entendimiento del sistema y del alcance vigente.

Actividades:

* Definición o refinamiento del dominio.
* Redacción o refinamiento del contexto del sistema.
* Definición de flujos de negocio.
* Creación o ajuste de historias de usuario.
* Identificación de requisitos funcionales y no funcionales.
* Definición de contratos de eventos.

Uso de IA:

* Ayuda a estructurar ideas.
* Refina redacciones.
* Propone alternativas conceptuales dentro del alcance definido.
* Identifica inconsistencias entre documentos.

Validación:

* El arquitecto revisa y aprueba.
* Ningún documento se considera definitivo sin validación humana.

Entregables:

* `context.md`
* `architecture.md` (solo si se toman nuevas decisiones estructurales)
* `events.md` (cuando se definen nuevos eventos)
* `HU-*.md`

---

### 5.2 Desarrollo

Objetivo: implementar historias de usuario de forma controlada.

Flujo por historia:

1. El arquitecto entrega a la IA:
    * Historia de usuario
    * Contexto relevante (`context.md`, `architecture.md`, `events.md`)
    * Lineamientos de arquitectura
    * Código existente relacionado

2. La IA genera un **Plan de Implementación**, que incluye:
    * Casos de uso involucrados
    * Capas afectadas
    * Componentes a crear o modificar
    * Flujo técnico esperado
    * Eventos a emitir o consumir (si aplica)
    * Dependencias con otros servicios

3. El arquitecto valida el plan.

4. La IA genera el código alineado al plan aprobado.

5. El arquitecto revisa:
    * Diseño
    * Ubicación del código
    * Cumplimiento de arquitectura hexagonal
    * Separación de capas
    * Claridad y limpieza
    * Manejo de eventos

6. La IA ajusta según feedback.

7. Solo entonces se integra el código.

Regla clave:
> La IA **no escribe código directamente sin un plan validado**.

---

### 5.3 Refactoring y Mantenimiento

Objetivo: mejorar código existente sin romper funcionalidad.

Uso de IA:

* Proponer mejoras de diseño.
* Identificar código duplicado.
* Sugerir aplicación de patrones.
* Detectar violaciones de arquitectura.
* Proponer optimizaciones.

Validación:

* El arquitecto define qué refactorizar.
* La IA propone cómo hacerlo.
* El arquitecto valida que no se rompa funcionalidad.
* Se ejecutan pruebas existentes.

Restricciones:

* No se modifica lógica de negocio sin instrucción explícita.
* No se cambian contratos públicos (APIs, eventos) sin análisis de impacto.

---

### 5.4 QA

Objetivo: validar calidad y corrección funcional.

Uso de IA:

* Proponer casos de prueba.
* Generar tests unitarios.
* Generar tests de integración.
* Proponer escenarios de error.
* Ayudar a detectar edge cases.
* Validar manejo de eventos asíncronos.

Validación humana:

* Ejecución de flujos funcionales.
* Revisión de manejo de errores.
* Validación de integración con mensajería y eventos.
* Pruebas de consistencia eventual.

Una historia **no se considera completa** hasta pasar QA.

---

### 5.5 Despliegue / Instalación

Objetivo: asegurar que el sistema pueda ejecutarse correctamente.

Uso de IA:

* Generar guías de instalación.
* Proponer configuración de entornos.
* Ayudar con Dockerización y scripts.
* Documentar variables de entorno.
* Crear docker-compose para desarrollo local.

Validación:

* El arquitecto prueba el proceso en entorno limpio.
* Se documenta el procedimiento final.

---

### 5.6 Gestión de Documentación

* Los archivos `context.md`, `architecture.md` y `events.md` solo se modifican
  durante la fase de planificación o por decisión explícita del arquitecto.
* Las historias de usuario pueden evolucionar durante el desarrollo.
* La IA **no modifica documentos base** sin instrucción explícita.
* La documentación debe mantenerse sincronizada con el código.

---

## 6. Reglas Obligatorias de Interacción

* Toda solicitud a la IA debe incluir:
    * Qué se está construyendo
    * En qué bounded context y capa
    * Qué restricciones aplicar
    * Qué ya existe
    * Qué documentos de contexto aplicar

* Ningún código entra sin revisión humana.
* Ninguna historia se marca como finalizada sin validación.
* La IA no toma decisiones finales.
* La IA debe señalar cuando detecta que falta información.

---

## 7. Formato Esperado de Solicitudes a la IA

### Ejemplo correcto para nueva funcionalidad:

> "Con base en la HU-ORDER-01, el `context.md`, `architecture.md` y `events.md`, genera el plan de implementación del caso de uso CreateOrderUseCase en order-service siguiendo arquitectura hexagonal. Este use case debe emitir el evento OrderCreated."

### Ejemplo correcto para refactoring:

> "Analiza el código actual de CreateOrderService en order-service y propón un refactoring para mejorar la separación de responsabilidades, respetando la arquitectura hexagonal definida en `architecture.md`."

### Ejemplo correcto para debugging:

> "El consumer OrderCreatedConsumer en product-service no está procesando eventos correctamente. Analiza el código del consumer, la configuración de RabbitMQ y el contrato del evento OrderCreated en `events.md` para identificar el problema."

### Ejemplos incorrectos:

❌ "Haz el servicio de órdenes."  
❌ "Arregla el bug."  
❌ "Implementa todo el cart-service."

---

## 8. Contexto Necesario por Tipo de Tarea

### Para implementar un Use Case:
* `context.md` → reglas de negocio
* `architecture.md` → estructura de capas
* `HU-*.md` → historia específica
* Código del agregado relacionado

### Para implementar un Producer de eventos:
* `context.md` → cuándo se genera el evento
* `architecture.md` → cómo publicar eventos
* `events.md` → contrato del evento
* Código del Use Case que emite el evento

### Para implementar un Consumer de eventos:
* `events.md` → contrato del evento a consumir
* `architecture.md` → cómo consumir eventos
* `context.md` → qué hacer al recibir el evento
* Configuración de RabbitMQ existente

### Para implementar comunicación REST:
* `architecture.md` → patrones de comunicación síncrona
* API del servicio destino
* Port Out correspondiente

---

## 9. Validación y Aprobación

El ciclo de validación es:

**Arquitecto define → IA propone → Arquitecto revisa → IA ajusta → Arquitecto aprueba → Se integra.**

Este ciclo es obligatorio para:

* Nuevas funcionalidades
* Refactorings significativos
* Cambios en contratos públicos
* Modificaciones arquitectónicas

Para cambios menores (ej: corrección de typos), el ciclo puede simplificarse.

---

## 10. Trabajo con Código Existente

Cuando el proyecto ya tiene código implementado:

### Principios:

* **Entender antes de modificar**: La IA debe analizar el código existente antes de proponer cambios.
* **Respetar decisiones previas**: No se cambia arquitectura sin justificación sólida.
* **Coherencia**: Nuevas implementaciones deben seguir los patrones existentes.
* **Documentar desviaciones**: Si el código existente no sigue `architecture.md`, documentar antes de refactorizar.

### Proceso:

1. La IA analiza el código existente del bounded context.
2. Identifica patrones y decisiones de diseño aplicadas.
3. Propone implementación coherente con lo existente.
4. Si detecta inconsistencias, las reporta al arquitecto.
5. El arquitecto decide si mantener coherencia o refactorizar.

---

## 11. Antipatrones (Prohibido)

* Pedir código sin historia de usuario.
* Pedir soluciones genéricas sin contexto.
* Permitir que la IA redefina arquitectura.
* Implementar bounded contexts sin autorización.
* Mezclar reglas de negocio en infraestructura.
* Integrar código sin revisión.
* Modificar contratos de eventos sin análisis de impacto.
* Cambiar código existente sin entender su propósito.
* Ignorar inconsistencias detectadas.

---

## 12. Buenas Prácticas

### Para el Arquitecto:

* Proveer contexto completo en cada solicitud.
* Validar planes antes de aprobar implementación.
* Documentar decisiones arquitectónicas tomadas.
* Mantener `context.md` y `architecture.md` actualizados.
* Revisar código generado con criterio crítico.

### Para la IA:

* Pedir aclaraciones cuando falta contexto.
* Justificar decisiones de diseño.
* Señalar riesgos y trade-offs.
* Proponer alternativas cuando sea pertinente.
* Documentar supuestos realizados.

---

## 13. Formato de Historias de Usuario

Las historias de usuario deben seguir el formato:

```markdown
# HU-<BOUNDED_CONTEXT>-<NUMBER>: <Título>

## Descripción
<Descripción clara de la funcionalidad>

## Contexto del Dominio
<Reglas de negocio aplicables desde context.md>

## Requisitos Técnicos
- Bounded Context: <nombre>
- Agregado(s) involucrado(s): <lista>
- Use Case(s) a implementar: <lista>
- Eventos a emitir/consumir: <lista>

## Criterios de Aceptación
- [ ] <Criterio 1>
- [ ] <Criterio 2>
...

## Notas Técnicas
<Consideraciones adicionales>
```

---

## 14. Ciclo de Vida de una Historia

1. **Planificación**: El arquitecto crea la HU basándose en `context.md`.
2. **Análisis**: La IA analiza contexto y propone plan de implementación.
3. **Validación del Plan**: El arquitecto revisa y aprueba.
4. **Implementación**: La IA genera código según plan aprobado.
5. **Revisión de Código**: El arquitecto valida cumplimiento.
6. **Ajustes**: La IA corrige según feedback.
7. **QA**: Se validan criterios de aceptación.
8. **Integración**: Se integra a la rama correspondiente.
9. **Documentación**: Se actualiza documentación si es necesario.

---

## 15. Evolución del Documento

Este documento puede evolucionar cuando:

* Se identifican nuevos antipatrones.
* Se descubren mejores prácticas.
* Cambian las herramientas de IA utilizadas.
* Se incorporan nuevos miembros al equipo.

Los cambios deben ser consensuados y documentados.

---

Este documento es **vinculante** para el uso de IA durante todo el proyecto.