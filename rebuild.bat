@echo off
echo ========================================
echo Recompilando el proyecto Maven
echo ========================================
echo.

cd /d "%~dp0"

echo Limpiando el proyecto...
call mvnw.cmd clean

if %errorlevel% neq 0 (
    echo ERROR: Fallo al limpiar el proyecto
    pause
    exit /b 1
)

echo.
echo Compilando el proyecto...
call mvnw.cmd compile

if %errorlevel% neq 0 (
    echo ERROR: Fallo al compilar el proyecto
    pause
    exit /b 1
)

echo.
echo ========================================
echo Compilacion exitosa
echo ========================================
echo.
echo Ahora puedes ejecutar la aplicacion desde IntelliJ:
echo 1. Abre: Parcial2Application.java
echo 2. Click derecho en el metodo main
echo 3. Selecciona "Run 'Parcial2Application.main()'"
echo.
pause

