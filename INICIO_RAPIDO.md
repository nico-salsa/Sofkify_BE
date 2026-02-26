# 🚀 Inicio Rápido - Sofkify Backend

## ⚡ Solución Rápida (1 comando)

Si tienes problemas con las bases de datos, ejecuta:

```cmd
cd Sofkify_BE
rebuild-all.bat
```

Este script hace TODO automáticamente:
1. ✅ Detiene contenedores
2. ✅ Elimina volúmenes antiguos
3. ✅ Reconstruye imágenes
4. ✅ Levanta todos los servicios

## 📋 Otros Scripts Útiles

### 🔍 Diagnosticar Problemas
```cmd
diagnose.bat
```
Muestra el estado completo del sistema.

### 🧹 Limpieza Forzada
```cmd
force-clean.bat
```
Elimina TODO relacionado con Sofkify (usar si rebuild-all.bat falla).

### ✅ Verificar Bases de Datos
```cmd
verify-databases.bat
```
Verifica que las 4 bases de datos existen correctamente.

## 🎯 Verificación Rápida

Después de levantar los servicios, verifica que todo funciona:

```cmd
REM Ver estado de contenedores
docker ps

REM Ver logs en tiempo real
docker-compose logs -f

REM Probar APIs
curl http://localhost:8080/api/users
curl http://localhost:8081/api/products
curl http://localhost:8082/api/orders
curl http://localhost:8083/api/carts
```

## 🆘 Si Algo Sale Mal

1. Ejecuta `diagnose.bat` para ver qué está fallando
2. Lee los logs: `docker-compose logs -f`
3. Si persiste, ejecuta `force-clean.bat` y luego `docker-compose up --build`

## 📚 Documentación Completa

Lee `SOLUCION_PROBLEMAS_BD.md` para más detalles.

## 🔗 Puertos

| Servicio | Puerto | URL |
|----------|--------|-----|
| User Service | 8080 | http://localhost:8080/api |
| Product Service | 8081 | http://localhost:8081/api |
| Order Service | 8082 | http://localhost:8082/api |
| Cart Service | 8083 | http://localhost:8083/api |
| RabbitMQ Management | 15672 | http://localhost:15672 |
| PostgreSQL Users | 5432 | localhost:5432 |
| PostgreSQL Products | 5433 | localhost:5433 |
| PostgreSQL Carts | 5434 | localhost:5434 |
| PostgreSQL Orders | 5435 | localhost:5435 |
