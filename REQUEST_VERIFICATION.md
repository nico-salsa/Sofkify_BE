# Request Verification Guide

Objetivo: validar que el frontend dispara requests HTTP reales y que esos requests quedan visibles en consola/logs.

## 1. Levantar stack integrado

```powershell
cd C:\Sofka_U_Semana_2\Sofkify_BE
docker compose -f docker-compose.yml -f docker-compose.integration.yml up -d --build
```

## 2. Abrir logs en tiempo real

```powershell
docker compose -f docker-compose.yml -f docker-compose.integration.yml logs -f frontend user-service product-service cart-service order-service
```

En backend debes ver lineas con este formato:

- `[HTTP] POST /api/users -> 201 (...)`
- `[HTTP] GET /api/products -> 200 (...)`
- `[HTTP] POST /api/carts/items -> 201 (...)`
- `[HTTP] POST /api/carts/{cartId}/confirm -> 200 (...)`
- `[HTTP] POST /api/orders/from-cart/{cartId} -> 201 (...)`

En frontend (modo dev) debes ver lineas del cliente HTTP:

- `[HTTP][REQ] METHOD URL`
- `[HTTP][RES] METHOD URL -> STATUS (Nms)`
- `[HTTP][ERR] ...` (solo en fallos)

## 3. Flujo manual desde UI

1. Abrir `http://localhost:5173`
2. Registrar usuario o iniciar sesion
3. Entrar a productos y agregar un producto al carrito
4. Ir a carrito y pulsar `Ir a Confirmar`
5. Confirmar carrito
6. Validar creacion de orden y pantalla de orden

## 4. Flujo API equivalente (sin UI)

```powershell
# 1) crear usuario
# 2) login
# 3) listar productos
# 4) agregar item a carrito
# 5) confirmar carrito
# 6) crear orden desde carrito
```

Este flujo se puede automatizar y se usa para troubleshooting cuando UI no esta disponible.

## 5. Criterio de aceptacion

Se considera correcto cuando:

- Cada accion funcional genera al menos 1 request real al backend
- Los logs de frontend y backend muestran el request correspondiente
- El flujo termina con orden creada (`status: PENDING` o estado de negocio esperado)

## 6. Evidencia (ejecucion real)

Fecha de verificacion: `2026-02-25` (hora local).

Resultado de flujo API automatizado:

```json
{
  "executedAt": "2026-02-25 14:41:18",
  "email": "integration.20260225144104@example.com",
  "userId": "6015bbc4-d836-4061-9562-48195d653a40",
  "loginSuccess": true,
  "productId": "b1d17745-e998-43a9-94c0-29e8f91ab6cb",
  "cartId": "38a82ebf-0de4-4902-8d94-69e402601e6c",
  "confirmSuccess": true,
  "orderId": "8cea5a1f-c8f9-4594-a1dd-946ef0fa5d7e",
  "orderStatus": "PENDING"
}
```

Muestra de logs backend capturados:

- `[HTTP] POST /api/users -> 201`
- `[HTTP] POST /api/users/auth/login -> 200`
- `[HTTP] GET /api/products -> 200`
- `[HTTP] POST /api/carts/items -> 201`
- `[HTTP] POST /api/carts/38a82ebf-0de4-4902-8d94-69e402601e6c/confirm -> 200`
- `[HTTP] POST /api/orders/from-cart/38a82ebf-0de4-4902-8d94-69e402601e6c -> 201`
- `[HTTP] GET /api/orders/8cea5a1f-c8f9-4594-a1dd-946ef0fa5d7e -> 200`
