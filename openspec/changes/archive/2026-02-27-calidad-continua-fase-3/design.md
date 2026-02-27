## Context

- Backend con microservicios (user, product, cart, order) y frontend Vite; existe docker-compose y endpoints documentados.
- Plan de pruebas actual es teórico y sin riesgos ni integración REST; no hay matriz ejecutable de escenarios.
- No existe CI en GitHub Actions; cobertura mínima no controlada.
- Repositorio incluye referencias a APIs (API_ENDPOINTS_REFERENCE.json) y cURL, útiles para escenarios Gherkin.

## Goals / Non-Goals

**Goals:**
- Consolidar un plan de pruebas con riesgos, alcance, niveles, herramientas y calendario, incluyendo integración REST.
- Crear y referenciar una hoja de cálculo como matriz Gherkin para registrar ejecución manual (Pasó/Falló).
- Implementar CI en GitHub Actions que ejecute build + pruebas en push/PR a main/develop/feature/** con cobertura ≥70% y fail temprano.

**Non-Goals:**
- No se crean nuevos endpoints ni lógica de negocio.
- No se automatiza despliegue a producción.

## Decisions

- Plan de pruebas: actualizar TEST_PLAN.md centralizando alcance, niveles (unit/integration/E2E manual), herramientas (JUnit/Vitest, Postman/cURL), calendario y riesgos; referenciar APIs existentes para cobertura de integración.
- Matriz Gherkin: crear hoja de cálculo (almacenada y enlazada desde el repo) con escenarios Given/When/Then, columnas de trazabilidad (HU/capacidad), datos, y resultado Pasó/Falló; reutilizar escenarios por servicio y flujos E2E mínimos.
- CI workflow: un pipeline en .github/workflows/ci.yml que, en push/PR a main/develop/feature/**, instale dependencias y ejecute pruebas con cobertura para backend (gradle test + jacoco) y frontend (npm test/vitest con cobertura si se incluye); falla si cobertura <70% o tests rojos.
- Cobertura mínima: usar reportes de cobertura (JaCoCo / Vitest) y, si es necesario, umbral configurable vía flags (p.ej. Gradle jacocoTestCoverageVerification o vitest --coverage --threshold).

## Risks / Trade-offs

- [Flaky tests] → Aislar tests de integración con datos conocidos; resets de BD en CI usando contenedores o perfiles en memoria.
- [Cobertura insuficiente] → Fijar umbral 70% y reportar brechas; priorizar rutas críticas (auth, cart, order).
- [Duración del pipeline] → Cache de dependencias (Gradle, npm) y ejecución paralela; limitar suites pesadas.
- [Secretos en CI] → No usar secretos sensibles; si se requiere, usar variables de entorno mínimas y seguras.
- [Matriz desactualizada] → Definir responsable y fecha de revisión en el plan; enlazar la hoja y versionar el enlace en README/TEST_PLAN.

## Migration Plan

1) Actualizar TEST_PLAN.md con riesgos y cobertura de integración; agregar enlace a la nueva hoja de cálculo.
2) Crear la hoja de cálculo de matriz Gherkin y documentar su ubicación/enlace.
3) Añadir .github/workflows/ci.yml; validar en rama feature antes de merge.
4) Ajustar umbrales/paths si fallan jobs; acordar responsables de mantenimiento.

## Open Questions

- ¿Se incluirá ejecución de frontend tests en el mismo pipeline o en job separado opcional?
- ¿Se usará base de datos real en CI (servicios con docker) o perfiles in-memory para integración mínima?