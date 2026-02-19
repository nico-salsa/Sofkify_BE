# Resumen Arquitectónico

Este repositorio implementa el backend de Sofkify como un conjunto de microservicios independientes, cada uno responsable de un dominio específico del e-commerce:

- **user-service** (puerto 8080): gestión de usuarios y autenticación.
- **product-service** (puerto 8081): catálogo y stock de productos.
- **order-service** (puerto 8082): gestión de órdenes y su ciclo de vida.
- **cart-service** (puerto 8083): manejo de carritos de compra.

La arquitectura sigue los principios Hexagonales (Ports & Adapters) y patrones tácticos de DDD:
- **Dominio**: lógica de negocio pura, sin dependencias de frameworks ni detalles de infraestructura.
- **Aplicación**: orquesta casos de uso y define puertos de entrada/salida.
- **Infraestructura**: implementa adaptadores para REST, persistencia, mensajería, etc.

Cada microservicio es autónomo, con su propia base de datos y sin acoplamiento directo entre ellos. La comunicación asíncrona se realiza mediante RabbitMQ para eventos críticos (por ejemplo, actualización de stock tras creación de orden).

---

# Guía de Estilo y Reglas de Negocio

## Reglas de Arquitectura
- Mantén la dirección de dependencias: `infraestructura -> aplicación -> dominio`.
- El dominio debe ser agnóstico a frameworks (sin anotaciones de Spring, JPA, ni detalles de HTTP o mensajería).
- Los controladores REST solo validan, mapean, llaman al caso de uso y retornan la respuesta.
- Usa DTOs en la capa de infraestructura; nunca expongas entidades de persistencia en los controladores.
- Los mapeos entre dominio, entidad y DTO deben ser explícitos.
- Cada microservicio gestiona su propia base de datos.

## Reglas de Negocio
- Solo usuarios registrados pueden operar.
- El precio de un producto debe ser mayor a 0.
- El stock de productos no puede ser negativo.
- Solo productos activos y con stock pueden agregarse al carrito.
- Un usuario solo puede tener un carrito activo.
- Las órdenes se crean a partir del carrito y disparan la actualización asíncrona de stock.

## Convenciones de API e Integración
- Endpoints REST bajo `/api/...`.
- Contratos de mensajería: Exchange `order.exchange`, Routing Key `order.created`, Queue `product.stock.decrement.queue`.
- No acoples servicios mediante acceso directo a bases de datos.

## Guía de Implementación
- Prefiere casos de uso pequeños y explícitos.
- Añade o actualiza tests al cambiar comportamiento.
- No introduzcas lógica de pagos, envíos o facturación salvo que se solicite explícitamente.

---
