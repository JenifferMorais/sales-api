# 🚀 Deploy Automático no Render

Guia completo para configurar deploy automático no Render usando webhook.

## 📋 Visão Geral

O fluxo de CI/CD completo:

```
Push to main
    ↓
GitHub Actions: Build & Push
    ↓
GitHub Container Registry (ghcr.io)
    ↓
Trigger Render Webhook
    ↓
Render: Pull & Deploy
    ↓
✅ App Live
```

---

## 🔧 Passo 1: Configurar Serviço no Render

### 1.1 Criar Conta no Render

1. Acesse: https://render.com
2. Clique em **Sign Up**
3. Conecte com GitHub

### 1.2 Criar Web Service

1. No Dashboard, clique em **New** → **Web Service**
2. Conecte seu repositório GitHub
3. Configure:

**Configurações Básicas:**
```
Name: sales-api
Region: Oregon (US West) ou Frankfurt (Europe Central)
Branch: main
```

**Runtime:**
```
Runtime: Docker
Docker Command: (deixar vazio, usa ENTRYPOINT do Dockerfile)
```

**Recursos:**
```
Instance Type: Starter (Free) ou Standard ($7/mês)
```

### 1.3 Configurar Imagem Docker

**Opção A: Build no Render (Não Recomendado)**
```
Build Command: docker build -f src/main/docker/Dockerfile.jvm -t sales-api .
```

**Opção B: Usar Imagem do GitHub Registry (Recomendado)**

1. Na seção **Advanced**, em **Image URL**:
```
ghcr.io/SEU-USUARIO/sales-api:latest
```

2. Se a imagem for privada, adicione credenciais:
   - **Registry Username:** seu-usuario-github
   - **Registry Password:** ${{ secrets.GITHUB_TOKEN }} (Personal Access Token)

### 1.4 Configurar Variáveis de Ambiente

No Render, adicione as seguintes variáveis:

```
DB_HOST=<seu-postgres-host>
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=<senha-forte>

JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15

SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_FROM=noreply@sales.com
SMTP_USERNAME=<seu-email>
SMTP_PASSWORD=<senha-app>
SMTP_MOCK=false

APP_URL=https://sua-app.onrender.com
```

---

## 🗄️ Passo 2: Configurar Banco de Dados PostgreSQL

### Opção A: PostgreSQL no Render (Recomendado)

1. **New** → **PostgreSQL**
2. Configure:
```
Name: sales-db
Database: sales_db
User: sales
Region: Same as web service
Plan: Free (até 90 dias) ou Starter ($7/mês)
```

3. Após criar, copie as credenciais:
   - **Internal Database URL** (para usar dentro do Render)
   - **External Database URL** (para acessar externamente)

4. Use a **Internal Database URL** na variável `DATABASE_URL` ou configure individualmente:
```
DB_HOST=dpg-xxxxxxxx-xxxx.oregon-postgres.render.com
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=<gerado-pelo-render>
```

### Opção B: PostgreSQL Externo

Use qualquer PostgreSQL:
- AWS RDS
- Azure Database
- Google Cloud SQL
- ElephantSQL
- Supabase

---

## 🔗 Passo 3: Obter Deploy Hook URL

1. No serviço criado, acesse **Settings**
2. Role até **Deploy Hook**
3. Copie a URL (formato: `https://api.render.com/deploy/srv-xxxxx?key=yyyyy`)

---

## 🔐 Passo 4: Configurar Secret no GitHub

1. No seu repositório GitHub, acesse **Settings** → **Secrets and variables** → **Actions**
2. Clique em **New repository secret**
3. Adicione:
   - **Name:** `RENDER_DEPLOY_HOOK_URL`
   - **Value:** Cole a URL do Deploy Hook

---

## ✅ Passo 5: Testar o Fluxo Completo

### 5.1 Fazer Push para Main

```bash
git add .
git commit -m "feat: configurar deploy automático no Render"
git push origin main
```

### 5.2 Acompanhar Pipeline

1. **GitHub Actions:**
   - Acesse: `https://github.com/seu-usuario/sales-api/actions`
   - Verifique workflow "Build and Push Docker Image"
   - Aguarde conclusão (~5-7 min)

