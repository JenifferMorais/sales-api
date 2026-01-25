#!/bin/bash

# ========================================
# Script para gerar chaves RSA para JWT
# ========================================

set -e  # Exit on error

echo "🔑 Gerando par de chaves RSA para JWT..."

# Criar diretório se não existir
mkdir -p src/main/resources/META-INF/resources

# Gerar chave privada
echo "📝 Gerando chave privada..."
openssl genpkey -algorithm RSA -out src/main/resources/META-INF/resources/privateKey.pem -pkeyopt rsa_keygen_bits:2048

# Gerar chave pública
echo "📝 Gerando chave pública..."
openssl rsa -pubout -in src/main/resources/META-INF/resources/privateKey.pem -out src/main/resources/META-INF/resources/publicKey.pem

# Configurar permissões
chmod 600 src/main/resources/META-INF/resources/privateKey.pem
chmod 644 src/main/resources/META-INF/resources/publicKey.pem

echo "✅ Chaves geradas com sucesso!"
echo ""
echo "Arquivos criados:"
echo "  - src/main/resources/META-INF/resources/privateKey.pem (privada)"
echo "  - src/main/resources/META-INF/resources/publicKey.pem (pública)"
echo ""
echo "⚠️  IMPORTANTE:"
echo "  - Nunca commite a chave privada (privateKey.pem)"
echo "  - Adicione *.pem ao .gitignore"
echo "  - Em produção, use serviços de gerenciamento de secrets (AWS KMS, Vault, etc.)"
