## Why

El frontend y el backend no están interactuando en local como sistema real: el frontend aparenta flujo tipo mock y no se observan requests HTTP efectivos hacia los servicios. Necesitamos restablecer la integración entre repositorios separados ahora para poder validar el MVP de extremo a extremo y evitar seguir construyendo sobre una infraestructura incierta.

## What Changes

- Redefinir la ejecución local conjunta de frontend y backend usando Docker Compose para comunicación confiable entre ambos repositorios.
- Auditar y corregir la configuración de consumo HTTP del frontend para que todas las acciones de negocio disparen requests reales al backend.
- Estandarizar configuración de entorno (URLs base, puertos, nombres de red/servicio) para que los contenedores y ejecución local no diverjan.
- Incorporar trazabilidad de requests en consola/logs para evidenciar tráfico HTTP durante operaciones clave.
- Limpiar artefactos/documentación obsoleta en ambos repositorios y actualizar README principal de backend y frontend con guía operativa única.

## Capabilities

### New Capabilities
- `frontend-backend-runtime-integration`: Define y valida la integración operativa FE/BE en entorno local con Docker Compose y configuración compartida.
- `http-request-observability`: Garantiza evidencia verificable de requests HTTP del frontend hacia backend en flujos principales.
- `repository-hygiene-and-readme`: Establece limpieza de artefactos no vigentes y documentación mínima de operación para ambos repositorios.

### Modified Capabilities
- `create-order`: Ajusta requisitos de consumo API para asegurar invocación real del backend en el flujo de creación de orden.
- `confirm-cart`: Ajusta requisitos de integración para confirmar carrito mediante request HTTP efectivo y observable.

## Impact

- Repositorios afectados: `C:\Sofka_U_Semana_2\Sofkify_BE` y `C:\Sofka_U_Semana_2\sofkify-fe`.
- Infraestructura: `docker-compose.yml`, redes, variables de entorno y estrategia de arranque local.
- Código FE: capa cliente HTTP, configuración de base URL y flujos UI que hoy no pegan al backend.
- Código BE: CORS/configuración de endpoints y logging de acceso cuando aplique.
- Documentación: `README.md` (backend y frontend) y documentos Docker de referencia para eliminar contradicciones.

