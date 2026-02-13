# Contexto del Sistema – Ecommerce Asíncrono

## 1. Propósito del Documento

Este documento define el **contexto funcional y de negocio** del sistema Ecommerce Asíncrono.
Es la **fuente de verdad del dominio** y debe ser utilizado por desarrolladores humanos y por sistemas de IA para comprender:

* Qué hace el sistema
* Qué conceptos de negocio existen
* Qué reglas gobiernan el dominio
* Qué capacidades funcionales ofrece el sistema completo

Este archivo **NO define decisiones técnicas ni de implementación**. Dichas decisiones se documentan en `architecture.md`.

---

## 2. Propósito del Sistema

El sistema Ecommerce Asíncrono permite gestionar el proceso de compra de productos,
actuando como núcleo de negocio para:

* La gestión de Users (clientes y administradores)
* La consulta y administración de productos
* La creación de carritos de compra
* La confirmación de órdenes

La confirmación de una orden representa un **hecho de negocio** y desencadena
procesamiento asíncrono mediante eventos de dominio.

---

## 3. Alcance Funcional del Sistema

El sistema Ecommerce Asíncrono provee las siguientes capacidades funcionales:

* Registro y autenticación de Users.
* Consulta de productos disponibles para compra.
* Gestión de stock de productos y validación de disponibilidad.
* Creación y modificación de un carrito de compra por User.
* Confirmación de un carrito para generar una orden.
* Cancelación de órdenes creadas.
* Emisión de eventos de dominio relacionados con Products y Orders.
* Procesamiento asíncrono de decrementos de stock.

> **Nota sobre implementación:**  
> La implementación de estos bounded contexts es **incremental**.  
> El orden y alcance específico de implementación se define mediante historias de usuario.  
> Este documento describe el **dominio completo del sistema**, no el estado actual de desarrollo.

---

## 4. Actores del Negocio

### 4.1 User

Concepto de negocio que representa a una persona registrada en el sistema.

Características:

* Debe registrarse explícitamente en el sistema.
* Puede autenticarse para operar.
* Posee un **Role** que determina sus capacidades.

#### Roles:

##### Role: Cliente

* **Es el rol por defecto** al registrarse un User.
* Representa al usuario que realiza compras.

Responsabilidades:

* Consultar productos disponibles.
* Crear y modificar un carrito.
* Confirmar un carrito para generar una orden.
* Consultar órdenes creadas.

##### Role: Admin

* Representa al usuario con permisos administrativos.
* Un User puede ascender de Cliente a Admin.

Responsabilidades:

* Crear, actualizar y desactivar productos.
* Crear y administrar categorías.
* Gestionar stock de productos.

Restricciones:

* Un User eliminado lógicamente no puede crear carritos ni órdenes.

---

## 5. Lenguaje Ubicuo (Conceptos del Dominio)

Esta sección define **los conceptos oficiales del dominio** y su significado.

Los conceptos definidos en esta sección:

* Representan **lenguaje de negocio**
* No son modelos técnicos ni estructuras de persistencia
* Definen responsabilidades, reglas e invariantes

---

### User

Entidad de negocio que representa a una persona registrada en el sistema.

Responsabilidad principal:

* Ser el titular de un carrito y de una orden (cuando tiene Role: Cliente).
* Gestionar productos (cuando tiene Role: Admin).

Reglas de dominio:

* El User debe estar registrado para operar.
* El User puede autenticarse.
* El User se elimina únicamente de forma lógica (soft delete).
* El identificador documental del User es único dentro del sistema.
* Por defecto, todo User tiene Role: Cliente.
* Un User puede ascender a Role: Admin.

---

### Category

Entidad de negocio utilizada para clasificar productos.

Responsabilidad principal:

* Organizar productos para su administración y consulta.

Reglas de dominio:

* Una categoría puede existir sin productos asociados.
* Un producto puede existir sin categorías asociadas.
* La categoría no participa en el flujo de compra.

---

### Product

Entidad de negocio que representa un artículo vendible.

Responsabilidad principal:

* Ser ofrecido a los clientes y seleccionado para una compra.

Reglas de dominio:

* Todo Product tiene stock asociado.
* Solo productos activos y con stock disponible pueden agregarse a un Cart.
* El precio del Product debe ser mayor que cero.
* Un Product eliminado lógicamente no es visible ni seleccionable.

---

### Cart

Agregado de negocio que representa la **intención de compra** de un User.

Responsabilidad principal:

* Permitir construir una compra antes de su confirmación.

Reglas de dominio:

