# 🔧 Ejemplos cURL - Sofkify API

Ejemplos de comandos cURL para probar los endpoints desde la terminal.

## Variables de Entorno (Opcional)

Puedes definir estas variables para facilitar los comandos:

```bash
# Windows CMD
set CUSTOMER_ID=tu-customer-id-aqui
set PRODUCT_ID=tu-product-id-aqui
set CART_ID=tu-cart-id-aqui
set ORDER_ID=tu-order-id-aqui

# Windows PowerShell
$env:CUSTOMER_ID="tu-customer-id-aqui"
$env:PRODUCT_ID="tu-product-id-aqui"
$env:CART_ID="tu-cart-id-aqui"
$env:ORDER_ID="tu-order-id-aqui"
```

---

## 1️⃣ USUARIOS

### Crear Usuario
```bash
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"cliente@sofkify.com\",\"password\":\"password123\",\"name\":\"Juan Perez\"}"
```

**Respuesta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "cliente@sofkify.com",
  "name": "Juan Perez",
  "role": "CUSTOMER",
  "active": true
}
```

### Login
```bash
curl -X POST http://localhost:8080/api/users/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"cliente@sofkify.com\",\"password\":\"password123\"}"
```

### Obtener Usuario por ID
```bash
curl -X GET http://localhost:8080/api/users/%CUSTOMER_ID%
```

### Obtener Usuario por Email
```bash
curl -X GET http://localhost:8080/api/users/email/cliente@sofkify.com
```

---

## 2️⃣ PRODUCTOS

### Crear Producto
```bash
curl -X POST http://localhost:8081/api/products ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Laptop Dell XPS 15\",\"description\":\"Laptop de alto rendimiento\",\"price\":3500000,\"stock\":10,\"status\":\"AVAILABLE\"}"
```

**Respuesta:**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "name": "Laptop Dell XPS 15",
  "description": "Laptop de alto rendimiento",
  "price": 3500000,
  "stock": 10,
  "status": "AVAILABLE",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### Obtener Todos los Productos
```bash
curl -X GET http://localhost:8081/api/products
```

### Obtener Producto por ID
```bash
curl -X GET http://localhost:8081/api/products/%PRODUCT_ID%
```

### Filtrar Productos por Estado
```bash
curl -X GET "http://localhost:8081/api/products?status=AVAILABLE"
```

---

## 3️⃣ CARRITO

### Agregar Item al Carrito
```bash
curl -X POST http://localhost:8083/api/carts/items ^
  -H "Content-Type: application/json" ^
  -H "X-Customer-Id: %CUSTOMER_ID%" ^
  -d "{\"productId\":\"%PRODUCT_ID%\",\"quantity\":2}"
```

**Respuesta:**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "ACTIVE",
  "items": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440003",
      "productId": "660e8400-e29b-41d4-a716-446655440001",
      "productName": "Laptop Dell XPS 15",
      "unitPrice": 3500000,
      "quantity": 2,
      "subtotal": 7000000
    }
  ],
  "totalAmount": 7000000,
  "createdAt": "2024-01-15T10:35:00",
  "updatedAt": "2024-01-15T10:35:00"
}
```

### Obtener Carrito Activo
```bash
curl -X GET http://localhost:8083/api/carts ^
  -H "X-Customer-Id: %CUSTOMER_ID%"
```

### Obtener Carrito por ID
```bash
curl -X GET http://localhost:8083/api/carts/%CART_ID%
```

### Actualizar Cantidad de Item
```bash
curl -X PUT http://localhost:8083/api/carts/items/%CART_ITEM_ID% ^
  -H "Content-Type: application/json" ^
  -H "X-Customer-Id: %CUSTOMER_ID%" ^
  -d "{\"quantity\":5}"
```

### Eliminar Item del Carrito
```bash
curl -X DELETE http://localhost:8083/api/carts/items/%CART_ITEM_ID% ^
  -H "X-Customer-Id: %CUSTOMER_ID%"
```

### Confirmar Carrito
```bash
curl -X POST http://localhost:8083/api/carts/%CART_ID%/confirm ^
  -H "X-Customer-Id: %CUSTOMER_ID%"
```

**Respuesta:**
```json
{
  "cartId": "770e8400-e29b-41d4-a716-446655440002",
  "status": "CONFIRMED",
  "message": "Cart confirmed successfully"
}
```

---

## 4️⃣ ÓRDENES

### Crear Orden desde Carrito
```bash
curl -X POST http://localhost:8082/api/orders/from-cart/%CART_ID%
```

