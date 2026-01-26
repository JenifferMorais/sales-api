# Sistema de Vendas

Sistema full-stack de gestão de vendas com backend Java/Quarkus e frontend Angular.

## Tecnologias

**Backend**
- Java 21
- Quarkus 3.17.5
- PostgreSQL 17
- JWT Authentication

**Frontend**
- Angular 19
- TypeScript 5.7
- PrimeNG 17.18.12

## Estrutura

```
Projeto/
├── sales-api/          # Backend (Quarkus + Java)
│   ├── src/
│   ├── docker/
│   └── pom.xml
└── sales-web/          # Frontend (Angular)
    ├── src/
    └── package.json
```

## Quick Start

### 1. Configuração de Segurança

**IMPORTANTE:** Por segurança, todas as credenciais foram removidas do código. Configure as variáveis de ambiente antes de iniciar:

```bash
# Copie o arquivo de exemplo e configure suas credenciais
cd sales-web
cp .env.example .env

# Edite o .env e defina senhas seguras:
# - DB_PASSWORD (senha do banco de dados)
# - SMTP_USERNAME e SMTP_PASSWORD (se usar email real)
```

Para desenvolvimento, você pode usar senhas de desenvolvimento (NÃO use em produção):
```bash
# Exemplo apenas para desenvolvimento local
DB_PASSWORD=dev_password_change_me
```

### 2. Desenvolvimento com Docker (Recomendado)

```bash
# Backend + PostgreSQL
cd sales-api/docker/dev
docker-compose up -d

# Frontend
cd sales-web
npm install
npm start
```

**URLs:**
- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- Swagger: http://localhost:8080/q/swagger-ui

### 3. Desenvolvimento Local

**Backend:**
```bash
cd sales-api
# Configure DB_PASSWORD como variável de ambiente
export DB_PASSWORD=your_dev_password  # Linux/Mac
# ou
set DB_PASSWORD=your_dev_password     # Windows

docker-compose -f docker/dev/docker-compose.yml up -d postgres
./mvnw quarkus:dev
```

**Frontend:**
```bash
cd sales-web
npm install
npm start
```

## Funcionalidades

- Gestão de Clientes (CRUD completo)
- Gestão de Produtos (CRUD completo)
- Gestão de Vendas (CRUD completo)
- Autenticação JWT
- Dashboard com métricas
- Relatórios gerenciais

## Credenciais de Desenvolvimento

Para facilitar o desenvolvimento local, o sistema cria automaticamente dados de teste ao iniciar:

**Usuário de teste:**
```
Email: john.silva@email.com
Senha: Test@123
```

**AVISO DE SEGURANÇA:**
- Esses dados são APENAS para desenvolvimento local
- O seed de usuários está em `DataSeeder.java:seedUsers()`
- **NUNCA** deixe seeds ativos em produção
- Configure a aplicação com `quarkus.profile=prod` e desabilite seeds para produção

### Criar Usuários em Produção

Em produção, crie usuários via API:

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "SecurePass@123",
    "customerCode": "CUST001"
  }'
```

## Testes

**Backend:**
```bash
cd sales-api
mvn test
```

**Frontend:**
```bash
cd sales-web
npm test
```

## Segurança

**Resumo:**
- ✅ Credenciais removidas do código fonte
- ✅ Seeds ativos apenas para desenvolvimento local
- ✅ Senhas obrigatórias via variáveis de ambiente
- ⚠️ Desabilite seeds antes de deploy em produção

## Deploy

Ver documentação específica em:
- Backend: `sales-api/docker/README.md`
- Frontend: `sales-web/docker/README.md`

## API Endpoints

Principais endpoints:
- `/api/v1/auth/login` - Autenticação
- `/api/v1/customers` - Clientes
- `/api/v1/products` - Produtos
- `/api/v1/sales` - Vendas
- `/api/v1/reports/*` - Relatórios

Documentação completa: http://localhost:8080/q/swagger-ui

## Configuração

### Variáveis de Ambiente (Obrigatório)

**Backend:**
```bash
# Obrigatórias
DB_PASSWORD=sua_senha_segura

# Opcionais (com valores padrão)
DB_USERNAME=sales
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sales_db

# JWT
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15
```

**Frontend** (`sales-web/src/environments/environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  inactivityTimeoutMinutes: 15,
  defaultTheme: 'light' as Theme
};
```


## Licença

MIT
