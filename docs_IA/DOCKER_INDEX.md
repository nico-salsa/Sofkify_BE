# Sofkify Backend - Docker Architecture Documentation Index

## 📑 Índice Completo de Documentación Docker

Bienvenido a la documentación de la arquitectura Docker para Sofkify Backend. Esta guía te ayudará a entender, ejecutar, extender y solucionar problemas de tu stack containerizado.

---

## 🚀 Inicio Rápido (Comienza Aquí)

Si es tu primer día, sigue este orden:

1. **[DOCKER.md](../DOCKER.md)** ← 📍 **EMPIEZA AQUÍ**
   - Guía de inicio rápido (5 minutos)
   - Cómo levantar el stack
   - Acceso a servicios
   - Comandos básicos

2. **[DOCKER_QUICK_REFERENCE.md](DOCKER_QUICK_REFERENCE.md)**
   - Resumen visual de la arquitectura
   - Tabla de puertos
   - Comandos más utilizados
   - Troubleshooting rápido

---

## 📚 Documentación Principal

### 1. Arquitectura y Diseño
📄 **[DOCKER_ARCHITECTURE.md](DOCKER_ARCHITECTURE.md)**
- Descripción detallada de todos los servicios
- Topología de la red
- Configuración de PostgreSQL
- Configuración de RabbitMQ
- Configuración de Microservicios
- Flujos de comunicación síncrona y asíncrona
- Health checks y dependencias
- Variables de entorno completas
- Volúmenes y persistencia

**📌 Cuándo leerlo**: Cuando necesitas entender cómo funciona todo el sistema

---

### 2. Cómo Usar Docker Compose
📄 **[DOCKER.md](../DOCKER.md)**
- Requisitos previos
- Levantamiento de servicios
- Configuración esencial
- Comandos útiles
- Monitoreo de contenedores
- Troubleshooting común
- Flujo de desarrollo

**📌 Cuándo leerlo**: Cuando quieres trabajar diariamente con docker-compose

---

### 3. Extensibilidad y Escalabilidad
📄 **[DOCKER_EXTENSIBILITY.md](DOCKER_EXTENSIBILITY.md)**
- Cómo agregar nuevos microservicios
- Agregar Frontend (React, Next.js)
- Agregar API Gateway (nginx)
- Agregar Monitoreo (Prometheus + Grafana)
- Agregar Cache (Redis)
- Agregar Logging (ELK Stack)
- Escalabilidad horizontal
- Replicación de servicios

**📌 Cuándo leerlo**: Cuando quieres extender la arquitectura o agregar nuevas capas

---

### 4. Troubleshooting y Diagnóstico
📄 **[DOCKER_TROUBLESHOOTING.md](DOCKER_TROUBLESHOOTING.md)**
- Diagnóstico rápido
- Problemas comunes y soluciones
- Debugging avanzado
- Monitoreo continuo
- Generación de dumps
- Escalada de problemas

**📌 Cuándo leerlo**: Cuando algo no funciona y necesitas solucionarlo rápido

---

## ⚙️ Archivos de Configuración

### docker-compose.yml
📄 **[docker-compose.yml](../docker-compose.yml)**
- Definición completa del stack
- Configuración de todos los servicios
- Networks y volúmenes
- Health checks
- Dependencias

**Secciones principales**:
- PostgreSQL (4 instancias)
- RabbitMQ (Message Broker)
- Microservicios Spring Boot (4 servicios)
- Sección comentada para Frontend

---

### .env.example
📄 [.env.example](../.env.example)
- Template de variables de entorno
- Valores por defecto
- Puertos configurables
- Credenciales

**Uso**:
```bash
cp .env.example .env
# Editar .env según necesidades
```

---

### docker-helper.sh
📄 [docker-helper.sh](../docker-helper.sh)
- Script de ayuda interactivo
- Comandos simplificados
- Acceso directo a BDs
- Gestión de logs

**Uso**:
```bash
./docker-helper.sh
# Ver opciones disponibles
```

---

## 📊 Referencia Visual

