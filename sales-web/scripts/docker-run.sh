#!/bin/bash

# Script para executar o container Docker do frontend

set -e

echo "🐳 Running Vendas Web container..."

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Configurações
CONTAINER_NAME="sales-web"
IMAGE_NAME="sales-web:latest"
PORT="${1:-80}"
API_URL="${2:-http://localhost:8080/api}"

# Verificar se container já existe
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${YELLOW}⚠️  Container $CONTAINER_NAME already exists. Removing...${NC}"
    docker rm -f "$CONTAINER_NAME"
fi

# Executar container
echo -e "${YELLOW}🚀 Starting container on port $PORT...${NC}"
docker run -d \
    -p "$PORT:80" \
    -e API_URL="$API_URL" \
    --name "$CONTAINER_NAME" \
    --restart unless-stopped \
    "$IMAGE_NAME"

# Verificar se container está rodando
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${GREEN}✅ Container started successfully!${NC}"
    echo ""
    echo -e "${GREEN}🌐 Access the application:${NC}"
    echo "   http://localhost:$PORT"
    echo ""
    echo -e "${YELLOW}📊 Container details:${NC}"
    docker ps --filter "name=$CONTAINER_NAME" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
    echo -e "${GREEN}📝 To view logs:${NC}"
    echo "   docker logs -f $CONTAINER_NAME"
    echo ""
    echo -e "${GREEN}🛑 To stop:${NC}"
    echo "   docker stop $CONTAINER_NAME"
else
    echo -e "${RED}❌ Failed to start container!${NC}"
    echo "Check logs with: docker logs $CONTAINER_NAME"
    exit 1
fi