* El Cart se crea cuando el User agrega el primer producto.
* Un User solo puede tener un Cart activo.
* Un Cart activo puede modificarse.
* Al confirmarse, el Cart deja de existir como agregado y da origen a una Order.
* El sistema no gestiona carritos abandonados.

---

### CartItem

Elemento que compone un Cart.

Responsabilidad principal:

* Representar un producto seleccionado con una cantidad específica.

Reglas de dominio:

* La cantidad debe ser mayor que cero.
* La cantidad no puede exceder el stock disponible al momento de la selección.

---

### Order

Agregado de negocio que representa una **compra confirmada**.

Responsabilidad principal:

* Registrar de forma definitiva una compra realizada por el User.

Reglas de dominio:

* Una Order se crea únicamente a partir de un Cart confirmado.
* Un User solo puede tener una Order activa.
* La Order es inmutable una vez creada.
* La Order se crea con estado inicial CREATED.
* La Order puede ser cancelada.

---

### OrderItem

Elemento que compone una Order.

Responsabilidad principal:

* Registrar el detalle de cada producto comprado.

Reglas de dominio:

* El OrderItem contiene un snapshot del producto al momento de la compra.
* Cambios posteriores en el Product no afectan a la Order.

---

## 6. Reglas Generales del Negocio

Las siguientes reglas son **invariantes del dominio** y deben cumplirse en cualquier implementación.

### User

* Un User debe estar registrado para operar.
* Un User eliminado lógicamente no puede crear Cart ni Order.
* El identificador documental del User es único.
* Por defecto, todo User tiene Role: Cliente.
* Solo Users con Role: Admin pueden gestionar productos y categorías.

### Product

* El precio debe ser mayor que cero.
* El stock no puede ser negativo.
* Solo productos activos y con stock pueden agregarse a un Cart.

### Cart

* Un User solo puede tener un Cart activo.
* Un Cart activo puede modificarse.
* Un Cart confirmado no puede modificarse.

### Order

* Una Order se crea únicamente desde un Cart confirmado.
* Una Order es inmutable.
* Un User solo puede tener una Order activa.
* Una Order puede cancelarse.

---

## 7. Flujo de Negocio Principal

1. El User se encuentra registrado y autenticado con Role: Cliente.
2. El User consulta los productos disponibles.
3. El User agrega productos al Cart.
4. El sistema valida stock al agregar productos.
5. El User confirma el Cart.
6. El sistema crea una Order.
7. El sistema emite un evento de negocio `OrderCreated`.
8. El Product Context consume el evento y decrementa el stock.
9. El Product Context emite `StockDecremented` (éxito) o `OrderFailed` (fallo).
10. El Order Context reacciona actualizando el estado de la Order.

---

## 8. Eventos de Dominio (Nivel Conceptual)

El dominio Ecommerce emite eventos que representan hechos de negocio ocurridos.

### Eventos relacionados con Order:

* `OrderCreated`

### Eventos relacionados con Product:

* `StockDecremented`
* `OrderFailed`

Estos eventos:

* Se generan **dentro del dominio**
* No conocen quién los consume
* No contienen lógica técnica ni dependencias externas

> **Nota importante:**  
> Los eventos listados en esta sección representan **hechos de negocio a nivel conceptual**.  
> No implican nombres de clases ni contratos técnicos.
>
> La materialización técnica de estos eventos (Domain Events vs Integration Events)  
> se define en `architecture.md` y `events.md`.

---

## 9. Consistencia del Sistema

El sistema opera bajo **consistencia eventual**.

* Las operaciones del dominio son consistentes de forma inmediata.
* Los efectos secundarios se procesan de forma asíncrona mediante eventos.

Ejemplos:

* La creación de una Order es inmediata y consistente.
* El decremento de stock en Product es asíncrono y eventual.
* La actualización del estado de Order tras el decremento de stock es asíncrona.

---

## 10. Fuera de Alcance

Las siguientes capacidades están explícitamente fuera del alcance del sistema:

* Pagos
* Envíos
* Facturación
* Gestión avanzada de roles y permisos granulares
* Notificaciones avanzadas multicanal (SMS, push)
* Reglas avanzadas de entrega o reintento de eventos
* Gestión de carritos abandonados

---

## 11. Relación con Otros Documentos

Este documento se complementa con:

* `architecture.md`: decisiones técnicas y estructurales.
* `events.md`: contratos de eventos y comunicación asíncrona.
* `HU-*.md`: historias técnicas que definen el alcance de implementación.
* `AI_WORKFLOW.md`: estrategia de uso de IA.

Este archivo debe mantenerse consistente a medida que evoluciona el dominio y define **el qué del dominio**, no el cómo técnico.