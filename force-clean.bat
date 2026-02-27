@echo off
echo ========================================
echo LIMPIEZA FORZADA DE DOCKER
echo ========================================
echo.
echo ADVERTENCIA: Este script eliminara TODOS los contenedores,
echo volumenes e imagenes relacionados con Sofkify.
echo.
pause

echo.
echo [1/6] Deteniendo docker-compose...
cd /d "%~dp0"
docker-compose down -v --remove-orphans

echo.
echo [2/6] Matando todos los contenedores de Sofkify...
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=postgres-users"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=postgres-products"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=postgres-carts"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=postgres-orders"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=rabbitmq"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=user-service"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=product-service"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=cart-service"') do docker rm -f %%i
for /f "tokens=*" %%i in ('docker ps -aq --filter "name=order-service"') do docker rm -f %%i

echo.
echo [3/6] Eliminando volumenes...
for /f "tokens=*" %%i in ('docker volume ls -q --filter "name=sofkify"') do docker volume rm %%i 2>nul

echo.
echo [4/6] Eliminando imagenes de servicios...
for /f "tokens=*" %%i in ('docker images -q sofkify_be-user-service') do docker rmi -f %%i
for /f "tokens=*" %%i in ('docker images -q sofkify_be-product-service') do docker rmi -f %%i
for /f "tokens=*" %%i in ('docker images -q sofkify_be-cart-service') do docker rmi -f %%i
for /f "tokens=*" %%i in ('docker images -q sofkify_be-order-service') do docker rmi -f %%i

echo.
echo [5/6] Limpiando recursos no utilizados...
docker system prune -f

echo.
echo [6/6] Verificando limpieza...
echo.
echo Contenedores restantes:
docker ps -a | findstr sofkify
echo.
echo Volumenes restantes:
docker volume ls | findstr sofkify
echo.
echo Imagenes restantes:
docker images | findstr sofkify

echo.
echo ========================================
echo LIMPIEZA COMPLETADA!
echo ========================================
echo.
echo Ahora ejecuta: docker-compose up --build
echo.
pause
