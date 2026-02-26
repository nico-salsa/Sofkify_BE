# 📮 Guía de Pruebas con Postman - Sofkify

## 📥 Importar la Colección

1. Abre Postman
2. Click en **Import** (esquina superior izquierda)
3. Selecciona el archivo `Sofkify_Postman_Collection.json`
4. La colección "Sofkify - Flujo Completo E2E" aparecerá en tu workspace

## 🔧 Configuración Inicial

### Variables de Colección

La colección incluye variables automáticas que se actualizan con cada request:

| Variable | Descripción | Se actualiza en |
|----------|-------------|-----------------|
| `base_url_users` | http://localhost:8080/api/users | Predefinido |
| `base_url_products` | http://localhost:8081/api/products | Predefinido |
| `base_url_orders` | http://localhost:8082/api/orders | Predefinido |
| `base_url_carts` | http://localhost:8083/api/carts | Predefinido |
| `customer_id` | ID del usuario creado | Al crear usuario |
| `product_id_1` | ID del primer producto | Al crear producto 1 |
| `product_id_2` | ID del segundo producto | Al crear producto 2 |
| `cart_id` | ID del carrito activo | Al agregar primer item |
| `cart_item_id` | ID de un item del carrito | Al agregar item |
| `order_id` | ID de la orden creada | Al crear orden |

### Verificar que el Backend esté corriendo

Asegúrate de que todos los microservicios estén activos:

```bash
cd Sofkify_BE
docker-compose up -d
```

Verifica los logs:
```bash
docker-compose logs -f
```

## 🚀 Flujos de Prueba

### Opción 1: Flujo Completo E2E (Recomendado para primera prueba)

La carpeta **"5. FLUJO COMPLETO E2E"** contiene 6 requests secuenciales que simulan el flujo completo:

1. **PASO 1: Crear Usuario** → Crea un usuario de prueba
2. **PASO 2: Crear Productos** → Crea un producto
3. **PASO 3: Agregar al Carrito** → Agrega el producto al carrito
4. **PASO 4: Confirmar Carrito** → Confirma el carrito
5. **PASO 5: Crear Orden** → Crea la orden desde el carrito
6. **PASO 6: Ver Mis Órdenes** → Lista todas las órdenes del usuario

**Cómo ejecutar:**
- Ejecuta cada request en orden (de arriba hacia abajo)
- Cada request guarda automáticamente los IDs necesarios para el siguiente paso
- Los scripts de test muestran mensajes de confirmación en la consola

### Opción 2: Pruebas por Módulo

Puedes probar cada módulo independientemente:

#### 1️⃣ USUARIOS
- `1.1 Crear Usuario` → Crea un nuevo usuario
- `1.2 Login Usuario` → Autentica y obtiene token
- `1.3 Obtener Usuario por ID` → Consulta por ID
- `1.4 Obtener Usuario por Email` → Consulta por email

#### 2️⃣ PRODUCTOS
- `2.1 Crear Producto 1` → Crea primer producto
- `2.2 Crear Producto 2` → Crea segundo producto
- `2.3 Obtener Todos los Productos` → Lista completa
- `2.4 Obtener Producto por ID` → Consulta específica
- `2.5 Filtrar Productos por Estado` → Filtra por AVAILABLE, etc.

#### 3️⃣ CARRITO
- `3.1 Agregar Producto 1 al Carrito` → Crea carrito y agrega item
- `3.2 Agregar Producto 2 al Carrito` → Agrega más items
- `3.3 Obtener Carrito Activo` → Consulta carrito del cliente
- `3.4 Obtener Carrito por ID` → Consulta por ID específico
- `3.5 Actualizar Cantidad de Item` → Modifica cantidad
- `3.6 Eliminar Item del Carrito` → Elimina un item (opcional)
- `3.7 Confirmar Carrito` → Marca carrito como CONFIRMED

#### 4️⃣ ÓRDENES
- `4.1 Crear Orden desde Carrito` → Crea orden desde carrito confirmado
- `4.2 Obtener Orden por ID` → Consulta orden específica
- `4.3 Obtener Órdenes por Cliente` → **Lista todas las órdenes del cliente** (usado por "Mis Órdenes")
- `4.4 Actualizar Estado de Orden` → Cambia estado (SHIPPED, DELIVERED, etc.)

## 📋 Casos de Uso Principales

### Caso 1: Crear una Orden Completa

```
1. Crear Usuario (1.1)
2. Crear Producto 1 (2.1)
3. Crear Producto 2 (2.2)
4. Agregar Producto 1 al Carrito (3.1)
5. Agregar Producto 2 al Carrito (3.2)
6. Obtener Carrito Activo (3.3) - Verificar contenido
7. Confirmar Carrito (3.7)
8. Crear Orden desde Carrito (4.1)
9. Obtener Orden por ID (4.2) - Verificar detalles
```

### Caso 2: Ver Historial de Órdenes (Simula "Mis Órdenes")

```
1. Crear Usuario (1.1)
2. Crear varios productos (2.1, 2.2)
3. Crear primera orden (3.1 → 3.7 → 4.1)
4. Crear segunda orden (3.1 → 3.7 → 4.1)
5. Obtener Órdenes por Cliente (4.3) - Ver todas las órdenes
```

