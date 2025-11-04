@echo off
echo ========================================
echo Verificando PostgreSQL...
echo ========================================
netstat -an | findstr :5432
if %errorlevel% neq 0 (
    echo ERROR: PostgreSQL no esta corriendo en el puerto 5432
    echo Por favor inicia PostgreSQL antes de ejecutar la aplicacion
    pause
    exit /b 1
)

echo.
echo ========================================
echo PostgreSQL esta corriendo
echo Iniciando la aplicacion Spring Boot...
echo ========================================
echo.

cd /d "%~dp0"
java -jar target\parcial2-0.0.1-SNAPSHOT.war

pause

