## Context

El proyecto opera en dos repositorios separados (`Sofkify_BE` y `sofkify-fe`) sin una estrategia unificada de ejecución local. En backend existe `docker-compose.yml` para microservicios y dependencias, pero no hay servicio de frontend integrado ni contrato operativo documentado para correr ambos repos de forma consistente.

En frontend hay mezcla de clientes HTTP reales y comportamiento mock. `src/services/products/productService.ts` mantiene datos en memoria (`products.ts`) y no emite requests al backend. Además, hay desalineación de rutas: frontend usa `/api/v1/...` para carrito/orden, mientras backend expone `/api/carts` y `/api/orders`.

También existe inconsistencia de infraestructura en backend: configuraciones CORS de `cart-service` y `order-service` están declaradas con package de `userservice`, por lo que no se cargan en esos microservicios y pueden bloquear llamadas cross-origin.

## Goals / Non-Goals

**Goals:**
- Garantizar ejecución local FE+BE reproducible con Docker Compose entre repos separados.
- Asegurar que los flujos críticos del frontend emitan requests HTTP reales al backend (sin mocks ocultos).
- Alinear contratos de endpoints FE/BE para carrito, órdenes, productos y autenticación.
- Habilitar observabilidad explícita de requests en consola (frontend y logs backend).
- Limpiar artefactos y documentación obsoleta en ambos repositorios y dejar READMEs operativos.

**Non-Goals:**
- Rediseñar el dominio de órdenes/carrito ni cambiar reglas de negocio internas existentes.
- Introducir un API Gateway productivo con autenticación avanzada en esta iteración.
- Optimizar rendimiento o seguridad de producción más allá de lo necesario para desarrollo local funcional.

## Decisions

### 1) Orquestación multi-repo con compose de integración
- **Decisión**: Crear una capa de orquestación para desarrollo que levante backend y frontend dentro de la misma red Docker, manteniendo compatibilidad con la ejecución backend standalone.
- **Rationale**: El problema principal es integración local inconsistente; una sola entrada de arranque reduce drift entre equipos.
- **Alternativas consideradas**:
  - Mantener `docker-compose` separados en cada repo: se descarta por duplicación de configuración y redes no coordinadas.
  - Crear un tercer repo de infraestructura: se descarta por costo operativo inmediato y mayor fricción para el equipo.

### 2) Configuración de API en frontend por servicio (sin mocks)
- **Decisión**: Definir variables de entorno explícitas para cada servicio backend y eliminar fallback a datos mock en rutas críticas.
- **Rationale**: Hoy un único `VITE_API_BASE_URL` no representa arquitectura de microservicios y enmascara fallos de contrato.
- **Alternativas consideradas**:
  - Mantener un único base URL con rewrites implícitos: se descarta por ambigüedad y dependencia de infraestructura no existente.
  - Conservar mocks para productos como "modo demo": se descarta para este objetivo porque impide verificar requests reales.

### 3) Alineación contractual FE/BE para create-order y confirm-cart
- **Decisión**: Ajustar frontend a endpoints y payloads reales existentes en backend (`/api/orders/from-cart/{cartId}`, `/api/carts/...`, `/api/products`, `/api/users/...`) o, si se detecta brecha funcional, implementar adaptación mínima en backend documentada en specs.
- **Rationale**: El fallo actual se origina en rutas/payloads inconsistentes más que en ausencia total de API.
- **Alternativas consideradas**:
  - Cambiar backend para replicar contrato `/api/v1` del frontend actual: se descarta por mayor impacto y desviación de capacidades ya modeladas.

### 4) Observabilidad obligatoria de requests en desarrollo
- **Decisión**: Añadir instrumentación de cliente HTTP en frontend (log de método, URL, estado, tiempo) y asegurar visibilidad de requests en logs de servicios backend.
- **Rationale**: El criterio de éxito del cambio es ver requests reales en consola al usar la app.
- **Alternativas consideradas**:
  - Verificación manual vía DevTools sin logs estructurados: se descarta por baja trazabilidad y difícil reproducibilidad entre miembros.

### 5) Limpieza de repositorios y README operativo
- **Decisión**: Consolidar documentación mínima de ejecución real y remover archivos/segmentos contradictorios u obsoletos en ambos repos.
- **Rationale**: La documentación actual es extensa pero no guía de forma confiable la integración FE+BE.
- **Alternativas consideradas**:
  - Mantener documentación histórica intacta: se descarta porque perpetúa confusión sobre el flujo de arranque correcto.

## Risks / Trade-offs

- [Riesgo] Dependencia de paths locales entre repositorios (`../sofkify-fe`) en compose de integración. -> **Mitigación**: Documentar estructura esperada y variables para override de ruta.
- [Riesgo] Cambios de endpoints frontend pueden romper pruebas existentes basadas en mocks. -> **Mitigación**: Actualizar tests a mocks de red explícitos (fetch mock/MSW) y separar unit vs integration.
- [Riesgo] CORS inconsistente entre microservicios impide requests desde navegador. -> **Mitigación**: Corregir package/config CORS en todos los servicios y validar preflight.
- [Riesgo] Limpieza de archivos puede eliminar contexto útil. -> **Mitigación**: Limitar limpieza a contenido obsoleto/duplicado y preservar historial en git.
- [Riesgo] Diferencias entre branch `develop` de FE y `main` de BE introducen incompatibilidades adicionales. -> **Mitigación**: Congelar versión objetivo por commit/branch al iniciar implementación y registrar matriz de compatibilidad.

## Migration Plan

1. Crear compose de integración multi-repo y validar arranque de red + servicios.
2. Corregir configuración CORS de microservicios backend y validar endpoints desde host/frontend.
3. Refactorizar clientes HTTP de frontend para usar contratos reales y retirar mocks en productos.
4. Añadir logging de requests/responses en frontend (solo modo desarrollo) y verificar logs backend.
5. Ejecutar pruebas manuales E2E mínimas (login, listar productos, agregar/confirmar carrito, crear orden).
6. Limpiar documentación y artefactos obsoletos; publicar README final en ambos repositorios.

**Rollback**:
- Mantener archivos de integración como aditivos y reversibles por commit.
- Si el refactor HTTP falla, restaurar cliente previo temporalmente y dejar bandera de compatibilidad documentada.

## Open Questions

- ¿Se adopta un API gateway liviano en esta iteración o se mantiene estrategia por servicio con múltiples base URLs?
- ¿Qué branch exacta de `sofkify-fe` se usará como base de implementación (mencionada `develop`), y en qué commit se congela?
- ¿Qué criterio de "repositorio limpio" se exigirá (solo docs/config o también formato/lint/test green en ambos repos)?

