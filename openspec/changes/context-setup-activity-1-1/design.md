## Context

El backend de Sofkify usa microservicios con arquitectura hexagonal y reglas de dominio repartidas entre `docs_IA/` y README por servicio. El equipo necesita que Copilot mantenga decisiones de capas, contratos REST/eventos y limites de negocio de forma consistente al trabajar desde prompts de workspace.

## Goals / Non-Goals

**Goals:**
- Consolidar un contexto arquitectonico backend util para consultas `@workspace /explain`.
- Definir reglas de repositorio para Copilot en `.github/copilot-instructions.md`.
- Reducir variacion en codigo generado por IA para capas domain/application/infrastructure.

**Non-Goals:**
- Cambiar endpoints, esquemas de base de datos o contratos RabbitMQ.
- Introducir nuevas funcionalidades de negocio (pagos, envios, facturacion).
- Reestructurar paquetes existentes o migrar framework.

## Decisions

1. **Versionar instrucciones de Copilot dentro del repo (`.github/copilot-instructions.md`).**
   - Rationale: asegura trazabilidad por commit y revisiones de equipo.
   - Alternative considered: reglas locales de IDE solamente; descartado por no ser compartidas por todo el equipo.

2. **Separar contexto por repositorio en lugar de un unico documento monolitico para FE+BE.**
   - Rationale: cada repo tiene stack, riesgos y contratos distintos.
   - Alternative considered: documento unificado en carpeta raiz; descartado por no vivir en el historial de cada repo.

3. **Alinear lenguaje de instrucciones con arquitectura hexagonal vigente.**
   - Rationale: Copilot debe reforzar la direccion de dependencias y evitar logica de negocio en adapters.
   - Alternative considered: instrucciones de estilo generales; descartado por baja utilidad para decisiones de arquitectura.

## Risks / Trade-offs

- [Riesgo] Las instrucciones pueden desactualizarse respecto al codigo real. -> **Mitigation**: mantenerlas como parte del Definition of Done en cambios arquitectonicos.
- [Riesgo] Instrucciones demasiado estrictas pueden frenar cambios validos. -> **Mitigation**: marcar excepciones explicitas en PR cuando una decision rompa una regla previa.
- [Trade-off] Mayor esfuerzo documental inicial. -> **Mitigation**: plantillas breves y enfoque en reglas de alto impacto.

## Migration Plan

1. Agregar documento de resumen arquitectonico backend y archivo de instrucciones de Copilot.
2. Validar que el equipo puede usar `@workspace /explain` con el nuevo contexto.
3. Versionar los cambios y comunicar al equipo los lineamientos vigentes.
4. Revisar en la siguiente iteracion si se requieren ajustes por feedback.

## Open Questions

- Conviene versionar tambien una version corta de prompts recomendados para onboarding rapido?
- Se debe agregar una politica minima de pruebas sugeridas por tipo de cambio en estas instrucciones?
