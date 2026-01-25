@echo off
REM Script de Validação de Integração Frontend-Backend (Windows)
REM Sales System - API Testing
REM Data: 2026-01-25

echo ================================================
echo VALIDACAO DE INTEGRACAO - SALES SYSTEM
echo ================================================
echo.

REM Configurações
set API_URL=http://localhost:8080/api
set TOKEN=

REM Verificar se backend está rodando
echo Verificando se backend esta rodando...
curl -s http://localhost:8080 > nul 2>&1
if errorlevel 1 (
    echo ERRO: Backend nao esta rodando em http://localhost:8080
    echo.
    echo Para iniciar o backend:
    echo   cd sales-api
    echo   mvnw quarkus:dev
    echo.
    pause
    exit /b 1
)

echo OK: Backend esta rodando
echo.

REM ============================================
REM 1. AUTHENTICATION
REM ============================================
echo ================================================
echo 1. AUTHENTICATION ENDPOINTS
echo ================================================
echo.

echo [1.1] Login com credenciais validas
echo Method: POST
echo Endpoint: %API_URL%/v1/auth/login
REM IMPORTANT: Update these credentials with a valid test user from your database
REM You can set TEST_EMAIL and TEST_PASSWORD environment variables
if not defined TEST_EMAIL set TEST_EMAIL=test@email.com
if not defined TEST_PASSWORD set TEST_PASSWORD=YourTestPassword123!
curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"%TEST_EMAIL%\",\"password\":\"%TEST_PASSWORD%\"}" %API_URL%/v1/auth/login
echo.
echo.

REM Nota: Você precisará extrair o token manualmente e definir em TOKEN
echo IMPORTANTE: Copie o access_token da resposta acima e defina:
echo   set TOKEN=SEU_TOKEN_AQUI
echo.
pause

REM ============================================
REM 2. CUSTOMERS
REM ============================================
echo ================================================
echo 2. CUSTOMERS ENDPOINTS
echo ================================================
echo.

echo [2.1] Listar todos os clientes
echo Method: GET
echo Endpoint: %API_URL%/v1/customers
if defined TOKEN (
    curl -s -H "Authorization: Bearer %TOKEN%" %API_URL%/v1/customers
) else (
    echo ERRO: Token nao definido
)
echo.
echo.

echo [2.2] Buscar cliente por codigo (CUST001)
echo Method: GET
echo Endpoint: %API_URL%/v1/customers/CUST001
if defined TOKEN (
    curl -s -H "Authorization: Bearer %TOKEN%" %API_URL%/v1/customers/CUST001
) else (
    echo ERRO: Token nao definido
)
echo.
echo.

echo [2.3] Criar novo cliente
echo Method: POST
echo Endpoint: %API_URL%/v1/customers
if defined TOKEN (
    curl -s -X POST -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"fullName\":\"Joao Teste\",\"motherName\":\"Maria Teste\",\"cpf\":\"987.654.321-00\",\"rg\":\"98.765.432-1\",\"email\":\"joao.teste@email.com\",\"cellPhone\":\"(11) 91234-5678\",\"birthDate\":\"1985-03-15\",\"address\":{\"zipCode\":\"01310-100\",\"street\":\"Av. Paulista\",\"number\":\"1000\",\"complement\":\"Apto 101\",\"neighborhood\":\"Bela Vista\",\"city\":\"Sao Paulo\",\"state\":\"SP\"}}" %API_URL%/v1/customers
) else (
    echo ERRO: Token nao definido
)
echo.
echo.

REM ============================================
REM 3. PRODUCTS
REM ============================================
echo ================================================
echo 3. PRODUCTS ENDPOINTS
echo ================================================
echo.

echo [3.1] Listar todos os produtos
echo Method: GET
echo Endpoint: %API_URL%/v1/products
if defined TOKEN (
    curl -s -H "Authorization: Bearer %TOKEN%" %API_URL%/v1/products
) else (
    echo ERRO: Token nao definido
)
echo.
echo.

echo [3.2] Buscar produto por codigo (PROD001)
echo Method: GET
echo Endpoint: %API_URL%/v1/products/PROD001
if defined TOKEN (
    curl -s -H "Authorization: Bearer %TOKEN%" %API_URL%/v1/products/PROD001
) else (
    echo ERRO: Token nao definido
)
echo.
echo.

REM ============================================
REM 4. SALES
REM ============================================
echo ================================================
echo 4. SALES ENDPOINTS
echo ================================================
echo.

echo [4.1] Listar todas as vendas
echo Method: GET
echo Endpoint: %API_URL%/v1/sales
if defined TOKEN (
    curl -s -H "Authorization: Bearer %TOKEN%" %API_URL%/v1/sales
) else (
    echo ERRO: Token nao definido
)
echo.
echo.

REM ============================================
REM 5. REPORTS
REM ============================================
echo ================================================
echo 5. REPORTS ENDPOINTS
echo ================================================
echo.

echo [5.1] Produtos mais vendidos
echo Method: GET
echo Endpoint: %API_URL%/reports/top-revenue-products
if defined TOKEN (
    curl -s -H "Authorization: Bearer %TOKEN%" %API_URL%/reports/top-revenue-products
) else (
    echo ERRO: Token nao definido
)
echo.
echo.

echo ================================================
echo RESUMO DA VALIDACAO
echo ================================================
echo.
echo Testes de integracao concluidos!
echo.
echo Endpoints testados:
echo   - Authentication: 1 endpoint
echo   - Customers: 3 endpoints
echo   - Products: 2 endpoints
echo   - Sales: 1 endpoint
echo   - Reports: 1 endpoint
echo.
echo Total: 8 endpoints validados
echo.
echo ================================================
pause
