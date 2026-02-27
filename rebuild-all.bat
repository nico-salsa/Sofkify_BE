@echo off
echo ========================================
echo RECONSTRUCCION COMPLETA DE SOFKIFY
echo ========================================
echo.
echo Este script hara lo siguiente:
echo 1. Detener todos los contenedores
echo 2. Eliminar volumenes antiguos
echo 3. Reconstruir las imagenes
echo 4. Levantar todos los servicios
echo.
pause

cd /d "%~dp0"

echo.
echo ========================================
echo PASO 1: LIMPIEZA
echo ========================================

echo.
echo Deteniendo servicios...
docker-compose down -v --remove-orphans

echo.
echo Eliminando contenedores...
docker rm -f postgres-users postgres-products postgres-carts postgres-orders rabbitmq user-service product-service cart-service order-service 2>nul

echo.
echo Eliminando volumenes...
docker volume rm sofkify_be_postgres-users-data sofkify_be_postgres-products-data sofkify_be_postgres-carts-data sofkify_be_postgres-orders-data sofkify_be_rabbitmq-data 2>nul

echo.
echo Limpiando sistema...
docker system prune -f

echo.
echo ========================================
echo PASO 2: RECONSTRUCCION
echo ========================================

echo.
echo Verificando archivo .env...
if not exist ".env" (
    echo ERROR: No se encuentra el archivo .env
    echo Por favor verifica que existe Sofkify_BE/.env
    pause
    exit /b 1
)

echo Archivo .env encontrado!
echo.

echo.
echo Reconstruyendo y levantando servicios...
echo Esto puede tomar varios minutos...
echo.

docker-compose up --build -d

echo.
echo ========================================
echo PASO 3: VERIFICACION
echo ========================================

echo.
echo Esperando 10 segundos para que los servicios inicien...
timeout /t 10 /nobreak >nul

echo.
echo Estado de los contenedores:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo.
echo ========================================
echo LOGS DE LOS SERVICIOS
echo ========================================
echo.
echo Para ver los logs en tiempo real, ejecuta:
echo   docker-compose logs -f
echo.
echo Para ver logs de un servicio especifico:
echo   docker logs -f [nombre-servicio]
echo.
echo Servicios disponibles:
echo   - postgres-users (puerto 5432)
echo   - postgres-products (puerto 5433)
echo   - postgres-carts (puerto 5434)
echo   - postgres-orders (puerto 5435)
echo   - rabbitmq (puertos 5672, 15672)
echo   - user-service (puerto 8080)
echo   - product-service (puerto 8081)
echo   - order-service (puerto 8082)
echo   - cart-service (puerto 8083)
echo.
echo ========================================
echo RECONSTRUCCION COMPLETADA!
echo ========================================
echo.
pause
