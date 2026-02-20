# Docker Compose Sofkify - Guía Maestro

**Versión**: 1.0  
**Última actualización**: 20 de Febrero 2026  
**Estado**: Fase 1 ✅ | Fase 2 📋  

---

## 🎯 Visión General

Este documento es el **punto de entrada único** para orquestar Sofkify Backend completo con docker-compose. El proyecto se divide en **2 fases**:

- **Fase 1**: Orquestación del backend (4 microservicios + infraestructura) ✅ **COMPLETADA**
- **Fase 2**: Integración del frontend (espera a que Raúl termina) 📋 **EN ESPERA**

---

## 📚 Documentación Rápida

### Para Empezar Ahora (Fase 1)
1. Lee: [FASE_1_RESUMEN.md](FASE_1_RESUMEN.md) (5 minutos)
2. Ejecuta: `docker-compose up -d --build` (2 minutos)
3. Verifica: `docker-compose ps` (30 segundos)

### Para Operar el Stack
- **Guía rápida**: [DOCKER.md](DOCKER.md)
- **Comandos útiles**: `./docker-helper.sh help`
- **Problemas**: Consulta [docs_IA/DOCKER_TROUBLESHOOTING.md](docs_IA/DOCKER_TROUBLESHOOTING.md)

### Para Entender la Arquitectura
- **Detalles técnicos**: [docs_IA/DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md)
- **Diagramas visuales**: [docs_IA/DOCKER_VISUAL_FLOWS.md](docs_IA/DOCKER_VISUAL_FLOWS.md)

### Cuando el Frontend Esté Listo
- **Plan Fase 2**: [FASE_2_PLAN.md](FASE_2_PLAN.md)
- **Guía de integración**: Se generará al iniciar Fase 2

---

## 🚀 Inicio Rápido (30 segundos)

```bash
# 1. Copiar configuración
cp .env.example .env

# 2. Levantar stack
docker-compose up -d --build

# 3. Verificar
docker-compose ps
```

✅ **Backend operativo en ~60 segundos**

---

## 📊 Estructura del Proyecto

```
Sofkify_BE/
├── 📋 Raíz (Docker)
│   ├── docker-compose.yml           # ⭐ Orquestación principal
│   ├── .env.example                 # Variables de entorno template
│   ├── .dockerignore                # Exclusiones de build
│   ├── docker-helper.sh             # ⭐ Script de ayuda
│   ├── init-db.sql                  # Inicialización BDs
│   │
│   └── 📖 Documentación
│       ├── FASE_1_RESUMEN.md        # ← Empieza aquí
│       ├── FASE_2_PLAN.md           # Plan para cuando frontend esté listo
│       ├── DOCKER.md                # Guía rápida
│       └── START_HERE.md            # Punto entrada alternativo
│
├── 📁 Microservicios (Código)
│   ├── user-service/
│   ├── product-service/
│   ├── order-service/
│   └── cart-service/
│
├── 📁 Documentación Técnica
│   └── docs_IA/
│       ├── DOCKER_ARCHITECTURE.md       # Arquitectura técnica
│       ├── DOCKER_QUICK_REFERENCE.md    # Referencia rápida
│       ├── DOCKER_EXTENSIBILITY.md      # Cómo extender
│       ├── DOCKER_TROUBLESHOOTING.md    # Solución problemas
│       ├── DOCKER_VISUAL_FLOWS.md       # Diagramas
│       └── DOCKER_INDEX.md              # Índice maestro
│
├── 📁 Frontend (Será agregado en Fase 2)
│   └── frontend/                   # ← Aquí cuando Raúl termine
│
└── 📝 Otros
    ├── HANDOVER_REPORT.md
    ├── README.md
    └── ...
```

---

## 🔄 Flujo de Fases

```
Fase 1: BACKEND ✅ COMPLETADA
├─ Agente 1: Diseño arquitectónico ✅
├─ Agente 2: Generación de archivos ✅
├─ Agente 3: Validación y testing ✅
└─ Resultado: docker-compose.yml listo para usar

        ↓
        
Fase 2: FRONTEND 📋 EN ESPERA
├─ Prerequisito: Raúl termina frontend
├─ Agente 1: Integración frontend
├─ Agente 2: Validación fullstack
├─ Agente 3: Generación de artefactos
└─ Resultado: Stack fullstack orquestado

        ↓
        
Producción: DEPLOYABLE ⏭️ PLANNIFICADO
├─ Agregar secrets management
├─ Implementar CI/CD
├─ Configurar monitoreo
└─ Deploy a k8s o cloud
```

