# Fase 2: Integración Frontend con Docker Compose

**Estado**: 📋 PLANIFICADO  
**Prerequisito**: Fase 1 completada (Agentes backend)  
**Inicio**: Cuando frontend de Raúl esté listo  

---

## 🎯 Objetivo Fase 2

Integrar el frontend (React/Vue/Angular, puerto 3000) al docker-compose existente, manteniendo:
- ✅ Orquestación del backend completada en Fase 1
- ✅ Redes internas y comunicación entre servicios
- ✅ Variables de entorno adicionales para frontend
- ✅ Health checks y dependencias

---

## 📋 Estructura de Agentes para Fase 2

La Fase 2 seguirá el mismo patrón que Fase 1: **3 agentes máximo**

### Agente 1: Integración Frontend
**Objetivo**: Actualizar docker-compose.yml con servicio frontend

**Prompt**:
```
Integra el frontend (puerto 3000) al docker-compose existente:

1. Agrega servicio frontend con:
   - Build context: ./frontend (o donde esté el proyecto)
   - Dockerfile: ./frontend/Dockerfile (multi-stage recomendado)
   - Puerto: 3000:3000
   - Variables de entorno:
     * REACT_APP_API_BASE_URL=http://user-service:8080
     * REACT_APP_PRODUCT_URL=http://product-service:8081
     * REACT_APP_CART_URL=http://cart-service:8083
     * REACT_APP_ORDER_URL=http://order-service:8082

2. Configuración de red:
   - Añade frontend a sofkify-network
   - Asegura que frontend puede resolver nombres de contenedores (user-service, etc.)

3. Dependencias:
   - Frontend depende de al menos un microservicio (ej: user-service)

4. Volúmenes (opcional):
   - Montaje para desarrollo (nodemon/hot-reload)

5. Documentación:
   - Instrucciones de cómo compilar frontend
   - Variables de entorno necesarias
   - Diferencias dev vs producción
```

---

### Agente 2: Validación Fullstack
**Objetivo**: Verificar que frontend y backend comunican correctamente

**Prompt**:
```
Valida la integración fullstack:

1. Estructura docker-compose.yml:
   - Servicio frontend agregado correctamente
   - Puertos únicos (3000 no conflictivo)
   - Red sofkify-network compartida

2. Conectividad frontend → backend:
   - Frontend está en la misma red que microservicios
   - Variables de entorno para URLs de backend
   - CORS configurado en microservicios (si es necesario)

3. Building y deployment:
   - Dockerfile del frontend multi-stage (opcional)
   - Imágenes se construyen sin errores
   - Ningún hardcoding de URLs (usar variables)

4. Testing:
   - El servicio frontend levanta sin errores
   - Puertos frontend (3000) y backend (8080-8083) accesibles
   - Docker network connectivity funciona

5. Seguridad:
   - Variables sensibles en .env (no en Dockerfile)
   - Frontend no expone puertos innecesarios

Reporta:
- ✅/❌ para cada validación
- Errores encontrados y sus soluciones
- Warnings sobre best practices
- Readiness para producción
```

---

### Agente 3: Generación de Artefactos Fase 2
**Objetivo**: Crear/actualizar archivos necesarios

**Prompt**:
```
Genera los artefactos necesarios para completar Fase 2:

1. Actualización docker-compose.yml:
   - Integración cleandown del servicio frontend
   - Variables de entorno adicionales para Fase 2

2. Actualización .env.example:
   - Nuevas variables para frontend
   - URLs de API (REACT_APP_API_BASE_URL, etc.)
   - Comentarios explicativos

3. docker-helper.sh actualizado:
   - Nuevos comandos para frontend (logs, restart, etc.)
   - Comando para levantar solo backend vs fullstack

4. FRONTEND.md:
   - Guía de setup para desarrolladores
   - Cómo construir imagen Docker
   - Variables de entorno

5. docker-compose.frontend.yml (opcional):
   - Alternativa sólo para development
   - Permite trabajar sin orquestar todo

Archivos a crear:
- Actualizado: docker-compose.yml
- Actualizado: .env.example
- Actualizado: docker-helper.sh
- Nuevo: FRONTEND.md
- Nuevo: FASE_2_RESUMEN.md
```

---

## 🔄 Flujo de Execution Fase 2

```
┌─────────────────────────────────────────────────────────────────┐
│ PREREQUISITO: Raúl confirma que frontend está listo             │
└──────────────────────┬──────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────────┐
│ AGENTE 1: Integración Frontend                                  │
│ - Actualiza docker-compose.yml                                  │
│ - Agrega servicio frontend (puerto 3000)                        │
│ - Configura variables de entorno y red                          │
└──────────────────────┬──────────────────────────────────────────┘
                       ↓
        ¿Docker-compose válido?
                       ↓ SÍ
┌─────────────────────────────────────────────────────────────────┐
│ AGENTE 2: Validación Fullstack                                  │
│ - Verifica conectividad frontend → backend                      │
│ - Valida puertos, redes, variables de entorno                   │
│ - Testing de build y deployment                                 │
└──────────────────────┬──────────────────────────────────────────┘
                       ↓
        ¿Validación exitosa?
                       ↓ SÍ
┌─────────────────────────────────────────────────────────────────┐
│ AGENTE 3: Generación de Artefactos Fase 2                       │
│ - Crea FRONTEND.md con guías                                    │
│ - Actualiza docker-helper.sh con comandos frontend              │
│ - Crea FASE_2_RESUMEN.md                                        │
└──────────────────────┬──────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────────┐
│ ✅ FASE 2 COMPLETADA                                            │
│ Stack fullstack listo: frontend + backend + BDs + message broker│
└─────────────────────────────────────────────────────────────────┘
```

