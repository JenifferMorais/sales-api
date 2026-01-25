#!/bin/bash

# Script para build do frontend

set -e

echo "🔨 Building Vendas Web Frontend..."

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar se está na pasta correta
if [ ! -f "package.json" ]; then
    echo -e "${RED}❌ package.json not found. Run this script from the project root.${NC}"
    exit 1
fi

# Limpar builds anteriores
echo -e "${YELLOW}🧹 Cleaning previous builds...${NC}"
rm -rf dist .angular

# Instalar dependências (se necessário)
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}📦 Installing dependencies...${NC}"
    npm install
fi

# Build de produção
echo -e "${YELLOW}🏗️  Building production bundle...${NC}"
npm run build

# Verificar se build foi bem sucedido
if [ -d "dist/sales-web/browser" ]; then
    echo -e "${GREEN}✅ Build completed successfully!${NC}"

    # Mostrar tamanho do bundle
    echo -e "${YELLOW}📊 Bundle size:${NC}"
    du -sh dist/sales-web/browser

    echo ""
    echo -e "${GREEN}✨ Ready to deploy!${NC}"
    echo "📂 Build output: dist/sales-web/browser"
else
    echo -e "${RED}❌ Build failed!${NC}"
    exit 1
fi
