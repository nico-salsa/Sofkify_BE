# Docker Troubleshooting & Diagnostics Guide

## 🔍 Diagnóstico Rápido

### Estado General del Sistema

```bash
# Ver estado de todos los contenedores
docker-compose ps

# Ver estado con detalles de salud
docker-compose ps --format "table {{.Service}}\t{{.Status}}\t{{.Image}}"

# Ver uso de recursos (CPU, memoria)
docker stats

# Ver redes disponibles
docker network ls
docker network inspect sofkify-network
```

### Verificación de Healthchecks

```bash
# Ver detalles completos del healthcheck de un servicio
docker inspect --format='{{json .State.Health}}' postgres-users | jq

# Todos los healthchecks con estado
docker-compose ps | grep -E "healthy|unhealthy"

# Ver logs de un contenedor específico
docker logs -f <container-name>

# Ver últimas 50 líneas
docker logs --tail=50 postgres-products
```

---

## 🐛 Problemas Comunes

### 1️⃣ Los Servicios No Inician

**Síntoma**: `docker-compose up` falla o los contenedores se detienen inmediatamente

**Diagnóstico**:
```bash
# Ver logs detallados
docker-compose logs

# Ver logs de un servicio específico
docker-compose logs order-service

# Ver estado del contenedor
docker inspect order-service
```

**Causas Comunes**:

#### a) Puertos en Uso
```bash
# Verificar qué proceso está usando el puerto
lsof -i :8080  # Linux/Mac
netstat -ano | findstr :8080  # Windows

# Liberar puerto (kill process)
kill -9 <PID>  # Linux/Mac
taskkill /PID <PID> /F  # Windows

# O cambiar puerto en .env
USER_SERVICE_PORT=9080
```

#### b) Imágenes No Construidas
```bash
# Forzar reconstrucción de imágenes
docker-compose build --no-cache

# Levantar con build
docker-compose up -d --build product-service
```

#### c) Variables de Entorno No Configuradas
```bash
# Verificar que .env existe
ls -la | grep ".env"

# Si no existe, crear desde template
cp .env.example .env

# Verificar que las variables están bien
docker-compose config

# Ver variables específicas
docker-compose config | grep RABBITMQ
```

---

### 2️⃣ Base de Datos No Disponible

**Síntoma**: `Connection refused` o `FATAL: database "sofkify_users" does not exist`

**Diagnóstico**:
```bash
# Verificar que PostgreSQL está corriendo
docker-compose ps | grep postgres

# Ver logs del contenedor PostgreSQL
docker-compose logs postgres-users

# Probar conexión desde otro contenedor
docker-compose exec product-service pg_isready -h postgres-products -p 5432

# Conectar directamente (si el contenedor está vivo)
docker exec -it postgres-users psql -U postgres -d sofkify_users
```

**Causas Comunes**:

#### a) PostgreSQL No está Healthy
```bash
# Ver estado del healthcheck
docker-compose ps postgres-users

# Si está "starting" o "unhealthy", esperar o ver logs
docker-compose logs postgres-users

# El script de inicialización puede estar tardando
# Esperar 30 segundos antes de conectar
```

#### b) Volumen Corrupto
```bash
# Eliminar volumen (CUIDADO: se pierden datos)
docker volume rm sofkify_postgres-users-data

# Recrear
docker-compose up -d postgres-users

# Esperar a que esté healthy
docker-compose ps | grep postgres-users
```

#### c) Script init-db.sql No Se Ejecutó
```bash
# Verificar que el archivo existe
ls -la init-db.sql

# Recrear la base de datos
docker-compose down
docker volume rm sofkify_postgres-users-data
docker-compose up -d postgres-users

# Monitorear logs mientras se inicializa
docker-compose logs -f postgres-users
```

#### d) Variables de Conexión Incorrectas
```bash
# Verificar la URL de conexión configurada
docker-compose exec product-service env | grep DATASOURCE

# Debe ser algo como:
# SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-products:5433/sofkify_products_bd

# Probar conectividad desde el servicio
docker-compose exec product-service bash
# Dentro del contenedor:
nc -zv postgres-products 5433
```