### Arquitectura en Diagrama
Ver [DOCKER_QUICK_REFERENCE.md - Arquitectura Visual](DOCKER_QUICK_REFERENCE.md#-arquitectura-visual-simplificada)

```
┌─ MICROSERVICIOS ──┐   ┌─ BASES DE DATOS ─┐   ┌─ MESSAGE BROKER ─┐
│ user-service      │   │ postgres-users    │   │                  │
│ product-service   │───│ postgres-products │───│ RabbitMQ         │
│ order-service     │   │ postgres-carts    │   │ (AMQP + UI)      │
│ cart-service      │   │ postgres-orders   │   │                  │
└───────────────────┘   └───────────────────┘   └──────────────────┘
```

---

## 🎯 Guías por Tarea

### Quiero...

#### 🚀 Levantar el Proyecto
→ [DOCKER.md - Inicio Rápido](../DOCKER.md#-inicio-rápido)

#### 🐛 Solucionar un Problema
→ [DOCKER_TROUBLESHOOTING.md - Problemas Comunes](DOCKER_TROUBLESHOOTING.md#-problemas-comunes)

#### 📦 Agregar un Nuevo Microservicio
→ [DOCKER_EXTENSIBILITY.md - Agregar Nuevos Microservicios](DOCKER_EXTENSIBILITY.md#-agregar-nuevos-microservicios)

#### 🎨 Agregar un Frontend
→ [DOCKER_EXTENSIBILITY.md - Agregar Frontend](DOCKER_EXTENSIBILITY.md#-agregar-frontend)

#### 📊 Configurar Monitoreo
→ [DOCKER_EXTENSIBILITY.md - Agregar Monitoreo](DOCKER_EXTENSIBILITY.md#-agregar-monitoreo-prometheus--grafana)

#### 💾 Hacer Backup de BD
→ [DOCKER.md - Base de Datos](../DOCKER.md#base-de-datos)

#### 🔍 Entender la Comunicación entre Servicios
→ [DOCKER_ARCHITECTURE.md - Flujo de Comunicación](DOCKER_ARCHITECTURE.md#-flujo-de-comunicación)

#### 🏥 Monitorear Servicios
→ [DOCKER.md - Monitoreo](../DOCKER.md#-monitoreo)

---

## 📈 Documentación por Tema

### Bases de Datos (PostgreSQL)
- [DOCKER_ARCHITECTURE.md - PostgreSQL Instances](DOCKER_ARCHITECTURE.md#1-postgresql-instances)
- [DOCKER_TROUBLESHOOTING.md - Base de Datos No Disponible](DOCKER_TROUBLESHOOTING.md#2️⃣-base-de-datos-no-disponible)
- [DOCKER.md - Conectar a bases de datos](../DOCKER.md#base-de-datos)

### Message Broker (RabbitMQ)
- [DOCKER_ARCHITECTURE.md - RabbitMQ](DOCKER_ARCHITECTURE.md#2-rabbitmq---message-broker)
- [DOCKER_TROUBLESHOOTING.md - RabbitMQ No Responde](DOCKER_TROUBLESHOOTING.md#3️⃣-rabbitmq-no-responde)
- [DOCKER.md - RabbitMQ Management UI](../DOCKER.md#acceso-a-servicios)

### Microservicios
- [DOCKER_ARCHITECTURE.md - Spring Boot Microservices](DOCKER_ARCHITECTURE.md#3-spring-boot-microservices)
- [DOCKER_TROUBLESHOOTING.md - Los Servicios No Inician](DOCKER_TROUBLESHOOTING.md#️-los-servicios-no-inician)
- [DOCKER_TROUBLESHOOTING.md - Microservicio No Conecta a RabbitMQ](DOCKER_TROUBLESHOOTING.md#4️⃣-microservicio-no-conecta-a-rabbitmq)

### Networking
- [DOCKER_ARCHITECTURE.md - Red Docker](DOCKER_ARCHITECTURE.md#-red-docker)
- [DOCKER_TROUBLESHOOTING.md - Inspeccionar Network](DOCKER_TROUBLESHOOTING.md#inspeccionar-network)

### Variables de Entorno
- [DOCKER_ARCHITECTURE.md - Variables de Entorno](DOCKER_ARCHITECTURE.md#-variables-de-entorno)
- [DOCKER.md - Configuración](../DOCKER.md#-configuración)

### Health Checks
- [DOCKER_ARCHITECTURE.md - Health Checks](DOCKER_ARCHITECTURE.md#-health-checks)
- [DOCKER_TROUBLESHOOTING.md - Verificación de Healthchecks](DOCKER_TROUBLESHOOTING.md#verificación-de-healthchecks)

### Volúmenes y Persistencia
- [DOCKER_ARCHITECTURE.md - Volúmenes Persistentes](DOCKER_ARCHITECTURE.md#-volúmenes-persistentes)
- [DOCKER_TROUBLESHOOTING.md - Disk Space Issues](DOCKER_TROUBLESHOOTING.md#6️⃣-disk-space-issues)

### Seguridad
- [DOCKER_ARCHITECTURE.md - Seguridad](DOCKER_ARCHITECTURE.md#-seguridad)

---

## 🔗 Tabla de Contenidos Rápida

| Documento | Secciones | Tiempo |
|-----------|-----------|--------|
| **DOCKER.md** | Inicio, comandos, troubleshooting, desarrollo | 15 min |
| **DOCKER_ARCHITECTURE.md** | Servicios, configuración, comunicación, health checks | 30 min |
| **DOCKER_QUICK_REFERENCE.md** | Diagrama, comandos, flujos, problemas comunes | 10 min |
| **DOCKER_EXTENSIBILITY.md** | Nuevos servicios, frontend, API gateway, monitoreo | 20 min |
| **DOCKER_TROUBLESHOOTING.md** | Diagnóstico, problemas, debugging, soluciones | 25 min |

---

## 🎓 Learning Path Recomendado

### Nivel 1: Usuario Básico (2 horas)
1. Leer [DOCKER.md](../DOCKER.md) completamente
2. Levantar el proyecto con `docker-compose up -d --build`
3. Acceder a cada servicio en navegador/curl
4. Ver logs básicos: `docker-compose logs -f`
5. Probar comando helper: `./docker-helper.sh`

**Resultado**: Puedes levantar y operar el sistema básico

---

### Nivel 2: Operador (4 horas)
1. Leer [DOCKER_ARCHITECTURE.md](DOCKER_ARCHITECTURE.md)
2. Entender flujos de comunicación
3. Leer [DOCKER_TROUBLESHOOTING.md - Problemas Comunes](DOCKER_TROUBLESHOOTING.md#-problemas-comunes) (primeros 3)
4. Practicar: fallar un servicio y arreglarlo
5. Practicar: conectar a cada BD y hacer queries

**Resultado**: Puedes operar, monitorear y solucionar problemas

---

### Nivel 3: Arquitecto (2 días)
1. Leer [DOCKER_ARCHITECTURE.md](DOCKER_ARCHITECTURE.md) completo
2. Leer [DOCKER_EXTENSIBILITY.md](DOCKER_EXTENSIBILITY.md) completo
3. Leer [DOCKER_TROUBLESHOOTING.md](DOCKER_TROUBLESHOOTING.md) completo
4. Practicar: agregar un nuevo servicio (ej: cache service)
5. Practicar: agregar frontend React/Next.js
6. Practicar: agregar monitoreo con Prometheus

**Resultado**: Puedes diseñar, extender y optimizar la arquitectura

---

## 📞 Preguntas Frecuentes

### ¿Cuál es la diferencia entre puerto local y puerto interno?
→ [DOCKER_QUICK_REFERENCE.md - Tabla de Puertos](DOCKER_QUICK_REFERENCE.md#-tabla-de-puertos-y-servicios)

### ¿Por qué mi servicio no termina de iniciar?
→ [DOCKER_TROUBLESHOOTING.md - Los Servicios No Inician](DOCKER_TROUBLESHOOTING.md#️-los-servicios-no-inician)

### ¿Cómo backing up mis datos si uso Docker Compose?
→ [DOCKER.md - Base de Datos - Backup](../DOCKER.md#base-de-datos)

### ¿Cómo puedo escalar un servicio a múltiples instancias?
→ [DOCKER_EXTENSIBILITY.md - Escalabilidad Horizontal](DOCKER_EXTENSIBILITY.md#-escalabilidad-horizontal)

### ¿Cómo agrego monitoreo a mi stack?
→ [DOCKER_EXTENSIBILITY.md - Agregar Monitoreo](DOCKER_EXTENSIBILITY.md#-agregar-monitoreo-prometheus--grafana)

### ¿Cómo cambio los puertos si ya están ocupados?
→ [DOCKER_TROUBLESHOOTING.md - Puerto en Conflicto](DOCKER_TROUBLESHOOTING.md#b-puerto-en-conflicto)

---

## 🛠️ Herramientas y Referencia

### Comandos de Docker Compose
```bash
docker-compose up -d --build    # Levantar
docker-compose down              # Detener (datos persisten)
docker-compose down -v           # Detener (elimina volúmenes)
docker-compose ps                # Ver estado
docker-compose logs -f <service> # Ver logs
```

### Acceso a Servicios
```
User Service:      http://localhost:8080
Product Service:   http://localhost:8081
Order Service:     http://localhost:8082
Cart Service:      http://localhost:8083
RabbitMQ UI:       http://localhost:15672 (guest/guest)
```

### Helper Script
```bash
./docker-helper.sh up          # Levantar
./docker-helper.sh logs        # Ver logs
./docker-helper.sh db-products # Conectar a BD
./docker-helper.sh status      # Ver estado
```

---

## 📋 Checklist para Nuevos Desarrolladores

- [ ] Leer [DOCKER.md](../DOCKER.md)
- [ ] Clonar repositorio
- [ ] Crear `.env` desde `.env.example`
- [ ] Ejecutar `docker-compose up -d --build`
- [ ] Verificar estado con `docker-compose ps`
- [ ] Probar acceso a servicios (curl o navegador)
- [ ] Leer [DOCKER_ARCHITECTURE.md](DOCKER_ARCHITECTURE.md) - sección de interés
- [ ] Agregar [DOCKER_QUICK_REFERENCE.md](DOCKER_QUICK_REFERENCE.md) a favoritos
- [ ] Bookmarkear [DOCKER_TROUBLESHOOTING.md](DOCKER_TROUBLESHOOTING.md)
- [ ] Practicar comandos del helper script

---

## 🔄 Versiones del Documento

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | Feb 2026 | Versión inicial completa |
| | | 4 BDs PostgreSQL |
| | | RabbitMQ configurado |
| | | 4 Microservicios Spring Boot |
| | | Health checks implementados |
| | | Documentación completa |
| | | Troubleshooting guide |
| | | Extensibility guide |

---

## 📝 Notas Importantes

### ⚠️ No Olvides
- Siempre hacer `docker-compose down` (sin `-v`) antes de clonar cambios
- Copiar `.env.example` a `.env` antes de levantar
- Esperar 30-60 segundos para que todos los healthchecks pasen
- Si cambias código, hacer `docker-compose up -d --build <service>`

### 🔐 Seguridad
- Las credenciales por defecto (`guest/guest`) son SOLO para desarrollo
- En producción, cambiar contraseñas en `.env`
- No commitear `.env` (agregado a `.gitignore`)

### 💾 Datos
- Los volúmenes persistentes se guardan en `/var/lib/docker/volumes`
- Hacer backup regularmente: `docker volume create backup && docker run --rm -v sofkify_postgres-users-data:/data -v backup:/backup alpine tar czf /backup/backup.tar.gz /data`

---

## 📚 Recursos Externos

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Docs](https://docs.docker.com/compose/)
- [PostgreSQL Docker](https://hub.docker.com/_/postgres)
- [RabbitMQ Docker](https://hub.docker.com/_/rabbitmq)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)

---

## 👥 Soporte y Contribución

Si encuentras errores en la documentación o tienes sugerencias:
1. Abre un issue en el repositorio
2. Incluye la sección afectada
3. Describe la mejora sugerida

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Mantenido por**: Equipo Sofkify Backend

