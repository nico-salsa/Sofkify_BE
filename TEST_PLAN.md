# TEST PLAN - Sofkify Backend

## 1. Objetivo
Definir y operacionalizar las pruebas para `Sofkify_BE`, cubriendo integración REST, gestión de riesgos y criterios de salida, con trazabilidad a una matriz de escenarios Gherkin.

## 2. Alcance y niveles de prueba
- Servicios: user-service, product-service, cart-service, order-service; frontend Vite para flujos E2E visibles.
- Niveles: unit (clases y dominio), integration (REST entre capas/servicios), E2E manual (flujo FE/BE).

## 3. Estrategia y herramientas
- Unit/Integration backend: JUnit 5 + Spring Boot Test; datos controlados; JaCoCo para cobertura.
- Integración REST manual/rápida: cURL/Postman usando endpoints documentados; ver [API_ENDPOINTS_REFERENCE.json](API_ENDPOINTS_REFERENCE.json) y [CURL_EXAMPLES.md](CURL_EXAMPLES.md).
- Frontend opcional: Vitest + Testing Library para componentes; validación manual en navegador para flujos críticos.
- Datos/fixtures: usar UUID conocidos y sembrado mínimo; limpiar entre ejecuciones.

## 4. Calendario y responsables
- Smoke diario (REST básico) antes de merges principales.
- Suite de integración REST en CI por push/PR.
- Revisión de matriz Gherkin y riesgos semanal o al cerrar iteración.

## 5. Gestion de riesgos
- Integración inter-servicios (stock/ordenes) → Mitigar con escenarios REST y verificación de eventos de orden/stock.
- Cobertura insuficiente (<70%) → CI con umbral y reporte Jacoco; priorizar rutas críticas.
- Datos inconsistentes en entornos → Scripts de seed y aislamiento de pruebas (containers o perfiles).
- Flujos FE/BE rotos por CORS/config → Validar en smoke FE y logs HTTP.

## 6. Pruebas de integración REST (endpoints base)
- user-service: POST /api/users, POST /api/users/auth/login, GET /api/users/{id}
- product-service: GET /api/products, GET /api/products/{id}, POST /api/products
- cart-service: POST /api/carts/items, PUT /api/carts/items/{cartItemId}, DELETE /api/carts/items/{cartItemId}, GET /api/carts
- order-service: POST /api/orders/from-cart/{cartId}, GET /api/orders/{orderId}, GET /api/orders/customer/{customerId}

## 7. Matriz de pruebas Gherkin
- Hoja de cálculo: [docs_IA/matriz_pruebas_gherkin.csv](docs_IA/matriz_pruebas_gherkin.csv)
- Columnas mínimas: ID, servicio/flujo, endpoint/ruta, GIVEN/WHEN/THEN, datos, esperado, estado (Pasó/Falló), fecha, ejecutor.
- Flujos cubiertos: login, listado de productos, agregar/actualizar/eliminar carrito, crear orden desde carrito, listar órdenes, flujo E2E FE.

## 8. Criterios de salida
- Cobertura de pruebas en CI ≥70% (fail si menor).
- Escenarios Gherkin de flujos críticos registrados en la matriz y ejecutados al menos una vez por iteración.
- Riesgos documentados con mitigación y sin issues abiertos bloqueantes en CI.

## 9. Referencias
- API REST: [API_ENDPOINTS_REFERENCE.json](API_ENDPOINTS_REFERENCE.json)
- Ejemplos rápidos: [CURL_EXAMPLES.md](CURL_EXAMPLES.md)
- Matriz: [docs_IA/matriz_pruebas_gherkin.csv](docs_IA/matriz_pruebas_gherkin.csv)