---

### 3️⃣ RabbitMQ No Responde

**Síntoma**: `Connection refused` en RabbitMQ, o servicios no publican/consumen mensajes

**Diagnóstico**:
```bash
# Verificar que RabbitMQ está corriendo
docker-compose ps rabbitmq

# Ver status del healthcheck
docker-compose ps | grep rabbitmq

# Ver logs
docker-compose logs rabbitmq

# Verificar que está escuchando en puerto 5672
docker-compose exec rabbitmq netstat -tlnp | grep 5672

# Probar diagnósticos
docker-compose exec rabbitmq rabbitmq-diagnostics ping_alarms
docker-compose exec rabbitmq rabbitmq-diagnostics status
```

**Causas Comunes**:

#### a) RabbitMQ No está Healthy
```bash
# Ver estado detallado
docker-compose ps --format "table {{.Service}}\t{{.Status}}" | grep rabbitmq

# Si está unhealthy:
docker-compose logs rabbitmq

# Reiniciar
docker-compose restart rabbitmq

# Esperar 30 segundos y verificar
sleep 30
docker-compose ps rabbitmq
```

#### b) Credenciales Incorrectas
```bash
# Verificar credenciales en contenedor
docker-compose exec rabbitmq env | grep RABBITMQ

# Deben coincidir con las del .env
# Verificar en línea de comandos
docker-compose exec rabbitmq rabbitmqctl eval 'rabbit_auth_backend_internal:user_credentials_from_username(<<"guest">>).'
```

#### c) Volumen Corrupto
```bash
# Limpiar volumen
docker volume rm sofkify_rabbitmq-data sofkify_rabbitmq-logs

# Recrear
docker-compose down
docker-compose up -d rabbitmq

# Esperar a que esté healthy
docker-compose ps rabbitmq
```

#### d) Puerto en Conflicto
```bash
# Verificar si puerto 5672 está en uso
lsof -i :5672  # Linux/Mac
netstat -ano | findstr :5672  # Windows

# Cambiar puerto en .env
RABBITMQ_PORT=5673

# Detener y levantar de nuevo
docker-compose down
docker-compose up -d rabbitmq
```

---

### 4️⃣ Microservicio No Conecta a RabbitMQ

**Síntoma**: `java.lang.RuntimeException: Failed to create channel` o `Connection timeout`

**Diagnóstico**:
```bash
# Verificar variables de entorno del servicio
docker-compose exec product-service env | grep RABBITMQ

# Deben ser:
# SPRING_RABBITMQ_HOST=rabbitmq (nombre del contenedor)
# SPRING_RABBITMQ_PORT=5672
# SPRING_RABBITMQ_USERNAME=guest
# SPRING_RABBITMQ_PASSWORD=guest

# Probar conectividad desde el servicio
docker-compose exec product-service bash
# Dentro:
nc -zv rabbitmq 5672

# Ver logs del servicio
docker-compose logs product-service | grep -i rabbit

# Ver logs de RabbitMQ para ver conexiones
docker-compose logs rabbitmq | grep -i "connection\|channel"
```

**Solución**:
```bash
# Asegurarse de que RabbitMQ está healthy
docker-compose ps rabbitmq

# Reiniciar el servicio después de que RabbitMQ esté ready
docker-compose restart product-service

# Ver logs para confirmar
docker-compose logs product-service
```

---

### 5️⃣ Problemas de Performance

**Síntoma**: Aplicaciones lentas, timeouts frecuentes

**Diagnóstico**:
```bash
# Ver uso de recursos
docker stats

# Ver CPU/Memoria
docker stats --no-stream

# Ver top de procesos en un contenedor
docker top <container-name>

# Analizar logs de errores
docker-compose logs | grep -i "timeout\|error\|exception"

# Ver qué tan lento está
time curl http://localhost:8081/api/v1/products
```

**Causas Comunes**:

