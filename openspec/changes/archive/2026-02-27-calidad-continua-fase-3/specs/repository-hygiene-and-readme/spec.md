## MODIFIED Requirements

### Requirement: Both repositories provide operational README guidance
Each repository SHALL include una guía operativa concisa con prerequisitos, pasos de inicio, variables de entorno, verificación de integración FE/BE **y referencias al plan de calidad y a la matriz de pruebas**.

#### Scenario: New contributor can run integration from README only
- **WHEN** a contributor follows README instructions from clean clone
- **THEN** they SHALL be able to start services and verify real frontend-to-backend requests without tribal knowledge
- **AND** they SHALL find links to the quality plan and the Gherkin test matrix for trazabilidad