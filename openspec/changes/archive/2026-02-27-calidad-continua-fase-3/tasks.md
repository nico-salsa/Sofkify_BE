## 1. Plan de Pruebas y Riesgos

- [x] 1.1 Actualizar TEST_PLAN.md con alcance, niveles (unit/integration/E2E), herramientas y calendario.
- [x] 1.2 Añadir sección de gestión de riesgos con severidad, probabilidad y mitigación.
- [x] 1.3 Mapear endpoints REST cubiertos por integración y criterios de salida (incluye umbral de cobertura ≥70%).

## 2. Matriz Gherkin (Hoja de Cálculo)

- [x] 2.1 Crear la hoja de cálculo para la matriz de pruebas y poblarla con columnas requeridas (ID, servicio/endpoint, GIVEN/WHEN/THEN, datos, esperado, estado, fecha, ejecutor).
- [x] 2.2 Cargar escenarios Gherkin de flujos críticos: login, listado de productos, agregar al carrito, confirmar carrito, crear orden, ver órdenes.
- [x] 2.3 Referenciar el enlace de la hoja en TEST_PLAN.md y README/guías pertinentes.

## 3. Pipeline CI con Cobertura

- [x] 3.1 Crear .github/workflows/ci.yml con triggers en push/PR a main/develop/feature/**.
- [x] 3.2 Configurar jobs para build + tests backend (Gradle) y opcional frontend (Vitest) incluyendo cobertura.
- [x] 3.3 Aplicar umbral de cobertura ≥70% y fallo temprano en caso de incumplimiento; publicar resultados/logs y, si aplica, reporte de cobertura.

## 4. Higiene de Repositorio y Referencias

- [x] 4.1 Actualizar README u otras guías operativas para incluir enlaces al plan de pruebas y a la matriz Gherkin.
- [x] 4.2 Verificar que la documentación de ejecución (compose/commands) siga siendo consistente tras los cambios de calidad.