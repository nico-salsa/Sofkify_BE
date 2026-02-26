# Sofkify — Debate Arquitectónico (3 Decisiones Estructurales)

**Versión:** 1.0  
**Fecha:** Febrero 2026  
**Autor:** Arquitectura / Plataforma  
**Audiencia:** Equipo de ingeniería (énfasis en perfiles junior)  
**Propósito:** Documentar, con criterio arquitectónico, tres decisiones estructurales necesarias para mejorar operabilidad, escalabilidad del equipo y estabilidad de contrato.

---

## 0. Resumen ejecutivo

Sofkify presenta tres problemas sistémicos que hoy limitan la velocidad de entrega, elevan el riesgo operativo y diluyen la “fuente de verdad” del sistema:

1) **Servicios acoplados por repositorio** (microservicios en un solo repo)  
→ Fricción de Git, builds innecesarios, dependencia accidental entre dominios, dificultad de despliegue independiente.  
**Decisión:** pasar a **multirepo** (1 repositorio por servicio).

2) **Ausencia de repositorio de plataforma** (no existe un “centro operativo” del sistema)  
→ Documentación dispersa, orquestación fuera de lugar, pruebas E2E sin owner claro, onboarding lento.  
**Decisión:** crear **sofkify-platform** como *single source of truth* (docs, orquestación, E2E, scripts).

