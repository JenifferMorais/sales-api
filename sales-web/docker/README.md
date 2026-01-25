# Docker - Frontend (Sales Web)

Esta pasta contém toda a configuração Docker para o frontend da aplicação.

## Estrutura

```
docker/
├── dev/                          # Desenvolvimento
│   ├── docker-compose.yml        # Full-stack (Frontend + Backend + DB)
│   ├── docker-compose.local.yml  # Apenas frontend
│   ├── Dockerfile                # Build de desenvolvimento
│   └── README.md                 # Instruções de desenvolvimento
├── prod/                         # Produção
│   ├── docker-compose.yml        # Setup de produção
│   ├── Dockerfile                # Build otimizado para produção
│   └── README.md                 # Guia de deploy
└── README.md                     # Este arquivo
```

## Uso Rápido

### Desenvolvimento Local (Sem Docker)

**Recomendado** para desenvolvimento com hot reload:

```bash
cd sales-web
npm install
npm start
```

Acesse: http://localhost:4200

### Desenvolvimento com Docker (Full-stack)

Roda tudo em Docker: Frontend + Backend + PostgreSQL

```bash
cd sales-web/docker/dev
docker-compose up -d
```

**Acessos**:
- Frontend: http://localhost:80
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/q/swagger-ui
- PostgreSQL: localhost:5432

### Produção

Deploy completo em produção:

```bash
cd sales-web/docker/prod
docker-compose up -d
```

**Acessos**:
- Frontend: http://localhost:80 (e :443 se SSL configurado)
- Backend: http://localhost:8080

## Dockerfiles

### dev/Dockerfile

Build de desenvolvimento:
- Node 18 Alpine
- Build da aplicação Angular
- Serve com Nginx
- Tamanho: ~50MB

```bash
# Build
cd sales-web
docker build -f docker/dev/Dockerfile -t sales-web:dev .

# Run
docker run -p 4200:80 sales-web:dev
```

### prod/Dockerfile

Build otimizado para produção:
- Multi-stage build
- Otimizações de produção
- Compressão gzip
- Cache headers configurados
- Tamanho: ~30MB

```bash
# Build
cd sales-web
docker build -f docker/prod/Dockerfile -t sales-web:prod .

# Run
docker run -p 80:80 sales-web:prod
```

## Variáveis de Ambiente

### Frontend

```bash
API_URL=http://localhost:8080/api
```

### Backend (quando incluso)

```bash
# Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=sales123

# JWT
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15

# Email
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_FROM=noreply@sales.com
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app
SMTP_MOCK=true
```

## Nginx Configuration

O Nginx é configurado para:
- Servir arquivos estáticos
- Redirecionamento para index.html (SPA routing)
- Compressão gzip
- Cache headers otimizados
- Fallback para /index.html em 404

Configuração em: `nginx.conf`

## Troubleshooting

### Erro "Cannot connect to API"

Verifique a variável `API_URL`:

```bash
# Verificar variável de ambiente
docker-compose config

# Deve apontar para o backend correto
# Dev: http://localhost:8080/api
# Prod: https://sua-api.com/api
```

### Porta 80 já está em uso

```bash
# Mudar porta no docker-compose.yml
ports:
  - "8081:80"
```

### Build falha

```bash
# Limpar cache e rebuild
docker-compose build --no-cache frontend

# Ver logs de build
docker-compose up --build
```

### Página em branco

```bash
# Verificar logs do Nginx
docker-compose logs frontend

# Verificar se os arquivos foram copiados corretamente
docker-compose exec frontend ls -la /usr/share/nginx/html
```

## Comandos Úteis

### Ver logs

```bash
# Todos os logs
docker-compose logs -f

# Apenas frontend
docker-compose logs -f frontend

# Últimas 100 linhas
docker-compose logs --tail=100 frontend
```

### Rebuild

```bash
# Rebuild completo
docker-compose up -d --build

# Rebuild sem cache
docker-compose build --no-cache
docker-compose up -d
```

### Executar comandos no container

```bash
# Acessar shell do container
docker-compose exec frontend sh

# Ver conteúdo servido
docker-compose exec frontend ls -la /usr/share/nginx/html

# Ver configuração do Nginx
docker-compose exec frontend cat /etc/nginx/nginx.conf
```

## Documentação Adicional

- [Desenvolvimento](./dev/README.md) - Setup de desenvolvimento com Docker
- [Produção](./prod/README.md) - Deploy em produção
- [Backend Docker](../../sales-api/docker/README.md) - Docker do backend
- [TROUBLESHOOTING.md](../../TROUBLESHOOTING.md) - Guia de problemas

## Comparação: Docker vs Local

| Aspecto | Docker | Local (npm start) |
|---------|--------|-------------------|
| Hot Reload | ❌ Não | ✅ Sim |
| Velocidade Build | 🐌 Lento (~2min) | ⚡ Rápido (~30s) |
| Isolamento | ✅ Completo | ❌ Depende do sistema |
| Produção-like | ✅ Nginx + build otimizado | ❌ Dev server |
| Fácil setup | ✅ Um comando | ⚠️ Requer Node instalado |

**Recomendação**:
- **Desenvolvimento**: Use `npm start` (hot reload)
- **Testes de integração**: Use Docker full-stack
- **Produção**: Use Docker com build otimizado
