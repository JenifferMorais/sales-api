@echo off
setlocal enabledelayedexpansion

echo ========================================
echo   VERIFICACAO RAPIDA - DOCKER BUILD
echo ========================================
echo.

echo [1/4] Verificando JWT keys existem...
if exist "sales-api\src\main\resources\META-INF\resources\privateKey.pem" (
    echo   [OK] privateKey.pem encontrada
) else (
    echo   [ERRO] privateKey.pem NAO encontrada
    exit /b 1
)

if exist "sales-api\src\main\resources\META-INF\resources\publicKey.pem" (
    echo   [OK] publicKey.pem encontrada
) else (
    echo   [ERRO] publicKey.pem NAO encontrada
    exit /b 1
)

echo.
echo [2/4] Testando build Maven...
cd sales-api
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo   [ERRO] Build Maven falhou
    exit /b 1
)
echo   [OK] Build Maven sucesso
cd ..

echo.
echo [3/4] Testando build Docker backend...
docker build -f sales-api\docker\dockerfiles\Dockerfile.simple -t sales-api:verify sales-api >nul 2>&1
if errorlevel 1 (
    echo   [ERRO] Build Docker backend falhou
    exit /b 1
)
echo   [OK] Build Docker backend sucesso

echo.
echo [4/4] Verificando JWT keys na imagem...
docker run --rm sales-api:verify ls /deployments/privateKey.pem >nul 2>&1
if errorlevel 1 (
    echo   [ERRO] privateKey.pem NAO encontrada na imagem
    exit /b 1
)

docker run --rm sales-api:verify ls /deployments/publicKey.pem >nul 2>&1
if errorlevel 1 (
    echo   [ERRO] publicKey.pem NAO encontrada na imagem
    exit /b 1
)
echo   [OK] JWT keys presentes na imagem

echo.
echo ========================================
echo   VERIFICACAO COMPLETA!
echo ========================================
echo.
echo [OK] Todos os testes passaram
echo [OK] Pronto para CI/CD
echo.

docker rmi sales-api:verify >nul 2>&1
exit /b 0
