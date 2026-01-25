# 🐳 Docker e Deploy - Frontend Angular

Guia completo para build, deploy e CI/CD do frontend Angular.

## 📋 Índice

1. [Docker Local](#docker-local)
2. [Docker Compose](#docker-compose)
3. [CI/CD Pipeline](#cicd-pipeline)
4. [Deploy em Produção](#deploy-em-produção)
5. [Troubleshooting](#troubleshooting)

---

## 🐳 Docker Local

### Build da Imagem

```bash
# Build simples
docker build -t sales-web:latest .

# Build com tag específica
docker build -t sales-web:1.0.0 .

# Build de produção otimizado
docker build -f Dockerfile.prod -t sales-web:prod .

# Ou use o script
./scripts/docker-build.sh
```

### Executar Container

```bash
# Básico
docker run -d -p 80:80 --name sales-web sales-web:latest

# Com variável de ambiente (API URL customizada)
docker run -d -p 80:80 \
  -e API_URL=https://sua-api.onrender.com/api \
  --name sales-web \
  sales-web:latest

# Ou use o script
./scripts/docker-run.sh 80 http://localhost:8080/api
```

### Verificar Container

```bash
# Ver logs
docker logs -f sales-web

# Entrar no container
docker exec -it sales-web sh

# Ver status
docker ps

# Health check
curl http://localhost:80/health
```

### Parar e Remover

```bash
# Parar
docker stop sales-web

# Remover
docker rm sales-web

# Remover imagem
docker rmi sales-web:latest
```

---

## 🎯 Docker Compose

### Estrutura

O projeto tem 2 arquivos Docker Compose:

1. **docker-compose.yml** - Desenvolvimento (build local)
2. **docker-compose.prod.yml** - Produção (pull de imagens)

### Configuração

**1. Criar arquivo .env:**

```bash
cp .env.example .env
```

**2. Editar .env:**

```properties
# GitHub
GITHUB_REPOSITORY=seu-usuario/sales-api

# Frontend
API_URL=http://localhost:8080/api

# Backend
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=senha-segura-aqui

# JWT
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24

# SMTP
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app
```

### Executar Stack Completo

**Desenvolvimento:**

```bash
# Iniciar tudo (Frontend + Backend + Database)
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar tudo
docker-compose down
```

**Produção:**

```bash
# Iniciar stack de produção
docker-compose -f docker-compose.prod.yml up -d

# Ver logs
docker-compose -f docker-compose.prod.yml logs -f

# Parar
docker-compose -f docker-compose.prod.yml down
```

**Ou use o script completo:**

```bash
# Desenvolvimento
./scripts/deploy-stack.sh dev

# Produção
./scripts/deploy-stack.sh prod
```

### Serviços Disponíveis

Após iniciar com Docker Compose:

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui
- **PostgreSQL:** localhost:5432

### Comandos Úteis

```bash
# Rebuild de um serviço específico
docker-compose up -d --build frontend

# Ver status
docker-compose ps

# Parar um serviço específico
docker-compose stop frontend

# Remover volumes (⚠️ apaga dados do banco)
docker-compose down -v

# Rebuild completo
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

---

## 🔄 CI/CD Pipeline

### Arquivos de Workflow

1. **`.github/workflows/docker-build-frontend.yml`**
   - Build automático da imagem Docker
   - Push para GitHub Container Registry (ghcr.io)
   - Trigger: push na branch `main`

2. **`.github/workflows/deploy-frontend.yml`**
   - Deploy automático no Render
   - Trigger: após sucesso do build
   - Usa webhook do Render

### Configuração do CI/CD

**1. Habilitar GitHub Packages:**

- Repo → Settings → Actions → General
- Workflow permissions → **Read and write permissions**
- Save

**2. Fazer Push:**

```bash
git add .
git commit -m "feat: adicionar frontend com CI/CD"
git push origin main
```

**3. Acompanhar Build:**

- GitHub → Actions → "Build and Push Frontend Docker Image"
- Aguardar conclusão (~5-8 min)

**4. Verificar Imagem:**

- GitHub → Packages
- Imagem disponível em: `ghcr.io/SEU-USUARIO/sales-api-web:latest`

### Tags Geradas

O pipeline cria múltiplas tags automaticamente:

```
ghcr.io/usuario/sales-api-web:latest
ghcr.io/usuario/sales-api-web:main
ghcr.io/usuario/sales-api-web:main-abc1234
```

### Configurar Deploy Automático (Render)

**1. Criar Web Service no Render:**

- Dashboard → New → Web Service
- Runtime: **Docker**
- Image URL: `ghcr.io/SEU-USUARIO/sales-api-web:latest`

**2. Configurar Variáveis:**

```
API_URL=https://sua-api-backend.onrender.com/api
```

**3. Copiar Deploy Hook:**

- Service → Settings → Deploy Hook
- Copiar URL

**4. Adicionar Secret no GitHub:**

- Repo → Settings → Secrets → Actions
- New secret:
  - Name: `RENDER_DEPLOY_HOOK_URL_FRONTEND`
  - Value: URL do deploy hook

**5. Testar:**

```bash
git commit -m "test: testar deploy automático" --allow-empty
git push origin main
```

Fluxo:
1. Push → GitHub Actions
2. Build → ghcr.io
3. Webhook → Render
4. Deploy → Live!

---

## 🚀 Deploy em Produção

### Opção 1: Render (Recomendado)

**Vantagens:**
- ✅ Deploy automático via webhook
- ✅ SSL grátis
- ✅ CDN global
- ✅ Free tier generoso

**Setup:**

1. **Criar Web Service:**
   - New → Web Service
   - Connect repository ou usar Docker Image

2. **Configurar:**
   ```
   Name: sales-web
   Region: Oregon (US West)
   Branch: main
   Runtime: Docker
   Docker Image: ghcr.io/seu-usuario/sales-api-web:latest
   ```

3. **Environment:**
   ```
   API_URL=https://sua-api.onrender.com/api
   ```

4. **Deploy:**
   - Manual: Click "Deploy"
   - Automático: Configure webhook (veja CI/CD acima)

### Opção 2: Netlify

```bash
# Build
npm run build

# Deploy
npm install -g netlify-cli
netlify deploy --prod --dir=dist/sales-web/browser
```

**Criar `_redirects` em `public/`:**
```
/*    /index.html   200
```

### Opção 3: Vercel

```bash
npm install -g vercel
vercel --prod
```

### Opção 4: AWS ECS / Azure / GCP

**Deploy com Docker:**

```bash
# Tag para registry
docker tag sales-web:latest seu-registry/sales-web:latest

# Push
docker push seu-registry/sales-web:latest

# Deploy (exemplo AWS ECS)
aws ecs update-service --cluster sales --service sales-web --force-new-deployment
```

### Opção 5: VPS com Docker

```bash
# No servidor
git clone https://github.com/seu-usuario/sales-web.git
cd sales-web

# Configurar .env
cp .env.example .env
nano .env

# Deploy
docker-compose -f docker-compose.prod.yml up -d
```

---

## 🔒 Segurança em Produção

### 1. Variáveis de Ambiente

**Nunca commitar:**
- ❌ Senhas
- ❌ API Keys
- ❌ Tokens

**Sempre usar:**
- ✅ Variáveis de ambiente
- ✅ Secrets do CI/CD
- ✅ .env (não commitado)

### 2. HTTPS

**Render/Vercel/Netlify:**
- SSL automático (Let's Encrypt)

**Nginx (VPS):**
```bash
# Instalar certbot
sudo apt install certbot python3-certbot-nginx

# Obter certificado
sudo certbot --nginx -d seu-dominio.com
```

### 3. Headers de Segurança

Já configurados em `nginx.prod.conf`:
- X-Frame-Options
- X-Content-Type-Options
- X-XSS-Protection
- Content-Security-Policy
- Referrer-Policy

### 4. Rate Limiting

No nginx (adicionar em `nginx.prod.conf`):

```nginx
limit_req_zone $binary_remote_addr zone=frontend:10m rate=10r/s;

server {
    location / {
        limit_req zone=frontend burst=20;
        # ...
    }
}
```

---

## 📊 Monitoramento

### Health Checks

```bash
# Frontend
curl http://localhost:80/health

# Backend
curl http://localhost:8080/q/health
```

### Logs

**Docker:**
```bash
docker logs -f sales-web
docker logs --tail 100 sales-web
```

**Docker Compose:**
```bash
docker-compose logs -f
docker-compose logs -f frontend
```

**Render:**
- Dashboard → Service → Logs

### Métricas

**Container:**
```bash
docker stats sales-web
```

**Render Dashboard:**
- CPU usage
- Memory usage
- Request count
- Response time

---

## 🐛 Troubleshooting

### Build Falha

**Problema:** `npm install` falha

**Solução:**
```bash
# Limpar cache
docker build --no-cache -t sales-web .

# Ou localmente
rm -rf node_modules package-lock.json
npm install
```

### Container Não Inicia

**Problema:** Container para imediatamente

**Solução:**
```bash
# Ver logs
docker logs sales-web

# Verificar se porta está ocupada
netstat -ano | findstr :80  # Windows
lsof -i :80                  # Linux/Mac

# Testar com outra porta
docker run -p 8080:80 sales-web
```

### API URL Não Funciona

**Problema:** Frontend não consegue conectar ao backend

**Solução:**

1. **Verificar variável de ambiente:**
```bash
docker run -e API_URL=http://backend:8080/api sales-web
```

2. **Ver dentro do container:**
```bash
docker exec -it sales-web sh
cat /usr/share/nginx/html/main*.js | grep api
```

3. **CORS no backend:**
```properties
quarkus.http.cors.origins=http://localhost,https://seu-frontend.onrender.com
```

### Deploy no Render Falha

**Problema:** Deploy falha no Render

**Solução:**

1. **Verificar logs do Render:**
   - Dashboard → Service → Logs

2. **Testar imagem localmente:**
```bash
docker pull ghcr.io/seu-usuario/sales-api-web:latest
docker run -p 80:80 ghcr.io/seu-usuario/sales-api-web:latest
```

3. **Verificar se imagem é pública:**
   - GitHub → Packages → sales-api-web
   - Settings → Change visibility → Public

### Imagem Muito Grande

**Problema:** Imagem com +500MB

**Solução:**

1. **Usar multi-stage build** (já implementado)

2. **Verificar .dockerignore:**
```
node_modules
dist
.git
```

3. **Usar imagem Alpine:**
```dockerfile
FROM node:18-alpine AS builder
FROM nginx:alpine
```

4. **Ver tamanho:**
```bash
docker images sales-web
```

---

## 📚 Scripts Disponíveis

```bash
# Build local
./scripts/build.sh

# Build Docker
./scripts/docker-build.sh
./scripts/docker-build.sh 1.0.0  # com tag

# Run Docker
./scripts/docker-run.sh
./scripts/docker-run.sh 8080 https://api.example.com/api

# Deploy stack completo
./scripts/deploy-stack.sh dev   # desenvolvimento
./scripts/deploy-stack.sh prod  # produção
```

---

## 🎯 Checklist de Deploy

### Desenvolvimento
- [ ] Docker instalado
- [ ] .env configurado
- [ ] `docker-compose up -d` funcionando
- [ ] Frontend acessível em http://localhost
- [ ] Backend acessível em http://localhost:8080

### CI/CD
- [ ] GitHub Actions habilitado
- [ ] Workflow permissions configurado
- [ ] Push em `main` dispara build
- [ ] Imagem publicada em ghcr.io
- [ ] Deploy hook configurado (se usar Render)

### Produção
- [ ] Variáveis de ambiente configuradas
- [ ] HTTPS habilitado
- [ ] Health checks funcionando
- [ ] Logs sendo monitorados
- [ ] Backup configurado (banco de dados)
- [ ] Domínio customizado (opcional)

---

## 🔗 Links Úteis

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Render Docs](https://render.com/docs)
- [Nginx](https://nginx.org/en/docs/)

---

**Última atualização:** 2026-01-24
**Versão:** 1.0.0

✨ **Deploy completo configurado e funcional!**
