## ADDED Requirements

### Requirement: Matriz Gherkin en hoja de calculo con trazabilidad
La matriz de pruebas SHALL existir en una hoja de calculo enlazada desde el repo, con escenarios en formato Gherkin y columnas minimas: ID, servicio/flujo, endpoint o ruta, GIVEN/WHEN/THEN, datos, esperado, estado (Paso/Fallo), fecha y ejecutor.

#### Scenario: Matriz accesible y completa
- **WHEN** un miembro del equipo abre el enlace de la matriz desde el repositorio
- **THEN** SHALL ver la hoja con las columnas definidas y escenarios poblados
- **AND** SHALL registrar resultados manuales (Paso/Fallo) por ejecucion

### Requirement: Cobertura de flujos criticos FE/BE
La matriz SHALL incluir escenarios que cubran al menos: login, listado de productos, agregar al carrito, confirmar carrito, crear orden, y ver ordenes del usuario.

#### Scenario: Validacion de cobertura minima
- **WHEN** QA revisa la matriz
- **THEN** SHALL encontrar escenarios para cada flujo critico FE/BE indicado
- **AND** SHALL mapear cada escenario a su endpoint o interaccion frontend correspondiente
