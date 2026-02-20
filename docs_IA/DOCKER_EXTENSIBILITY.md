# Docker Architecture - Extensibilidad y Escalabilidad

## 📚 Tabla de Contenidos
1. [Agregar Nuevos Microservicios](#agregar-nuevos-microservicios)
2. [Agregar Frontend](#agregar-frontend)
3. [Agregar API Gateway](#agregar-api-gateway)
4. [Agregar Monitoreo](#agregar-monitoreo)
5. [Agregar Cache (Redis)](#agregar-cache-redis)
6. [Agregar Logging (ELK Stack)](#agregar-logging-elk-stack)
7. [Escalabilidad Horizontal](#escalabilidad-horizontal)
8. [Métricas y Performance](#métricas-y-performance)

---

## 🆕 Agregar Nuevos Microservicios

### Patrón Base para un Nuevo Servicio

Estructura recomendada en `docker-compose.yml`:

```yaml
# 1. Base de datos del nuevo servicio
my-service-db:
  image: postgres:16-alpine
  container_name: postgres-my-service
  environment:
    POSTGRES_USER: ${DB_USER:-postgres}
    POSTGRES_PASSWORD: ${DB_PASSWORD:-postgres}
    POSTGRES_DB: ${MY_SERVICE_DB_NAME:-sofkify_my_service_bd}
  ports:
    - "${POSTGRES_MY_SERVICE_PORT:-5436}:5432"
  volumes:
    - postgres-my-service-data:/var/lib/postgresql/data
    - ./init-db.sql:/docker-entrypoint-initdb.d/01-init.sql
  networks:
    - sofkify-network
  healthcheck:
    test: [ "CMD-SHELL", "pg_isready -U ${DB_USER:-postgres} -d ${MY_SERVICE_DB_NAME:-sofkify_my_service_bd}" ]
    interval: 10s
    timeout: 5s
    retries: 5
    start_period: 10s
  restart: unless-stopped

# 2. El microservicio
my-service:
  build:
    context: ./my-service
    dockerfile: Dockerfile
  container_name: my-service
  ports:
    - "${MY_SERVICE_PORT:-8084}:8084"
  environment:
    JAVA_TOOL_OPTIONS: "-Dspring.profiles.active=docker"
    SPRING_DATASOURCE_URL: jdbc:postgresql://${DB_HOST_MY_SERVICE:-postgres-my-service}:${POSTGRES_MY_SERVICE_PORT:-5436}/${MY_SERVICE_DB_NAME:-sofkify_my_service_bd}
    SPRING_DATASOURCE_USERNAME: ${DB_USER:-postgres}
    SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-postgres}
    SPRING_JPA_HIBERNATE_DDL_AUTO: validate
    SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.PostgreSQLDialect
    # Si el servicio necesita RabbitMQ
    SPRING_RABBITMQ_HOST: ${RABBITMQ_HOST:-rabbitmq}
    SPRING_RABBITMQ_PORT: ${RABBITMQ_PORT:-5672}
    SPRING_RABBITMQ_USERNAME: ${RABBITMQ_USER:-guest}
    SPRING_RABBITMQ_PASSWORD: ${RABBITMQ_PASSWORD:-guest}
  depends_on:
    postgres-my-service:
      condition: service_healthy
    rabbitmq:
      condition: service_healthy
  networks:
    - sofkify-network
  restart: unless-stopped
```

### Pasos para Agregar un Nuevo Servicio

1. **Crear directorio del servicio**:
```bash
mkdir my-service
cd my-service
# Crear estructura Spring Boot estándar
```

2. **Crear Dockerfile**:
```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]
```

3. **Agregar a docker-compose.yml**:
   - Seguir patrón base anterior
   - Asignar puerto único (8084+)
   - Asignar puerto BD único (5436+)

4. **Actualizar `.env.example`**:
```env
# My Service
MY_SERVICE_DB_NAME=sofkify_my_service_bd
DB_HOST_MY_SERVICE=postgres-my-service
POSTGRES_MY_SERVICE_PORT=5436
MY_SERVICE_PORT=8084
```

5. **Crear Dockerfile del servicio** en el directorio raíz (si no existe):
```bash
cd my-service
gradle build
cd ..
```

6. **Levantar nuevo servicio**:
```bash
docker-compose up -d --build my-service
```

---

## 🎨 Agregar Frontend

### Opción 1: React con Vite

```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
    args:
      - NODE_VERSION=20
  container_name: frontend
  ports:
    - "${FRONTEND_PORT:-3000}:3000"
  environment:
    VITE_API_URL: http://localhost:8000
    VITE_USER_API: http://localhost:8080/api/v1
    VITE_PRODUCT_API: http://localhost:8081/api/v1
    VITE_ORDER_API: http://localhost:8082/api/v1
    VITE_CART_API: http://localhost:8083/api/v1
  depends_on:
    - user-service
    - product-service
    - order-service
    - cart-service
  networks:
    - sofkify-network
  restart: unless-stopped
```

**Dockerfile para React**:
```dockerfile
# Build stage
FROM node:20-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Runtime stage
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/nginx.conf
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
```

**nginx.conf**:
```nginx
events {
  worker_connections 1024;
}

http {
  server {
    listen 3000;
    root /usr/share/nginx/html;
    index index.html;

    location / {
      try_files $uri $uri/ /index.html;
    }

    location /api {
      proxy_pass http://api-gateway:8000;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
    }
  }
}
```

### Opción 2: Next.js

```yaml
frontend:
  build:
    context: ./frontend
    dockerfile: Dockerfile
  container_name: frontend
  ports:
    - "${FRONTEND_PORT:-3000}:3000"
  environment:
    NEXT_PUBLIC_API_URL: http://localhost:8000
  depends_on:
    - api-gateway
  networks:
    - sofkify-network
  restart: unless-stopped
```

**Dockerfile para Next.js**:
```dockerfile
FROM node:20-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
EXPOSE 3000
CMD ["npm", "start"]
```

### Aggiungi a `.env.example`:
```env
FRONTEND_PORT=3000
VITE_API_URL=http://localhost:8000
```

---

## 🔀 Agregar API Gateway (nginx)

Para centralizar routing de los microservicios:

```yaml
api-gateway:
  image: nginx:alpine
  container_name: api-gateway
  ports:
    - "${API_GATEWAY_PORT:-8000}:8000"
  volumes:
    - ./nginx-gateway.conf:/etc/nginx/nginx.conf:ro
  depends_on:
    - user-service
    - product-service
    - order-service
    - cart-service
  networks:
    - sofkify-network
  restart: unless-stopped
```

**nginx-gateway.conf**:
```nginx
events {
  worker_connections 1024;
}

http {
  upstream user-service {
    server user-service:8080;
  }

  upstream product-service {
    server product-service:8081;
  }

  upstream order-service {
    server order-service:8082;
  }

  upstream cart-service {
    server cart-service:8083;
  }

  server {
    listen 8000;

    # User Service
    location /api/v1/users {
      proxy_pass http://user-service;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Product Service
    location /api/v1/products {
      proxy_pass http://product-service;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Order Service
    location /api/v1/orders {
      proxy_pass http://order-service;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Cart Service
    location /api/v1/carts {
      proxy_pass http://cart-service;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Health check
    location /health {
      access_log off;
      return 200 "healthy\n";
      add_header Content-Type text/plain;
    }
  }
}
```

**Aggiungi a `.env.example`**:
```env
API_GATEWAY_PORT=8000
```

---

## 📊 Agregar Monitoreo (Prometheus + Grafana)

### Prometheus
```yaml
prometheus:
  image: prom/prometheus:latest
  container_name: prometheus
  ports:
    - "${PROMETHEUS_PORT:-9090}:9090"
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml:ro
    - prometheus-data:/prometheus
  command:
    - '--config.file=/etc/prometheus/prometheus.yml'
    - '--storage.tsdb.path=/prometheus'
  depends_on:
    - user-service
    - product-service
    - order-service
    - cart-service
  networks:
    - sofkify-network
  restart: unless-stopped
```

**prometheus.yml**:
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'user-service'
    static_configs:
      - targets: ['user-service:8080']
    metrics_path: '/actuator/prometheus'

  - job_name: 'product-service'
    static_configs:
      - targets: ['product-service:8081']
    metrics_path: '/actuator/prometheus'

  - job_name: 'order-service'
    static_configs:
      - targets: ['order-service:8082']
    metrics_path: '/actuator/prometheus'

  - job_name: 'cart-service'
    static_configs:
      - targets: ['cart-service:8083']
    metrics_path: '/actuator/prometheus'

  - job_name: 'rabbitmq'
    static_configs:
      - targets: ['rabbitmq:15672']
```

### Grafana
```yaml
grafana:
  image: grafana/grafana:latest
  container_name: grafana
  ports:
    - "${GRAFANA_PORT:-3001}:3000"
  environment:
    GF_SECURITY_ADMIN_USER: admin
    GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD:-admin}
    GF_INSTALL_PLUGINS: grafana-piechart-panel
  volumes:
    - grafana-data:/var/lib/grafana
  depends_on:
    - prometheus
  networks:
    - sofkify-network
  restart: unless-stopped
```

**Acceso**:
```
URL: http://localhost:3001
Usuario: admin
Contraseña: admin (o la configurada en .env)
```

---

## 💾 Agregar Cache (Redis)

Para cachear respuestas y mejorar performance:

```yaml
redis:
  image: redis:7-alpine
  container_name: redis
  ports:
    - "${REDIS_PORT:-6379}:6379"
  volumes:
    - redis-data:/data
  command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD:-redis}
  networks:
    - sofkify-network
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
  restart: unless-stopped
```

**Configuración en servicios**:
```yaml
product-service:
  # ... config anterior ...
  environment:
    SPRING_REDIS_HOST: ${REDIS_HOST:-redis}
    SPRING_REDIS_PORT: ${REDIS_PORT:-6379}
    SPRING_REDIS_PASSWORD: ${REDIS_PASSWORD:-redis}
  depends_on:
    - redis
    - postgres-products
    - rabbitmq
```

**Aggiungi a `.env.example`**:
```env
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=redis
```

---

## 📋 Agregar Logging (ELK Stack)

Para análisis centralizado de logs:

### Elasticsearch
```yaml
elasticsearch:
  image: docker.elastic.co/elasticsearch/elasticsearch:8.0.0
  container_name: elasticsearch
  environment:
    - discovery.type=single-node
    - xpack.security.enabled=false
    - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
  ports:
    - "${ELASTICSEARCH_PORT:-9200}:9200"
  volumes:
    - elasticsearch-data:/usr/share/elasticsearch/data
  networks:
    - sofkify-network
  healthcheck:
    test: ["CMD-SHELL", "curl -s http://localhost:9200/_cluster/health | grep -q 'yellow\\|green'"]
    interval: 10s
    timeout: 5s
    retries: 5
  restart: unless-stopped
```

### Kibana
```yaml
kibana:
  image: docker.elastic.co/kibana/kibana:8.0.0
  container_name: kibana
  ports:
    - "${KIBANA_PORT:-5601}:5601"
  environment:
    ELASTICSEARCH_HOSTS: http://elasticsearch:9200
  depends_on:
    - elasticsearch
  networks:
    - sofkify-network
  restart: unless-stopped
```

### Filebeat (en cada servicio)
Adicionar a cada microservicio en docker-compose:

```yaml
filebeat:
  image: docker.elastic.co/beats/filebeat:8.0.0
  user: root
  container_name: filebeat
  command: filebeat -e -strict.perms=false
  volumes:
    - ./filebeat.yml:/usr/share/filebeat/filebeat.yml:ro
    - /var/lib/docker/containers:/var/lib/docker/containers:ro
  depends_on:
    - elasticsearch
  networks:
    - sofkify-network
  restart: unless-stopped
```

**filebeat.yml**:
```yaml
filebeat.inputs:
- type: container
  paths:
    - '/var/lib/docker/containers/*/*.log'

processors:
  - add_docker_metadata: ~

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
```

**Acceso**:
```
Elasticsearch: http://localhost:9200
Kibana: http://localhost:5601
```

---

## 🔄 Escalabilidad Horizontal

### Replicar un Servicio (Docker Swarm)

```bash
# Inicializar Swarm
docker swarm init

# Crear stack desde compose
docker stack deploy -c docker-compose.yml sofkify

# Escalar un servicio a 3 replicas
docker service update --replicas 3 sofkify_product-service

# Ver replicas
docker service ls
docker service ps sofkify_product-service
```

### Replicar con Load Balancing (Nginx)

```yaml
product-service-1:
  # ... config ...
  container_name: product-service-1
  ports:
    - "8091:8081"

product-service-2:
  # ... config ...
  container_name: product-service-2
  ports:
    - "8092:8081"

product-service-lb:
  image: nginx:alpine
  ports:
    - "8081:80"
  volumes:
    - ./nginx-lb.conf:/etc/nginx/nginx.conf:ro
  depends_on:
    - product-service-1
    - product-service-2
```

**nginx-lb.conf**:
```nginx
upstream product-service-backend {
  server product-service-1:8081 weight=1;
  server product-service-2:8081 weight=1;
}

server {
  listen 80;
  location / {
    proxy_pass http://product-service-backend;
    proxy_set_header Host $host;
  }
}
```

---

## 📈 Métricas y Performance

### Spring Boot Actuator Endpoints

Habilitar en `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,threaddump,heapdump
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### Endpoints disponibles:
- `/actuator/health` - Estado del servicio
- `/actuator/metrics` - Métricas disponibles
- `/actuator/prometheus` - Métricas para Prometheus
- `/actuator/info` - Info de la aplicación
- `/actuator/threaddump` - Dump de threads
- `/actuator/heapdump` - Dump de memoria

### Monitoreo de RabbitMQ

Acceder a `http://localhost:15672`:
1. **Queues**: Ver mensajes pendientes
2. **Connections**: Ver clientes conectados
3. **Channels**: Ver canales activos
4. **Admin > Users**: Gestionar usuarios

---

## 🎯 Checklist de Extensión

Cuando agregues un nuevo servicio/componente:

- [ ] Crear Dockerfile si es contenedor custom
- [ ] Agregar servicio a docker-compose.yml
- [ ] Agregar volúmenes si es necesario
- [ ] Agregar al archivo .env.example
- [ ] Configurar healthcheck si aplica
- [ ] Agregar dependencias necesarias
- [ ] Agregar a la red sofkify-network
- [ ] Documentar puertos y variables
- [ ] Testear levantamiento: `docker-compose up -d <servicio>`
- [ ] Verificar logs: `docker-compose logs <servicio>`
- [ ] Actualizar documentación (DOCKER.md, DOCKER_ARCHITECTURE.md)

---

## 📚 Recursos Adicionales

- [Docker Compose Best Practices](https://docs.docker.com/compose/compose-file/)
- [nginx Proxy Configuration](https://nginx.org/en/docs/)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Prometheus Scrape Config](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [ELK Stack Guide](https://www.elastic.co/what-is/elk-stack)

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Mantenido por**: Equipo Sofkify Backend
