# Sales API - Backend

REST API desenvolvida com Quarkus seguindo **Arquitetura Hexagonal** (Ports & Adapters) e princípios **SOLID**.

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Formas de Executar](#formas-de-executar)
- [Configuração](#configuração)
- [Endpoints](#endpoints)
- [Testes](#testes)
- [Docker](#docker)
- [Banco de Dados](#banco-de-dados)
- [Segurança](#segurança)
- [Troubleshooting](#troubleshooting)

## 🚀 Tecnologias

### Core
- **Java 21** - LTS com Virtual Threads
- **Quarkus 3.17.5** - Framework reativo de alto desempenho
- **PostgreSQL 17** - Banco de dados relacional
- **Hibernate Panache** - ORM simplificado

### Segurança
- **JWT (JSON Web Tokens)** - Autenticação stateless
- **BCrypt** - Hash de senhas
- **SmallRye JWT** - Implementação MicroProfile JWT

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks e stubs
- **RestAssured** - Testes de API REST
- **Testcontainers** - Containers para testes de integração

### Qualidade
- **SonarQube** (opcional) - Análise de código
- **JaCoCo** - Cobertura de testes
- **Maven Enforcer** - Regras de build

## 🏗️ Arquitetura

### Arquitetura Hexagonal (Ports & Adapters)

```
src/main/java/com/sales/
│
├── domain/                          # Núcleo da aplicação (Regras de Negócio)
│   ├── entity/                     # Entidades de domínio
│   │   ├── Customer.java
│   │   ├── Product.java
│   │   ├── Sale.java
│   │   └── User.java
│   │
│   ├── exception/                  # Exceções de domínio
│   │   ├── CustomerNotFoundException.java
│   │   ├── ProductNotFoundException.java
│   │   └── ValidationException.java
│   │
│   └── valueobject/                # Value Objects
│       ├── Email.java
│       ├── Money.java
│       └── SaleStatus.java
│
├── application/                     # Casos de Uso (Lógica de Aplicação)
│   ├── usecase/                    # Casos de uso
│   │   ├── customer/
│   │   │   ├── CreateCustomerUseCase.java
│   │   │   ├── UpdateCustomerUseCase.java
│   │   │   └── DeleteCustomerUseCase.java
│   │   ├── product/
│   │   └── sale/
│   │
│   ├── port/                       # Interfaces (Ports)
│   │   ├── in/                    # Portas de entrada
│   │   │   ├── CustomerService.java
│   │   │   ├── ProductService.java
│   │   │   └── SaleService.java
│   │   │
│   │   └── out/                   # Portas de saída
│   │       ├── CustomerRepository.java
│   │       ├── ProductRepository.java
│   │       └── EmailNotifier.java
│   │
│   └── dto/                        # DTOs de aplicação
│       ├── CustomerDTO.java
│       ├── ProductDTO.java
│       └── SaleDTO.java
│
└── infrastructure/                  # Implementações (Adapters)
    ├── rest/                       # Adaptador REST (entrada)
    │   ├── controller/
    │   │   ├── CustomerController.java
    │   │   ├── ProductController.java
    │   │   └── SaleController.java
    │   │
    │   └── mapper/                # Mapeadores REST
    │       ├── CustomerMapper.java
    │       └── ProductMapper.java
    │
    ├── persistence/                # Adaptador de Persistência (saída)
    │   ├── entity/                # Entidades JPA
    │   │   ├── CustomerEntity.java
    │   │   ├── ProductEntity.java
    │   │   └── SaleEntity.java
    │   │
    │   ├── repository/            # Implementações Panache
    │   │   ├── CustomerPanacheRepository.java
    │   │   ├── ProductPanacheRepository.java
    │   │   └── SalePanacheRepository.java
    │   │
    │   └── mapper/                # Mapeadores de persistência
    │       ├── CustomerEntityMapper.java
    │       └── ProductEntityMapper.java
    │
    ├── security/                   # Adaptador de Segurança
    │   ├── jwt/
    │   │   ├── JwtService.java
    │   │   └── JwtTokenProvider.java
    │   │
    │   └── auth/
    │       ├── AuthController.java
    │       └── UserActivityTracker.java
    │
    ├── email/                      # Adaptador de Email
    │   ├── SmtpEmailNotifier.java
    │   └── MockEmailNotifier.java
    │
    └── config/                     # Configurações
        ├── CorsConfiguration.java
        ├── ExceptionMapper.java
        └── DataSeeder.java
```

### Princípios SOLID Aplicados

- **S**ingle Responsibility: Cada classe tem uma única responsabilidade
- **O**pen/Closed: Extensível via interfaces, fechado para modificação
- **L**iskov Substitution: Implementações intercambiáveis via interfaces
- **I**nterface Segregation: Interfaces específicas (ports)
- **D**ependency Inversion: Dependências apontam para abstrações (ports)

## 🎯 Formas de Executar

### Opção 1: Modo Desenvolvimento Quarkus (Recomendado)

Hot reload automático, Dev UI, e ferramentas de desenvolvimento.

**Pré-requisitos:**
- Java 21
- Maven 3.9+
- Docker (para PostgreSQL)

**Passos:**

```bash
# 1. Configurar variáveis de ambiente
export DB_PASSWORD=dev_password_change_me  # Linux/Mac
set DB_PASSWORD=dev_password_change_me     # Windows CMD
$env:DB_PASSWORD="dev_password_change_me"  # Windows PowerShell

# 2. Subir PostgreSQL
cd docker/dev
docker-compose up -d postgres

# 3. Rodar em modo dev
cd ../..
./mvnw quarkus:dev
```

**Recursos disponíveis:**
- API: http://localhost:8080
- Swagger: http://localhost:8080/q/swagger-ui
- Dev UI: http://localhost:8080/q/dev
- Health: http://localhost:8080/q/health
- Metrics: http://localhost:8080/q/metrics

**Hot Reload:**
- Alterações em código Java são detectadas automaticamente
- Sem necessidade de restart

---

### Opção 2: Docker Compose (Stack Completa)

Backend + PostgreSQL + pgAdmin em containers.

```bash
cd docker/dev
docker-compose up -d
```

**Serviços:**
- API: http://localhost:8080
- PostgreSQL: localhost:5433
- pgAdmin: http://localhost:5050

**Logs:**
```bash
docker-compose logs -f          # Todos os logs
docker-compose logs -f postgres # Apenas PostgreSQL
```

**Parar:**
```bash
docker-compose down             # Parar containers
docker-compose down -v          # Parar e remover volumes (apaga dados)
```

---

### Opção 3: Executar JAR Localmente

Build e execução do JAR sem Docker.

```bash
# 1. Build
./mvnw package -DskipTests

# 2. Configurar variáveis
export DB_PASSWORD=dev_password_change_me
export DB_HOST=localhost
export DB_PORT=5433

# 3. Executar JAR
java -jar target/quarkus-app/quarkus-run.jar
```

---

### Opção 4: Docker Production Build

Build otimizado para produção.

```bash
# Build da imagem
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:latest .

# Executar (necessita PostgreSQL)
docker run -p 8080:8080 \
  -e DB_HOST=postgres \
  -e DB_PASSWORD=your_secure_password \
  -e JWT_ISSUER=sales-api \
  sales-api:latest
```

---

### Opção 5: Native Build (GraalVM)

Compilação nativa para startup ultra-rápido.

**Pré-requisitos:**
- GraalVM 21
- Native Image instalado

```bash
# Build nativo
./mvnw package -Dnative -DskipTests

# Executar binário
./target/sales-api-1.0.0-SNAPSHOT-runner
```

**Benefícios:**
- Startup em milissegundos
- Menor consumo de memória
- Ideal para serverless/containers

---

## ⚙️ Configuração

### Variáveis de Ambiente

**Obrigatórias:**
```bash
DB_PASSWORD=your_secure_password         # Senha do PostgreSQL
```

**Opcionais (com valores padrão):**

```bash
# Database
DB_USERNAME=sales                        # Usuário do banco
DB_HOST=localhost                        # Host do PostgreSQL
DB_PORT=5432                             # Porta do PostgreSQL
DB_NAME=sales_db                         # Nome do banco
DB_MAX_POOL_SIZE=16                      # Tamanho do pool de conexões

# JWT
JWT_ISSUER=sales-api                     # Emissor do token
JWT_EXPIRATION_HOURS=24                  # Expiração do token (horas)
JWT_INACTIVITY_TIMEOUT_MINUTES=15        # Timeout de inatividade (minutos)

# Email
SMTP_FROM=noreply@sales.com              # Email remetente
SMTP_HOST=smtp.gmail.com                 # Servidor SMTP
SMTP_PORT=587                            # Porta SMTP
SMTP_USERNAME=your_email@gmail.com       # Usuário SMTP
SMTP_PASSWORD=your_app_password          # Senha de app do Gmail
SMTP_MOCK=true                           # true = mock, false = email real

# Application
APP_URL=http://localhost:8080            # URL base da aplicação
QUARKUS_HTTP_PORT=8080                   # Porta HTTP
```

### application.properties

Arquivo principal: `src/main/resources/application.properties`

```properties
# Database
quarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:sales_db}
quarkus.datasource.username=${DB_USERNAME:sales}
quarkus.datasource.password=${DB_PASSWORD}

# Hibernate
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.log.sql=false

# JWT
mp.jwt.verify.publickey.location=/META-INF/resources/publicKey.pem
smallrye.jwt.sign.key.location=/META-INF/resources/privateKey.pem

# CORS
quarkus.http.cors=true
quarkus.http.cors.origins=*
```

### Perfis de Configuração

**Desenvolvimento (`%dev`):**
```properties
%dev.quarkus.hibernate-orm.log.sql=true
%dev.quarkus.log.level=DEBUG
```

**Teste (`%test`):**
```properties
%test.quarkus.datasource.devservices.enabled=true
%test.quarkus.hibernate-orm.database.generation=drop-and-create
```

**Produção (`%prod`):**
```properties
%prod.quarkus.hibernate-orm.log.sql=false
%prod.quarkus.log.level=INFO
%prod.quarkus.datasource.devservices.enabled=false
```

---

## 📡 Endpoints

### Autenticação

```http
POST   /api/v1/auth/login              Login com email/senha
POST   /api/v1/auth/register           Registro de novo usuário
POST   /api/v1/auth/forgot-password    Recuperação de senha
POST   /api/v1/auth/reset-password     Reset de senha com token
GET    /api/v1/auth/validate-token     Validar token JWT
```

### Clientes

```http
GET    /api/v1/customers               Listar todos (paginado)
GET    /api/v1/customers/{id}          Buscar por ID
POST   /api/v1/customers               Criar novo cliente
PUT    /api/v1/customers/{id}          Atualizar cliente
DELETE /api/v1/customers/{id}          Deletar cliente
GET    /api/v1/customers/search        Buscar por filtros
```

### Produtos

```http
GET    /api/v1/products                Listar todos (paginado)
GET    /api/v1/products/{id}           Buscar por ID
POST   /api/v1/products                Criar novo produto
PUT    /api/v1/products/{id}           Atualizar produto
DELETE /api/v1/products/{id}           Deletar produto
GET    /api/v1/products/low-stock      Produtos com estoque baixo
```

### Vendas

```http
GET    /api/v1/sales                   Listar todas (paginado)
GET    /api/v1/sales/{id}              Buscar por ID
POST   /api/v1/sales                   Criar nova venda
PUT    /api/v1/sales/{id}              Atualizar venda
DELETE /api/v1/sales/{id}              Cancelar venda
GET    /api/v1/sales/by-customer/{id}  Vendas por cliente
```

### Relatórios

```http
POST   /api/v1/reports/monthly-revenue          Receita mensal
POST   /api/v1/reports/top-selling-products     Top produtos vendidos
POST   /api/v1/reports/customer-purchases       Compras por cliente
POST   /api/v1/reports/sales-by-period          Vendas por período
```

### Health & Metrics

```http
GET    /q/health                       Health check completo
GET    /q/health/live                  Liveness probe
GET    /q/health/ready                 Readiness probe
GET    /q/metrics                      Métricas Prometheus
GET    /q/swagger-ui                   Documentação Swagger
GET    /q/dev                          Dev UI (apenas dev mode)
```

### Exemplos de Requisição

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.silva@email.com",
    "password": "Test@123"
  }'
```

**Criar Cliente (com JWT):**
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "phone": "11999999999",
    "address": "Rua Example, 123"
  }'
```

**Documentação completa:** http://localhost:8080/q/swagger-ui

---

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=CustomerServiceTest

# Testes de integração
./mvnw verify

# Com cobertura
./mvnw clean verify
```

### Relatório de Cobertura

```bash
./mvnw clean verify

# Abrir relatório
# Linux/Mac
open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Estrutura de Testes

```
src/test/java/com/sales/
├── application/
│   └── usecase/
│       ├── CustomerServiceTest.java       # Testes unitários
│       ├── ProductServiceTest.java
│       └── SaleServiceTest.java
│
├── infrastructure/
│   ├── rest/
│   │   ├── CustomerControllerTest.java    # Testes de API
│   │   └── ProductControllerTest.java
│   │
│   └── persistence/
│       └── CustomerRepositoryTest.java    # Testes de repositório
│
└── integration/
    ├── CustomerIntegrationTest.java       # Testes end-to-end
    └── SaleIntegrationTest.java
```

### Testes com Testcontainers

Usa PostgreSQL real em container:

```java
@QuarkusTest
@TestProfile(PostgresTestProfile.class)
class CustomerIntegrationTest {

    @Test
    void shouldCreateCustomer() {
        // Testa contra PostgreSQL real
    }
}
```

---

## 🐳 Docker

### Dockerfiles Disponíveis

1. **Dockerfile.simple** - Build simples para desenvolvimento
2. **Dockerfile.jvm** - Build otimizado JVM
3. **Dockerfile.native** - Build nativo GraalVM
4. **Dockerfile.legacy-jar** - JAR tradicional (fat jar)

### Build Local

```bash
# JVM (recomendado para dev/produção)
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:jvm .

# Native (produção otimizada)
docker build -f docker/dockerfiles/Dockerfile.native -t sales-api:native .
```

### Executar Container

```bash
docker run -d \
  --name sales-api \
  -p 8080:8080 \
  -e DB_HOST=postgres \
  -e DB_PASSWORD=your_password \
  -e JWT_ISSUER=sales-api \
  sales-api:jvm
```

### Docker Compose

**Desenvolvimento:**
```bash
cd docker/dev
docker-compose up -d
```

**Produção:**
```bash
cd docker/prod

# Criar .env com credenciais
cp .env.example .env
nano .env  # Editar credenciais

docker-compose up -d
```

---

## 🗄️ Banco de Dados

### Schema

```sql
-- Customers
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sales
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    total_amount DECIMAL(10,2) NOT NULL,
    sale_date TIMESTAMP NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sale Items
CREATE TABLE sale_items (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT REFERENCES sales(id),
    product_id BIGINT REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);
```

### Migrations

Hibernate gerencia o schema automaticamente:

```properties
# Desenvolvimento - atualiza schema
quarkus.hibernate-orm.database.generation=update

# Produção - valida sem alterar
quarkus.hibernate-orm.database.generation=validate
```

Para controle manual, use Flyway:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-flyway</artifactId>
</dependency>
```

### Backup e Restore

```bash
# Backup
docker exec sales-postgres pg_dump -U sales sales_db > backup.sql

# Restore
docker exec -i sales-postgres psql -U sales sales_db < backup.sql
```

---

## 🔒 Segurança

### JWT Authentication

**Chaves RSA:**
- Privada: `src/main/resources/META-INF/resources/privateKey.pem`
- Pública: `src/main/resources/META-INF/resources/publicKey.pem`

**Gerar novas chaves para produção:**
```bash
# Gerar chave privada
openssl genrsa -out privateKey.pem 2048

# Extrair chave pública
openssl rsa -in privateKey.pem -pubout -out publicKey.pem

# Copiar para resources
cp privateKey.pem src/main/resources/META-INF/resources/
cp publicKey.pem src/main/resources/META-INF/resources/
```

### Proteção de Endpoints

```java
@RolesAllowed("USER")
@GET
@Path("/{id}")
public Response getCustomer(@PathParam("id") Long id) {
    // Apenas usuários autenticados
}
```

### Timeout de Inatividade

Configurado para 15 minutos (personalizável via `JWT_INACTIVITY_TIMEOUT_MINUTES`).

### CORS

```properties
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:4200,https://seu-frontend.com
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
```

### Checklist de Segurança para Produção

- [ ] Gerar novas chaves JWT
- [ ] Configurar HTTPS/TLS
- [ ] Desabilitar seeds de dados
- [ ] Usar secrets manager (AWS, Azure, etc)
- [ ] Configurar CORS específico
- [ ] Habilitar rate limiting
- [ ] Configurar logs de auditoria
- [ ] Revisar permissões de banco
- [ ] Implementar monitoramento
- [ ] Configurar alertas

---

## 🔍 Troubleshooting

### Problema: Porta 8080 em uso

```bash
# Descobrir processo
lsof -i :8080        # Linux/Mac
netstat -ano | findstr :8080  # Windows

# Mudar porta
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

---

### Problema: "DB_PASSWORD not set"

```bash
export DB_PASSWORD=dev_password_change_me
```

Ou crie arquivo `.env` na raiz do projeto:
```bash
DB_PASSWORD=dev_password_change_me
```

---

### Problema: Erro de conexão com PostgreSQL

```bash
# Verificar se PostgreSQL está rodando
docker ps | grep postgres

# Ver logs do PostgreSQL
docker logs sales-postgres

# Testar conexão
psql -h localhost -p 5433 -U sales -d sales_db
```

---

### Problema: Testes falhando

```bash
# Limpar e rebuildar
./mvnw clean compile

# Verificar Testcontainers (Docker deve estar rodando)
docker info

# Executar com logs
./mvnw test -X
```

---

### Problema: Hot reload não funciona

```bash
# Limpar cache do Quarkus
rm -rf .quarkus/

# Reiniciar em modo dev
./mvnw clean quarkus:dev
```

---

## 📚 Documentação Adicional

- [Quarkus Guides](https://quarkus.io/guides/)
- [Hibernate Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [SmallRye JWT](https://quarkus.io/guides/security-jwt)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

## 📝 Licença

MIT

---

## 🤝 Contribuindo

Veja [CONTRIBUTING.md](../CONTRIBUTING.md) para detalhes sobre como contribuir com o projeto.
