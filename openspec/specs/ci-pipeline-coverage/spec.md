## ADDED Requirements

### Requirement: Pipeline CI con disparadores en push/PR
El workflow CI SHALL ejecutarse automaticamente en push y pull request hacia main, develop y feature/**, construyendo el backend (y frontend si se incluye) y ejecutando las pruebas.

#### Scenario: Disparo en push/PR
- **WHEN** se hace push o PR a main/develop/feature/**
- **THEN** el pipeline SHALL correr build + tests sin intervencion manual

### Requirement: Umbral de cobertura minima del 70%
El pipeline SHALL fallar si la cobertura global reportada es menor al 70% en las suites habilitadas.

#### Scenario: Cobertura insuficiente falla la integracion
- **WHEN** la cobertura calculada es 69% o menor
- **THEN** el job de CI SHALL finalizar en estado failed

### Requirement: Reportes de pruebas disponibles
El pipeline SHALL publicar resultados de pruebas (logs o artifacts) para diagnostico rapido.

#### Scenario: Acceso a reportes tras la ejecucion
- **WHEN** un desarrollador abre la ejecucion del workflow
- **THEN** SHALL encontrar los resultados de pruebas y, si aplica, el reporte de cobertura adjunto
