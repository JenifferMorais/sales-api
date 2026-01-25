# 🚀 CI/CD Completo - Build, Registry e Deploy

## ✅ Implementação Completa

Sistema de CI/CD totalmente automatizado com 3 etapas:

1. **Build** - Maven + Docker
2. **Registry** - GitHub Container Registry (ghcr.io)
3. **Deploy** - Render (webhook automático)

---

## 📊 Fluxo Completo

```
Developer                    GitHub Actions                Registry                  Render
    │                              │                          │                        │
    │  git push origin main        │                          │                        │
    ├─────────────────────────────>│                          │                        │
    │                              │                          │                        │
    │                              │  Build with Maven        │                        │
    │                              │  Run tests               │                        │
    │                              │  Build Docker image      │                        │
    │                              ├─────────────────────────>│                        │
    │                              │                          │                        │
    │                              │  Push to ghcr.io         │                        │
    │                              │<─────────────────────────┤                        │
    │                              │  ✅ Image published      │                        │
    │                              │                          │                        │
    │                              │  Trigger Render Webhook  │                        │
    │                              ├────────────────────────────────────────────────────>│
    │                              │                          │                        │
    │                              │                          │    Pull from ghcr.io   │
    │                              │                          │<───────────────────────┤
    │                              │                          │                        │
    │                              │                          │    Deploy new image    │
    │                              │                          │    ✅ App updated      │
    │                              │                          │                        │
    │  ✅ Deploy complete          │                          │                        │
    │<──────────────────────────────────────────────────────────────────────────────────┤
```

**Tempo total:** ~8-12 minutos do push ao deploy completo

---

## 🔨 Etapa 1: Build

### Arquivo
`.github/workflows/docker-build.yml`

### O Que Faz
1. ✅ Checkout do código
2. ✅ Setup JDK 21
3. ✅ Build com Maven
4. ✅ Executa testes
5. ✅ Build da imagem Docker (multi-stage)
6. ✅ Login no ghcr.io
7. ✅ Tag da imagem (latest, main, sha)
8. ✅ Push para registry

### Trigger
- Push na branch `main`
- Exclui mudanças em markdown e docs

### Tempo
~5-7 minutos

---

## 📦 Etapa 2: Registry

### Container Registry
**GitHub Container Registry (ghcr.io)**

### Tags Geradas
```
ghcr.io/SEU-USUARIO/sales-api:latest
ghcr.io/SEU-USUARIO/sales-api:main
ghcr.io/SEU-USUARIO/sales-api:main-abc1234
```

### Vantagens ghcr.io
- ✅ Integrado com GitHub
- ✅ Automático (usa GITHUB_TOKEN)
- ✅ Gratuito para repositórios públicos
- ✅ 500MB para privados (free tier)
- ✅ Unlimited para públicos

### Alternativa: Docker Hub
Use `.github/workflows/docker-build-dockerhub.yml.example`

---

## 🌐 Etapa 3: Deploy

### Arquivo
`.github/workflows/deploy-render.yml`

