#!/bin/sh

# Script de entrypoint para substituir variáveis de ambiente no runtime
# Permite configurar API_URL sem rebuild da imagem

set -e

echo "🚀 Starting Vendas Web Application..."

# Substituir API_URL se fornecido
if [ -n "$API_URL" ]; then
    echo "📡 Configuring API URL: $API_URL"

    # Encontrar todos os arquivos JS e substituir placeholder
    find /usr/share/nginx/html -type f -name "*.js" -exec sed -i \
        "s|http://localhost:8080/api|$API_URL|g" {} +

    echo "✅ API URL configured successfully"
else
    echo "⚠️  API_URL not set, using default: http://localhost:8080/api"
fi

# Criar endpoint de health check
cat > /usr/share/nginx/html/health <<EOF
OK
EOF

echo "✨ Application ready!"

# Executar nginx
exec "$@"
