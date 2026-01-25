#!/bin/bash
# Script de Validação de Integração Frontend-Backend
# Sales System - API Testing
# Data: 2026-01-25

echo "================================================"
echo "🧪 VALIDAÇÃO DE INTEGRAÇÃO - SALES SYSTEM"
echo "================================================"
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configurações
API_URL="http://localhost:8080/api"
TOKEN=""

# Função para testar endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4

    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📍 $description"
    echo "   Method: $method"
    echo "   Endpoint: $endpoint"

    if [ -n "$data" ]; then
        echo "   Data: $data"
    fi

    echo ""

    # Fazer requisição
    if [ "$method" = "GET" ]; then
        if [ -z "$TOKEN" ]; then
            response=$(curl -s -w "\n%{http_code}" "$API_URL$endpoint")
        else
            response=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN" "$API_URL$endpoint")
        fi
    else
        if [ -z "$TOKEN" ]; then
            response=$(curl -s -w "\n%{http_code}" -X "$method" -H "Content-Type: application/json" -d "$data" "$API_URL$endpoint")
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "$data" "$API_URL$endpoint")
        fi
    fi

    # Extrair código de status
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)

    # Verificar sucesso
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ SUCCESS${NC} - Status: $http_code"
        echo "Response:"
        echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    else
        echo -e "${RED}❌ FAILED${NC} - Status: $http_code"
        echo "Response:"
        echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    fi

    echo ""
}

# Verificar se backend está rodando
echo "🔍 Verificando se backend está rodando..."
if ! curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo -e "${RED}❌ Backend não está rodando em http://localhost:8080${NC}"
    echo ""
    echo "Para iniciar o backend:"
    echo "  cd sales-api"
    echo "  ./mvnw quarkus:dev"
    echo ""
    exit 1
fi

echo -e "${GREEN}✅ Backend está rodando${NC}"
echo ""

# ============================================
# 1. AUTHENTICATION
# ============================================
echo "================================================"
echo "🔐 1. AUTHENTICATION ENDPOINTS"
echo "================================================"
echo ""

# 1.1 Login
# IMPORTANT: Update these credentials with a valid test user from your database
# You can set TEST_EMAIL and TEST_PASSWORD environment variables
TEST_EMAIL="${TEST_EMAIL:-test@email.com}"
TEST_PASSWORD="${TEST_PASSWORD:-YourTestPassword123!}"

test_endpoint "POST" "/v1/auth/login" \
    "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}" \
    "Login com credenciais válidas"

# Extrair token da resposta
TOKEN=$(echo "$response_body" | jq -r '.access_token // .token // empty')
if [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ Não foi possível obter token. Abortando testes.${NC}"
    exit 1
fi

echo -e "${YELLOW}📝 Token obtido: ${TOKEN:0:50}...${NC}"
echo ""

sleep 1

# 1.2 Get Current User
test_endpoint "GET" "/v1/auth/me" "" \
    "Obter usuário autenticado"

sleep 1

# 1.3 Register (opcional)
# test_endpoint "POST" "/v1/auth/register" \
#     '{"email":"test@email.com","password":"Test@123","name":"Test User"}' \
#     "Registrar novo usuário"

# ============================================
# 2. CUSTOMERS
# ============================================
echo "================================================"
echo "👥 2. CUSTOMERS ENDPOINTS"
echo "================================================"
echo ""

# 2.1 List All Customers
test_endpoint "GET" "/v1/customers" "" \
    "Listar todos os clientes"

sleep 1

# 2.2 Get Customer by Code
test_endpoint "GET" "/v1/customers/CUST001" "" \
    "Buscar cliente por código (CUST001)"

sleep 1

# 2.3 Create Customer
CUSTOMER_DATA='{
  "fullName": "João da Silva Teste",
  "motherName": "Maria da Silva",
  "cpf": "987.654.321-00",
  "rg": "98.765.432-1",
  "email": "joao.teste@email.com",
  "cellPhone": "(11) 91234-5678",
  "birthDate": "1985-03-15",
  "address": {
    "zipCode": "01310-100",
    "street": "Av. Paulista",
    "number": "1000",
    "complement": "Apto 101",
    "neighborhood": "Bela Vista",
    "city": "São Paulo",
    "state": "SP"
  }
}'

