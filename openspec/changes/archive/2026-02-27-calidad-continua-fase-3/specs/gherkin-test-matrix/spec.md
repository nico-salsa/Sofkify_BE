## ADDED Requirements

### Requirement: Matriz Gherkin en hoja de cálculo con trazabilidad
La matriz de pruebas SHALL existir en una hoja de cálculo enlazada desde el repo, con escenarios en formato Gherkin y columnas mínimas: ID, servicio/flujo, endpoint o ruta, GIVEN/WHEN/THEN, datos, resultado esperado, estado (Pasó/Falló), fecha y ejecutor.

#### Scenario: Matriz accesible y completa
- **WHEN** un miembro del equipo abre el enlace de la matriz desde el repositorio
- **THEN** SHALL ver la hoja con las columnas definidas y escenarios poblados
- **AND** SHALL registrar resultados manuales (Pasó/Falló) por ejecución

### Requirement: Cobertura de flujos críticos FE/BE
La matriz SHALL incluir escenarios que cubran al menos: login, listado de productos, agregar al carrito, confirmar carrito, crear orden, y ver órdenes del usuario.

#### Scenario: Validación de cobertura mínima
- **WHEN** QA revisa la matriz
- **THEN** SHALL encontrar escenarios para cada flujo crítico FE/BE indicado
- **AND** SHALL mapear cada escenario a su endpoint o interacción frontend correspondiente