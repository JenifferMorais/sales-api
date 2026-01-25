# 🐳 Guia de Docker - API de Vendas

Este documento descreve como usar Docker para build, deploy e execução da aplicação.

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Dockerfiles Disponíveis](#dockerfiles-disponíveis)
3. [Build Local](#build-local)
4. [CI/CD Automático](#cicd-automático)
5. [Deploy em Produção](#deploy-em-produção)
6. [Troubleshooting](#troubleshooting)

---

## 🎯 Visão Geral

O projeto possui 3 estratégias de containerização:

1. **JVM (Recomendado)** - Imagem otimizada com JVM
2. **Native** - Compilação nativa GraalVM (startup rápido)
3. **Legacy JAR** - Uber-jar para compatibilidade

---

## 📦 Dockerfiles Disponíveis

### 1. Dockerfile.jvm (Recomendado)

**Localização:** `src/main/docker/Dockerfile.jvm`

**Características:**
- ✅ Multi-stage build (reduz tamanho)
- ✅ Usa OpenJDK 21
- ✅ Otimizado para produção
- ✅ Imagem final ~350MB
- ✅ Startup rápido (~3 segundos)

**Quando usar:**
- Produção padrão
- Quando não precisa de startup ultra-rápido
- Compatibilidade máxima

### 2. Dockerfile.native

**Localização:** `src/main/docker/Dockerfile.native`

**Características:**
- ✅ Compilação nativa GraalVM
- ✅ Startup extremamente rápido (~0.05s)
- ✅ Menor consumo de memória
- ✅ Imagem final ~150MB
- ⚠️ Build demorado (5-10 min)
- ⚠️ Requer GraalVM

**Quando usar:**
- Serverless/Lambda
- Microserviços com escala rápida
- Ambientes com recursos limitados

### 3. Dockerfile.legacy-jar

**Localização:** `src/main/docker/Dockerfile.legacy-jar`

**Características:**
- ✅ Uber-jar simples
- ✅ Compatibilidade legado
- ⚠️ Imagem maior (~400MB)

**Quando usar:**
- Apenas para compatibilidade
- Não recomendado para novos deploys

---

## 🔨 Build Local

### Build JVM (Recomendado)

```bash
# 1. Build da aplicação com Maven
./mvnw clean package -DskipTests

# 2. Build da imagem Docker
docker build -f src/main/docker/Dockerfile.jvm -t sales-api:latest .

# 3. Executar
docker run -i --rm -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=sales_db \
  -e DB_USERNAME=sales \
  -e DB_PASSWORD=sales123 \
  sales-api:latest
```

### Build Native

```bash
# 1. Build nativo (requer GraalVM)
./mvnw package -Pnative -DskipTests

# 2. Build da imagem
docker build -f src/main/docker/Dockerfile.native -t sales-api:native .

# 3. Executar
docker run -i --rm -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  sales-api:native
```

### Build via Maven Quarkus Plugin

```bash
# JVM
./mvnw clean package -Dquarkus.container-image.build=true

# Native
./mvnw clean package -Pnative -Dquarkus.container-image.build=true
```

---

## 🚀 CI/CD Automático

### GitHub Actions (Configurado)

O projeto possui pipeline automático que:
- ✅ Roda a cada push na branch `main`
- ✅ Executa testes
- ✅ Build da aplicação com Maven
- ✅ Cria imagem Docker
- ✅ Publica no GitHub Container Registry (ghcr.io)
- ✅ Cria múltiplas tags automaticamente

**Arquivo:** `.github/workflows/docker-build.yml`

### Tags Geradas Automaticamente

```
ghcr.io/seu-usuario/sales-api:latest          # Branch main
ghcr.io/seu-usuario/sales-api:main            # Branch name
ghcr.io/seu-usuario/sales-api:main-abc1234    # SHA do commit
ghcr.io/seu-usuario/sales-api:v1.0.0          # Tag semântica (se criar)
ghcr.io/seu-usuario/sales-api:1.0             # Major.Minor
```

### Ativar GitHub Actions

1. **Fazer push para branch main:**
```bash
git add .
git commit -m "feat: adicionar CI/CD com Docker"
git push origin main
```

2. **Verificar execução:**
- Acesse: `https://github.com/seu-usuario/sales-api/actions`
- Verifique o workflow "Build and Push Docker Image"

3. **Acessar imagem publicada:**
- Acesse: `https://github.com/seu-usuario/sales-api/pkgs/container/sales-api`
- A imagem estará disponível em: `ghcr.io/seu-usuario/sales-api:latest`

### Tornar Imagem Pública (Opcional)

1. Acesse o package no GitHub
2. Settings → Change visibility → Public

### Docker Hub (Alternativa)

Se preferir usar Docker Hub ao invés de GitHub Container Registry:

1. **Configurar secrets no GitHub:**
   - `DOCKERHUB_USERNAME` - Seu usuário do Docker Hub
   - `DOCKERHUB_TOKEN` - Token de acesso (criar em Docker Hub)

2. **Renomear workflow:**
```bash
mv .github/workflows/docker-build-dockerhub.yml.example \
   .github/workflows/docker-build-dockerhub.yml
```

3. **Editar variável `IMAGE_NAME`:**
```yaml
env:
  IMAGE_NAME: seu-usuario/sales-api
```

---

## 🌐 Deploy em Produção

### Docker Compose (Recomendado)

**Arquivo:** `docker-compose.prod.yml`

```bash
# 1. Criar arquivo .env
cat > .env << EOF
# Database
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=sua-senha-forte-aqui

# JWT
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15

# Email
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app
SMTP_FROM=noreply@sales.com
SMTP_MOCK=false

# App
APP_URL=https://api.sales.com

# GitHub
GITHUB_REPOSITORY=seu-usuario/sales-api
EOF

# 2. Pull da imagem mais recente
docker compose -f docker-compose.prod.yml pull

# 3. Iniciar serviços
docker compose -f docker-compose.prod.yml up -d

# 4. Verificar logs
docker compose -f docker-compose.prod.yml logs -f sales-api

# 5. Verificar saúde
curl http://localhost:8080/q/health
```

### Kubernetes (K8s)

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sales-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: sales-api
  template:
    metadata:
      labels:
        app: sales-api
    spec:
      containers:
      - name: sales-api
        image: ghcr.io/seu-usuario/sales-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: sales-config
              key: db-host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: sales-secrets
              key: db-password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /q/health/live
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /q/health/ready
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: sales-api
spec:
  selector:
    app: sales-api
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

### AWS ECS

```bash
# 1. Login no ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789.dkr.ecr.us-east-1.amazonaws.com

# 2. Tag e push
docker tag sales-api:latest \
  123456789.dkr.ecr.us-east-1.amazonaws.com/sales-api:latest

docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/sales-api:latest

# 3. Criar task definition e service via Console ou CLI
```

### Azure Container Apps

```bash
# 1. Login no ACR
az acr login --name sales

# 2. Tag e push
docker tag sales-api:latest sales.azurecr.io/sales-api:latest
docker push sales.azurecr.io/sales-api:latest

# 3. Deploy
az containerapp create \
  --name sales-api \
  --resource-group sales-rg \
  --image sales.azurecr.io/sales-api:latest \
  --target-port 8080 \
  --ingress external \
  --env-vars \
    DB_HOST=postgres-server \
    DB_NAME=sales_db
```

---

## 🔍 Verificação e Testes

### Health Checks

```bash
# Health geral
curl http://localhost:8080/q/health

# Liveness (aplicação está viva)
curl http://localhost:8080/q/health/live

# Readiness (pronta para receber tráfego)
curl http://localhost:8080/q/health/ready
```

### Logs

```bash
# Docker
docker logs -f sales-api

# Docker Compose
docker compose -f docker-compose.prod.yml logs -f sales-api

# Kubernetes
kubectl logs -f deployment/sales-api
```

### Acessar Shell do Container

```bash
# Docker
docker exec -it sales-api /bin/bash

# Kubernetes
kubectl exec -it deployment/sales-api -- /bin/bash
```

---

## 🐛 Troubleshooting

### Problema: Imagem muito grande

**Solução:**
```bash
# Use multi-stage build (já configurado em Dockerfile.jvm)
# Verifique o .dockerignore para excluir arquivos desnecessários

# Ver tamanho das camadas
docker history sales-api:latest

# Limpar cache de build
docker builder prune -a
```

### Problema: Build falha no CI/CD

**Causa comum:** Testes falhando

**Solução:**
```bash
# Testar localmente primeiro
./mvnw clean test

# Se OK, verificar logs do GitHub Actions
# Actions → Workflow run → Job → Step com erro
```

### Problema: Container não inicia

**Verificar logs:**
```bash
docker logs sales-api
```

**Causas comuns:**
1. Banco de dados não acessível
2. Variáveis de ambiente faltando
3. Porta 8080 já em uso

**Solução:**
```bash
# Verificar conectividade com DB
docker exec sales-api ping postgres

# Verificar variáveis
docker exec sales-api env | grep DB_

# Verificar portas
netstat -tulpn | grep 8080
```

### Problema: "Unhealthy" no health check

**Verificar:**
```bash
# Health endpoint
curl http://localhost:8080/q/health

# Verificar se DB está acessível
docker exec sales-api curl postgres:5432

# Logs detalhados
docker logs sales-api --tail 100
```

### Problema: Permissão negada no GitHub Registry

**Solução:**
```bash
# 1. Criar Personal Access Token (PAT) no GitHub
# Settings → Developer settings → Personal access tokens
# Scope: write:packages, read:packages

# 2. Login no ghcr.io
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# 3. Verificar permissões do repositório
# Repo settings → Actions → Workflow permissions → Read and write
```

---

## 📊 Comparação de Estratégias

| Aspecto | JVM | Native | Legacy JAR |
|---------|-----|--------|------------|
| **Tamanho imagem** | ~350MB | ~150MB | ~400MB |
| **Startup time** | ~3s | ~0.05s | ~5s |
| **Build time** | ~2 min | ~10 min | ~2 min |
| **Memória (idle)** | ~200MB | ~50MB | ~250MB |
| **Throughput** | Alto | Médio | Alto |
| **Compatibilidade** | ✅ 100% | ⚠️ 95% | ✅ 100% |
| **Recomendado para** | Produção | Serverless | Legacy |

---

## 🔐 Segurança

### Boas Práticas

1. **Não commitar secrets:**
```bash
# Sempre use .env ou secrets do CI/CD
# NUNCA hardcode senhas nos Dockerfiles
```

2. **Scan de vulnerabilidades:**
```bash
# Trivy
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image sales-api:latest

# Snyk
snyk container test sales-api:latest
```

3. **Usuário não-root:**
```dockerfile
# Já configurado nos Dockerfiles
USER 185  # JVM
USER 1001 # Native
```

4. **Imagens base atualizadas:**
```bash
# Pull regularmente das imagens base
docker pull registry.access.redhat.com/ubi8/openjdk-21:1.18
```

---

## 📝 Scripts Úteis

### Deploy Rápido

```bash
#!/bin/bash
# deploy.sh

set -e

echo "🚀 Deploying Vendas API..."

# Pull latest
docker compose -f docker-compose.prod.yml pull

# Stop old containers
docker compose -f docker-compose.prod.yml down

# Start new containers
docker compose -f docker-compose.prod.yml up -d

# Wait for health
echo "⏳ Waiting for health check..."
sleep 10

# Check health
if curl -f http://localhost:8080/q/health > /dev/null 2>&1; then
  echo "✅ Deploy successful!"
else
  echo "❌ Deploy failed! Rolling back..."
  docker compose -f docker-compose.prod.yml down
  exit 1
fi
```

### Backup e Restore

```bash
# Backup do banco
docker exec sales-postgres pg_dump -U sales sales_db > backup.sql

# Restore
docker exec -i sales-postgres psql -U sales sales_db < backup.sql
```

---

## 🔗 Links Úteis

- [Quarkus Container Images](https://quarkus.io/guides/container-image)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [Multi-stage Builds](https://docs.docker.com/build/building/multi-stage/)

---

**Última atualização:** 2026-01-24
**Versão:** 1.0.0