**Respuesta:**
```json
{
  "id": "990e8400-e29b-41d4-a716-446655440004",
  "cartId": "770e8400-e29b-41d4-a716-446655440002",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "items": [
    {
      "id": "aa0e8400-e29b-41d4-a716-446655440005",
      "productId": "660e8400-e29b-41d4-a716-446655440001",
      "productName": "Laptop Dell XPS 15",
      "unitPrice": 3500000,
      "quantity": 2,
      "subtotal": 7000000
    }
  ],
  "totalAmount": 7000000,
  "createdAt": "2024-01-15T10:40:00"
}
```

### Obtener Orden por ID
```bash
curl -X GET http://localhost:8082/api/orders/%ORDER_ID%
```

### Obtener Órdenes por Cliente (Mis Órdenes)
```bash
curl -X GET http://localhost:8082/api/orders/customer/%CUSTOMER_ID%
```

**Respuesta:**
```json
[
  {
    "id": "990e8400-e29b-41d4-a716-446655440004",
    "cartId": "770e8400-e29b-41d4-a716-446655440002",
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "PENDING",
    "items": [...],
    "totalAmount": 7000000,
    "createdAt": "2024-01-15T10:40:00"
  },
  {
    "id": "bb0e8400-e29b-41d4-a716-446655440006",
    "cartId": "cc0e8400-e29b-41d4-a716-446655440007",
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "DELIVERED",
    "items": [...],
    "totalAmount": 350000,
    "createdAt": "2024-01-14T15:20:00"
  }
]
```

### Actualizar Estado de Orden
```bash
curl -X PUT http://localhost:8082/api/orders/%ORDER_ID%/status ^
  -H "Content-Type: application/json" ^
  -d "{\"status\":\"SHIPPED\"}"
```

---

## 🚀 Flujo Completo E2E (Script)

### Windows CMD
```batch
@echo off
echo === FLUJO COMPLETO E2E ===

echo.
echo [1/6] Creando usuario...
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"email\":\"test@sofkify.com\",\"password\":\"test123\",\"name\":\"Test User\"}"

echo.
echo [2/6] Creando producto...
curl -X POST http://localhost:8081/api/products -H "Content-Type: application/json" -d "{\"name\":\"Producto Test\",\"description\":\"Descripcion\",\"price\":100000,\"stock\":50,\"status\":\"AVAILABLE\"}"

echo.
echo Ingresa el CUSTOMER_ID obtenido:
set /p CUSTOMER_ID=

echo Ingresa el PRODUCT_ID obtenido:
set /p PRODUCT_ID=

echo.
echo [3/6] Agregando al carrito...
curl -X POST http://localhost:8083/api/carts/items -H "Content-Type: application/json" -H "X-Customer-Id: %CUSTOMER_ID%" -d "{\"productId\":\"%PRODUCT_ID%\",\"quantity\":2}"

echo.
echo Ingresa el CART_ID obtenido:
set /p CART_ID=

echo.
echo [4/6] Confirmando carrito...
curl -X POST http://localhost:8083/api/carts/%CART_ID%/confirm -H "X-Customer-Id: %CUSTOMER_ID%"

echo.
echo [5/6] Creando orden...
curl -X POST http://localhost:8082/api/orders/from-cart/%CART_ID%

echo.
echo Ingresa el ORDER_ID obtenido:
set /p ORDER_ID=

echo.
echo [6/6] Obteniendo ordenes del cliente...
curl -X GET http://localhost:8082/api/orders/customer/%CUSTOMER_ID%

echo.
echo === FLUJO COMPLETADO ===
```

---

## 📝 Notas

### Formato de Respuestas
Todas las respuestas son en formato JSON. Para mejor legibilidad, puedes usar herramientas como `jq`:

```bash
curl -X GET http://localhost:8081/api/products | jq
```

### Headers Importantes
- **Content-Type: application/json** - Requerido en todos los POST/PUT
- **X-Customer-Id: {UUID}** - Requerido en endpoints de carrito

### Códigos de Respuesta
- **200 OK** - Operación exitosa (GET, PUT, DELETE)
- **201 Created** - Recurso creado exitosamente (POST)
- **400 Bad Request** - Datos inválidos o regla de negocio violada
- **404 Not Found** - Recurso no encontrado
- **500 Internal Server Error** - Error del servidor

### Debugging
Para ver headers de respuesta:
```bash
curl -v -X GET http://localhost:8081/api/products
```

Para guardar respuesta en archivo:
```bash
curl -X GET http://localhost:8081/api/products > productos.json
```

---

## 🔍 Verificación Rápida

### Verificar que todos los servicios estén activos:
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

### Verificar base de datos (desde contenedor):
```bash
docker exec -it sofkify_be-postgres-1 psql -U postgres -c "\l"
```

---

**¡Listo para probar!** 🎉

Usa estos comandos directamente en tu terminal CMD o PowerShell.
