# 🚀 CI/CD Completo - Frontend + Backend

Pipeline completo de CI/CD para o Sistema de Vendas (Frontend Angular + Backend Quarkus).

## 📊 Visão Geral

```
┌─────────────────────────────────────────────────────────────────┐
│                        DEVELOPER                                 │
│                            │                                     │
│                    git push origin main                          │
│                            │                                     │
└────────────────────────────┼─────────────────────────────────────┘
                             │
                             v
┌─────────────────────────────────────────────────────────────────┐
│                     GITHUB ACTIONS                               │
│                                                                  │
│  ┌──────────────────┐              ┌──────────────────┐        │
│  │   Backend CI/CD   │              │  Frontend CI/CD  │        │
│  │                   │              │                  │        │
│  │ 1. Build Maven    │              │ 1. Build Angular │        │
│  │ 2. Run Tests      │              │ 2. Build Docker  │        │
│  │ 3. Build Docker   │              │ 3. Push to ghcr  │        │
│  │ 4. Push to ghcr   │              │ 4. Trigger Deploy│        │
│  └─────────┬─────────┘              └─────────┬────────┘        │
│            │                                   │                 │
└────────────┼───────────────────────────────────┼─────────────────┘
             │                                   │
             v                                   v
┌─────────────────────────────────────────────────────────────────┐
│           GITHUB CONTAINER REGISTRY (ghcr.io)                    │
│                                                                  │
│  📦 ghcr.io/user/sales-api:latest                              │
│  📦 ghcr.io/user/sales-api-web:latest                          │
│                                                                  │
└────────────┬───────────────────────────────────┬─────────────────┘
             │                                   │
             v                                   v
┌─────────────────────────────────────────────────────────────────┐
│                        RENDER CLOUD                              │
│                                                                  │
│  ┌──────────────────┐              ┌──────────────────┐        │
│  │ Backend Service   │              │ Frontend Service │        │
│  │                   │              │                  │        │
│  │ Pull latest image │              │ Pull latest image│        │
│  │ Deploy & Restart  │              │ Deploy & Restart │        │
│  │                   │              │                  │        │
│  │ :8080            │◄─────────────┤ :80              │        │
│  └──────────────────┘              └──────────────────┘        │
│           │                                                      │
│           v                                                      │
│  ┌──────────────────┐                                           │
│  │   PostgreSQL     │                                           │
│  │   Database       │                                           │
│  └──────────────────┘                                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
             │
             v
        ✅ PRODUCTION
```

## ⚡ Fluxo Automático

### 1. Developer Push
```bash
git add .
git commit -m "feat: nova funcionalidade"
git push origin main
```

### 2. GitHub Actions (Paralelo)

**Backend Pipeline:**
- ✅ Checkout código
- ✅ Setup JDK 21
- ✅ Build Maven
- ✅ Run tests
- ✅ Build Docker (multi-stage)
- ✅ Push para ghcr.io/user/sales-api
- ✅ Trigger Render webhook

**Frontend Pipeline:**
- ✅ Checkout código
- ✅ Setup Node.js
- ✅ Build Angular (produção)
- ✅ Build Docker (nginx)
- ✅ Push para ghcr.io/user/sales-api-web
- ✅ Trigger Render webhook

**Tempo total:** ~8-12 minutos

### 3. Deploy Automático (Render)

**Backend:**
- Pull imagem do ghcr.io
- Deploy nova versão
- Health check
- ✅ Live em https://sales-api.onrender.com

**Frontend:**
- Pull imagem do ghcr.io
- Deploy nova versão
- Health check
- ✅ Live em https://sales-web.onrender.com

---

## 🔧 Configuração Inicial

### Passo 1: GitHub Actions

**1.1 Backend (sales-api):**

Criar `.github/workflows/docker-build.yml`:
```yaml
name: Build and Push Docker Image
on:
  push:
    branches: [main]
jobs:
  build-and-push:
    # ... (já configurado)
```

**1.2 Frontend (sales-web):**

Criar `.github/workflows/docker-build-frontend.yml`:
```yaml
name: Build and Push Frontend Docker Image
on:
  push:
    branches: [main]
jobs:
  build-and-push:
    # ... (já configurado)
```

**1.3 Habilitar Permissões:**

Em AMBOS os repositórios:
- Settings → Actions → General
- Workflow permissions → **Read and write permissions**
- ✅ Save

### Passo 2: Render - Backend

**2.1 Criar PostgreSQL:**
```
Dashboard → New → PostgreSQL
Name: sales-db
Plan: Free ou Starter ($7/mês)
```