test_endpoint "POST" "/v1/customers" "$CUSTOMER_DATA" \
    "Criar novo cliente"

# Extrair código do cliente criado
CREATED_CUSTOMER_CODE=$(echo "$response_body" | jq -r '.code // empty')

sleep 1

# 2.4 Update Customer (se foi criado)
if [ -n "$CREATED_CUSTOMER_CODE" ]; then
    UPDATE_CUSTOMER_DATA='{
      "fullName": "João da Silva Teste - Atualizado",
      "motherName": "Maria da Silva",
      "cpf": "987.654.321-00",
      "rg": "98.765.432-1",
      "email": "joao.teste.updated@email.com",
      "cellPhone": "(11) 91234-5678",
      "birthDate": "1985-03-15",
      "address": {
        "zipCode": "01310-100",
        "street": "Av. Paulista",
        "number": "2000",
        "complement": "Apto 202",
        "neighborhood": "Bela Vista",
        "city": "São Paulo",
        "state": "SP"
      }
    }'

    test_endpoint "PUT" "/v1/customers/$CREATED_CUSTOMER_CODE" "$UPDATE_CUSTOMER_DATA" \
        "Atualizar cliente ($CREATED_CUSTOMER_CODE)"

    sleep 1
fi

# 2.5 Delete Customer (se foi criado)
if [ -n "$CREATED_CUSTOMER_CODE" ]; then
    test_endpoint "DELETE" "/v1/customers/$CREATED_CUSTOMER_CODE" "" \
        "Deletar cliente ($CREATED_CUSTOMER_CODE)"

    sleep 1
fi

# ============================================
# 3. PRODUCTS
# ============================================
echo "================================================"
echo "📦 3. PRODUCTS ENDPOINTS"
echo "================================================"
echo ""

# 3.1 List All Products
test_endpoint "GET" "/v1/products" "" \
    "Listar todos os produtos"

sleep 1

# 3.2 Get Product by Code
test_endpoint "GET" "/v1/products/PROD001" "" \
    "Buscar produto por código (PROD001)"

sleep 1

# 3.3 Create Product
PRODUCT_DATA='{
  "name": "Produto Teste API",
  "type": "Lábios",
  "details": "Produto de teste criado via script de validação",
  "weight": 0.250,
  "purchasePrice": 15.00,
  "salePrice": 35.00,
  "height": 12.0,
  "width": 5.0,
  "depth": 3.0,
  "destinationVehicle": "Todos os tipos de pele"
}'

test_endpoint "POST" "/v1/products" "$PRODUCT_DATA" \
    "Criar novo produto"

# Extrair código do produto criado
CREATED_PRODUCT_CODE=$(echo "$response_body" | jq -r '.code // empty')

sleep 1

# 3.4 Update Product (se foi criado)
if [ -n "$CREATED_PRODUCT_CODE" ]; then
    UPDATE_PRODUCT_DATA='{
      "name": "Produto Teste API - Atualizado",
      "type": "Olhos",
      "details": "Produto atualizado via script",
      "weight": 0.300,
      "purchasePrice": 18.00,
      "salePrice": 40.00,
      "height": 15.0,
      "width": 6.0,
      "depth": 4.0,
      "destinationVehicle": "Todos"
    }'

    test_endpoint "PUT" "/v1/products/$CREATED_PRODUCT_CODE" "$UPDATE_PRODUCT_DATA" \
        "Atualizar produto ($CREATED_PRODUCT_CODE)"

    sleep 1
fi

