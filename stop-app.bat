@echo off
echo ========================================
echo Deteniendo la aplicacion Spring Boot
echo ========================================
echo.

REM Buscar el proceso que usa el puerto 8080
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    set PID=%%a
    goto :found
)

echo No se encontro ninguna aplicacion corriendo en el puerto 8080
pause
exit /b 0

:found
echo Se encontro la aplicacion corriendo con PID: %PID%
echo.
tasklist | findstr %PID%
echo.
echo Deteniendo el proceso...
taskkill /F /PID %PID%

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Aplicacion detenida exitosamente
    echo ========================================
) else (
    echo.
    echo ERROR: No se pudo detener la aplicacion
)

echo.
pause