**2.2 Criar Web Service (Backend):**
```
Dashboard → New → Web Service
Runtime: Docker
Image URL: ghcr.io/SEU-USUARIO/sales-api:latest
```

**2.3 Variáveis de Ambiente:**
```
DB_HOST=<postgres-host>
DB_PASSWORD=<postgres-password>
JWT_ISSUER=sales-api
SMTP_PASSWORD=<senha-app>
```

**2.4 Copiar Deploy Hook:**
```
Settings → Deploy Hook
Copiar URL
```

**2.5 Adicionar no GitHub:**
```
Repo sales-api → Settings → Secrets → Actions
New: RENDER_DEPLOY_HOOK_URL = <url-copiada>
```

### Passo 3: Render - Frontend

**3.1 Criar Web Service (Frontend):**
```
Dashboard → New → Web Service
Runtime: Docker
Image URL: ghcr.io/SEU-USUARIO/sales-api-web:latest
```

**3.2 Variáveis de Ambiente:**
```
API_URL=https://sales-api.onrender.com/api
```

**3.3 Copiar Deploy Hook:**
```
Settings → Deploy Hook
Copiar URL
```

**3.4 Adicionar no GitHub:**
```
Repo sales-web → Settings → Secrets → Actions
New: RENDER_DEPLOY_HOOK_URL_FRONTEND = <url-copiada>
```

### Passo 4: Testar

```bash
# Backend
cd sales-api
git commit -m "test: CI/CD" --allow-empty
git push origin main

# Frontend
cd sales-web
git commit -m "test: CI/CD" --allow-empty
git push origin main
```

Acompanhar em:
- GitHub → Actions
- Render → Dashboard

---

## 🎯 Workflow Diário

### Feature Development

```bash
# 1. Criar branch
git checkout -b feature/nova-funcionalidade

# 2. Desenvolver
# ... código ...

# 3. Commit e push
git add .
git commit -m "feat: adicionar nova funcionalidade"
git push origin feature/nova-funcionalidade

# 4. Criar Pull Request
# GitHub → New Pull Request

# 5. Build automático executa (sem deploy)
# - Tests rodam
# - Docker build testa
# - Imagem NÃO é publicada

# 6. Code Review + Merge
# GitHub → Merge PR

# 7. Deploy automático!
# - Build
# - Push para ghcr.io
# - Deploy no Render
# - ✅ Live
```

### Hotfix

```bash
# 1. Branch de hotfix
git checkout -b hotfix/corrigir-bug-critico

# 2. Corrigir
# ... código ...

# 3. Push direto para main (emergência)
git checkout main
git merge hotfix/corrigir-bug-critico
git push origin main

# Deploy automático em ~10 min
```

### Rollback

**Opção 1: Via Render (Rápido)**
```
Dashboard → Service → Manual Deploy → Selecionar versão anterior
```

**Opção 2: Via Git**
```bash
git revert <commit-hash>
git push origin main
# Pipeline executa automaticamente
```

**Opção 3: Tag Específica**
```bash
# Pull da imagem anterior
docker pull ghcr.io/user/sales-api:main-abc1234

# Atualizar Render para usar essa tag
# Settings → Image URL → ghcr.io/user/sales-api:main-abc1234
```

---

## 📦 Imagens Docker

### Tags Geradas

**Backend:**
```
ghcr.io/user/sales-api:latest
ghcr.io/user/sales-api:main
ghcr.io/user/sales-api:main-abc1234
```

**Frontend:**
```
ghcr.io/user/sales-api-web:latest
ghcr.io/user/sales-api-web:main
ghcr.io/user/sales-api-web:main-abc1234
```

### Gerenciamento

**Ver imagens:**
```
GitHub → Packages
```

**Limpar imagens antigas:**
```
Package → Settings → Manage versions
Delete old versions
```

**Tornar pública (se necessário):**
```
Package → Settings → Change visibility → Public
```

---

## 🔒 Segurança

### Secrets Configurados

**GitHub (Backend):**
- ✅ `GITHUB_TOKEN` - Automático
- ✅ `RENDER_DEPLOY_HOOK_URL` - Manual

**GitHub (Frontend):**
- ✅ `GITHUB_TOKEN` - Automático
- ✅ `RENDER_DEPLOY_HOOK_URL_FRONTEND` - Manual

**Render (Backend):**
- ✅ DB_PASSWORD
- ✅ SMTP_PASSWORD
- ✅ JWT_ISSUER

**Render (Frontend):**
- ✅ API_URL

### Boas Práticas

