@echo off
echo ========================================
echo Verificando estado de la aplicacion
echo ========================================
echo.

REM Verificar PostgreSQL
echo [1/3] Verificando PostgreSQL (puerto 5432)...
netstat -an | findstr :5432 | findstr LISTENING >nul
if %errorlevel% equ 0 (
    echo   ✓ PostgreSQL esta corriendo
) else (
    echo   ✗ PostgreSQL NO esta corriendo
)

echo.

REM Verificar la aplicacion Spring Boot
echo [2/3] Verificando Spring Boot (puerto 8080)...
netstat -an | findstr :8080 | findstr LISTENING >nul
if %errorlevel% equ 0 (
    echo   ✓ Aplicacion Spring Boot esta corriendo
    echo.
    echo Detalles del proceso:
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        echo   PID: %%a
        tasklist | findstr %%a
    )
) else (
    echo   ✗ Aplicacion Spring Boot NO esta corriendo
)

echo.

REM Intentar acceder a la URL
echo [3/3] Probando conexion HTTP...
echo   URL: http://localhost:8080
curl -s -o nul -w "%%{http_code}" http://localhost:8080 >nul 2>&1
if %errorlevel% equ 0 (
    echo   ✓ La aplicacion responde correctamente
) else (
    echo   ℹ No se pudo verificar (curl no disponible o app no responde)
)

echo.
echo ========================================
echo Resumen
echo ========================================
echo Si ambos servicios estan corriendo, puedes acceder a:
echo   http://localhost:8080
echo.
pause

