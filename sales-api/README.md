# Sales API - Backend

REST API desenvolvida com Quarkus seguindo Arquitetura Hexagonal e princípios SOLID.

## Tecnologias

- Java 21
- Quarkus 3.17.5
- PostgreSQL 17
- Hibernate Panache
- JWT Authentication
- JUnit 5 + Mockito

## Arquitetura

```
src/main/java/com/sales/
├── domain/              # Entidades e regras de negócio
├── application/         # Casos de uso
└── infrastructure/      # Implementações (DB, REST)
```

## Início Rápido

### 1. Configurar Variáveis de Ambiente

```bash
# OBRIGATÓRIO: Defina a senha do banco de dados
export DB_PASSWORD=your_secure_password  # Linux/Mac
# ou
set DB_PASSWORD=your_secure_password     # Windows
```

### 2. Iniciar Serviços

```bash
# Subir PostgreSQL
cd docker/dev
docker-compose up -d

# Rodar em modo dev
cd ../..
./mvnw quarkus:dev
```

**API:** http://localhost:8080
**Swagger:** http://localhost:8080/q/swagger-ui

## Funcionalidades

- CRUD de Clientes
- CRUD de Produtos
- CRUD de Vendas
- Autenticação JWT
- Relatórios gerenciais
- Dashboard com métricas

## Endpoints Principais

```
POST   /api/v1/auth/login                    - Login
GET    /api/v1/customers                     - Listar clientes
POST   /api/v1/customers                     - Criar cliente
GET    /api/v1/products                      - Listar produtos
POST   /api/v1/products                      - Criar produto
GET    /api/v1/sales                         - Listar vendas
POST   /api/v1/sales                         - Criar venda
POST   /api/v1/reports/monthly-revenue       - Receita mensal
```

## Testes

```bash
# Executar testes
./mvnw test

# Gerar relatório de cobertura
./mvnw verify
```

Relatório em: `target/site/jacoco/index.html`

## Configuração

### Variáveis de Ambiente (Obrigatórias)

```bash
# Database (obrigatória)
DB_PASSWORD=your_secure_password

# Opcionais (com valores padrão)
DB_USERNAME=sales
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sales_db

# JWT
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15

# Email (opcional se SMTP_MOCK=true)
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
SMTP_MOCK=true
```

### Credenciais de Desenvolvimento

Para facilitar o desenvolvimento local, o sistema cria automaticamente 10 usuários de teste:

```
Email: john.silva@email.com
Senha: Test@123
```

**Outros usuários de teste:**
- maria.oliveira@email.com
- carlos.mendes@email.com
- pedro.lima@email.com
- juliana.alves@email.com
(Todos com senha: Test@123)

**AVISO DE SEGURANÇA:**
- Esses dados são APENAS para desenvolvimento local
- Seed de usuários em: `DataSeeder.java:seedUsers()`
- **NUNCA** deixe seeds ativos em produção
- Para produção, desabilite o seeding e crie usuários via API

### Criar Usuários em Produção

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "SecurePass@123",
    "customerCode": "CUST001"
  }'
```

## Docker

```bash
# Desenvolvimento
cd docker/dev
docker-compose up -d

# Produção (com variáveis de ambiente)
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api .
docker run -p 8080:8080 \
  -e DB_PASSWORD=your_secure_password \
  -e JWT_ISSUER=sales-api \
  sales-api
```

## Segurança

### Desenvolvimento Local
- **Seeds ativos:** Dados de teste são criados automaticamente (incluindo usuários)
- **Credenciais conhecidas:** Usuários de teste usam senha `Test@123`
- **Apenas local:** Seeds executam apenas se banco estiver vazio
- **Logs de aviso:** Sistema alerta ao criar dados de teste

### Produção
- **Desabilite seeds:** Configure checks para prevenir execução em produção
- **Variáveis obrigatórias:** DB_PASSWORD deve vir de variáveis de ambiente
- **Sem defaults:** Senhas padrão foram removidas de configurações de produção
- **Secrets management:** Use AWS Secrets Manager, Kubernetes Secrets, etc.
- **Nunca commite:** Arquivos .env estão no .gitignore

### Proteções Implementadas
- Formulário de login não preenche credenciais automaticamente
- Senhas hardcoded apenas em seed de desenvolvimento
- Docker compose de produção não possui senhas padrão
- Warnings explícitos nos logs quando seeds são executados

## Documentação Adicional

- [Autenticação](docs/authentication/) - JWT, secrets, timeout
- [Relatórios](docs/reports/) - Relatórios gerenciais
- [Deploy](docs/deployment/) - Docker e produção

## Licença

MIT
