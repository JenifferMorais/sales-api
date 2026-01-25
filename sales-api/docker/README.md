# Docker - Backend (Sales API)

Esta pasta contém toda a configuração Docker para o backend da aplicação.

## Estrutura

```
docker/
├── dev/                          # Desenvolvimento Local
│   ├── docker-compose.yml        # PostgreSQL + pgAdmin
│   └── README.md                 # Instruções de desenvolvimento
├── prod/                         # Produção
│   ├── docker-compose.yml        # Setup completo de produção
│   └── README.md                 # Guia de deploy
├── dockerfiles/                  # Imagens Docker
│   ├── Dockerfile.simple         # Build simples (desenvolvimento)
│   ├── Dockerfile.jvm            # Quarkus JVM mode
│   ├── Dockerfile.native         # Quarkus Native mode
│   └── Dockerfile.legacy-jar     # Legacy JAR mode
└── README.md                     # Este arquivo
```

## Uso Rápido

### Desenvolvimento Local

```bash
# Subir apenas PostgreSQL + pgAdmin
cd docker/dev
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f postgres

# Parar
docker-compose down
```

**Acessos**:
- PostgreSQL: `localhost:5432`
- pgAdmin: `http://localhost:5050`
  - Email: admin@sales.com
  - Senha: admin123

### Produção

```bash
# Subir aplicação completa (Backend + PostgreSQL)
cd docker/prod
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f sales-api

# Parar
docker-compose down
```

**Acessos**:
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Health Check: `http://localhost:8080/q/health`

## Build de Imagens

### Dockerfile.simple (Recomendado para Dev)

Build rápido em 2 estágios - ideal para desenvolvimento:

```bash
# Build
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:dev .

# Run
docker run -p 8080:8080 sales-api:dev
```

### Dockerfile.jvm (Quarkus JVM Mode)

Modo JVM padrão - melhor compatibilidade:

```bash
# Build
mvn package
docker build -f docker/dockerfiles/Dockerfile.jvm -t sales-api:jvm .

# Run
docker run -p 8080:8080 sales-api:jvm
```

### Dockerfile.native (Quarkus Native Mode)

Build nativo - melhor performance e menor footprint:

```bash
# Build (requer GraalVM)
mvn package -Pnative
docker build -f docker/dockerfiles/Dockerfile.native -t sales-api:native .

# Run
docker run -p 8080:8080 sales-api:native
```

## Variáveis de Ambiente

### Banco de Dados

```bash
DB_HOST=postgres
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=sales123
DB_MAX_POOL_SIZE=16
```

### JWT

```bash
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15
```

### Email (SMTP)

```bash
SMTP_FROM=noreply@sales.com
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app
SMTP_MOCK=false
```

### Aplicação

```bash
APP_URL=http://localhost:8080
```

## Troubleshooting

### Porta 8080 já está em uso

```bash
# Windows - descobrir processo
netstat -ano | findstr :8080

# Linux/Mac - descobrir processo
lsof -i :8080

# Ou mudar a porta no docker-compose.yml
ports:
  - "8081:8080"
```

### PostgreSQL não conecta

```bash
# Verificar se está rodando
docker-compose ps

# Ver logs do postgres
docker-compose logs postgres

# Restart
docker-compose restart postgres

# Aguardar 10 segundos para inicialização completa
```

### Rebuild completo

```bash
# Parar e remover tudo
docker-compose down -v

# Rebuild e restart
docker-compose up -d --build
```

## Documentação Adicional

- [Desenvolvimento Local](./dev/README.md) - Setup de desenvolvimento
- [Produção](./prod/README.md) - Deploy em produção
- [TROUBLESHOOTING.md](../../TROUBLESHOOTING.md) - Guia completo de problemas

## Comandos Úteis

```bash
# Ver logs em tempo real
docker-compose logs -f

# Executar comando no container
docker-compose exec sales-api sh

# Conectar ao PostgreSQL
docker-compose exec postgres psql -U sales -d sales_db

# Ver uso de recursos
docker stats

# Limpar volumes não utilizados
docker volume prune

# Limpar imagens antigas
docker image prune -a
```
