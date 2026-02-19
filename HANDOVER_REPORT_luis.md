# HANDOVER_REPORT_luis.md

## Descripción General del Sistema

Sofkify es una plataforma de e-commerce diseñada bajo una arquitectura de microservicios desacoplados, orientada a la escalabilidad, mantenibilidad y autonomía de cada dominio de negocio. El sistema está compuesto por dos grandes repositorios: uno para el backend (Sofkify_BE) y otro para el frontend (sofkify-fe), cada uno con responsabilidades y tecnologías bien definidas.

---

## ¿Qué hace el sistema?

Sofkify permite a los usuarios:
- Registrarse y autenticarse.
- Navegar y consultar productos activos con stock.
- Agregar productos a un carrito de compras (un carrito activo por usuario).
- Generar órdenes a partir del carrito.
- Gestionar el stock de productos de forma asíncrona tras la creación de órdenes.

El sistema asegura reglas de negocio clave como la validación de stock, precios positivos, y la integridad de los flujos de usuario, producto, carrito y orden.

---

## Arquitectura y Construcción

### Backend (Sofkify_BE)
- **Microservicios independientes:**
  - `user-service`: gestión de usuarios y autenticación (Java 21).
  - `product-service`: catálogo y stock de productos (Java 17).
  - `order-service`: gestión de órdenes (Java 17).
  - `cart-service`: manejo de carritos (Java 17).
- **Patrones:** Hexagonal (Ports & Adapters) + DDD táctico.
- **Capas:**
  - **Dominio:** lógica de negocio pura, sin dependencias de frameworks.
  - **Aplicación:** orquestación de casos de uso y puertos.
  - **Infraestructura:** adaptadores REST, persistencia, mensajería (RabbitMQ), configuración.
- **Bases de datos:** cada microservicio gestiona su propia base de datos.
- **Mensajería:** RabbitMQ para eventos de negocio (ej. actualización de stock tras orden).
- **Integración:** REST APIs bajo `/api/...`, DTOs para entrada/salida, sin exposición de entidades de persistencia.

### Frontend (sofkify-fe)
- **Stack:** React + TypeScript + Vite + Tailwind.
- **Estructura modular:**
  - `pages`: vistas principales.
  - `components`: UI reutilizable.
  - `hooks`: lógica de React reutilizable.
  - `services`: integración con APIs y lógica de negocio.
  - `types`: definición estricta de tipos y DTOs.
- **Integración:** consumo de microservicios vía servicios, respetando contratos y reglas de negocio del backend.
- **Validaciones:** en UI para reglas de dominio (precio, stock, cantidades positivas).
- **Configuración:** la URL base de la API se define por variable de entorno.

---

## Consideraciones Finales

- El sistema está preparado para escalar funcionalmente y de manera independiente por dominio.
- La separación estricta de responsabilidades y la adopción de DDD facilitan la mantenibilidad y evolución.
- La integración asíncrona mediante eventos permite desacoplar procesos críticos como la actualización de stock.
- El frontend está alineado con las reglas de negocio del backend, asegurando coherencia de validaciones y flujos.

Este handover proporciona una visión clara para cualquier desarrollador o arquitecto que deba continuar, mantener o evolucionar la plataforma Sofkify.