# 🔍 Análisis de Pipelines CI/CD - Sofkify Backend

**Fecha:** 26 de febrero de 2026  
**Rol:** DevOps Senior  
**Objetivo:** Documentar la estrategia de CI/CD para los servicios backend

---

## 📋 Contexto Identificado

### Características del Proyecto Backend
```
Tecnología:
  ✅ Build Tool: Gradle
  ✅ Lenguaje: Java (17-21)
  ✅ Framework: Spring Boot 4.0.2
  ✅ Test Framework: JUnit Platform (configurado)

Servicios:
  1. cart-service (Java 17)
  2. order-service (Java 17)
  3. product-service (Java 17)
  4. user-service (Java 21)

Testing:
  ❌ Tests NO implementados aún (se crean en Taller 2)
  ✅ Infraestructura de testing lista (dependencias incluidas en build.gradle)
```

### Dependencias de Testing Detectadas
Todos los servicios tienen configuradas estas dependencias:
```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
testImplementation 'org.springframework.boot:spring-boot-starter-validation-test'
testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## 🎯 Reglas de Negocio del Pipeline

### Trigger (Cuándo se ejecuta)
```yaml
Ramas permitidas: main, develop, feature/**
Eventos:
  - Push a cualquiera de las ramas
  - Pull Request hacia cualquiera de las ramas
```

### Requisitos de Éxito
```
✅ TODOS los tests deben pasar
✅ Cobertura de código mínima: 70%
❌ Fallar si alguna prueba no pasa
❌ Fallar si cobertura < 70%
```

---

## ⚙️ Estrategia de Ejecución

### Enfoque: Matriz Paralela
```
Justificación:
  - 4 servicios independientes
  - Sin dependencias entre tests
  - Mayor velocidad de ejecución
  - Mejor utilización de recursos CI/CD

Resultado:
  - Tiempo estimado: ~5-7 minutos (vs ~20 minutos en secuencial)
```

### Flujo del Pipeline
```
1. Checkout del código
   └─ Descargar repositorio en el runner

2. Configurar JDK (por servicio)
   ├─ cart-service: JDK 17
   ├─ order-service: JDK 17
   ├─ product-service: JDK 17
   └─ user-service: JDK 21

3. Cache de Gradle (en paralelo)
   └─ Reutilizar dependencias descargadas

4. Ejecutar Tests
   ├─ ./gradlew test (en cada servicio)
   └─ JUnit Platform ejecuta todos los tests encontrados

5. Generar Reporte de Cobertura
   ├─ JaCoCo (Java Code Coverage)
   ├─ XML report (para análisis)
   └─ HTML report (para visualización)

6. Validar Cobertura Mínima
   └─ Fallar si cobertura < 70%

7. Guardar Artefactos
   └─ Reportes de cobertura y tests
```

---

## 🔧 Configuraciones Necesarias

### 1. Extensión de build.gradle (Cobertura)

**Agregar a TODOS los servicios:**
```gradle
plugins {
    id 'jacoco'  // ← Plugin para recolectar cobertura
}

// Después de tasks.named('test')
jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}
```

**Por qué JaCoCo:**
- Estándar en Spring Boot
- Integración nativa con Gradle
- Genera reportes XML para CI/CD
- Permite definir límites (70%)

### 2. Validación de Cobertura

```gradle
jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = 'PACKAGE'
            includes = ['com.sofkify.*']
            
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.70  // ← 70% mínimo
            }
        }
    }
}
```

---

## 📊 Estructura del Pipeline YAML

```yaml
name: CI Pipeline Backend
on:
  push:
    branches: [main, develop, 'feature/**']
  pull_request:
    branches: [main, develop, 'feature/**']

jobs:
  test:
    strategy:
      matrix:
        service: [cart-service, order-service, product-service, user-service]
        jdk: [17 o 21 según servicio]
    steps:
      1. Checkout
      2. Setup JDK
      3. Cache Gradle
      4. Run Tests
      5. Generate Coverage
      6. Verify Coverage (fail si < 70%)
      7. Upload Reports
```

---

## 🚦 Estados del Pipeline

```
✅ SUCCESS
   └─ Todos los tests pasaron
   └─ Cobertura >= 70%
   └─ PR puede ser mergeado

⚠️ WARNING
   └─ Tests pasaron pero cobertura < 70%
   └─ Recomendación: Agregar más tests

❌ FAILURE
   └─ Al menos 1 test falló O cobertura < 70%
   └─ Bloquea la integración (merge bloqueado)
```

---

## 📈 Esperado en Producción

Una vez que se implementen los tests en Taller 2:

```
Por cada Push/PR:
├─ Backend: 
│  ├─ cart-service: 5-10 min (tests + cobertura)
│  ├─ order-service: 5-10 min (tests + cobertura)
│  ├─ product-service: 5-10 min (tests + cobertura)
│  └─ user-service: 5-10 min (tests + cobertura)
│     [Todos en paralelo = ~10 min total]
│
└─ Frontend:
   └─ sofkify-fe: 3-5 min (tests + cobertura)
```

---

## 🔐 Mecanismos de Control

### 1. Branch Protection Rules (GitHub)
**Configurar después:**
```
Require status checks to pass before merging:
  ✅ CI Pipeline Backend / test (cart-service, order-service, product-service, user-service)
  ✅ CI Pipeline Frontend / test
  ✅ Require branches to be up to date before merging
```

### 2. Reportes Visibles en GitHub
```
Pull Request View:
  ├─ ✅ All checks passed
  ├─ 📊 Coverage: 78% (expandible)
  └─ 📋 Test Results: 45 passed, 0 failed
```

---

## 📝 Archivos a Modificar/Crear

### Backend (Sofkify_BE)
```
Crear:
  .github/workflows/ci.yml  ← Pipeline principal

Modificar (agregar jacoco):
  cart-service/build.gradle
  order-service/build.gradle
  product-service/build.gradle
  user-service/build.gradle
```

### Frontend (sofkify-fe)
```
Crear:
  .github/workflows/ci.yml  ← Pipeline principal

Modificar (agregar script):
  package.json  ← Agregar script "test:coverage"
```

---

## ✅ Checklist de Implementación

- [ ] Paso 1: Agregar plugin JaCoCo a todos los build.gradle
- [ ] Paso 2: Crear workflow CI para Backend
- [ ] Paso 3: Crear workflow CI para Frontend
- [ ] Paso 4: Hacer un push con cambios para validar
- [ ] Paso 5: Verificar checks en GitHub
- [ ] Paso 6: Configurar branch protection rules en GitHub

---

## 🎓 Conceptos Clave para Junior

| Concepto | Explicación |
|----------|-------------|
| **Trigger** | El evento que inicia automáticamente el pipeline (push, PR) |
| **Matrix** | Estrategia para ejecutar el mismo job con diferentes variables (ej: 4 servicios) |
| **JaCoCo** | Herramienta que mide qué porcentaje de código está cubierto por tests |
| **Cobertura** | Métrica que indica: líneas ejecutadas / total de líneas |
| **Artefacto** | Fichero que GitHub Actions guarda para descargar después (reportes) |

---

**Próximos pasos:** Implementar configuración y crear los archivos CI/CD.
