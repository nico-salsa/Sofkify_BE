## ADDED Requirements

### Requirement: Plan de pruebas con riesgos y cobertura de integración REST
El plan de pruebas SHALL incluir alcance, niveles (unit/integration/E2E manual), herramientas, calendario y una sección de gestión de riesgos, cubriendo integración REST para user/product/cart/order.

#### Scenario: Plan publicado y trazable a endpoints
- **WHEN** se revisa TEST_PLAN.md
- **THEN** SHALL documentar los endpoints REST cubiertos por integración y su trazabilidad a servicios
- **AND** SHALL enumerar riesgos con severidad, probabilidad y mitigación
- **AND** SHALL indicar calendario y responsables de ejecución

### Requirement: Riesgos y criterios de salida claros
El plan SHALL definir criterios de salida y las condiciones de aceptación de calidad, ligados a los riesgos identificados.

#### Scenario: Revisión de criterios de salida
- **WHEN** QA valida el plan
- **THEN** SHALL encontrar criterios de salida alineados a los riesgos principales
- **AND** SHALL definir umbrales mínimos (p.ej., cobertura ≥70% en CI)