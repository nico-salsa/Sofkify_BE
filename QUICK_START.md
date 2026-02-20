# 🚀 Quick Start - Sofkify Backend

> ⏱️ **3 minutos para un backend e-commerce completamente funcional**

---

## ✅ Requisitos Previos

- ✅ **Docker** instalado (`docker --version`)
- ✅ **docker-compose** instalado (`docker-compose --version`)
- ✅ **Git** instalado (para clonar)

---

## 🏃 Empezar en 3 Pasos

### **Paso 1: Preparar (1 minuto)**
```bash
# Clonar repositorio (si aún no está clonado)
git clone https://github.com/nico-salsa/Sofkify_BE.git
cd Sofkify_BE

# Copiar variables de entorno
cp .env.example .env
```

### **Paso 2: Levantar Stack (1 minuto)**
```bash
# Construir e iniciar todos los contenedores
docker-compose up -d --build

# Verifica que todo esté UP (espera ~60s)
docker-compose ps
```

### **Paso 3: Acceder (1 minuto)**
```
✅ Backend está listo en:
- User API:        http://localhost:8080
- Product API:     http://localhost:8081
- Cart API:        http://localhost:8083
- Order API:       http://localhost:8082
- RabbitMQ UI:     http://localhost:15672 (usuario: guest, password: guest)
```

---

## 📊 Qué Se Levanta

| Servicio | Puerto | Función |
|----------|--------|---------|
| 🗄️ **4 PostgreSQL** | 5432-5435 | Bases de datos independientes |
| 🐰 **RabbitMQ** | 5672 | Message broker |
| 🚀 **4 Microservicios** | 8080-8083 | APIs REST |

---

## 🛠️ Comandos Útiles

```bash
# Ver estado
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs user-service

# Detener todo
docker-compose down

# Reiniciar un servicio
docker-compose restart product-service

# Ver todos los comandos disponibles
./docker-helper.sh help
```

---

## 🎯 Próximas Acciones

### **Opción A: Solo explorar (5 minutos)**
```bash
# Test a Product API
curl http://localhost:8081/api/v1/products

# Ver interfaz RabbitMQ
open http://localhost:15672
# login: guest / guest
```

### **Opción B: Documentación completa (15 minutos)**
Ver: [DOCKER_MAESTRO.md](DOCKER_MAESTRO.md)

### **Opción C: Entender la arquitectura (30 minutos)**
Ver: [docs_IA/DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md)

---

## ❌ Si Algo Falla

### **Checklist de troubleshooting:**
```bash
# 1. ¿Docker está corriendo?
docker ps

# 2. ¿La configuración es válida?
docker-compose config > /dev/null && echo "✅ OK" || echo "❌ ERROR"

# 3. ¿Hay conflictos de puertos?
lsof -i :5432  # Si algo responde, hay conflicto

# 4. Ver logs detallados
docker-compose up  # Sin -d para ver los logs

# 5. Más ayuda
cat DOCKER_TROUBLESHOOTING.md
```

---

## 📚 Documentación

| Necesario | Documento | Tiempo |
|-----------|-----------|--------|
| **Ahora** | [DOCKER.md](DOCKER.md) | 5 min |
| **Pronto** | [DOCKER_MAESTRO.md](DOCKER_MAESTRO.md) | 15 min |
| **Arquitectura** | [docs_IA/DOCKER_ARCHITECTURE.md](docs_IA/DOCKER_ARCHITECTURE.md) | 30 min |
| **Todos** | [DOCKER_INDEX.md](docs_IA/DOCKER_INDEX.md) | variable |

---

## 🎉 ¡Listo!

Backend e-commerce **completamente funcional** en menos de 5 minutos.

---

**Siguiente paso**: Consulta [DOCKER_MAESTRO.md](DOCKER_MAESTRO.md) para documentación completa