2. **Webhook Trigger:**
   - Após build, workflow "Deploy to Render" é executado
   - Webhook é chamado automaticamente

3. **Render Deploy:**
   - Acesse: `https://dashboard.render.com`
   - Clique no serviço `sales-api`
   - Aba **Logs** mostra deploy em andamento
   - Aguarde conclusão (~3-5 min)

### 5.3 Verificar Aplicação

```bash
# Health check
curl https://sua-app.onrender.com/q/health

# Swagger
# Acesse: https://sua-app.onrender.com/swagger-ui
```

---

## 🔄 Como Funciona

### Fluxo Automático

1. **Developer Push:**
```bash
git push origin main
```

2. **GitHub Actions (Workflow 1):**
```yaml
# .github/workflows/docker-build.yml
- Build com Maven
- Run tests
- Build Docker image
- Push para ghcr.io
- ✅ Sucesso
```

3. **GitHub Actions (Workflow 2):**
```yaml
# .github/workflows/deploy-render.yml
- Aguarda sucesso do Workflow 1
- Chama webhook do Render
- ✅ Deploy triggered
```

4. **Render:**
```
- Recebe webhook
- Pull da imagem ghcr.io/usuario/sales-api:latest
- Restart com nova imagem
- ✅ App atualizada
```

### Tempo Total
- Build & Push: ~5-7 min
- Deploy Render: ~3-5 min
- **Total: ~8-12 minutos** do push ao deploy completo

---

## 🛠️ Configurações Avançadas

### Health Check no Render

Configure no `render.yaml`:

```yaml
services:
  - type: web
    name: sales-api
    runtime: docker
    dockerfilePath: ./src/main/docker/Dockerfile.jvm
    healthCheckPath: /q/health/ready
    envVars:
      - key: DATABASE_URL
        fromDatabase:
          name: sales-db
          property: connectionString
```

### Autoscaling

No Render, configure:
```
Instance Count: 1-3
Auto Deploy: Yes
```

### Custom Domain

1. **Render Dashboard** → Service → **Settings** → **Custom Domain**
2. Adicione: `api.seudominio.com`
3. Configure DNS:
```
Type: CNAME
Name: api
Value: sua-app.onrender.com
```

### SSL/TLS

