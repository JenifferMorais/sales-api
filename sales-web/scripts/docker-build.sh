#!/bin/bash

# Script para build da imagem Docker do frontend

set -e

echo "🐳 Building Docker image for Vendas Web..."

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Configurações
IMAGE_NAME="sales-web"
TAG="${1:-latest}"
FULL_IMAGE="$IMAGE_NAME:$TAG"

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Build da imagem
echo -e "${YELLOW}🔨 Building Docker image: $FULL_IMAGE${NC}"
docker build -t "$FULL_IMAGE" .

# Verificar se build foi bem sucedido
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Docker image built successfully!${NC}"

    # Mostrar informações da imagem
    echo ""
    echo -e "${YELLOW}📊 Image details:${NC}"
    docker images "$IMAGE_NAME" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"

    echo ""
    echo -e "${GREEN}🚀 To run the container:${NC}"
    echo "docker run -d -p 80:80 --name sales-web $FULL_IMAGE"
    echo ""
    echo -e "${GREEN}🔧 To run with custom API URL:${NC}"
    echo "docker run -d -p 80:80 -e API_URL=https://your-api.com/api --name sales-web $FULL_IMAGE"
else
    echo -e "${RED}❌ Docker build failed!${NC}"
    exit 1
fi
