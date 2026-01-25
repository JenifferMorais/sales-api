# Docker - Desenvolvimento (Frontend)

Configuração Docker para desenvolvimento do frontend.

## Opções de Desenvolvimento

### Opção 1: Local (Recomendado) ⭐

**Vantagens**: Hot reload, build rápido, fácil debug

```bash
cd sales-web
npm install
npm start
```

Acesse: http://localhost:4200

### Opção 2: Docker Full-stack

**Vantagens**: Tudo junto, ambiente isolado, produção-like

```bash
cd sales-web/docker/dev
docker-compose up -d
```

Acesse: http://localhost:80

## Full-stack Docker Setup

Este setup contém:
- **Frontend** (Angular + Nginx)
- **Backend** (Quarkus)
- **PostgreSQL** (Banco de dados)

### Como usar

```bash
# 1. Subir tudo
cd sales-web/docker/dev
docker-compose up -d

# 2. Verificar status
docker-compose ps

# 3. Ver logs
docker-compose logs -f

# 4. Acessar aplicação
# Frontend: http://localhost:80
# Backend: http://localhost:8080
# Swagger: http://localhost:8080/q/swagger-ui
```

### Acessos

**Frontend**
- URL: http://localhost:80
- Container: `frontend`

**Backend**
- API: http://localhost:8080
- Swagger: http://localhost:8080/q/swagger-ui
- Dev UI: http://localhost:8080/q/dev
- Health: http://localhost:8080/q/health
- Container: `backend`

**PostgreSQL**
- Host: localhost
- Porta: 5432
- Database: sales_db
- Usuário: sales
- Senha: sales123
- Container: `postgres`

### Credenciais de Teste

Após o sistema subir, você pode fazer login com:

**Usuário Admin**:
- Email: john.silva@email.com
- Senha: Test@123

## Comandos Úteis

### Gerenciar containers

```bash
# Ver status
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f

# Logs de um serviço específico
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f postgres

# Parar todos os serviços
docker-compose stop

# Parar e remover (mantém dados)
docker-compose down

# Parar e remover TUDO (incluindo dados do banco)
docker-compose down -v

# Restart de um serviço
docker-compose restart frontend
```

### Rebuild

```bash
# Rebuild do frontend
docker-compose up -d --build frontend

# Rebuild sem cache
docker-compose build --no-cache frontend
docker-compose up -d

# Rebuild de tudo
docker-compose down
docker-compose up -d --build
```

### Executar comandos

```bash
# Acessar shell do frontend
docker-compose exec frontend sh

# Ver arquivos servidos
docker-compose exec frontend ls -la /usr/share/nginx/html

# Ver config do Nginx
docker-compose exec frontend cat /etc/nginx/nginx.conf

# Conectar ao PostgreSQL
docker-compose exec postgres psql -U sales -d sales_db
```

### Atualizar código

**Frontend**:
```bash
# Fazer alterações no código

# Rebuild e restart
docker-compose up -d --build frontend
```

**NOTA**: Não há hot reload no Docker. Para desenvolvimento ativo, use `npm start` localmente.

## Configuração do docker-compose.yml

### Frontend Service

```yaml
frontend:
  build:
    context: ../..  # Aponta para sales-web/
    dockerfile: docker/dev/Dockerfile
  ports:
    - "80:80"
  environment:
    - API_URL=http://localhost:8080/api
  depends_on:
    - backend
```

### Backend Service

```yaml
backend:
  image: ghcr.io/seu-usuario/sales-api:latest
  ports:
    - "8080:8080"
  environment:
    - DB_HOST=postgres
    # ... outras variáveis
  depends_on:
    postgres:
      condition: service_healthy
```

### PostgreSQL Service

```yaml
postgres:
  image: postgres:16-alpine
  ports:
    - "5432:5432"
  environment:
    - POSTGRES_DB=sales_db
    - POSTGRES_USER=sales
    - POSTGRES_PASSWORD=sales123
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U sales"]
```

## Dockerfile de Desenvolvimento

Localizado em: `docker/dev/Dockerfile`

**Processo**:
1. **Stage 1**: Build da aplicação Angular
   - Usa Node 18 Alpine
   - Instala dependências
   - Build da aplicação

2. **Stage 2**: Serve com Nginx
   - Copia arquivos buildados
   - Configura Nginx
   - Expõe porta 80

