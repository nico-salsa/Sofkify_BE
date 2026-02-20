# Sofkify Backend - Docker Compose Setup

## 🚀 Inicio Rápido

### Prerrequisitos
- Docker 24.0+
- Docker Compose 2.20+
- (Opcional) Git Bash en Windows

### Levantar el Stack Completo

```bash
# 1. Clonar el repositorio (si no lo tienes)
git clone https://github.com/nico-salsa/Sofkify_BE.git
cd Sofkify_BE

# 2. (Opcional) Personalizar variables de entorno
cp .env.example .env
# Editar .env si necesitas cambiar valores

# 3. Levantar servicios
docker-compose up -d --build

# 4. Verificar estado
docker-compose ps
```

**Tiempo aproximado**: 60 segundos (primer levantamiento)

### Acceso a Servicios

| Servicio | URL/Acceso | Descripción |
|----------|-----------|-------------|
| User Service | `http://localhost:8080` | Gestión de usuarios |
| Product Service | `http://localhost:8081` | Catálogo de productos |
| Order Service | `http://localhost:8082` | Gestión de órdenes |
| Cart Service | `http://localhost:8083` | Carrito de compras |
| RabbitMQ UI | `http://localhost:15672` | user: `guest`, pass: `guest` |
| SQLite (Usuarios) | `localhost:5432` | psql connection |
| SQLite (Productos) | `localhost:5433` | psql connection |
| SQLite (Carritos) | `localhost:5434` | psql connection |
| SQLite (Órdenes) | `localhost:5435` | psql connection |

---

## 📝 Configuración

### Variables de Entorno Importantes

El archivo `.env.example` contiene todas las variables. Las más críticas:

```env
# Credenciales de PostgreSQL
DB_USER=postgres
DB_PASSWORD=postgres

# Puertos expuestos
POSTGRES_USERS_PORT=5432
POSTGRES_PRODUCTS_PORT=5433
POSTGRES_CARTS_PORT=5434
POSTGRES_ORDERS_PORT=5435

# RabbitMQ
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# Puertos de servicios
USER_SERVICE_PORT=8080
PRODUCT_SERVICE_PORT=8081
ORDER_SERVICE_PORT=8082
CART_SERVICE_PORT=8083
```

---

## 🔧 Comandos Útiles

### Con Helper Script (Recomendado)

```bash
# Ver todos los comandos disponibles
./docker-helper.sh

# Levantar servicios
./docker-helper.sh up

# Ver logs de un servicio
./docker-helper.sh logs product-service

# Conectar a una base de datos
./docker-helper.sh db-products

# Ver estado
./docker-helper.sh status

# Detener
./docker-helper.sh down

# Limpieza completa (elimina volúmenes)
./docker-helper.sh clean-all
```

### Comandos Directos (Docker Compose)

```bash
# Levantar y construir
docker-compose up -d --build

# Ver logs de un servicio
docker-compose logs -f product-service

# Ejecutar comando en contenedor
docker-compose exec postgres-users psql -U postgres -d sofkify_users

# Detener servicios
docker-compose down

# Detener todo y eliminar volúmenes (CUIDADO)
docker-compose down -v

# Reiniciar un servicio
docker-compose restart order-service

# Recompilar una imagen
docker-compose build --no-cache product-service
```

---

## 📊 Monitoreo

### Ver estado de contenedores
```bash
docker-compose ps
```

### Ver logs en tiempo real
```bash
docker-compose logs -f
```

### Acceder a RabbitMQ Management
```bash
# Abrir navegador
http://localhost:15672

# Credenciales
Usuario: guest
Contraseña: guest
```

### Conectar a bases de datos
```bash
# Usuarios
docker-compose exec postgres-users psql -U postgres -d sofkify_users

# Productos
docker-compose exec postgres-products psql -U postgres -d sofkify_products_bd

# Carritos
docker-compose exec postgres-carts psql -U postgres -d sofkify_cars_bd

# Órdenes
docker-compose exec postgres-orders psql -U postgres -d sofkify_orders_bd
```

---

## 🐛 Troubleshooting

