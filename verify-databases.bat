@echo off
echo ========================================
echo Verificando bases de datos PostgreSQL
echo ========================================

echo.
echo [1/4] Verificando base de datos de USERS (puerto 5432)...
docker exec -it postgres-users psql -U sofkify -d sofkify_users -c "\l" -c "\dt"

echo.
echo [2/4] Verificando base de datos de PRODUCTS (puerto 5433)...
docker exec -it postgres-products psql -U sofkify -d sofkify_products_bd -c "\l" -c "\dt"

echo.
echo [3/4] Verificando base de datos de CARTS (puerto 5434)...
docker exec -it postgres-carts psql -U sofkify -d sofkify_carts_bd -c "\l" -c "\dt"

echo.
echo [4/4] Verificando base de datos de ORDERS (puerto 5435)...
docker exec -it postgres-orders psql -U sofkify -d sofkify_orders_bd -c "\l" -c "\dt"

echo.
echo ========================================
echo Verificacion completada!
echo ========================================
pause