---

## 📍 Archivos a Modificar en Fase 2

| Archivo | Acción | Detalles |
|---------|--------|---------|
| **docker-compose.yml** | Actualizar | Agregar servicio frontend |
| **.env.example** | Actualizar | Agregar variables REACT_APP_* |
| **docker-helper.sh** | Actualizar | Agregar comandos frontend |
| **README.md** | Actualizar | Agregar sección Frontend en setup |
| **FASE_2_RESUMEN.md** | Crear | Resumen de cambios realizados |
| **FRONTEND.md** | Crear | Guía de setup para frontend |
| **.dockerignore** | ¿Actualizar? | Si frontend tiene node_modules diferente |

---

## 🛠️ Configuraciones Necesarias Fase 2

### 1. Variables de Entorno para Frontend

```env
# Frontend URLs (comunicación con backend)
REACT_APP_API_BASE_URL=http://user-service:8080
REACT_APP_PRODUCT_URL=http://product-service:8081
REACT_APP_CART_URL=http://cart-service:8083
REACT_APP_ORDER_URL=http://order-service:8082

# CORS y Seguridad (si es necesario)
REACT_APP_ENABLE_CORS=true
REACT_APP_API_TIMEOUT=30000  # milisegundos

# Environment
NODE_ENV=development  # o production
REACT_APP_VERSION=1.0.0
```

### 2. Ejemplo de Dockerfile Frontend

```dockerfile
# Multi-stage build recomendado
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:18-alpine
WORKDIR /app
RUN npm install -g serve
COPY --from=builder /app/build ./build
EXPOSE 3000
CMD ["serve", "-s", "build", "-l", "3000"]
```

### 3. healthcheck para Frontend (opcional)

```yaml
frontend:
  # ...resto de config...
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:3000"]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 40s
```

---

## 📊 Checklist Previo a Fase 2

Antes de ejecutar Agentes de Fase 2, confirma:

- ✅ Fase 1 completada y backend funcional (`docker-compose ps`)
- ✅ Frontend Raúl "listo" (repositorio inicializado)
- ✅ Dockerfile del frontend existe
- ✅ package.json del frontend con "build" script
- ✅ Variables de entorno del frontend documentadas
- ✅ Puerto 3000 libre (no conflictivo)
- ✅ Ruta del frontend clara (./frontend, ./client, etc.)

---

## 🚀 Ejecución de Fase 2 (Cuando esté listo)

```bash
# 1. Confirmar que Raúl completó frontend
# 2. Ejecutar Agente 1
prompt_agente_1 > ./frontend-integration-output.md

# 3. Ejecutar Agente 2
prompt_agente_2 > ./frontend-validation-output.md

# 4. Ejecutar Agente 3
prompt_agente_3 > ./frontend-artifacts-output.md

# 5. Copiar nuevas variables de entorno
cp .env.example .env

# 6. Levantar stack fullstack
docker-compose up -d --build

# 7. Verificar
docker-compose ps
docker-compose logs frontend --tail=20
```

---

## 🔍 Validación Post-Fase 2

Una vez ejecutado `docker-compose up -d`:

```bash
# Acceder al frontend
curl http://localhost:3000

# Ver logs del frontend
./docker-helper.sh logs frontend

# Verificar que frontend puede resolver nombres del backend
docker-compose exec frontend ping -c 2 user-service
docker-compose exec frontend ping -c 2 product-service

# Test de conectividad fullstack
./docker-helper.sh test-connectivity
```

---

## 📈 Comparación: Fase 1 vs Fase 2

| Aspecto | Fase 1 | Fase 2 |
|---------|--------|--------|
| **Servicios** | Backend (4) + Infraestructura (2) | + Frontend (1) = 7 Servicios |
| **Agentes** | 3 | 3 |
| **Tiempo estimado** | ✅ Completado | ⏳ ~30 minutos por agente |
| **Archivos nuevos** | 4 | ~3 actualizados + 2 nuevos |
| **Complejidad** | Media | Media-Alta (CORS, networking) |
| **Riesgo** | Bajo | Bajo |

---

## ❓ FAQ Fase 2

**P: ¿Qué pasa si el frontend no puede conectar al backend?**  
R: Revisar variables REACT_APP_*, CORS en microservicios, y network connectivity con `docker-compose exec frontend ping user-service`

**P: ¿Puedo usar desarrollo local (npm start) sin Docker?**  
R: Sí, pero los microservicios deben estar en Docker. Configurar proxy en package.json (`"proxy": "http://localhost:8080"`)

**P: ¿Qué versión de Node usar para frontend?**  
R: Recomendado Node 18-20 LTS y Alpine para imágenes Docker

**P: ¿Cómo compilar frontend sin Docker?**  
R: `npm install && npm run build`, pero Fase 2 asume compilación en Docker

---

## 🎯 Próxima Acción

**Cuando Raúl confirme que el frontend está listo:**

1. ✔️ Notificar que vamos a ejecutar Fase 2
2. ✔️ Ejecutar los 3 agentes
3. ✔️ Integrar frontend al docker-compose
4. ✔️ Validar fullstack
5. ✔️ Enviar resumen de Fase 2

---

**Documento Fase 2**  
**Versión**: 1.0  
**Estado**: 📋 En espera de frontend  
**Próxima revisión**: Cuando frontend esté listo

