# Sofkify Backend (Sofkify_BE)

Backend de microservicios para Sofkify. Este repositorio se integra con el frontend ubicado en:

- Repo FE: `https://github.com/nico-salsa/sofkify-fe.git`
- Path local FE esperado: `C:\Sofka_U_Semana_2\sofkify-fe`

## Requisitos

- Docker Desktop + Docker Compose
- Git

## Estructura de ejecucion local

- `docker-compose.yml`: stack backend (PostgreSQL, RabbitMQ, user/product/cart/order)
- `docker-compose.integration.yml`: agrega frontend (desde `../sofkify-fe`) al mismo stack

## Inicio rapido (backend + frontend)

1. En backend, crear `.env` desde plantilla:

```powershell
cd C:\Sofka_U_Semana_2\Sofkify_BE
Copy-Item .env.example .env
```

2. En frontend, crear `.env` desde plantilla:

```powershell
cd C:\Sofka_U_Semana_2\sofkify-fe
Copy-Item .env.example .env
```

3. Levantar stack integrado:

```powershell
cd C:\Sofka_U_Semana_2\Sofkify_BE
docker compose -f docker-compose.yml -f docker-compose.integration.yml up -d --build
```

4. Ver estado:

```powershell
docker compose -f docker-compose.yml -f docker-compose.integration.yml ps
```

## URLs locales

- Frontend: `http://localhost:5173`
- User service: `http://localhost:8080`
- Product service: `http://localhost:8081`
- Order service: `http://localhost:8082`
- Cart service: `http://localhost:8083`
- RabbitMQ UI: `http://localhost:15672` (`guest/guest`)

## Verificacion rapida de APIs

```powershell
# Productos
curl http://localhost:8081/api/products

# Health de conectividad basica (respuesta HTTP)
curl http://localhost:8080/api/users
curl http://localhost:8083/api/carts
curl http://localhost:8082/api/orders
```

## Verificacion de requests HTTP en consola

Ver guia completa: [REQUEST_VERIFICATION.md](REQUEST_VERIFICATION.md)

Comando base para seguir logs:

```powershell
docker compose -f docker-compose.yml -f docker-compose.integration.yml logs -f user-service product-service cart-service order-service frontend
```

## Calidad y pruebas

- Plan y riesgos: [TEST_PLAN.md](TEST_PLAN.md)
- Matriz Gherkin: [docs_IA/matriz_pruebas_gherkin.csv](docs_IA/matriz_pruebas_gherkin.csv)

## Detener stack

```powershell
docker compose -f docker-compose.yml -f docker-compose.integration.yml down
```

Para limpiar tambien volumenes:

```powershell
docker compose -f docker-compose.yml -f docker-compose.integration.yml down -v
```
