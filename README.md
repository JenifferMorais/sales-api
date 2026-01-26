# Sistema de Vendas

Sistema full-stack de gestão de vendas com backend Java/Quarkus e frontend Angular.

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Formas de Executar](#formas-de-executar)
  - [Opção 1: Docker Compose (Recomendado)](#opção-1-docker-compose-recomendado)
  - [Opção 2: Desenvolvimento Local](#opção-2-desenvolvimento-local)
  - [Opção 3: Docker Produção](#opção-3-docker-produção)
  - [Opção 4: Deploy Render](#opção-4-deploy-render)
- [Configuração](#configuração)
- [Funcionalidades](#funcionalidades)
- [Testes](#testes)
- [CI/CD](#cicd)
- [Troubleshooting](#troubleshooting)

## 🚀 Tecnologias

**Backend**
- Java 21
- Quarkus 3.17.5
- PostgreSQL 17
- JWT Authentication
- Hibernate Panache

**Frontend**
- Angular 19
- TypeScript 5.7
- PrimeNG 17.18.12
- Angular Material 19

**DevOps**
- Docker & Docker Compose
- GitHub Actions
- Render (deploy)

## 📁 Estrutura do Projeto

```
Projeto/
├── sales-api/              # Backend (Quarkus + Java)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/      # Código fonte
│   │   │   └── resources/ # Configurações
│   │   └── test/          # Testes
│   ├── docker/            # Configurações Docker
│   │   ├── dev/           # Docker Compose desenvolvimento
│   │   ├── prod/          # Docker Compose produção
│   │   └── dockerfiles/   # Dockerfiles
│   └── pom.xml
│
├── sales-web/             # Frontend (Angular)
│   ├── src/
│   │   ├── app/          # Código fonte
│   │   └── environments/ # Configurações
│   ├── docker/           # Configurações Docker
│   └── package.json
│
├── .github/              # GitHub Actions workflows
│   └── workflows/
│       ├── backend-ci-cd.yml
│       └── frontend-ci-cd.yml
│
└── render.yaml           # Configuração Render
```

## 🎯 Formas de Executar

### Opção 1: Docker Compose (Recomendado)

Executa backend, frontend e banco de dados com um único comando.

**Pré-requisitos:**
- Docker 20.10+
- Docker Compose 2.0+

**Desenvolvimento:**
```bash
# Backend + PostgreSQL + pgAdmin
cd sales-api/docker/dev
docker-compose up -d

# Frontend (em outro terminal)
cd sales-web
npm install
npm start
```

**Acesso:**
- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- Swagger: http://localhost:8080/q/swagger-ui
- pgAdmin: http://localhost:5050 (admin@sales.com / dev_password_change_me)

**Parar serviços:**
```bash
docker-compose down
```

---

### Opção 2: Desenvolvimento Local

Executa o código diretamente sem Docker (exceto banco de dados).

**Pré-requisitos:**
- Java 21
- Node.js 18+
- Maven 3.9+
- Docker (apenas para PostgreSQL)

**1. Configurar variáveis de ambiente:**
```bash
# Linux/Mac
export DB_PASSWORD=dev_password_change_me
export SMTP_MOCK=true

# Windows (CMD)
set DB_PASSWORD=dev_password_change_me
set SMTP_MOCK=true

# Windows (PowerShell)
$env:DB_PASSWORD="dev_password_change_me"
$env:SMTP_MOCK="true"
```

**2. Iniciar PostgreSQL:**
```bash
cd sales-api/docker/dev
docker-compose up -d postgres
```

**3. Iniciar Backend:**
```bash
cd sales-api
./mvnw quarkus:dev

# Ou no Windows:
mvnw.cmd quarkus:dev
```

**4. Iniciar Frontend:**
```bash
cd sales-web
npm install
npm start
```

**Acesso:**
- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- Swagger: http://localhost:8080/q/swagger-ui

---

### Opção 3: Docker Produção

Build completo com otimizações para produção.

**Backend:**
```bash
cd sales-api

# Build da imagem
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:latest .

# Executar (necessita PostgreSQL rodando)
docker run -p 8080:8080 \
  -e DB_HOST=postgres \
  -e DB_PASSWORD=your_secure_password \
  -e JWT_ISSUER=sales-api \
  sales-api:latest
```

**Frontend:**
```bash
cd sales-web

# Build da imagem
docker build -f docker/prod/Dockerfile -t sales-web:latest .

# Executar
docker run -p 80:80 \
  -e API_URL=http://localhost:8080/api \
  sales-web:latest
```

**Stack completo (backend + frontend + banco):**
```bash
cd sales-api/docker/prod

# Criar arquivo .env com suas credenciais
cat > .env << EOF
DB_PASSWORD=your_secure_password
JWT_ISSUER=sales-api
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
EOF

# Iniciar stack
docker-compose up -d
```

**Acesso:**
- Frontend: http://localhost
- Backend: http://localhost:8080

---

### Opção 4: Deploy Render

Deploy automático via GitHub Actions ou manual.

**A. Deploy Automático (GitHub Actions):**

1. Configure secrets no GitHub:
   - `Settings` → `Secrets and variables` → `Actions`
   - Adicione: `RENDER_BACKEND_DEPLOY_HOOK` e `RENDER_FRONTEND_DEPLOY_HOOK`

2. Push para main:
```bash
git add .
git commit -m "Deploy to Render"
git push origin main
```

O GitHub Actions vai automaticamente:
- ✅ Buildar backend e frontend
- ✅ Rodar testes
- ✅ Criar imagens Docker
- ✅ Fazer deploy no Render

**B. Deploy Manual (Render Dashboard):**

1. Crie conta no [Render](https://render.com)

2. **Backend:**
   - New → Web Service
   - Connect repository: `seu-usuario/sales-api`
   - **Root Directory**: `sales-api`
   - **Dockerfile Path**: `docker/dockerfiles/Dockerfile.simple`
   - **Docker Build Context**: `.`
   - Adicione variáveis de ambiente:
     - `DB_PASSWORD`
     - `JWT_ISSUER`
     - `SMTP_USERNAME`, `SMTP_PASSWORD` (opcional)

3. **Frontend:**
   - New → Web Service
   - Connect repository: `seu-usuario/sales-api`
   - **Root Directory**: `sales-web`
   - **Dockerfile Path**: `docker/prod/Dockerfile`
   - **Docker Build Context**: `.`
   - Adicione variável: `API_URL=https://seu-backend.onrender.com/api`

4. **Banco de Dados:**
   - New → PostgreSQL
   - Nome: `sales-db`
   - Conecte ao backend via variáveis de ambiente

**C. Deploy via Blueprint (render.yaml):**

```bash
# O arquivo render.yaml na raiz já configura tudo
# No Render Dashboard:
# 1. New → Blueprint
# 2. Selecione o repositório
# 3. Render detecta o render.yaml automaticamente
# 4. Configure as variáveis de ambiente necessárias
# 5. Deploy!
```

---

## ⚙️ Configuração

### Variáveis de Ambiente Obrigatórias

**Backend:**
```bash
# Database
DB_PASSWORD=sua_senha_segura         # OBRIGATÓRIO

# Opcionais (com valores padrão)
DB_USERNAME=sales
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sales_db
DB_MAX_POOL_SIZE=16

# JWT
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15

# Email (opcional se SMTP_MOCK=true)
SMTP_FROM=noreply@sales.com
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=seu_email@gmail.com
SMTP_PASSWORD=sua_app_password
SMTP_MOCK=true                       # true para mock (dev), false para email real
```

**Frontend:**

Edite `sales-web/src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  inactivityTimeoutMinutes: 15,
  defaultTheme: 'light' as Theme
};
```

Para produção, edite `environment.prod.ts`.

### Credenciais de Desenvolvimento

O sistema cria automaticamente usuários de teste no primeiro start:

```
Email: john.silva@email.com
Senha: Test@123
```

**Outros usuários:**
- maria.oliveira@email.com
- carlos.mendes@email.com
- pedro.lima@email.com
- juliana.alves@email.com

**⚠️ AVISO DE SEGURANÇA:**
- Apenas para desenvolvimento local
- **NUNCA** use em produção
- Desabilite seeds antes de deploy produção

---

## 🎨 Funcionalidades

- ✅ **Gestão de Clientes** - CRUD completo com validações
- ✅ **Gestão de Produtos** - Controle de estoque e preços
- ✅ **Gestão de Vendas** - Registro e acompanhamento
- ✅ **Autenticação JWT** - Login seguro com timeout de inatividade
- ✅ **Dashboard** - Métricas em tempo real
- ✅ **Relatórios** - 4 tipos de relatórios gerenciais
- ✅ **API REST** - Documentação Swagger/OpenAPI
- ✅ **Responsivo** - Interface adaptável mobile/desktop

---

## 🧪 Testes

**Backend:**
```bash
cd sales-api

# Executar todos os testes
./mvnw test

# Testes com relatório de cobertura
./mvnw verify

# Ver relatório
# Abra: target/site/jacoco/index.html
```

**Frontend:**
```bash
cd sales-web

# Testes unitários
npm test

# Testes com cobertura
npm run test:coverage

# Lint
npm run lint
```

---

## 🔄 CI/CD

O projeto usa GitHub Actions para CI/CD automático.

**Workflows configurados:**

1. **Backend CI/CD** (`.github/workflows/backend-ci-cd.yml`)
   - Trigger: push em `sales-api/**` na branch `main`
   - Passos:
     - ✅ Checkout código
     - ✅ Setup Java 21
     - ✅ Build Maven
     - ✅ Build Docker image
     - ✅ Push para GitHub Container Registry
     - ✅ Deploy Render (via webhook)

2. **Frontend CI/CD** (`.github/workflows/frontend-ci-cd.yml`)
   - Trigger: push em `sales-web/**` na branch `main`
   - Passos:
     - ✅ Checkout código
     - ✅ Setup Node.js 18
     - ✅ Install dependencies
     - ✅ Build produção
     - ✅ Build Docker image
     - ✅ Push para GitHub Container Registry
     - ✅ Deploy Render (via webhook)

**Configurar:**

1. Configure secrets no GitHub:
   ```
   Settings → Secrets → Actions → New repository secret
   ```

2. Adicione:
   - `RENDER_BACKEND_DEPLOY_HOOK` - Webhook URL do backend
   - `RENDER_FRONTEND_DEPLOY_HOOK` - Webhook URL do frontend

3. Workflows executam automaticamente em cada push para `main`

---

## 🔍 Troubleshooting

### Problema: "DB_PASSWORD not set"

**Solução:**
```bash
# Linux/Mac
export DB_PASSWORD=dev_password_change_me

# Windows (CMD)
set DB_PASSWORD=dev_password_change_me

# Windows (PowerShell)
$env:DB_PASSWORD="dev_password_change_me"
```

---

### Problema: Porta 8080 ou 4200 já em uso

**Solução:**
```bash
# Descobrir processo usando a porta
# Linux/Mac
lsof -i :8080
lsof -i :4200

# Windows
netstat -ano | findstr :8080
netstat -ano | findstr :4200

# Matar processo
kill <PID>           # Linux/Mac
taskkill /PID <PID>  # Windows
```

---

### Problema: Docker "Cannot connect to Docker daemon"

**Solução:**
```bash
# Verificar se Docker está rodando
docker ps

# Iniciar Docker Desktop (Windows/Mac)
# Ou iniciar serviço Docker (Linux):
sudo systemctl start docker
```

---

### Problema: Frontend não conecta no backend

**Verificar:**

1. Backend está rodando?
   ```bash
   curl http://localhost:8080/q/health
   ```

2. URL correta no frontend?
   ```typescript
   // sales-web/src/environments/environment.ts
   apiUrl: 'http://localhost:8080/api'  // Deve terminar em /api
   ```

3. CORS configurado? (já configurado no backend)

---

### Problema: "privateKey.pem not found" no build

**Solução:**

Os arquivos JWT não estão no repositório. Adicione:

```bash
git add sales-api/src/main/resources/META-INF/resources/*.pem
git commit -m "Add JWT keys"
git push
```

---

### Problema: Render "permission_denied: write_package"

**Solução:**

Configure permissões do GitHub:

1. `Settings` → `Actions` → `General`
2. Workflow permissions → `Read and write permissions`
3. Salvar

---

## 📚 API Endpoints

### Autenticação
```
POST   /api/v1/auth/login              - Login
POST   /api/v1/auth/register           - Registro
POST   /api/v1/auth/forgot-password    - Recuperar senha
```

### Clientes
```
GET    /api/v1/customers               - Listar
POST   /api/v1/customers               - Criar
GET    /api/v1/customers/{id}          - Buscar
PUT    /api/v1/customers/{id}          - Atualizar
DELETE /api/v1/customers/{id}          - Deletar
```

### Produtos
```
GET    /api/v1/products                - Listar
POST   /api/v1/products                - Criar
GET    /api/v1/products/{id}           - Buscar
PUT    /api/v1/products/{id}           - Atualizar
DELETE /api/v1/products/{id}           - Deletar
```

### Vendas
```
GET    /api/v1/sales                   - Listar
POST   /api/v1/sales                   - Criar
GET    /api/v1/sales/{id}              - Buscar
PUT    /api/v1/sales/{id}              - Atualizar
DELETE /api/v1/sales/{id}              - Deletar
```

### Relatórios
```
POST   /api/v1/reports/monthly-revenue         - Receita mensal
POST   /api/v1/reports/top-selling-products    - Produtos mais vendidos
POST   /api/v1/reports/customer-purchases      - Compras por cliente
POST   /api/v1/reports/sales-by-period         - Vendas por período
```

**Documentação completa:** http://localhost:8080/q/swagger-ui

---

## 🔒 Segurança

**✅ Implementado:**
- Senhas hasheadas (BCrypt)
- JWT com expiração
- Timeout de inatividade (15 min)
- CORS configurado
- Variáveis de ambiente para secrets
- .env no .gitignore

**⚠️ Antes de Produção:**
- [ ] Gerar novas chaves JWT
- [ ] Desabilitar seeds de desenvolvimento
- [ ] Configurar HTTPS
- [ ] Usar secrets manager (AWS Secrets, Azure Key Vault)
- [ ] Revisar permissões de banco
- [ ] Configurar rate limiting
- [ ] Habilitar logs de auditoria

---

## 📝 Licença

MIT

---

## 👥 Contribuindo

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/nova-funcionalidade`
3. Commit: `git commit -m 'Add nova funcionalidade'`
4. Push: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

---

## 📞 Suporte

- Issues: [GitHub Issues](https://github.com/seu-usuario/sales-api/issues)
- Documentação: README.md de cada módulo
  - [Backend](sales-api/README.md)
  - [Frontend](sales-web/README.md)