#### a) Memoria Insuficiente
```bash
# Ver cuánta RAM usa cada contenedor
docker stats --no-stream

# Aumentar límites en docker-compose.yml
services:
  product-service:
    # ...
    deploy:
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M

# O desde línea de comandos
docker-compose down
docker-compose up -d --compatibility
```

#### b) CPU Limitada
```bash
# Ver uso de CPU
docker stats

# Verificar si hay throttling
docker inspect <container-name> | grep -A 5 "CpuQuota"

# Aumentar CPU limits
services:
  product-service:
    deploy:
      resources:
        limits:
          cpus: '1.5'
```

#### c) Base de Datos Lenta
```bash
# Conectar a la BD y verificar
docker-compose exec postgres-products psql -U postgres -d sofkify_products_bd

# Ver queries lentas
SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;

# Crear índices si es necesario
CREATE INDEX idx_products_stock ON products(stock);

# Ver tamaño de la BD
SELECT pg_size_pretty(pg_database_size('sofkify_products_bd'));
```

#### d) Red Saturada
```bash
# Ver tráfico de red en el contenedor
docker exec <container-name> iftop

# Ver estadísticas de red
docker-compose stats | grep -E "NetIn|NetOut"

# Cambiar de bridge a overlay network (en Swarm)
docker network create -d overlay sofkify-network
```

---

### 6️⃣ Disk Space Issues

**Síntoma**: `No space left on device` o `Error response from daemon`

**Diagnóstico**:
```bash
# Ver espacio disponible
df -h

# Ver qué consume Docker
docker system df

# Ver tamaño de volúmenes
docker volume ls -q | xargs -I {} docker volume inspect {} | grep -E "Name|Mountpoint|Size"

# También:
du -sh /var/lib/docker/*
```

**Solución**:
```bash
# Limpiar contenedores parados
docker container prune -f

# Limpiar imágenes sin usar
docker image prune -a -f

# Limpiar volúmenes sin usar
docker volume prune -f

# Limpiar redes sin usar
docker network prune -f

# Limpiar TODO
docker system prune -a --volumes -f

# Verificar nuevamente
docker system df
```

---

### 7️⃣ Reiniciar Servicio Específico

**Síntoma**: Un servicio específico falla y necesita reset

```bash
# Opción 1: Solo reiniciar el contenedor
docker-compose restart product-service

# Opción 2: Forzar recreación
docker-compose up -d --force-recreate product-service

# Opción 3: Reconstruir imagen
docker-compose up -d --build product-service

# Opción 4: Nuclear (elimina BD también)
docker-compose down order-service
docker volume rm sofkify_postgres-orders-data
docker-compose up -d order-service
```

---

## 🔍 Debugging Avanzado

### Ejecutar Comandos Dentro de Contenedores

```bash
# Acceder a shell del contenedor
docker-compose exec product-service bash

# Una vez dentro, puedes:
ps aux  # Ver procesos
jps -l  # Ver proceso Java y su PID
jconsole  # Conectar a JVM (si está habilitado)
curl http://localhost:8081/api/v1/products  # Testear API
```

### Inspeccionar Network

```bash
# Ver servicios en la red
docker network inspect sofkify-network

# Verificar conectividad entre contenedores
docker-compose exec product-service ping postgres-products
docker-compose exec product-service nslookup rabbitmq
docker-compose exec product-service telnet rabbitmq 5672
```

### Generar Heap Dump y Thread Dump

```bash
# Thread dump (para analizar deadlocks)
docker-compose exec product-service jstack $(docker-compose exec -it product-service jps -l | grep java | awk '{print $1}') > thread-dump.txt

# Heap dump (para analizar memory leaks)
docker-compose exec product-service jmap -dump:live,format=b,file=heap-dump.bin $(docker-compose exec -it product-service jps -l | grep java | awk '{print $1}')

# Extraer archivos
docker cp product-service:/heap-dump.bin ./
docker cp product-service:/thread-dump.txt ./
```

### Monitorear Conexiones de BD