### Los servicios no inician

```bash
# Ver logs detallados
docker-compose logs

# Ver estado detallado
docker-compose ps --all

# Recrear un servicio
docker-compose up -d --force-recreate product-service
```

### Database connection refused

```bash
# Verificar que PostgreSQL esté corriendo y healthy
docker-compose ps | grep postgres

# Ver logs de PostgreSQL
docker-compose logs postgres-users

# Probar conectividad
docker-compose exec product-service pg_isready -h postgres-products -p 5432
```

### RabbitMQ no responde

```bash
# Verificar estado
docker-compose ps rabbitmq

# Ver logs
docker-compose logs rabbitmq

# Generar diagnostic report
docker-compose exec rabbitmq rabbitmq-diagnostics
```

### Puerto ya está en uso

```bash
# Cambiar puerto en .env
# Por ejemplo, para user-service:
USER_SERVICE_PORT=9080

# O liberar puerto
lsof -i :8080  # Listar procesos usando puerto 8080
kill -9 <PID>  # Terminar proceso
```

---

## 🔄 Flujo de Desarrollo

### 1. Cambiar código en un microservicio
```bash
# Editar código en, ej: product-service/src/...

# Recompilar y reiniciar servicio
docker-compose up -d --build product-service

# Ver logs para verificar
docker-compose logs -f product-service
```

### 2. Testear con curl
```bash
# Obtener todos los productos
curl -X GET http://localhost:8081/api/v1/products

# Crear un usuario
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com"}'

# Crear un carrito
curl -X POST http://localhost:8083/api/v1/carts \
  -H "Content-Type: application/json" \
  -d '{"customerId":"uuid-here"}'
```

### 3. Inspeccionar base de datos
```bash
# Ver tablas
docker-compose exec postgres-products psql -U postgres -d sofkify_products_bd -c "\dt"

# Ver contenido de tabla
docker-compose exec postgres-products psql -U postgres -d sofkify_products_bd -c "SELECT * FROM products LIMIT 10;"
```

### 4. Monitorear RabbitMQ
1. Acceder a http://localhost:15672
2. Ver **Queues** para mensajes pendientes
3. Ver **Connections** para clientes conectados

---

## 📈 Escalabilidad

### Agregar Frontend

Descomentar en `docker-compose.yml`:
```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
  container_name: frontend
  ports:
    - "3000:3000"
  depends_on:
    - user-service
    - product-service
    - cart-service
    - order-service
  networks:
    - sofkify-network
```

Crear estructura:
```
frontend/
├── Dockerfile
├── package.json
├── src/
└── ...
```

### Agregar más réplicas de un servicio

En producción con Swarm:
```bash
docker service update --replicas 3 order-service
```

---

## 🧹 Limpieza

### Ahorrar espacio en disco

```bash
# Eliminar contenedores detenidos
docker container prune

# Eliminar imágenes no usadas
docker image prune -a

# Eliminar volúmenes no usados
docker volume prune

# Todo de una vez (CUIDADO: elimina datos)
docker system prune -a --volumes
```

### Resetear completamente

```bash
# Esto elimina TODO (contenedores, imágenes, volúmenes, redes)
docker-compose down -v
docker image prune -a --force
docker network prune --force

# Volver a empezar
docker-compose up -d --build
```

---

## 📚 Documentación Completa

Para documentación detallada ver: [DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md)

Temas cubiertos:
- Topología de servicios
- Configuración detallada de cada servicio
- Variables de entorno
- Health checks y estrategia de dependencias
- Flujos de comunicación (síncrona/asíncrona)
- Volúmenes y persistencia
- Seguridad y mejores prácticas
- Troubleshooting avanzado

---

## 🆘 Soporte

Para problemas específicos:

1. **Microservicios no inician**: Ver logs con `docker-compose logs <service>`
2. **Base de datos no disponible**: Verificar healthcheck con `docker-compose ps`
3. **RabbitMQ issues**: Acceder a UI en http://localhost:15672
4. **Puertos en conflicto**: Cambiar en `.env` y relanzar

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0
