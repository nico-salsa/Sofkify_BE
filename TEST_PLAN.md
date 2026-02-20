# TEST PLAN - Sofkify Backend (Teorico)

## 1. Objetivo
Definir casos teoricos para nuevas funcionalidades en `Sofkify_BE`, aplicando ISTQB y tecnicas de diseno de pruebas.

## 2. Principio ISTQB priorizado
- Principal: 2 (pruebas basadas en riesgo).
- Complementarios: 4 (agrupacion de defectos), 6 (dependencia del contexto).

## 3. Tecnicas aplicadas
- Particion de equivalencia.
- Valores limite.
- Tabla de decision.

## 4. Caso base de referencia
HU: `Sofkify_BE/product-service/histories/HU-PRODUCT-01.md`

### 4.1 Tabla de decision
| Condicion | R1 | R2 | R3 | R4 |
|---|---|---|---|---|
| precio > 0 | Si | No | Si | Si |
| stock >= 0 | Si | Si | No | Si |
| categorias validas | Si | Si | Si | No |
| Resultado | 201 | 400 | 400 | 400 |

### 4.2 Escenarios Gherkin
```gherkin
Feature: Crear producto

  Scenario: Crear producto con datos validos
    Given administrador autenticado
    And precio > 0 y stock >= 0
    When envia POST /api/products
    Then responde 201
    And retorna estado ACTIVE

  Scenario: Rechazar precio invalido
    Given administrador autenticado
    And precio = 0
    When envia POST /api/products
    Then responde 400 por regla de negocio

  Scenario: Rechazar stock negativo
    Given administrador autenticado
    And stock = -1
    When envia POST /api/products
    Then responde 400 por regla de negocio
```

### 4.3 Casos teoricos
| ID | Tecnica | Datos | Esperado | Prioridad |
|---|---|---|---|---|
| BE-01 | Equivalencia valida | precio=100, stock=10 | 201 + ACTIVE | Alta |
| BE-02 | Equivalencia invalida | precio=0 | 400 | Alta |
| BE-03 | Equivalencia invalida | precio=-1 | 400 | Alta |
| BE-04 | Limite | stock=-1 | 400 | Alta |
| BE-05 | Limite | stock=0 | 201 | Alta |
| BE-06 | Limite | stock=1 | 201 | Media |

## 5. Riesgos prioritarios adicionales
- Publicacion/consumo de eventos `OrderCreated`.
- Errores transaccionales en decremento de stock.
- Integraciones REST entre servicios.

## 6. Criterio de salida
- 100% de criterios de aceptacion de la HU mapeados.
- Casos felices + error + borde definidos.
- Trazabilidad HU -> criterio -> caso.
