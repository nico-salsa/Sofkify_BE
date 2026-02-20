## Why

El repositorio backend no tenia una especificacion formal para guiar a Copilot con reglas de arquitectura hexagonal y reglas de negocio del dominio. Esto causa respuestas inconsistentes en prompts `@workspace` y mayor riesgo de cambios fuera de convencion.

## What Changes

- Definir y versionar un resumen arquitectonico orientado al backend para consultas `@workspace /explain`.
- Formalizar instrucciones de repositorio para Copilot en `.github/copilot-instructions.md` con reglas de capas, puertos, contratos y dominio.
- Alinear la documentacion de contexto para que nuevas implementaciones partan de restricciones explicitas.

## Capabilities

### New Capabilities
- `copilot-context-setup`: Establece artefactos de contexto y lineamientos de Copilot para trabajo consistente en el backend.

### Modified Capabilities
- Ninguna.

## Impact

- Documentacion versionada en repositorio backend (`.github`, docs de contexto y artefactos OpenSpec).
- Flujo de desarrollo asistido por IA mas consistente para tareas de implementacion y refactor.
- Sin cambios en APIs runtime, contratos HTTP o eventos RabbitMQ.
