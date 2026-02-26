@echo off
echo ========================================
echo Limpiando contenedores y volumenes de Docker
echo ========================================

echo.
echo [1/5] Deteniendo todos los contenedores...
docker-compose down

echo.
echo [2/5] Forzando detencion de contenedores individuales...
docker stop postgres-users postgres-products postgres-carts postgres-orders rabbitmq user-service product-service cart-service order-service 2>nul

echo.
echo [3/5] Eliminando contenedores...
docker rm -f postgres-users postgres-products postgres-carts postgres-orders rabbitmq user-service product-service cart-service order-service 2>nul

echo.
echo [4/5] Eliminando volumenes con docker-compose down -v...
docker-compose down -v

echo.
echo [5/5] Intentando eliminar volumenes manualmente...
docker volume rm sofkify_be_postgres-users-data 2>nul
docker volume rm sofkify_be_postgres-products-data 2>nul
docker volume rm sofkify_be_postgres-carts-data 2>nul
docker volume rm sofkify_be_postgres-orders-data 2>nul
docker volume rm sofkify_be_rabbitmq-data 2>nul

echo.
echo Listando volumenes restantes...
docker volume ls | findstr sofkify

echo.
echo ========================================
echo Limpieza completada!
echo ========================================
echo.
echo Si aun ves volumenes arriba, ejecuta:
echo   docker volume prune -f
echo.
echo Luego ejecuta: docker-compose up --build
echo.
pause
