# 🚀 Deployment Documentation

Documentação completa sobre deployment, CI/CD e Docker da API de Vendas.

## 📚 Documentos Disponíveis

### 🐳 [DOCKER_GUIDE.md](DOCKER_GUIDE.md)
Guia completo de Docker com:
- Estratégias de build (JVM, Native, Legacy)
- Build local e CI/CD automático
- Deploy em produção (Docker Compose, K8s, AWS, Azure)
- Troubleshooting e boas práticas
- Comparação de estratégias

### 🔄 [CI_CD_SETUP.md](CI_CD_SETUP.md)
Setup do pipeline CI/CD com GitHub Actions:
- Configuração passo a passo
- Sistema de tags automáticas
- Troubleshooting do pipeline
- Segurança e monitoramento

### 🌐 [RENDER_DEPLOY.md](RENDER_DEPLOY.md)
Deploy automático no Render:
- Configuração do serviço no Render
- PostgreSQL no Render
- Webhook para deploy automático
- Variáveis de ambiente
- Monitoramento e troubleshooting

### ✅ [CI_CD_COMPLETO.md](CI_CD_COMPLETO.md)
Visão completa do CI/CD (Build + Registry + Deploy):
- Fluxo completo automatizado
- Integração GitHub Actions + Render
- Configuração inicial
- Workflow diário

## 🎯 Início Rápido

### Para Desenvolvedores

**Build local:**
```bash
./mvnw clean package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t sales-api:latest .
docker run -p 8080:8080 sales-api:latest
```

### Para DevOps/SRE

**Deploy em produção:**
```bash
# 1. Configurar .env
cp .env.example .env
# Editar .env com credenciais

# 2. Deploy
docker compose -f docker-compose.prod.yml up -d

# 3. Verificar
curl http://localhost:8080/q/health
```

### Para CI/CD

**Ativar pipeline automático:**
1. Habilitar GitHub Packages (Settings → Actions)
2. Fazer push na branch `main`
3. Acompanhar em Actions
4. Imagem publicada em `ghcr.io/seu-usuario/sales-api`

## 📋 Checklist de Deploy

### Desenvolvimento
- [ ] Código testado localmente
- [ ] Build Docker funciona
- [ ] Variáveis de ambiente configuradas
- [ ] Health checks passando

### Staging/Produção
- [ ] Secrets configurados (.env ou CI/CD)
- [ ] Banco de dados provisionado
- [ ] Backup configurado
- [ ] Monitoring/logs configurados
- [ ] SSL/TLS configurado
- [ ] Firewall/segurança configurado

## 🏗️ Arquitetura de Deploy

### Componentes

```
┌─────────────────────────────────────────┐
│         GitHub Actions (CI/CD)          │
│  Build → Test → Docker → Push (ghcr.io) │
└──────────────┬──────────────────────────┘
               │
               v
┌─────────────────────────────────────────┐
│    GitHub Container Registry (ghcr.io)  │
│         sales-api:latest               │
└──────────────┬──────────────────────────┘
               │
               v
┌─────────────────────────────────────────┐
│         Production Environment          │
│  ┌──────────────┐   ┌──────────────┐   │
│  │ sales-api   │   │  PostgreSQL  │   │
│  │ (Container)  │──▶│   (Database) │   │
│  └──────────────┘   └──────────────┘   │
└─────────────────────────────────────────┘
```

### Fluxo de Deploy

```
Developer Push (main)
  ↓
GitHub Actions Triggered
  ↓
Build & Test
  ↓
Docker Build
  ↓
Push to ghcr.io
  ↓
Deploy to Production (manual/auto)
  ↓
Health Check
  ↓
✅ Live
```

## 📊 Estratégias Disponíveis

| Estratégia | Tamanho | Startup | Build Time | Uso |
|------------|---------|---------|------------|-----|
| **JVM** | ~350MB | ~3s | ~2min | ⭐ Produção |
| **Native** | ~150MB | ~0.05s | ~10min | Serverless |
| **Legacy** | ~400MB | ~5s | ~2min | Compatibilidade |

## 🔐 Segurança

### Secrets Necessários

**GitHub Actions (automático):**
- `GITHUB_TOKEN` - Fornecido automaticamente

**Produção (.env ou secrets):**
- `DB_PASSWORD` - Senha do banco
- `SMTP_PASSWORD` - Senha do email
- `JWT_ISSUER` - Emissor do JWT

### Scan de Vulnerabilidades

```bash
# Trivy
docker run --rm aquasec/trivy image sales-api:latest

# Snyk
snyk container test sales-api:latest
```

## 🧪 Ambientes

### Local (Development)
```bash
docker-compose up -d  # Usa docker-compose.yml
```

### Staging/Production
```bash
docker compose -f docker-compose.prod.yml up -d
```

### Kubernetes
```bash
kubectl apply -f k8s/deployment.yaml
```

## 📈 Monitoramento

### Health Checks

```bash
# Liveness
curl http://localhost:8080/q/health/live

# Readiness
curl http://localhost:8080/q/health/ready

# Metrics (Prometheus)
curl http://localhost:8080/q/metrics
```

### Logs

```bash
# Docker
docker logs -f sales-api

# Docker Compose
docker compose logs -f sales-api

# Kubernetes
kubectl logs -f deployment/sales-api
```

## 🛠️ Troubleshooting

### Build Falha

**Verificar:**
```bash
./mvnw clean test  # Testes passam?
docker build -f src/main/docker/Dockerfile.jvm -t test .  # Build local funciona?
```

### Container Não Inicia

**Verificar:**
```bash
docker logs sales-api  # Ver logs
docker exec sales-api env  # Ver variáveis
curl http://localhost:8080/q/health  # Health check
```

### Imagem Não Publica

**Verificar:**
- Workflow permissions (Settings → Actions)
- Logs do GitHub Actions
- Personal Access Token (se aplicável)

## 📝 Scripts Úteis

### Deploy Script

```bash
#!/bin/bash
# deploy.sh
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
docker compose -f docker-compose.prod.yml logs -f
```

### Rollback Script

```bash
#!/bin/bash
# rollback.sh
docker compose -f docker-compose.prod.yml down
docker compose -f docker-compose.prod.yml up -d sales-api:previous
```

### Backup Script

```bash
#!/bin/bash
# backup.sh
docker exec sales-postgres pg_dump -U sales sales_db > backup-$(date +%Y%m%d).sql
```

## 🔗 Links Rápidos

- **Swagger UI:** http://localhost:8080/swagger-ui
- **Health:** http://localhost:8080/q/health
- **Metrics:** http://localhost:8080/q/metrics
- **GitHub Actions:** https://github.com/seu-usuario/sales-api/actions
- **Packages:** https://github.com/seu-usuario/sales-api/pkgs/container/sales-api

## 📚 Documentação Relacionada

- [Autenticação](../authentication/AUTHENTICATION.md)
- [Secrets Setup](../authentication/SECRETS_SETUP.md)
- [Arquitetura](../architecture/ARCHITECTURE.md)
- [Relatórios](../reports/RELATORIOS_GERENCIAIS.md)

## 🎓 Tutoriais Recomendados

1. [Setup Inicial](CI_CD_SETUP.md) - Configure o CI/CD
2. [Build Local](DOCKER_GUIDE.md#build-local) - Teste localmente
3. [Deploy Produção](DOCKER_GUIDE.md#deploy-em-produção) - Deploy real

---

**Última atualização:** 2026-01-24
**Versão:** 1.0.0
**Contribuidores:** Equipe Vendas API
