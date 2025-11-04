# 📋 Instrucciones para Ejecutar la API - Parcial 2

## ✅ PROBLEMA RESUELTO

**Error encontrado:** El puerto 8080 estaba siendo usado por otra instancia de la aplicación.

**Solución aplicada:** Se detuvo el proceso anterior y se inició la aplicación correctamente.

---

## 🚀 Cómo Ejecutar la API desde IntelliJ IDEA

### **Opción 1: Ejecutar la clase principal (RECOMENDADO)**

1. Abre IntelliJ IDEA
2. Navega a: `src/main/java/usco/edu/co/Parcial2Application.java`
3. Busca el método `main`
4. Haz clic en el **botón verde de play** (▶️) junto a la clase o método
5. Selecciona **"Run 'Parcial2Application.main()'"**

### **Opción 2: Usar Maven desde IntelliJ**

1. Abre la pestaña "Maven" en el lado derecho de IntelliJ
2. Navega a: `parcial2 > Plugins > spring-boot > spring-boot:run`
3. Haz doble clic en `spring-boot:run`

### **Opción 3: Usar la terminal integrada de IntelliJ**

1. Abre la terminal en IntelliJ (Alt + F12)
2. Ejecuta:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

### **Opción 4: Ejecutar desde línea de comandos**

```cmd
cd C:\Users\dasan\Downloads\parcial2
mvnw.cmd spring-boot:run
```

---

## 🔧 Solución a Errores Comunes

### ❌ Error: "Port 8080 was already in use"

**Causa:** Ya hay una instancia de la aplicación corriendo en el puerto 8080.

**Solución 1 - Encontrar y detener el proceso:**

```cmd
REM 1. Buscar qué proceso usa el puerto 8080
netstat -ano | findstr :8080

REM 2. Identificar el proceso (anota el PID de la última columna)
tasklist | findstr <PID>

REM 3. Detener el proceso
taskkill /F /PID <PID>
```

**Solución 2 - Usar otro puerto:**

Edita `src/main/resources/application.properties` y agrega:
```properties
server.port=8081
```

---

## 🌐 Acceder a la Aplicación

Una vez iniciada la aplicación, abre tu navegador en:

**URL:** http://localhost:8080

**Usuarios de prueba (revisar DataInitializer.java):**
- **Rector:** usuario/contraseña según tu configuración
- **Docente:** usuario/contraseña según tu configuración  
- **Estudiante:** usuario/contraseña según tu configuración

---

## ✔️ Verificar que la API está corriendo

**Desde línea de comandos:**
```cmd
netstat -ano | findstr :8080
```

Si ves resultados con estado "LISTENING", la aplicación está corriendo.

---

## 📦 Requisitos Previos

✅ **Java 21** - Verificar: `java -version`
✅ **PostgreSQL** corriendo en puerto 5432
✅ **Base de datos:** `asignacion_colegio`
✅ **Usuario PostgreSQL:** `postgres` / contraseña: `12345678`

---

## 🛠️ Scripts de Ayuda Creados

### `run-app.bat`
Script que verifica PostgreSQL y ejecuta la aplicación.

**Uso:**
```cmd
run-app.bat
```

### `stop-app.bat` (crear si necesitas)
Script para detener la aplicación fácilmente.

---

## 📝 Notas Importantes

1. **No confundas los tests con la ejecución normal:**
   - Cuando ejecutas tests (`Parcial2ApplicationTests`), la app inicia y se detiene automáticamente.
   - Para que la app quede corriendo, debes ejecutar `Parcial2Application` (sin "Tests").

2. **DevTools está habilitado:**
   - La aplicación se reinicia automáticamente cuando detecta cambios en el código.

3. **Puerto por defecto:** 8080
   - Asegúrate de que no esté siendo usado por otro proceso.

---

## 📞 Comandos Útiles

```cmd
REM Ver todos los procesos Java corriendo
tasklist | findstr java.exe

REM Ver puertos en uso
netstat -ano

REM Compilar sin ejecutar
mvnw.cmd clean package -DskipTests

REM Ejecutar el WAR compilado
java -jar target\parcial2-0.0.1-SNAPSHOT.war
```

---

**Última actualización:** 2025-11-04
**Estado actual:** ✅ Aplicación corriendo exitosamente en puerto 8080

