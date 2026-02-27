## ADDED Requirements

### Requirement: Plan de pruebas con riesgos y cobertura de integracion REST
El plan de pruebas SHALL incluir alcance, niveles (unit/integration/E2E manual), herramientas, calendario y una seccion de gestion de riesgos, cubriendo integracion REST para user/product/cart/order.

#### Scenario: Plan publicado y trazable a endpoints
- **WHEN** se revisa TEST_PLAN.md
- **THEN** SHALL documentar los endpoints REST cubiertos por integracion y su trazabilidad a servicios
- **AND** SHALL enumerar riesgos con severidad, probabilidad y mitigacion
- **AND** SHALL indicar calendario y responsables de ejecucion

### Requirement: Riesgos y criterios de salida claros
El plan SHALL definir criterios de salida y las condiciones de aceptacion de calidad, ligados a los riesgos identificados.

#### Scenario: Revision de criterios de salida
- **WHEN** QA valida el plan
- **THEN** SHALL encontrar criterios de salida alineados a los riesgos principales
- **AND** SHALL definir umbrales minimos (p.ej., cobertura >=70%)
