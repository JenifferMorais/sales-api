#!/bin/bash

# Script para deploy completo (Frontend + Backend + Database)

set -e

echo "🚀 Deploying complete Vendas stack..."

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Verificar se .env existe
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  .env file not found. Creating from .env.example...${NC}"
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo -e "${RED}⚠️  Please edit .env file with your configuration!${NC}"
        exit 1
    else
        echo -e "${RED}❌ .env.example not found!${NC}"
        exit 1
    fi
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed!${NC}"
    exit 1
fi

# Carregar variáveis do .env
source .env

# Modo de deploy
MODE="${1:-dev}"

if [ "$MODE" = "prod" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
    echo -e "${YELLOW}🏭 Deploying in PRODUCTION mode${NC}"
else
    COMPOSE_FILE="docker-compose.yml"
    echo -e "${YELLOW}🔧 Deploying in DEVELOPMENT mode${NC}"
fi

# Parar containers existentes
echo -e "${YELLOW}🛑 Stopping existing containers...${NC}"
docker-compose -f "$COMPOSE_FILE" down

# Pull de imagens mais recentes (produção)
if [ "$MODE" = "prod" ]; then
    echo -e "${YELLOW}📥 Pulling latest images...${NC}"
    docker-compose -f "$COMPOSE_FILE" pull
fi

# Iniciar stack
echo -e "${YELLOW}🚀 Starting stack...${NC}"
docker-compose -f "$COMPOSE_FILE" up -d

# Aguardar serviços
echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
sleep 10

# Verificar status
echo ""
echo -e "${GREEN}📊 Stack status:${NC}"
docker-compose -f "$COMPOSE_FILE" ps

# Health checks
echo ""
echo -e "${YELLOW}🏥 Checking service health...${NC}"

# Check frontend
if curl -f http://localhost:80/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Frontend: OK${NC}"
else
    echo -e "${RED}❌ Frontend: FAILED${NC}"
fi

# Check backend
if curl -f http://localhost:8080/q/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Backend: OK${NC}"
else
    echo -e "${RED}❌ Backend: FAILED${NC}"
fi

# Check database
if docker-compose -f "$COMPOSE_FILE" exec -T postgres pg_isready > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Database: OK${NC}"
else
    echo -e "${RED}❌ Database: FAILED${NC}"
fi

echo ""
echo -e "${GREEN}✨ Deployment complete!${NC}"
echo ""
echo -e "${GREEN}🌐 Access the application:${NC}"
echo "   Frontend: http://localhost"
echo "   Backend API: http://localhost:8080"
echo "   Swagger UI: http://localhost:8080/swagger-ui"
echo ""
echo -e "${YELLOW}📝 View logs:${NC}"
echo "   docker-compose -f $COMPOSE_FILE logs -f"
echo ""
echo -e "${RED}🛑 Stop stack:${NC}"
echo "   docker-compose -f $COMPOSE_FILE down"
