## Why

El proyecto carece de una estrategia de calidad unificada: el plan de pruebas es teórico, no cubre integración REST ni riesgos, no existe matriz ejecutable de escenarios Gherkin y no hay CI que garantice cobertura mínima. Esto bloquea la validación continua y aumenta el riesgo de regresiones en los microservicios y el frontend.

## What Changes

- Actualizar el plan de pruebas para incluir alcance, niveles, herramientas, calendario y gestión de riesgos, con foco en pruebas de integración de los endpoints REST.
- Crear una matriz de pruebas basada en escenarios Gherkin (hoja de cálculo nueva) para registrar y trazar la ejecución manual (Pasó/Falló).
- Incorporar un workflow de CI en GitHub Actions que ejecute build + pruebas en push/PR a main/develop/feature/**, con umbral de cobertura ≥70% y fail temprano.

## Capabilities

### New Capabilities
- `quality-plan-integration-tests`: Plan de pruebas con sección de riesgos y cobertura de pruebas de integración REST para los microservicios.
- `gherkin-test-matrix`: Matriz de pruebas en hoja de cálculo con escenarios Gherkin y espacio para registrar resultados manuales.
- `ci-pipeline-coverage`: Workflow CI en GitHub Actions que ejecuta build + pruebas con cobertura mínima del 70% en todas las ramas relevantes.

### Modified Capabilities
- `repository-hygiene-and-readme`: Se amplía el alineamiento de repositorio incorporando referencias al plan de calidad y a la matriz de pruebas.

## Impact

- Documentación: actualización de TEST_PLAN.md y referencia a la nueva hoja de cálculo de matriz de pruebas.
- Infra de CI: creación de .github/workflows/ci.yml para backend (y opcionalmente frontend) con cobertura mínima.
- Operación y QA: adopción de Gherkin para integración REST en microservicios user/product/cart/order y su trazabilidad con el frontend Vite.