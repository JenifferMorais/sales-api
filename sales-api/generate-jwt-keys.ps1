# ========================================
# Script para gerar chaves RSA para JWT (Windows PowerShell)
# ========================================

Write-Host "🔑 Gerando par de chaves RSA para JWT..." -ForegroundColor Cyan

# Verificar se OpenSSL está instalado
try {
    $null = Get-Command openssl -ErrorAction Stop
} catch {
    Write-Host "❌ OpenSSL não encontrado!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Instale o OpenSSL:" -ForegroundColor Yellow
    Write-Host "  1. Baixe: https://slproweb.com/products/Win32OpenSSL.html" -ForegroundColor Yellow
    Write-Host "  2. Ou use Chocolatey: choco install openssl" -ForegroundColor Yellow
    Write-Host "  3. Ou use Git Bash (incluído no Git for Windows)" -ForegroundColor Yellow
    exit 1
}

# Criar diretório se não existir
$targetDir = "src\main\resources\META-INF\resources"
if (-not (Test-Path $targetDir)) {
    New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
}

# Gerar chave privada
Write-Host "📝 Gerando chave privada..." -ForegroundColor Yellow
openssl genpkey -algorithm RSA -out "$targetDir\privateKey.pem" -pkeyopt rsa_keygen_bits:2048

# Gerar chave pública
Write-Host "📝 Gerando chave pública..." -ForegroundColor Yellow
openssl rsa -pubout -in "$targetDir\privateKey.pem" -out "$targetDir\publicKey.pem"

Write-Host ""
Write-Host "✅ Chaves geradas com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "Arquivos criados:" -ForegroundColor Cyan
Write-Host "  - $targetDir\privateKey.pem (privada)" -ForegroundColor White
Write-Host "  - $targetDir\publicKey.pem (pública)" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  IMPORTANTE:" -ForegroundColor Yellow
Write-Host "  - Nunca commite a chave privada (privateKey.pem)" -ForegroundColor Yellow
Write-Host "  - Adicione *.pem ao .gitignore" -ForegroundColor Yellow
Write-Host "  - Em produção, use serviços de gerenciamento de secrets" -ForegroundColor Yellow
Write-Host "    (AWS KMS, Azure Key Vault, HashiCorp Vault, etc.)" -ForegroundColor Yellow