### Plataforma
**Render** (https://render.com)

### O Que Faz
1. ✅ Aguarda sucesso do build
2. ✅ Trigger webhook do Render
3. ✅ Render faz pull da imagem
4. ✅ Render faz deploy

### Webhook
- URL fornecida pelo Render
- Armazenada em GitHub Secret: `RENDER_DEPLOY_HOOK_URL`
- Chamada automaticamente após push bem-sucedido

### Tempo
~3-5 minutos

---

## ⚙️ Configuração Inicial

### 1. GitHub (Já Configurado)

Arquivos criados:
- `.github/workflows/docker-build.yml`
- `.github/workflows/deploy-render.yml`
- `src/main/docker/Dockerfile.jvm`
- `.dockerignore`

Configuração necessária:
- Settings → Actions → Workflow permissions → **Read and write**

### 2. Render

**Passo a passo:**

1. **Criar conta:** https://render.com
2. **Criar PostgreSQL:**
   - New → PostgreSQL
   - Name: `sales-db`
   - Plan: Free ou Starter ($7/mês)

3. **Criar Web Service:**
   - New → Web Service
   - Runtime: Docker
   - Image URL: `ghcr.io/SEU-USUARIO/sales-api:latest`

4. **Configurar variáveis:**
   ```
   DB_HOST=<do PostgreSQL>
   DB_PASSWORD=<do PostgreSQL>
   JWT_ISSUER=sales-api
   SMTP_PASSWORD=<seu-email-password>
   APP_URL=https://sua-app.onrender.com
   ```

5. **Copiar Deploy Hook:**
   - Settings → Deploy Hook
   - Copiar URL

6. **Adicionar Secret no GitHub:**
   - Repo → Settings → Secrets → Actions
   - New secret: `RENDER_DEPLOY_HOOK_URL`
   - Value: URL copiada do Render

### 3. Primeiro Deploy

```bash
git add .
git commit -m "ci: adicionar deploy automático no Render"
git push origin main
```

Acompanhar:
1. GitHub Actions: https://github.com/usuario/sales-api/actions
2. Render Dashboard: https://dashboard.render.com

---

## 🔄 Workflow Diário

### Desenvolvimento Normal

```bash
# 1. Desenvolver feature
git checkout -b feature/nova-funcionalidade
# ... código ...
git commit -m "feat: adicionar nova funcionalidade"
git push origin feature/nova-funcionalidade

# 2. Criar Pull Request
# GitHub → Pull Requests → New

# 3. Build automático executa (mas NÃO faz deploy)
# - Testes são executados
# - Docker build é testado
# - Imagem NÃO é publicada

# 4. Merge para main
# GitHub → Merge PR

# 5. Deploy automático!
# - Build executado
# - Imagem publicada no ghcr.io
# - Webhook dispara deploy no Render
# - App atualizada automaticamente
```

### Rollback

```bash
# Opção 1: Via Render Dashboard
# Dashboard → Service → Manual Deploy → Selecionar versão anterior

# Opção 2: Via Git
git revert <commit-hash>
git push origin main
# Pipeline executará automaticamente
```

---

## 📊 Comparação de Plataformas de Deploy

| Plataforma | Free Tier | Auto Deploy | Docker | Preço |
|------------|-----------|-------------|--------|-------|
| **Render** | ✅ 750h/mês | ✅ | ✅ | $7/mês |
| Heroku | ❌ | ✅ | ✅ | $5-$7/mês |
| Railway | ✅ $5 crédito | ✅ | ✅ | $5/mês |
| Fly.io | ✅ $5 crédito | ⚠️ | ✅ | $3/mês |
| AWS ECS | ⚠️ Complexo | ⚠️ | ✅ | Variável |
| Azure | ⚠️ Complexo | ⚠️ | ✅ | Variável |

**Render é recomendado por:**
- ✅ Simplicidade
- ✅ Deploy automático fácil
- ✅ Free tier generoso
- ✅ Integração com Docker
- ✅ PostgreSQL incluído

---

## 🔐 Segurança

### Secrets Configurados

**GitHub:**
- `GITHUB_TOKEN` - Automático
- `RENDER_DEPLOY_HOOK_URL` - Manual

**Render:**
- Todas as variáveis de ambiente
- Database credentials
- SMTP credentials

### Boas Práticas

1. ✅ Nunca commitar secrets
2. ✅ Usar variáveis de ambiente
3. ✅ Webhook URL em secret
4. ✅ Imagens Docker sem secrets
5. ✅ PostgreSQL com senha forte

---

## 📈 Monitoramento

### GitHub Actions

```
Repo → Actions → Workflow
```

Métricas:
- ✅ Build success rate
- ✅ Test pass rate
- ✅ Deploy frequency
- ✅ Time to deploy

### Render

```
Dashboard → Service → Metrics
```

Métricas:
- ✅ CPU usage
- ✅ Memory usage
- ✅ Request count
- ✅ Response time
- ✅ Error rate

### Health Checks

```bash
# Produção
curl https://sua-app.onrender.com/q/health

# Liveness
curl https://sua-app.onrender.com/q/health/live

# Readiness
curl https://sua-app.onrender.com/q/health/ready
```

---

## 🐛 Troubleshooting Comum

### Build Falha

**Causa:** Testes falhando

**Solução:**
```bash
./mvnw clean test
# Consertar testes
git commit -m "fix: corrigir testes"
git push
```

### Deploy Não Dispara

**Causa:** Secret não configurado

**Solução:**
1. Verificar: GitHub → Settings → Secrets
2. Secret `RENDER_DEPLOY_HOOK_URL` existe?
3. URL está correta?

### App Não Inicia no Render

**Causa:** Variáveis de ambiente faltando

**Solução:**
1. Render → Settings → Environment
2. Verificar todas as variáveis necessárias
3. Especialmente: DB_*, SMTP_*, JWT_*

---

## 💡 Dicas

### 1. Proteger Branch Main

```
GitHub → Settings → Branches → Add rule
- Branch name: main
- Require pull request before merging
- Require status checks (build)
```

### 2. Notifications

**GitHub:**
- Settings → Notifications
- Enable Actions notifications

**Render:**
- Settings → Notifications
- Email on deploy failures

**Slack Integration:**
```yaml
# Adicionar ao workflow
- name: Notify Slack
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

### 3. Preview Environments

**Para PRs (opcional):**
```yaml
# .github/workflows/preview.yml
on:
  pull_request:
    types: [opened, synchronize]

jobs:
  deploy-preview:
    # Criar ambiente temporário no Render
    # Útil para testar antes do merge
```

---

## 📝 Checklist Completo

### Setup Inicial
- [ ] GitHub Actions configurado
- [ ] Docker build funcionando
- [ ] Testes passando
- [ ] Conta Render criada
- [ ] PostgreSQL provisionado no Render
- [ ] Web Service criado no Render
- [ ] Variáveis de ambiente configuradas
- [ ] Deploy Hook copiado
- [ ] Secret `RENDER_DEPLOY_HOOK_URL` adicionado
- [ ] Primeiro deploy testado com sucesso

### Deploy Diário
- [ ] Feature desenvolvida em branch
- [ ] Testes locais passando
- [ ] PR criado
- [ ] Build do PR passou
- [ ] Code review aprovado
- [ ] Merge para main
- [ ] Deploy automático executado
- [ ] Health check confirma sucesso
- [ ] Funcionalidade verificada em produção

---

## 📚 Documentação

- [Setup CI/CD](CI_CD_SETUP.md) - GitHub Actions
- [Docker Guide](DOCKER_GUIDE.md) - Build e imagens
- [Render Deploy](RENDER_DEPLOY.md) - Deploy detalhado
- [Deployment Index](README.md) - Índice geral

---

## 🎉 Resultado Final

**Sistema 100% automatizado!**

✅ Push para main
✅ Build automático
✅ Testes automáticos
✅ Docker build automático
✅ Push para registry automático
✅ Deploy automático
✅ App atualizada automaticamente

**Tempo total: 8-12 minutos**

**Desenvolvedor apenas faz push. O resto é automático! 🚀**

---

**Última atualização:** 2026-01-24
**Versão:** 1.0.0
**Status:** ✅ COMPLETO