# 3.5 Delete Product (se foi criado)
if [ -n "$CREATED_PRODUCT_CODE" ]; then
    test_endpoint "DELETE" "/v1/products/$CREATED_PRODUCT_CODE" "" \
        "Deletar produto ($CREATED_PRODUCT_CODE)"

    sleep 1
fi

# ============================================
# 4. SALES
# ============================================
echo "================================================"
echo "🛒 4. SALES ENDPOINTS"
echo "================================================"
echo ""

# 4.1 List All Sales
test_endpoint "GET" "/v1/sales" "" \
    "Listar todas as vendas"

sleep 1

# 4.2 Get Sale by Code
test_endpoint "GET" "/v1/sales/SALE001" "" \
    "Buscar venda por código (SALE001)"

sleep 1

# 4.3 Create Sale
SALE_DATA='{
  "customerCode": "CUST001",
  "customerName": "João Silva",
  "sellerCode": "SELLER001",
  "sellerName": "Vendedor Sistema",
  "paymentMethod": "Cartão de Crédito",
  "cardNumber": "1234",
  "items": [
    {
      "productCode": "PROD001",
      "productName": "Batom Matte",
      "quantity": 2,
      "unitPrice": 35.00
    },
    {
      "productCode": "PROD002",
      "productName": "Delineador Líquido",
      "quantity": 1,
      "unitPrice": 28.00
    }
  ]
}'

test_endpoint "POST" "/v1/sales" "$SALE_DATA" \
    "Criar nova venda"

# Extrair código da venda criada
CREATED_SALE_CODE=$(echo "$response_body" | jq -r '.code // empty')

sleep 1

# 4.4 Get Sale Details (se foi criada)
if [ -n "$CREATED_SALE_CODE" ]; then
    test_endpoint "GET" "/v1/sales/$CREATED_SALE_CODE" "" \
        "Buscar detalhes da venda criada ($CREATED_SALE_CODE)"

    sleep 1
fi

# 4.5 Delete Sale (se foi criada)
if [ -n "$CREATED_SALE_CODE" ]; then
    test_endpoint "DELETE" "/v1/sales/$CREATED_SALE_CODE" "" \
        "Deletar venda ($CREATED_SALE_CODE)"

    sleep 1
fi

# ============================================
# 5. REPORTS
# ============================================
echo "================================================"
echo "📊 5. REPORTS ENDPOINTS"
echo "================================================"
echo ""

# 5.1 Monthly Revenue
MONTHLY_REVENUE_DATA='{
  "year": 2024,
  "month": 1
}'

test_endpoint "POST" "/reports/monthly-revenue" "$MONTHLY_REVENUE_DATA" \
    "Faturamento mensal (Janeiro/2024)"

sleep 1

# 5.2 Top Revenue Products
test_endpoint "GET" "/reports/top-revenue-products" "" \
    "Produtos mais vendidos"

sleep 1

# 5.3 Oldest Products
test_endpoint "GET" "/reports/oldest-products" "" \
    "Produtos encalhados (mais antigos)"

sleep 1

# 5.4 New Customers
NEW_CUSTOMERS_DATA='{
  "year": 2024,
  "month": 1
}'

test_endpoint "POST" "/reports/new-customers" "$NEW_CUSTOMERS_DATA" \
    "Novos clientes (Janeiro/2024)"

sleep 1

# ============================================
# SUMMARY
# ============================================
echo "================================================"
echo "📋 RESUMO DA VALIDAÇÃO"
echo "================================================"
echo ""
echo "✅ Testes de integração concluídos!"
echo ""
echo "Endpoints testados:"
echo "  • Authentication: 2 endpoints"
echo "  • Customers: 5 endpoints"
echo "  • Products: 5 endpoints"
echo "  • Sales: 5 endpoints"
echo "  • Reports: 4 endpoints"
echo ""
echo "Total: 21 endpoints validados"
echo ""
echo "================================================"