- ✅ Automático no Render (Let's Encrypt)
- Certificado renovado automaticamente

---

## 🔍 Monitoramento

### Logs no Render

```bash
# Via Dashboard
Render Dashboard → Service → Logs (live)

# Via CLI (opcional)
render-cli logs --service sales-api --tail
```

### Metrics

No Dashboard:
- CPU Usage
- Memory Usage
- Request Count
- Response Time
- Error Rate

### Alerts

Configure em **Settings** → **Notifications**:
- Email em deploys falhados
- Slack/Discord webhooks
- PagerDuty integration

---

## 🐛 Troubleshooting

### Deploy Falha

**Verificar logs:**
```
Render Dashboard → Logs
```

**Causas comuns:**
1. Variáveis de ambiente faltando
2. Banco de dados inacessível
3. Porta errada (Render usa porta do binding, não 8080)
4. Imagem Docker com erro

**Solução:**
```bash
# Testar localmente
docker run -p 8080:8080 ghcr.io/usuario/sales-api:latest

# Verificar variáveis
# Render → Settings → Environment Variables
```

### App Não Responde

**Verificar health:**
```bash
curl https://sua-app.onrender.com/q/health
```

**Causas:**
1. Health check path errado
2. App levando muito tempo para iniciar
3. Memória insuficiente

**Solução:**
```
# Aumentar timeout do health check
Render → Settings → Health Check Grace Period: 300s

# Ou aumentar recursos
Upgrade para Standard instance
```

### Webhook Não Dispara

**Verificar:**
1. Secret `RENDER_DEPLOY_HOOK_URL` está configurado?
2. URL está correta?
3. Workflow "Deploy to Render" executou?

**Testar webhook manualmente:**
```bash
curl -X POST "https://api.render.com/deploy/srv-xxxxx?key=yyyyy"
```

### Deploy Lento

**Otimizar:**
1. Use imagem do registry (não build no Render)
2. Use instância mais potente
3. Configure cache de dependências

---

## 💰 Custos

### Free Tier
- ✅ Web Service: Free (com limitações)
  - 750 horas/mês
  - Sleep após 15 min inativo
  - 512 MB RAM
  - 0.5 CPU

- ✅ PostgreSQL: Free (90 dias)
  - 1 GB storage
  - Expira após 90 dias

### Paid Plans

**Starter ($7/mês por serviço):**
- Sem sleep
- 512 MB RAM
- 0.5 CPU
- Custom domains

**Standard ($25/mês):**
- 2 GB RAM
- 1 CPU
- Autoscaling

**PostgreSQL Starter ($7/mês):**
- 1 GB storage
- 1 GB RAM
- Sem limite de tempo

---

## 📊 Comparação: Render vs Outras Plataformas

| Feature | Render | Heroku | Railway | Fly.io |
|---------|--------|--------|---------|--------|
| **Free Tier** | ✅ 750h | ❌ Removido | ✅ $5 crédito | ✅ $5 crédito |
| **Auto Deploy** | ✅ | ✅ | ✅ | ⚠️ Manual |
| **Docker** | ✅ | ✅ | ✅ | ✅ |
| **PostgreSQL** | ✅ | ✅ | ✅ | ✅ |
| **SSL** | ✅ Auto | ✅ Auto | ✅ Auto | ✅ Auto |
| **Preço Inicial** | $7/mês | $5/mês | $5/mês | $3/mês |

---

## 🚀 Deploy Manual (Sem GitHub Actions)

### Via CLI

```bash
# Instalar CLI
npm install -g render-cli

# Login
render login

# Deploy
render deploy --service sales-api
```

### Via Dashboard

1. **Render Dashboard** → Service
2. Clique em **Manual Deploy**
3. Selecione branch/commit
4. Clique em **Deploy**

---

## 📝 Arquivo render.yaml (Opcional)

Crie `render.yaml` na raiz para Infrastructure as Code:

```yaml
services:
  # Web Service
  - type: web
    name: sales-api
    runtime: docker
    dockerfilePath: ./src/main/docker/Dockerfile.jvm
    dockerContext: .
    envVars:
      - key: DB_HOST
        fromDatabase:
          name: sales-db
          property: host
      - key: DB_PORT
        fromDatabase:
          name: sales-db
          property: port
      - key: DB_NAME
        fromDatabase:
          name: sales-db
          property: database
      - key: DB_USERNAME
        fromDatabase:
          name: sales-db
          property: user
      - key: DB_PASSWORD
        fromDatabase:
          name: sales-db
          property: password
      - key: JWT_ISSUER
        value: sales-api
      - key: JWT_EXPIRATION_HOURS
        value: 24
      - key: JWT_INACTIVITY_TIMEOUT_MINUTES
        value: 15
    healthCheckPath: /q/health/ready
    autoDeploy: true

  # PostgreSQL
databases:
  - name: sales-db
    databaseName: sales_db
    user: sales
    plan: starter
```

---

## ✅ Checklist de Deploy

### Antes do Primeiro Deploy
- [ ] Conta no Render criada
- [ ] Repositório GitHub conectado
- [ ] PostgreSQL provisionado
- [ ] Variáveis de ambiente configuradas
- [ ] Deploy hook URL copiado
- [ ] Secret `RENDER_DEPLOY_HOOK_URL` no GitHub
- [ ] Workflow `.github/workflows/deploy-render.yml` commitado

### Teste de Deploy
- [ ] Push para main
- [ ] GitHub Actions executou com sucesso
- [ ] Webhook disparado
- [ ] Deploy no Render concluído
- [ ] Health check retorna 200
- [ ] Swagger UI acessível
- [ ] API responde corretamente

### Pós-Deploy
- [ ] Custom domain configurado (opcional)
- [ ] Monitoring configurado
- [ ] Alerts configurados
- [ ] Backup do banco configurado
- [ ] Documentação atualizada

---

## 🔗 Links Úteis

- **Render Dashboard:** https://dashboard.render.com
- **Render Docs:** https://render.com/docs
- **Render Status:** https://status.render.com
- **Render CLI:** https://github.com/render-oss/render-cli
- **Support:** https://render.com/support

---

**Última atualização:** 2026-01-24
**Versão:** 1.0.0

🎉 **Deploy automático completo e funcional!**