3) **Contrato de API inconsistente** (/api vs /api/v1)  
→ Roturas por falta de convención única y falta de ruta de evolución.  
**Decisión:** formalizar **/api/v1/** como contrato base para todos los servicios.

Estas tres decisiones son coherentes entre sí: separan responsabilidad (bounded contexts), centralizan coordinación del sistema y estabilizan contratos.

---

## 1. Contexto y estado actual

Sofkify opera con cuatro servicios (user, product, cart, order) que se comunican por REST y mensajería, con bases de datos separadas. Sin embargo:

- Los servicios viven dentro de un único repositorio.
- Artefactos transversales (docker-compose, documentación, guías) viven mezclados con código de servicios.
- No existe un repositorio explícito para coordinación de sistema.
- El contrato de API no está estandarizado.

**Efecto neto:** se incrementa la fricción diaria y se reducen garantías de independencia real.

---

## 2. Decisión 1 — De “monolito de microservicios” a Multirepo

### 2.1 Problema
Mantener múltiples servicios en un solo repositorio introduce acoplamientos operativos y técnicos:

- **Colisiones en Git:** el repositorio se convierte en un punto de contención.
- **Builds/pipelines no aislados:** se ejecuta más de lo necesario, afectando tiempos de feedback.
- **Acoplamiento accidental:** se facilita el “import” de código entre servicios (violación de límites).
- **Despliegue condicionado:** el release de un servicio queda bloqueado por estado de otro.

### 2.2 Alternativas consideradas

**A) Mantener monorepo**  
Adecuado cuando existe disciplina estricta y tooling de monorepo (build graph, ownership, boundaries enforcement, pipelines selectivos). Sin ello, tiende a degradarse.

**B) Migrar a multirepo**  
Alineado con independencia por servicio y con un modelo operativo donde cada equipo/servicio puede versionar, construir y desplegar de forma autónoma.

### 2.3 Decisión
**Adoptar Multirepo**: un repositorio por servicio.

Repositorios propuestos:
- `sofkify-user-service`
- `sofkify-product-service`
- `sofkify-cart-service`
- `sofkify-order-service`

### 2.4 Justificación
- Los servicios **ya están diseñados como unidades autónomas** (DB separada, integración por APIs/eventos). El repositorio único contradice esa intención.
- La fricción actual (conflictos + coordinación) **crece con el equipo** y afecta throughput.
- Multirepo hace explícitos los límites y reduce la probabilidad de dependencia accidental.

### 2.5 Principios arquitectónicos reforzados
- **Bounded Contexts:** cada servicio encapsula su dominio.
- **Dependencias explícitas:** integración solo vía contratos (HTTP/eventos), no vía imports.
- **Autonomía operativa:** build, test, release por servicio.

### 2.6 Criterios de aceptación
- Cada servicio compila y ejecuta tests en aislamiento.
- No existen dependencias de código compartido no versionado entre servicios.
- El despliegue de un servicio no requiere sincronización de releases con otros (excepto cambios de contrato).

### 2.7 Plan de migración (alto nivel)
1. Separación de repos preservando historial (subtree/split).
2. Ajuste de pipelines por servicio.
3. Extracción de artefactos transversales a `sofkify-platform` (ver Decisión 2).
4. Definición de ownership y convenciones (versionado, branching, releases).

---

## 3. Decisión 2 — Crear repositorio de plataforma (sofkify-platform)

### 3.1 Problema
Hoy los artefactos transversales están dispersos (o ubicados donde no corresponde). Esto genera:

- **Ausencia de fuente de verdad:** documentación duplicada, desactualizada o contradictoria.
- **Onboarding ineficiente:** no existe un punto único de entrada para entender y ejecutar el sistema.
- **Orquestación sin owner:** docker-compose y scripts “viven” en repos que no representan al sistema completo.
- **Pruebas E2E sin hogar natural:** se fragmenta la validación del flujo de negocio end-to-end.

### 3.2 Alternativas consideradas

**A) Mantener docs/orquestación en un repo existente (FE o un servicio)**  
Mezcla responsabilidades: frontend no debe ser “centro operativo” del sistema; un servicio tampoco representa al sistema completo.

**B) Repositorio dedicado de plataforma**  
Concentra coordinación, documentación oficial y pruebas de sistema. Es la estructura habitual cuando el sistema supera un único servicio y se requiere gobierno.

### 3.3 Decisión
**Crear `sofkify-platform`** como repositorio central para:
- Documentación oficial (*single source of truth*).
- Orquestación local (docker-compose) y scripts operativos.
- Pruebas E2E / smoke tests del sistema completo.
- Convenciones de gobierno (owners, estándares, runbooks).

### 3.4 Estructura propuesta

sofkify-platform/
├─ docs/
│ ├─ ARCHITECTURE.md
│ ├─ ONBOARDING.md
│ ├─ API_CONTRACTS.md
│ ├─ DEPLOYMENT.md
│ └─ TROUBLESHOOTING.md
├─ docker-compose.yml
├─ scripts/
│ ├─ setup.sh
│ ├─ logs.sh
│ └─ deploy.sh
└─ tests/
└─ e2e/


### 3.5 Criterios de aceptación
- Un desarrollador nuevo puede levantar el sistema siguiendo `docs/ONBOARDING.md` sin intervención manual de terceros.
- `docker-compose.yml` oficial vive en `sofkify-platform`.
- Existe suite E2E mínima para flujos críticos (auth, catálogo, carrito, orden).
- Las decisiones y convenciones se documentan y se mantienen versionadas.

---

## 4. Decisión 3 — Formalizar contrato de API con `/api/v1`

### 4.1 Problema
La API presenta inconsistencias de rutas (p. ej. `/api/...` vs `/api/v1/...`), lo cual provoca:

- Errores de integración y pérdida de tiempo de diagnóstico.
- Ausencia de un contrato explícito: los cambios pueden romper clientes sin ruta de deprecación.
- Dificultad para incorporar consumidores futuros (apps móviles, integraciones externas).

### 4.2 Alternativas consideradas

**A) Sin versionado**  
Reducido overhead inicial, pero frágil ante cambios inevitables. Funciona únicamente con control estricto de compatibilidad y consumidores acoplados al release cadence.

**B) Versionado por URL (`/api/v1`, `/api/v2`)**  
Contrato explícito, ruta de evolución clara y mecanismo formal para gestionar breaking changes.

### 4.3 Decisión
**Estandarizar `/api/v1/`** en todos los servicios como contrato base.

### 4.4 Reglas de contrato (operativas)
- Cambios compatibles se mantienen dentro de `v1`.
- Cambios incompatibles se publican en `v2` manteniendo `v1` durante una ventana de transición definida.
- El contrato debe estar documentado (idealmente OpenAPI) y validado en CI.

### 4.5 Plan de implementación (alto nivel)
1. Backend: mover controladores a `/api/v1/...` en user/product/cart/order.
2. Frontend: unificar todos los llamados a `/api/v1/...`.
3. (Recomendado) Gateway: enrutar `/api/v1/*` y manejar deprecaciones de rutas legacy.

### 4.6 Criterios de aceptación
- No existen llamadas del frontend a rutas sin versión.
- Todos los servicios exponen rutas consistentes bajo `/api/v1`.
- El contrato está documentado y su estabilidad está protegida por tests (contract/E2E).

---

## 5. Roadmap recomendado (MVP de arquitectura)

- **Semana 1–2:** Multirepo (migración + builds independientes + CI básica por servicio).
- **Semana 2–3:** Platform repo (docs + compose + scripts + punto de entrada).
- **Semana 3–4:** API v1 (estandarización + pruebas + gateway opcional).
- **Continuo:** madurar E2E, contract testing y observabilidad.

> Nota: los tiempos dependen de cobertura de pruebas y complejidad de integración; la prioridad es asegurar criterios de aceptación y control de riesgo por fases.

---

## 6. Riesgos y mitigaciones

- **Riesgo:** ruptura temporal del entorno local/CI durante la separación de repos.  
  **Mitigación:** migración incremental por servicio; `sofkify-platform` se convierte en el runbook oficial.

- **Riesgo:** se evidencian dependencias implícitas entre servicios al separarlos.  
  **Mitigación:** formalizar contratos (OpenAPI/event schemas) y eliminar imports directos.

- **Riesgo:** duplicación de utilidades compartidas.  
  **Mitigación:** definir estrategia explícita: duplicación mínima vs librería compartida versionada y gobernada (evitar “shared” informal).

---

## 7. Definiciones (mínimas)

- **Monorepo:** múltiples proyectos/servicios en un repositorio.
- **Multirepo:** un repositorio por proyecto/servicio.
- **Platform repo:** repositorio transversal para coordinación del sistema (docs, orquestación, E2E, runbooks).
- **Contrato:** promesa de una interfaz (endpoints, requests/responses, eventos).
- **Breaking change:** cambio que rompe consumidores existentes.
- **Versionado de API:** mecanismo para evolucionar contratos sin romper integraciones.

---

## 8. Conclusión

Las tres decisiones responden a un objetivo único: **hacer que Sofkify sea operable y escalable** a nivel de equipo y de sistema.

- Multirepo reduce fricción y refuerza límites reales.
- Platform repo establece una fuente de verdad y habilita operación consistente.
- `/api/v1` estabiliza el contrato e introduce un mecanismo formal de evolución.

La implementación debe ejecutarse por fases, protegida por criterios de aceptación y validación E2E mínima.
