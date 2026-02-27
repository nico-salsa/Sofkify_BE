# 🔧 TROUBLESHOOTING - Pipeline CI/CD

**Fecha:** 26 de febrero de 2026  
**Objetivo:** Diagnosticar y resolver problemas con los pipelines

---

## ❓ PROBLEMA: "No me sale" - ¿Qué verificar?

### 1️⃣ ¿El workflow aparece en GitHub Actions?

**En Backend (https://github.com/nico-salsa/Sofkify_BE/actions):**
- [ ] ¿Ves "CI Pipeline Backend" alguna vez?
- [ ] ¿O no aparece ningún workflow?

**En Frontend (https://github.com/nico-salsa/sofkify-fe/actions):**
- [ ] ¿Ves "CI Pipeline Frontend"?

---

### 2️⃣ Si NO aparece el workflow:

**Posibles causas:**

| Causa | Síntoma | Solución |
|-------|---------|----------|
| Archivo YAML en rama equivocada | No ves nada en Actions | Verifica: `git log --oneline -5` ¿Estás en `fix/pipeline`? |
| Archivo YAML con errores | Ves warning en GitHub | Revisa errores de indentación |
| Los archivos no se subieron | NA | Ejecuta: `git push origin fix/pipeline` |
| Rama no existe en GitHub | NA | Ejecuta: `git remote -v` para confirmar origen |

**Verifica primero:**

```powershell
# En c:\Sofka_U_Semana_2\Sofkify_BE
git branch -a
# Deberías ver:
#   * fix/pipeline (rama actual)
#   remotes/origin/fix/pipeline (en GitHub)
#   remotes/origin/main
#   remotes/origin/develop
```

---

### 3️⃣ Si el workflow APARECE pero está en ❌ (ROJO):

**Click en el workflow y busca:**

1. **¿Qué job falló?**
   - [ ] Test cart-service
   - [ ] Test order-service
   - [ ] Test product-service
   - [ ] Test user-service
   - [ ] Tests & Coverage (frontend)

2. **Haz click en el job fallido**

3. **Busca el step en ROJO:**
   - [ ] "Checkout code"
   - [ ] "Set up JDK"
   - [ ] "Run tests"
   - [ ] "Generate coverage report"
   - [ ] "Verify test coverage"
   - [ ] "Install dependencies"
   - [ ] "Run linting"

4. **Lee el error message completo:**
   - Copy del error
   - Pégalo aquí para que lo traduzca

---

## 🔍 ERRORES COMUNES Y SOLUCIONES

### Error 1: `chmod: command not found`

**En Windows PowerShell esto puede fallar.** 

**Solución:** Cambiar comando en workflow:

```yaml
# Cambiar esto:
- run: chmod +x ${{ matrix.service }}/gradlew

# Por esto:
- run: icacls "${{ matrix.service }}/gradlew" /grant Everyone:F
```

---

### Error 2: `No such file or directory: .../build/reports/jacoco/test/jacocoTestReport.xml`

**Causa:** No hay tests, así que JaCoCo no genera reporte.

**Solución:** Es NORMAL en esta etapa. El workflow debería continuar (no fallar).

---

### Error 3: `Cannot find module vitest`

**Causa:** npm install no fue exitoso.

**Solución:**
- Verifica que `package.json` esté bien formado
- Intenta localmente: `npm ci`

---

### Error 4: `ESLint errors`

**Causa:** Hay errores de sintaxis JavaScript/TypeScript.

**Solución:**
```bash
npm run lint -- --fix
git add .
git commit -m "fix: linting errors"
git push
```

---

## 📋 CHECKLIST DE VERIFICACIÓN

### ✅ Antes de cualquier otra cosa:

- [ ] ¿Estoy en la rama `fix/pipeline`?
  ```
  git status → debe decir "On branch fix/pipeline"
  ```

- [ ] ¿Los archivos están en GitHub?
  ```
  https://github.com/nico-salsa/Sofkify_BE/blob/fix/pipeline/.github/workflows/ci.yml
  https://github.com/nico-salsa/sofkify-fe/blob/fix/pipeline/.github/workflows/ci.yml
  ```

- [ ] ¿El YAML tiene formato correcto?
  - Indentación con espacios (NO tabs)
  - Sin caracteres especiales extraños

- [ ] ¿Los archivos de test existen?
  ```
  ✅ c:\Sofka_U_Semana_2\Sofkify_BE\cart-service\src\test\java\com\sofkify\cartservice\PipelineValidationTest.java
  ✅ c:\Sofka_U_Semana_2\sofkify-fe\src\__tests__\pipeline.test.ts
  ```

---

## 🆘 CÓMO REPORTAR EL PROBLEMA

Si aún no sale, dame:

1. **Captura de GitHub Actions** (si existe el workflow)
2. **El error EXACTO** que ves (si hay)
3. **El comando que ejecutaste** antes de fallar
4. **La rama actual** donde estás:
   ```
   git status
   git remote -v
   git log --oneline -1
   ```

---

## 🔗 URLs IMPORTANTES

- **Backend Actions:**
  https://github.com/nico-salsa/Sofkify_BE/actions

- **Frontend Actions:**
  https://github.com/nico-salsa/sofkify-fe/actions

- **Backend CI Workflow:**
  https://github.com/nico-salsa/Sofkify_BE/blob/fix/pipeline/.github/workflows/ci.yml

- **Frontend CI Workflow:**
  https://github.com/nico-salsa/sofkify-fe/blob/fix/pipeline/.github/workflows/ci.yml

---

**Última actualización:** 26 de febrero de 2026