---

## 🎯 Fase 1: Backend (Actual)

### Lo Que Se Creó

| Componente | Detalles |
|-----------|----------|
| **postgres-users** | BD para user-service (puerto 5432) |
| **postgres-products** | BD para product-service (puerto 5433) |
| **postgres-carts** | BD para cart-service (puerto 5434) |
| **postgres-orders** | BD para order-service (puerto 5435) |
| **rabbitmq** | Message broker (AMQP: 5672, UI: 15672) |
| **user-service** | Autenticación (puerto 8080) |
| **product-service** | Catálogo (puerto 8081) |
| **order-service** | Órdenes (puerto 8082) |
| **cart-service** | Carritos (puerto 8083) |

### Características Clave
- ✅ Red privada `sofkify-network` para seguridad
- ✅ Volúmenes persistentes para BDs
- ✅ Health checks para BD y RabbitMQ
- ✅ Dependencias ordenadas (BDs → RabbitMQ → Servicios)
- ✅ Variables de entorno configurables

### Cómo Operar
```bash
# Iniciar
docker-compose up -d --build

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs user-service

# Detener
docker-compose down

# Ver todos los comandos disponibles
./docker-helper.sh help
```

---

## 📋 Fase 2: Frontend (Cuando esté listo)

### Lo Que se Agregará

| Componente | Detalles |
|-----------|----------|
| **frontend** | React/Vue/Angular (puerto 3000) |
| **Variables** | REACT_APP_API_BASE_URL, etc. |
| **Network** | Mismo sofkify-network que backend |
| **Documentación** | FRONTEND.md, FASE_2_RESUMEN.md |

### Cómo Proceder
1. Esperar a que Raúl confirme frontend listo
2. Revisar [FASE_2_PLAN.md](FASE_2_PLAN.md)
3. Ejecutar 3 agentes de Fase 2
4. Levantar: `docker-compose up -d --build`

---

## 🛠️ Comandos Principales

### Operación Diaria

```bash
# Iniciar (primera vez)
docker-compose up -d --build

# Iniciar (sin rebuild)
docker-compose up -d

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f              # todos
docker-compose logs user-service    # específico
docker-compose logs --tail=50       # últimas 50 líneas

# Detener
docker-compose down                 # sin eliminar volúmenes
docker-compose down -v              # eliminar volúmenes

# Reiniciar un servicio
docker-compose restart user-service

# Ejecutar comando en contenedor
docker-compose exec user-service sh
```

### Con Helper Script

```bash
./docker-helper.sh help              # Ver todos los comandos
./docker-helper.sh up                # Iniciar
./docker-helper.sh down              # Detener
./docker-helper.sh logs user-service # Ver logs
./docker-helper.sh restart product-service
./docker-helper.sh test-connectivity # Verificar conectividad
./docker-helper.sh clean             # Limpiar todo
```

---

## 📍 Acceso a Servicios

Después de `docker-compose up -d`:

### APIs REST

| Servicio | URL | Función |
|----------|-----|---------|
| User | http://localhost:8080/api/users | Autenticación |
| Product | http://localhost:8081/api/products | Catálogo |
| Cart | http://localhost:8083/api/carts | Carritos |
| Order | http://localhost:8082/api/orders | Órdenes |

### Herramientas

| Herramienta | URL | Credenciales |
|------------|-----|-------------|
| RabbitMQ UI | http://localhost:15672 | guest / guest |

### Bases de Datos

```bash
# Conectar con psql
psql -h localhost -p 5432 -U sofkify -d sofkify_users

# O desde Docker
docker-compose exec postgres-users psql -U sofkify -d sofkify_users
```

---

## ⚙️ Variables de Entorno

Configurables en `.env`:

```env
# Credenciales BD (compartidas)
DB_USERNAME=sofkify
DB_PASSWORD=sofkify_secure_pass

# Hosts de BDs (nombre del contenedor)
DB_HOST_USERS=postgres-users
DB_HOST_PRODUCTS=postgres-products
DB_HOST_CARTS=postgres-carts
DB_HOST_ORDERS=postgres-orders

# RabbitMQ
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# Fase 2: Variables de frontend (se agregarán)
# REACT_APP_API_BASE_URL=http://user-service:8080
# ...
```

---

## 🐛 Si Algo No Funciona

### Checklist Rápido