1. ✅ Nunca commitar secrets
2. ✅ Usar variáveis de ambiente
3. ✅ Secrets em GitHub Actions
4. ✅ Imagens Docker sem secrets
5. ✅ PostgreSQL com senha forte
6. ✅ HTTPS em produção

---

## 📊 Monitoramento

### GitHub Actions

```
Repo → Actions
```

Métricas:
- ✅ Build success rate
- ✅ Test pass rate
- ✅ Deploy frequency
- ✅ Time to deploy

### Render

```
Dashboard → Services → Metrics
```

Métricas:
- ✅ CPU usage
- ✅ Memory usage
- ✅ Request count
- ✅ Response time
- ✅ Error rate

### Health Checks

```bash
# Frontend
curl https://sales-web.onrender.com/health

# Backend
curl https://sales-api.onrender.com/q/health/live
curl https://sales-api.onrender.com/q/health/ready
```

---

## 🐛 Troubleshooting

### Build Falha (Backend)

**Problema:** Tests falhando

**Solução:**
```bash
./mvnw clean test
# Consertar testes
git commit -m "fix: corrigir testes"
git push
```

### Build Falha (Frontend)

**Problema:** Build Angular falha

**Solução:**
```bash
npm run build
# Verificar erros
git commit -m "fix: corrigir build"
git push
```

### Deploy Não Dispara

**Problema:** Webhook não configurado

**Solução:**
1. GitHub → Settings → Secrets
2. Verificar `RENDER_DEPLOY_HOOK_URL` (backend)
3. Verificar `RENDER_DEPLOY_HOOK_URL_FRONTEND` (frontend)

### App Não Inicia no Render

**Problema:** Variáveis de ambiente

**Solução:**
1. Render → Service → Environment
2. Verificar todas as variáveis
3. Especialmente DB_*, SMTP_*, JWT_*

### CORS Error

**Problema:** Frontend não consegue acessar backend

**Solução:**

Backend `application.properties`:
```properties
quarkus.http.cors.origins=https://sales-web.onrender.com
```

---

## 💡 Dicas e Otimizações

### 1. Proteger Branch Main

```
GitHub → Settings → Branches
Add rule:
- Branch name: main
- Require pull request before merging
- Require status checks (build)
```

### 2. Notificações

**GitHub:**
- Settings → Notifications
- Enable Actions notifications

**Render:**
- Settings → Notifications
- Email on deploy failures

### 3. Cache de Dependências

Já configurado nos workflows:
```yaml
cache-from: type=gha
cache-to: type=gha,mode=max
```

### 4. Deploy Preview (PRs)

Criar workflow para preview:
```yaml
# .github/workflows/preview.yml
on:
  pull_request:
    types: [opened, synchronize]
jobs:
  deploy-preview:
    # Deploy ambiente temporário
```

---

## 📋 Checklist Completo

### Setup Inicial

**Backend:**
- [ ] Workflow docker-build.yml criado
- [ ] GitHub Actions permissions configurado
- [ ] PostgreSQL no Render provisionado
- [ ] Web Service backend criado
- [ ] Variáveis de ambiente configuradas
- [ ] Deploy Hook copiado
- [ ] Secret RENDER_DEPLOY_HOOK_URL adicionado
- [ ] Primeiro deploy testado

**Frontend:**
- [ ] Workflow docker-build-frontend.yml criado
- [ ] GitHub Actions permissions configurado
- [ ] Web Service frontend criado
- [ ] Variável API_URL configurada
- [ ] Deploy Hook copiado
- [ ] Secret RENDER_DEPLOY_HOOK_URL_FRONTEND adicionado
- [ ] Primeiro deploy testado

### Deploy Diário

- [ ] Feature desenvolvida em branch
- [ ] Testes locais passando
- [ ] PR criado
- [ ] Build do PR passou
- [ ] Code review aprovado
- [ ] Merge para main
- [ ] Deploy automático executado (backend)
- [ ] Deploy automático executado (frontend)
- [ ] Health checks confirmam sucesso
- [ ] Funcionalidade verificada em produção

---

## 🎉 Resultado Final

**Sistema 100% Automatizado!**

✅ Push para main
✅ Build automático (backend + frontend)
✅ Tests automáticos
✅ Docker build automático
✅ Push para registry automático
✅ Deploy automático (backend + frontend)
✅ Apps atualizadas automaticamente

**Tempo total: 10-15 minutos**
**Intervenção manual: ZERO** 🚀

---

**Última atualização:** 2026-01-24
**Versão:** 1.0.0
**Status:** ✅ COMPLETO
