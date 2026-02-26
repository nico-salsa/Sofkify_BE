# 🔧 Solución a Problemas de Base de Datos

## 🔍 Problemas Identificados

1. **Volúmenes de Docker con datos antiguos**: Los volúmenes contienen bases de datos con nombres incorrectos (`sofkify_cars_bd` en lugar de `sofkify_carts_bd`)
2. **Script init-db.sql inefectivo**: El script anterior no funcionaba correctamente
3. **Falta archivo .env**: Docker Compose no podía leer las variables de entorno

## ✅ Soluciones Implementadas

### 1. Creado archivo `.env`
- Ubicación: `Sofkify_BE/.env`
- Contiene todas las variables de entorno necesarias
- Nombres de bases de datos corregidos

### 2. Corregido `init-db.sql`
- Ahora verifica la conexión a la base de datos
- Registra la inicialización correcta en los logs

### 3. Corregido `docker-compose.yml`
- Nombre de base de datos de carts: `sofkify_carts_bd` (antes era `sofkify_cars_bd`)
- Variables de entorno correctas en todos los servicios

### 4. Scripts de ayuda creados
- `reset-docker.bat`: Limpia completamente Docker
- `verify-databases.bat`: Verifica que las bases de datos existen

## 📋 PASOS PARA SOLUCIONAR

### ⚡ Opción 1: Script Automatizado TODO-EN-UNO (RECOMENDADO)

Este script hace todo automáticamente: limpia, reconstruye y levanta los servicios.

```cmd
cd Sofkify_BE
rebuild-all.bat
```

### 🔧 Opción 2: Limpieza Forzada + Reconstrucción Manual

Si la Opción 1 no funciona, usa la limpieza forzada:

```cmd
cd Sofkify_BE
force-clean.bat
docker-compose up --build
```

### 🛠️ Opción 3: Limpieza Estándar

```cmd
cd Sofkify_BE
reset-docker.bat
docker-compose up --build
```

### 📊 Opción 4: Diagnóstico Completo

Si quieres ver qué está pasando antes de limpiar:

```cmd
cd Sofkify_BE
diagnose.bat
```

Este script te mostrará:
- Estado del archivo .env
- Contenedores corriendo
- Volúmenes existentes
- Conectividad de bases de datos
- Estado de los microservicios
- Últimas líneas de logs

## 🎯 Scripts Disponibles

| Script | Descripción | Cuándo Usar |
|--------|-------------|-------------|
| `rebuild-all.bat` | Limpia y reconstruye todo automáticamente | Primera opción, solución completa |
| `force-clean.bat` | Limpieza agresiva de todos los recursos | Cuando rebuild-all.bat no funciona |
| `reset-docker.bat` | Limpieza estándar | Limpieza rápida |
| `diagnose.bat` | Diagnóstico completo del sistema | Para ver qué está fallando |
| `verify-databases.bat` | Verifica que las BDs existen | Después de levantar los servicios |

## 🔍 Verificación

### 1. Verificar que los contenedores están corriendo:
```cmd
docker ps
```

Deberías ver 9 contenedores:
- postgres-users
- postgres-products
- postgres-carts
- postgres-orders
- rabbitmq
- user-service
- product-service
- cart-service
- order-service

### 2. Verificar las bases de datos:
```cmd
verify-databases.bat
```

O manualmente:
```cmd
docker exec -it postgres-users psql -U sofkify -d sofkify_users -c "\l"
docker exec -it postgres-products psql -U sofkify -d sofkify_products_bd -c "\l"
docker exec -it postgres-carts psql -U sofkify -d sofkify_carts_bd -c "\l"
docker exec -it postgres-orders psql -U sofkify -d sofkify_orders_bd -c "\l"
```

### 3. Verificar los logs de los servicios:
```cmd
docker logs user-service
docker logs product-service
docker logs cart-service
docker logs order-service
```

Busca mensajes como:
- ✅ "HikariPool-1 - Start completed"
- ✅ "Started [ServiceName]Application"
- ❌ "Database does not exist" (esto indica problema)
- ❌ "Connection refused" (esto indica problema)

### 4. Probar las APIs:

**User Service:**
```cmd
curl http://localhost:8080/api/health
```

**Product Service:**
```cmd
curl http://localhost:8081/api/products
```

**Cart Service:**
```cmd
curl http://localhost:8083/api/health
```

**Order Service:**
```cmd
curl http://localhost:8082/api/health
```

## 🚨 Si Persisten los Problemas

### Problema: "Database does not exist"

**Causa**: Los volúmenes antiguos tienen datos corruptos

**Solución**:
```cmd
docker-compose down
docker system prune -a --volumes
docker-compose up --build
```

### Problema: "Connection refused"

**Causa**: El servicio intenta conectarse antes de que la BD esté lista

**Solución**: Ya está configurado con `healthcheck` y `depends_on`, pero puedes aumentar el tiempo:
- Edita `docker-compose.yml`
- En cada servicio de microservicio, agrega:
```yaml
restart: on-failure
restart_policy:
  max_attempts: 5
  delay: 10s
```

### Problema: Servicios en loop de reinicio

**Causa**: Error en la aplicación Java o configuración incorrecta

**Solución**:
1. Ver logs detallados: `docker logs -f [nombre-servicio]`
2. Verificar que el archivo `.env` existe y tiene los valores correctos
3. Verificar que las variables de entorno se están leyendo: `docker exec -it [nombre-servicio] env | grep DB`

## 📊 Configuración de Puertos

| Servicio | Puerto Host | Puerto Contenedor | Base de Datos |
|----------|-------------|-------------------|---------------|
| postgres-users | 5432 | 5432 | sofkify_users |
| postgres-products | 5433 | 5432 | sofkify_products_bd |
| postgres-carts | 5434 | 5432 | sofkify_carts_bd |
| postgres-orders | 5435 | 5432 | sofkify_orders_bd |
| rabbitmq | 5672, 15672 | 5672, 15672 | N/A |
| user-service | 8080 | 8080 | N/A |
| product-service | 8081 | 8081 | N/A |
| order-service | 8082 | 8082 | N/A |
| cart-service | 8083 | 8083 | N/A |

## 🎯 Checklist Final

- [ ] Archivo `.env` existe en `Sofkify_BE/`
- [ ] Ejecutado `docker-compose down -v`
- [ ] Eliminados volúmenes antiguos
- [ ] Ejecutado `docker-compose up --build`
- [ ] Todos los contenedores están en estado "healthy" o "running"
- [ ] Las 4 bases de datos existen con los nombres correctos
- [ ] Los servicios responden en sus puertos
- [ ] El frontend puede conectarse a las APIs

## 📞 Comandos Útiles

```cmd
REM Ver todos los contenedores
docker ps -a

REM Ver logs de un servicio específico
docker logs -f [nombre-contenedor]

REM Entrar a un contenedor
docker exec -it [nombre-contenedor] /bin/sh

REM Ver volúmenes
docker volume ls

REM Limpiar todo Docker (CUIDADO: elimina TODO)
docker system prune -a --volumes

REM Reconstruir un servicio específico
docker-compose up --build [nombre-servicio]
```
