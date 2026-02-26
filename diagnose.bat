@echo off
echo ========================================
echo DIAGNOSTICO DE SOFKIFY
echo ========================================

cd /d "%~dp0"

echo.
echo [1/8] Verificando archivo .env...
if exist ".env" (
    echo [OK] Archivo .env existe
    echo.
    echo Contenido del .env:
    type .env
) else (
    echo [ERROR] Archivo .env NO existe
    echo Debes crear el archivo .env con la configuracion correcta
)

echo.
echo ========================================
echo [2/8] Estado de contenedores...
docker ps -a --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo.
echo ========================================
echo [3/8] Volumenes de Docker...
docker volume ls | findstr sofkify

echo.
echo ========================================
echo [4/8] Imagenes de Docker...
docker images | findstr sofkify

echo.
echo ========================================
echo [5/8] Verificando conectividad de bases de datos...

echo.
echo [5.1] postgres-users (puerto 5432):
docker exec postgres-users pg_isready -U sofkify 2>nul
if %errorlevel% equ 0 (
    echo [OK] Base de datos accesible
    docker exec postgres-users psql -U sofkify -d sofkify_users -c "SELECT current_database();" 2>nul
) else (
    echo [ERROR] No se puede conectar a la base de datos
)

echo.
echo [5.2] postgres-products (puerto 5433):
docker exec postgres-products pg_isready -U sofkify 2>nul
if %errorlevel% equ 0 (
    echo [OK] Base de datos accesible
    docker exec postgres-products psql -U sofkify -d sofkify_products_bd -c "SELECT current_database();" 2>nul
) else (
    echo [ERROR] No se puede conectar a la base de datos
)

echo.
echo [5.3] postgres-carts (puerto 5434):
docker exec postgres-carts pg_isready -U sofkify 2>nul
if %errorlevel% equ 0 (
    echo [OK] Base de datos accesible
    docker exec postgres-carts psql -U sofkify -d sofkify_carts_bd -c "SELECT current_database();" 2>nul
) else (
    echo [ERROR] No se puede conectar a la base de datos
)

echo.
echo [5.4] postgres-orders (puerto 5435):
docker exec postgres-orders pg_isready -U sofkify 2>nul
if %errorlevel% equ 0 (
    echo [OK] Base de datos accesible
    docker exec postgres-orders psql -U sofkify -d sofkify_orders_bd -c "SELECT current_database();" 2>nul
) else (
    echo [ERROR] No se puede conectar a la base de datos
)

echo.
echo ========================================
echo [6/8] Verificando servicios de microservicios...

echo.
echo [6.1] user-service (puerto 8080):
curl -s http://localhost:8080/actuator/health 2>nul
if %errorlevel% equ 0 (
    echo [OK] Servicio respondiendo
) else (
    echo [INFO] Servicio no responde o no tiene endpoint /actuator/health
)

echo.
echo [6.2] product-service (puerto 8081):
curl -s http://localhost:8081/actuator/health 2>nul
if %errorlevel% equ 0 (
    echo [OK] Servicio respondiendo
) else (
    echo [INFO] Servicio no responde o no tiene endpoint /actuator/health
)

echo.
echo [6.3] order-service (puerto 8082):
curl -s http://localhost:8082/actuator/health 2>nul
if %errorlevel% equ 0 (
    echo [OK] Servicio respondiendo
) else (
    echo [INFO] Servicio no responde o no tiene endpoint /actuator/health
)

echo.
echo [6.4] cart-service (puerto 8083):
curl -s http://localhost:8083/actuator/health 2>nul
if %errorlevel% equ 0 (
    echo [OK] Servicio respondiendo
) else (
    echo [INFO] Servicio no responde o no tiene endpoint /actuator/health
)

echo.
echo ========================================
echo [7/8] Ultimas lineas de logs de cada servicio...

echo.
echo [7.1] user-service:
docker logs --tail 5 user-service 2>nul

echo.
echo [7.2] product-service:
docker logs --tail 5 product-service 2>nul

echo.
echo [7.3] cart-service:
docker logs --tail 5 cart-service 2>nul

echo.
echo [7.4] order-service:
docker logs --tail 5 order-service 2>nul

echo.
echo ========================================
echo [8/8] Verificando red de Docker...
docker network inspect sofkify_be_sofkify-network 2>nul | findstr "Name"

echo.
echo ========================================
echo DIAGNOSTICO COMPLETADO
echo ========================================
echo.
echo Para ver logs completos de un servicio:
echo   docker logs -f [nombre-servicio]
echo.
echo Para ver logs de todos los servicios:
echo   docker-compose logs -f
echo.
pause