```bash
# Conectar a PostgreSQL
docker-compose exec postgres-products psql -U postgres -d sofkify_products_bd

# Ver conexiones activas
SELECT * FROM pg_stat_activity;

# Ver conexiones por usuario
SELECT usename, COUNT(*) FROM pg_stat_activity GROUP BY usename;

# Matar conexión
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE pid <> pg_backend_pid();
```

### Monitorear RabbitMQ

```bash
# Ver queues
docker-compose exec rabbitmq rabbitmqctl list_queues name messages consumers

# Ver exchanges
docker-compose exec rabbitmq rabbitmqctl list_exchanges

# Ver bindings
docker-compose exec rabbitmq rabbitmqctl list_bindings

# Ver conexiones
docker-compose exec rabbitmq rabbitmqctl list_connections

# Ver canales
docker-compose exec rabbitmq rabbitmqctl list_channels

# Purge una queue (CUIDADO: pierde mensajes)
docker-compose exec rabbitmq rabbitmqctl purge_queue product.stock.decrement.queue
```

---

## 📊 Monitoreo Continuo

### Setup Básico de Monitoreo

```bash
# Ver logs en tiempo real de todos los servicios
docker-compose logs -f

# Filtrar por servicio
docker-compose logs -f product-service

# Filtrar por palabras clave (errores)
docker-compose logs -f | grep -i error

# Últimas 100 líneas
docker-compose logs --tail=100

# Monitorear recursos en tiempo real
watch -n 1 'docker stats --no-stream'

# Ver eventos de Docker
docker events --filter type=container
```

### Alertas Automáticas

Crear script `monitor.sh`:
```bash
#!/bin/bash

while true; do
  # Verificar healthchecks
  status=$(docker-compose ps --format "table {{.Service}}\t{{.Status}}" | grep -v healthy | grep -v "running\|up")
  
  if [ ! -z "$status" ]; then
    echo "⚠️  ALERTA: Servicio no healthy!"
    echo "$status"
    # Enviar notificación (email, Slack, etc.)
  fi
  
  sleep 60
done
```

---

## 🆘 Escalada de Problemas

Si con los pasos anteriores no resuelves, considera:

1. **Verificar logs de Docker daemon**:
```bash
journalctl -u docker -n 50  # Linux
# O desde Desktop Docker: Preferences > Resources > Advanced
```

2. **Verificar health del host**:
```bash
# Espacio disponible
df -h

# Memoria
free -h

# Procesos
top -b -n 1 | head -20
```

3. **Contactar soporte**:
- Incluir: `docker-compose ps`, logs, `.env` (sin contraseñas)
- Describir cuándo started el problema
- Pasos que ya intentaste

4. **Nuclear Reset** (Último recurso):
```bash
# Backup de datos primero
docker-compose down
docker volume ls | grep sofkify | xargs docker volume remove

# Limpiar todo
docker system prune -a --volumes -f

# Empezar de cero
docker-compose up -d --build

# Opcionalmente restaurar datos
```

---

## 📝 Logging y Auditoría

### Configurar Logging en Docker Compose

```yaml
services:
  product-service:
    # ...
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "3"
        labels: "service=product-service"
```

### Revisar logs guardados

```bash
# Ubicación de logs en el host
ls -la /var/lib/docker/containers/*/

# Ver logs de un contenedor específico
docker logs <container-id>

# Con grep para filtrar
docker logs <container-id> 2>&1 | grep "ERROR\|Exception"
```

---

## 📚 Checklist de Troubleshooting

- [ ] Ejecutar `docker-compose ps` para ver estado
- [ ] Revisar `docker-compose logs` para errores
- [ ] Verificar `docker-compose ps` muestre servicios healthy
- [ ] Confirmar `.env` existe y tiene valores
- [ ] Probar conectividad entre servicios
- [ ] Revisar puertos no estén en conflicto
- [ ] Verificar espacio en disco (docker system df)
- [ ] Confirmar permisos de archivos
- [ ] Reiniciar Docker daemon si nada funciona
- [ ] Verificar versiones de Docker: `docker --version`, `docker-compose --version`

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Mantenido por**: Equipo Sofkify Backend