### Caso 3: Modificar Carrito antes de Confirmar

```
1. Crear Usuario (1.1)
2. Crear Productos (2.1, 2.2)
3. Agregar items al carrito (3.1, 3.2)
4. Actualizar cantidad de un item (3.5)
5. Eliminar un item (3.6)
6. Obtener carrito actualizado (3.3)
7. Confirmar carrito (3.7)
8. Crear orden (4.1)
```

### Caso 4: Seguimiento de Orden

```
1. Crear orden completa (Caso 1)
2. Actualizar estado a SHIPPED (4.4) - Body: {"status": "SHIPPED"}
3. Actualizar estado a DELIVERED (4.4) - Body: {"status": "DELIVERED"}
4. Obtener orden actualizada (4.2)
```

## 🎯 Estados Válidos

### Estados de Carrito
- `ACTIVE` - Carrito activo, se pueden agregar/modificar items
- `CONFIRMED` - Carrito confirmado, listo para crear orden
- `CONVERTED_TO_ORDER` - Carrito convertido en orden

### Estados de Orden
- `PENDING` - Orden creada, pendiente de confirmación
- `CONFIRMED` - Orden confirmada
- `SHIPPED` - Orden enviada
- `DELIVERED` - Orden entregada
- `CANCELLED` - Orden cancelada
- `FAILED` - Orden fallida

### Estados de Producto
- `AVAILABLE` - Producto disponible
- `OUT_OF_STOCK` - Sin stock
- `DISCONTINUED` - Descontinuado

## ⚠️ Notas Importantes

### Headers Requeridos

**Carrito (todos los endpoints excepto GET por ID):**
```
X-Customer-Id: {{customer_id}}
```

**Requests con Body:**
```
Content-Type: application/json
```

### Orden de Ejecución Crítica

1. **Crear Usuario ANTES de crear carrito** - El carrito necesita un customer_id válido
2. **Crear Productos ANTES de agregar al carrito** - Necesitas product_id válidos
3. **Confirmar Carrito ANTES de crear orden** - Solo carritos CONFIRMED pueden convertirse en órdenes
4. **NO agregar items a carrito CONFIRMED** - El backend rechazará la operación

### Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| 404 Not Found | ID no existe | Verifica que el request anterior se ejecutó correctamente |
| 400 Bad Request | Carrito ya confirmado | Crea un nuevo carrito agregando un item |
| 400 Bad Request | Producto sin stock | Verifica el stock del producto |
| 500 Internal Server Error | Microservicio caído | Verifica `docker-compose logs` |

## 🔍 Debugging

### Ver Variables Actuales

1. Click en la colección "Sofkify - Flujo Completo E2E"
2. Tab "Variables"
3. Verifica los valores en la columna "Current Value"

### Ver Logs de Scripts

1. Abre la consola de Postman (View → Show Postman Console)
2. Ejecuta un request
3. Los scripts de test muestran mensajes como:
   ```
   ✅ PASO 1 COMPLETADO - Customer ID: abc-123-def
   ```

### Verificar Respuestas

Cada request muestra:
- **Status**: Código HTTP (200, 201, 400, etc.)
- **Body**: Respuesta JSON del servidor
- **Headers**: Headers de respuesta
- **Test Results**: Resultados de los scripts de test

## 🧪 Pruebas Automatizadas

Los scripts de test incluidos:

1. **Guardan automáticamente IDs** en variables de colección
2. **Validan códigos de respuesta** (200, 201, etc.)
3. **Muestran mensajes de confirmación** en la consola
4. **Facilitan el flujo secuencial** sin copiar/pegar IDs manualmente

## 📊 Ejemplo de Flujo Completo

```
POST /api/users
  → customer_id: "550e8400-e29b-41d4-a716-446655440000"

POST /api/products
  → product_id_1: "660e8400-e29b-41d4-a716-446655440001"

POST /api/carts/items (Header: X-Customer-Id)
  → cart_id: "770e8400-e29b-41d4-a716-446655440002"

POST /api/carts/{cart_id}/confirm
  → status: "CONFIRMED"

POST /api/orders/from-cart/{cart_id}
  → order_id: "880e8400-e29b-41d4-a716-446655440003"

GET /api/orders/customer/{customer_id}
  → [order1, order2, order3, ...]
```

## 🎉 Verificación Final

Después de ejecutar el flujo completo, verifica:

1. ✅ Usuario creado en base de datos `sofkify_users_bd`
2. ✅ Productos creados en `sofkify_products_bd`
3. ✅ Carrito confirmado en `sofkify_carts_bd`
4. ✅ Orden creada en `sofkify_orders_bd`
5. ✅ Endpoint "Obtener Órdenes por Cliente" retorna la orden

## 🆘 Soporte

Si encuentras problemas:

1. Verifica que Docker esté corriendo: `docker ps`
2. Revisa logs del backend: `docker-compose logs -f`
3. Verifica las variables de colección en Postman
4. Asegúrate de ejecutar los requests en orden
5. Limpia la base de datos si es necesario: `docker-compose down -v`

---

**¡Listo para probar!** 🚀

Comienza con la carpeta **"5. FLUJO COMPLETO E2E"** para una prueba rápida de todo el sistema.
