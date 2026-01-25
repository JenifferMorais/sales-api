#!/bin/bash

set -e

echo "========================================"
echo "  TESTE DE BUILD DOCKER - SALES SYSTEM"
echo "========================================"
echo ""

BACKEND_IMAGE="sales-api:test"
FRONTEND_IMAGE="sales-web:test"

test_backend() {
    echo "🔨 Testando build do BACKEND..."
    echo ""

    cd sales-api

    echo "1️⃣ Build com Maven..."
    mvn clean package -DskipTests

    echo ""
    echo "2️⃣ Build da imagem Docker..."
    docker build -f docker/dockerfiles/Dockerfile.simple -t $BACKEND_IMAGE .

    echo ""
    echo "✅ Backend build SUCESSO!"
    echo "   Imagem: $BACKEND_IMAGE"
    echo "   Tamanho: $(docker images $BACKEND_IMAGE --format "{{.Size}}")"

    cd ..
}

test_frontend() {
    echo ""
    echo "🎨 Testando build do FRONTEND..."
    echo ""

    cd sales-web

    echo "1️⃣ Instalando dependências..."
    npm ci --legacy-peer-deps

    echo ""
    echo "2️⃣ Build da aplicação..."
    npm run build -- --configuration production

    echo ""
    echo "3️⃣ Build da imagem Docker..."
    docker build -f docker/prod/Dockerfile -t $FRONTEND_IMAGE .

    echo ""
    echo "✅ Frontend build SUCESSO!"
    echo "   Imagem: $FRONTEND_IMAGE"
    echo "   Tamanho: $(docker images $FRONTEND_IMAGE --format "{{.Size}}")"

    cd ..
}

test_run_backend() {
    echo ""
    echo "🚀 Testando execução do BACKEND..."
    echo ""

    docker run -d --name sales-api-test \
        -p 8082:8080 \
        -e DB_HOST=host.docker.internal \
        -e DB_PORT=5432 \
        -e DB_NAME=sales_db \
        -e DB_USERNAME=sales \
        -e DB_PASSWORD=sales123 \
        $BACKEND_IMAGE

    echo "Aguardando aplicação iniciar..."
    sleep 10

    echo "Testando health check..."
    if curl -f http://localhost:8082/q/health > /dev/null 2>&1; then
        echo "✅ Backend rodando corretamente!"
        docker logs sales-api-test --tail=20
    else
        echo "❌ Backend falhou health check"
        docker logs sales-api-test
        docker stop sales-api-test
        docker rm sales-api-test
        exit 1
    fi

    docker stop sales-api-test
    docker rm sales-api-test
}

test_run_frontend() {
    echo ""
    echo "🚀 Testando execução do FRONTEND..."
    echo ""

    docker run -d --name sales-web-test \
        -p 8083:80 \
        -e API_URL=http://localhost:8082/api \
        $FRONTEND_IMAGE

    echo "Aguardando aplicação iniciar..."
    sleep 5

    echo "Testando acesso..."
    if curl -f http://localhost:8083 > /dev/null 2>&1; then
        echo "✅ Frontend rodando corretamente!"
    else
        echo "❌ Frontend não está acessível"
        docker logs sales-web-test
        docker stop sales-web-test
        docker rm sales-web-test
        exit 1
    fi

    docker stop sales-web-test
    docker rm sales-web-test
}

cleanup() {
    echo ""
    echo "🧹 Limpando imagens de teste..."
    docker rmi $BACKEND_IMAGE 2>/dev/null || true
    docker rmi $FRONTEND_IMAGE 2>/dev/null || true
    echo "✅ Limpeza completa"
}

show_summary() {
    echo ""
    echo "========================================"
    echo "  RESUMO DO TESTE"
    echo "========================================"
    echo ""
    echo "✅ Backend build: SUCESSO"
    echo "✅ Frontend build: SUCESSO"
    echo "✅ Backend runtime: SUCESSO"
    echo "✅ Frontend runtime: SUCESSO"
    echo ""
    echo "🎉 Tudo pronto para deploy!"
    echo ""
}

case "${1}" in
    backend)
        test_backend
        test_run_backend
        ;;
    frontend)
        test_frontend
        test_run_frontend
        ;;
    all)
        test_backend
        test_frontend
        test_run_backend
        test_run_frontend
        show_summary
        ;;
    cleanup)
        cleanup
        ;;
    *)
        echo "Uso: $0 {backend|frontend|all|cleanup}"
        echo ""
        echo "Opções:"
        echo "  backend  - Testa apenas o backend"
        echo "  frontend - Testa apenas o frontend"
        echo "  all      - Testa backend e frontend"
        echo "  cleanup  - Remove imagens de teste"
        exit 1
        ;;
esac