**Tamanho**: ~50MB

## Troubleshooting

### Frontend não carrega

```bash
# Verificar se o container está rodando
docker-compose ps frontend

# Ver logs
docker-compose logs frontend

# Verificar se os arquivos foram copiados
docker-compose exec frontend ls -la /usr/share/nginx/html

# Deve conter: index.html, main.js, styles.css, etc.
```

### Erro "Cannot connect to backend"

```bash
# Verificar se o backend está rodando
docker-compose ps backend

# Verificar health do backend
curl http://localhost:8080/q/health

# Verificar variável API_URL
docker-compose config | grep API_URL

# Testar endpoint
curl http://localhost:8080/api/customers
```

### PostgreSQL não conecta

```bash
# Verificar se está healthy
docker-compose ps postgres

# Ver logs
docker-compose logs postgres

# Testar conexão
docker-compose exec postgres psql -U sales -d sales_db -c "SELECT 1;"

# Se falhar, recriar
docker-compose down -v
docker-compose up -d postgres
sleep 10
docker-compose up -d backend frontend
```

### Porta 80 já está em uso

```bash
# Verificar o que está usando
# Windows
netstat -ano | findstr :80

# Linux/Mac
lsof -i :80

# Mudar porta no docker-compose.yml
ports:
  - "8081:80"  # Usar 8081 ao invés de 80
```

### Build falha

```bash
# Ver logs de build completos
docker-compose build frontend

# Se houver erro de dependências
# Verificar se package.json está correto

# Limpar e rebuild
docker-compose down
docker-compose build --no-cache frontend
docker-compose up -d
```

### Página em branco após login

```bash
# Verificar console do navegador (F12)

# Verificar se a API está respondendo
curl http://localhost:8080/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"john.silva@email.com","password":"Test@123"}'

# Verificar CORS
# Deve estar habilitado no backend
```

## Performance

### Build times

- **First build**: ~2 minutos (download de dependências)
- **Rebuild** (sem cache): ~2 minutos
- **Rebuild** (com cache): ~30 segundos

### Comparação com npm start

| Métrica | Docker | npm start |
|---------|--------|-----------|
| First start | 2 min | 30 seg |
| Hot reload | ❌ | ✅ |
| Rebuild | 30 seg | Instantâneo |
| Memory | ~200MB | ~300MB |

## Variáveis de Ambiente

### Customizar API URL

```bash
# No docker-compose.yml
environment:
  - API_URL=http://sua-api:8080/api
```

### Customizar porta

```bash
# No docker-compose.yml
ports:
  - "4200:80"  # Acessar em localhost:4200
```

## Volumes

Por padrão, não há volumes montados para o frontend (build estático).

**Para desenvolvimento com hot reload**, você poderia montar:

```yaml
volumes:
  - ../../src:/app/src  # Código fonte
  - ../../dist:/app/dist  # Build output
```

Mas isso não é recomendado. Use `npm start` para desenvolvimento ativo.

## Rede

Todos os containers estão na rede `sales-network`.

```bash
# Inspecionar rede
docker network inspect sales-network

# Containers podem se comunicar pelo nome do serviço:
# frontend -> http://backend:8080
# backend -> postgres:5432
```

## docker-compose.local.yml

Setup apenas do frontend (assumindo backend rodando localmente):

```bash
# Backend rodando em: http://localhost:8080

# Subir apenas frontend
cd sales-web/docker/dev
docker-compose -f docker-compose.local.yml up -d
```

Útil quando:
- Backend está em modo dev (`mvn quarkus:dev`)
- Quer testar apenas o build do frontend
- Quer testar Nginx sem subir tudo

## Próximos Passos

Após subir o ambiente:

1. Acessar aplicação: http://localhost:80
2. Fazer login com credenciais de teste
3. Explorar funcionalidades:
   - Gestão de Clientes
   - Gestão de Produtos
   - Gestão de Vendas
   - Dashboards
4. Testar API diretamente: http://localhost:8080/q/swagger-ui

## Documentação Relacionada

- [Frontend Docker README](../README.md)
- [Produção](../prod/README.md)
- [Backend Docker](../../../sales-api/docker/README.md)
- [TROUBLESHOOTING.md](../../../TROUBLESHOOTING.md)