```bash
# 1. ¿Docker y docker-compose están instalados?
docker --version
docker-compose --version

# 2. ¿.env existe?
ls -la .env

# 3. ¿Sintaxis docker-compose válida?
docker-compose config > /dev/null && echo "✅ Válido" || echo "❌ Error"

# 4. ¿Puertos libres?
lsof -i :5432    # PostgreSQL
lsof -i :5672    # RabbitMQ
lsof -i :8080    # User Service

# 5. ¿Contenedores levantando?
docker-compose up      # (Ctrl+C para ver los logs sin detach)
```

### Problemas Comunes

| Problema | Solución |
|----------|----------|
| `Port already in use` | Cambiar puertos en .env o docker-compose.yml |
| `psql: could not translate host name` | Esperar a que BD esté healthy (ver logs) |
| `docker-compose not found` | Instalar docker-compose o usar `docker compose` |
| `Permission denied` | En Linux: `chmod +x docker-helper.sh` |
| `Connection refused` | Verificar que servicios estén UP con `docker-compose ps` |

**Más problemas**: Consulta [docs_IA/DOCKER_TROUBLESHOOTING.md](docs_IA/DOCKER_TROUBLESHOOTING.md)

---

## 📊 Estados del Stack

### Fase 1: Backend Operativo ✅

```
docker-compose ps

NAME                  STATUS
postgres-users        Up (healthy)
postgres-products     Up (healthy)
postgres-carts        Up (healthy)
postgres-orders       Up (healthy)
rabbitmq              Up (healthy)
user-service          Up
product-service       Up
order-service         Up
cart-service          Up
```

### Fase 2: Frontend Agregado (Próximo)

```
docker-compose ps

[Los anteriores] +
frontend              Up
```

---

## 📚 Documentación Disponible

### Para Diferentes Públicos

| Perfil | Documentar | Tiempo |
|--------|----------|--------|
| **Usuario** | FASE_1_RESUMEN.md →  DOCKER.md | 15 min |
| **DevOps/Operador** | DOCKER_ARCHITECTURE.md + TROUBLESHOOTING | 1 h |
| **Arquitecto** | DOCKER_EXTENSIBILITY.md | 2 h |
| **Todos** | DOCKER_INDEX.md (índice maestro) | variable |

### Acceso Rápido

```
📄 FASE_1_RESUMEN.md              ← **EMPIEZA AQUÍ** después de clonar
📄 PHASE_2_PLAN.md                ← Cuando Raúl termina frontend
📄 DOCKER.md                        ← Guía operacional rápida
📄 docker-helper.sh help            ← Comandos disponibles
📂 docs_IA/                         ← Documentación técnica profunda
  ├── DOCKER_ARCHITECTURE.md
  ├── DOCKER_QUICK_REFERENCE.md
  ├── DOCKER_TROUBLESHOOTING.md
  ├── DOCKER_EXTENSIBILITY.md
  └── DOCKER_INDEX.md
```

---

## ✨ Resumen

| Item | Detalle |
|------|--------|
| **Fase 1** | ✅ Completada (3 agentes) |
| **Stack Backend** | 4 BDs + RabbitMQ + 4 Microservicios |
| **Tiempo Startup** | ~60 segundos |
| **Documentación** | 10+ documentos técnicos |
| **Helper Script** | 12+ comandos disponibles |
| **Fase 2** | 📋 Espera a que Frontend esté listo |

---

## 🚀 Próxima Acción

### Ahora (Fase 1)
```bash
# 1. Fase 1: Backend
cp .env.example .env
docker-compose up -d --build
docker-compose ps
# ✅ Backend operativo
```

### Cuando Frontend Esté Listo (Fase 2)
```bash
# 2. Fase 2: Agregador frontend
# - Revisar FASE_2_PLAN.md
# - Ejecutar 3 agentes
# - docker-compose up -d --build
# ✅ Fullstack operativo
```

---

## 📞 Contacto / Soporte

- **Backend Issues**: Revisar [DOCKER_TROUBLESHOOTING.md](docs_IA/DOCKER_TROUBLESHOOTING.md)
- **Frontend Integration**: Revisar [FASE_2_PLAN.md](FASE_2_PLAN.md) cuando Raúl termina
- **Architecture Questions**: Leer [DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md)

---

**Documento Principal**  
**Versión**: 1.0  
**Última Actualización**: 20 de Febrero 2026  
**Mantenido por**: Equipo Sofkify Backend  

🚀 **¡El proyecto está listo para escalar!**

