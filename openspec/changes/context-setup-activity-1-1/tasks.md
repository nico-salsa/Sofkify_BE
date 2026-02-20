## 1. Context Artifacts

- [x] 1.1 Consolidar o actualizar el resumen arquitectonico backend para consultas `@workspace /explain`
- [x] 1.2 Crear o ajustar `.github/copilot-instructions.md` con reglas de arquitectura hexagonal y dominio
- [x] 1.3 Verificar que las instrucciones incluyan limites de alcance y contratos existentes (REST/RabbitMQ/DB ownership)

## 2. Validation

- [x] 2.1 Ejecutar `@workspace /explain` en el repo backend y validar cobertura de servicios e integraciones
- [x] 2.2 Revisar consistencia entre `proposal.md`, `design.md` y `specs/copilot-context-setup/spec.md`
- [x] 2.3 Confirmar que el cambio no introduce modificaciones de runtime ni ruptura de contratos

## 3. Delivery

- [x] 3.1 Preparar commit convencional para artefactos de contexto del backend
- [x] 3.2 Publicar cambios en el remoto fork del backend
