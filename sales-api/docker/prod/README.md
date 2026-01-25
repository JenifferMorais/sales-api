# Docker - Produção (Backend)

Configuração Docker para deploy em produção do backend.

## O que este setup contém

- Backend (Quarkus) - API REST completa
- PostgreSQL 16 (Alpine) - Banco de dados
- Health checks automáticos
- Restart policies configuradas
- Resource limits definidos

## Pré-requisitos

### 1. Build da Imagem Docker

Você precisa ter a imagem do backend publicada. Opções:

**Opção A: GitHub Container Registry (Recomendado)**

```bash
# 1. Build local
cd sales-api
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:latest .

# 2. Tag para GHCR
docker tag sales-api:latest ghcr.io/seu-usuario/sales-api:latest

# 3. Login no GHCR
echo $GITHUB_TOKEN | docker login ghcr.io -u seu-usuario --password-stdin

# 4. Push
docker push ghcr.io/seu-usuario/sales-api:latest
```

**Opção B: Docker Hub**

```bash
# 1. Build local
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:latest .

# 2. Tag para Docker Hub
docker tag sales-api:latest seu-usuario/sales-api:latest

# 3. Login no Docker Hub
docker login

# 4. Push
docker push seu-usuario/sales-api:latest
```

**Opção C: Build local apenas**

```bash
# Build local (sem push)
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:latest .

# Comentar a linha 'image:' no docker-compose.yml e adicionar:
# build:
#   context: ../..
#   dockerfile: docker/dockerfiles/Dockerfile.simple
```

### 2. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na pasta `docker/prod/`:

```bash
# .env
# GitHub Container Registry
GITHUB_REPOSITORY=seu-usuario/sales-api

# Database
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=SenhaForte@123456

# JWT
JWT_ISSUER=sales-api-production
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15

# Email (SMTP)
SMTP_FROM=noreply@suaempresa.com
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app
SMTP_MOCK=false

# Application
APP_URL=https://sua-api.com
```

**IMPORTANTE**: Adicione `.env` ao `.gitignore`!

## Como usar

### 1. Subir os serviços

```bash
cd sales-api/docker/prod

# Primeira vez (pull + start)
docker-compose up -d

# Ver logs durante o startup
docker-compose logs -f
```

### 2. Verificar se está rodando

```bash
docker-compose ps
```

Saída esperada:
```
NAME                    STATUS          PORTS
sales-postgres         Up (healthy)    5432/tcp
sales-api              Up (healthy)    0.0.0.0:8080->8080/tcp
```

### 3. Verificar health checks

```bash
# Via docker-compose
docker-compose ps

# Via curl
curl http://localhost:8080/q/health

# Resposta esperada:
{
  "status": "UP",
  "checks": [...]
}
```

## Acessos

### Backend API

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **Health**: http://localhost:8080/q/health
- **Metrics**: http://localhost:8080/q/metrics

### PostgreSQL

- **Host**: localhost (interno: postgres)
- **Porta**: Não exposta externamente (apenas para outros containers)
- **Database**: Conforme `.env`
- **Usuário**: Conforme `.env`
- **Senha**: Conforme `.env`

**IMPORTANTE**: Em produção, o PostgreSQL NÃO está exposto para fora do Docker. Apenas o backend consegue acessá-lo.

## Comandos de Produção

### Atualizar a aplicação

```bash
# 1. Pull da nova imagem
docker-compose pull sales-api

# 2. Restart com nova imagem
docker-compose up -d sales-api

# 3. Verificar logs
docker-compose logs -f sales-api
```

### Zero-downtime deployment

```bash
# 1. Pull da nova imagem
docker-compose pull sales-api

# 2. Escalar para 2 instâncias
docker-compose up -d --scale sales-api=2

# 3. Aguardar novo container ficar healthy
sleep 30

# 4. Voltar para 1 instância (remove o antigo)
docker-compose up -d --scale sales-api=1
```

### Ver logs

```bash
# Logs em tempo real
docker-compose logs -f

# Últimas 100 linhas
docker-compose logs --tail=100

# Apenas do backend
docker-compose logs -f sales-api

# Apenas do postgres
docker-compose logs -f postgres
```

### Backup do banco de dados

```bash
# Backup completo
docker-compose exec postgres pg_dump -U sales sales_db > backup-$(date +%Y%m%d).sql

# Backup com compressão
docker-compose exec postgres pg_dump -U sales sales_db | gzip > backup-$(date +%Y%m%d).sql.gz

# Backup de schema apenas (sem dados)
docker-compose exec postgres pg_dump -U sales --schema-only sales_db > schema-backup.sql

# Backup de dados apenas (sem schema)
docker-compose exec postgres pg_dump -U sales --data-only sales_db > data-backup.sql
```

### Restaurar backup

```bash
# Restaurar de backup
docker-compose exec -T postgres psql -U sales sales_db < backup.sql

# Restaurar de backup comprimido
gunzip -c backup.sql.gz | docker-compose exec -T postgres psql -U sales sales_db
```

### Executar comandos SQL

```bash
# Conectar ao PostgreSQL
docker-compose exec postgres psql -U sales -d sales_db

# Executar comando direto
docker-compose exec postgres psql -U sales -d sales_db -c "SELECT COUNT(*) FROM customers;"
```

### Monitoramento

```bash
# Ver uso de recursos
docker stats

# Ver uso apenas dos containers do projeto
docker stats sales-api sales-postgres

# Informações do container
docker inspect sales-api
```

### Parar e remover

```bash
# Parar (mantém volumes/dados)
docker-compose stop

# Parar e remover containers (mantém volumes/dados)
docker-compose down

# Parar e remover TUDO (incluindo dados)
# ATENÇÃO: Isso apaga o banco de dados!
docker-compose down -v
```

## Troubleshooting

### Backend não inicia

```bash
# Ver logs completos
docker-compose logs sales-api

# Verificar health do postgres
docker-compose ps postgres

# Verificar se o postgres está healthy antes de subir o backend
docker-compose up -d postgres
sleep 10
docker-compose up -d sales-api
```

### Health check falhando

```bash
# Verificar health manualmente
curl http://localhost:8080/q/health

# Ver logs do backend
docker-compose logs -f sales-api

# Verificar se o postgres está acessível do backend
docker-compose exec sales-api ping postgres
```

### Erro de conexão com banco

```bash
# Verificar se postgres está healthy
docker-compose ps postgres

# Verificar variáveis de ambiente
docker-compose config

# Verificar se as credenciais estão corretas
docker-compose exec postgres psql -U $DB_USERNAME -d $DB_NAME -c "SELECT 1;"
```

### Container reiniciando continuamente

```bash
# Ver logs
docker-compose logs -f sales-api

# Ver últimos eventos
docker events --filter container=sales-api

# Verificar health check
docker inspect sales-api | grep -A 10 Health
```

### Limpar recursos

```bash
# Remover containers parados
docker-compose down

# Remover volumes não utilizados
docker volume prune

# Remover imagens antigas
docker image prune -a

# Limpar tudo (CUIDADO!)
docker system prune -a --volumes
```

## Segurança em Produção

### Boas Práticas

1. **Não exponha o PostgreSQL para fora do Docker**
   - Remova a seção `ports:` do serviço postgres

2. **Use senhas fortes**
   - Gere senhas complexas para `DB_PASSWORD`
   - Nunca commite o arquivo `.env`

3. **Use secrets do Docker (opcional)**
   ```bash
   # Criar secret
   echo "SenhaForte@123" | docker secret create db_password -

   # Usar no docker-compose.yml
   secrets:
     - db_password
   ```

4. **Configure SSL/TLS para o PostgreSQL**
   - Monte certificados SSL
   - Configure `quarkus.datasource.jdbc.url` com `sslmode=require`

5. **Limite recursos**
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1.0'
         memory: 1G
       reservations:
         cpus: '0.5'
         memory: 512M
   ```

6. **Configure firewall**
   - Apenas a porta 8080 deve estar acessível
   - Use HTTPS em produção (adicione nginx/traefik)

7. **Monitore logs**
   - Use uma solução de log aggregation (ELK, Grafana Loki)
   - Configure alertas para erros críticos

## Deploy em Cloud

### AWS (EC2 / ECS)

```bash
# 1. Build e push da imagem
docker build -t sales-api:latest .
docker tag sales-api:latest <ecr-url>/sales-api:latest
docker push <ecr-url>/sales-api:latest

# 2. SSH na instância EC2
ssh ec2-user@your-instance

# 3. Clone o repositório
git clone <seu-repo>
cd sales-api/docker/prod

# 4. Configurar .env

# 5. Deploy
docker-compose up -d
```

### DigitalOcean (Droplet)

```bash
# 1. Criar Droplet com Docker pre-instalado
# 2. SSH no Droplet
ssh root@your-droplet

# 3. Clone e deploy
git clone <seu-repo>
cd sales-api/docker/prod
cp .env.example .env
# Editar .env
docker-compose up -d
```

### Render / Railway / Fly.io

Essas plataformas geralmente detectam o Dockerfile automaticamente:

1. Conecte o repositório
2. Configure variáveis de ambiente no painel
3. Deploy automático via Git push

## Backup Automatizado

Script de backup automático (cron):

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/vendas_backup_$DATE.sql.gz"

# Criar backup
docker-compose exec -T postgres pg_dump -U sales sales_db | gzip > "$BACKUP_FILE"

# Manter apenas últimos 7 backups
find $BACKUP_DIR -name "vendas_backup_*.sql.gz" -mtime +7 -delete

echo "Backup criado: $BACKUP_FILE"
```

Configurar no crontab:

```bash
# Rodar todo dia às 3h da manhã
0 3 * * * /opt/sales/docker/prod/backup.sh
```

## Documentação Relacionada

- [Docker README Principal](../README.md)
- [Desenvolvimento](../dev/README.md)
- [TROUBLESHOOTING.md](../../../TROUBLESHOOTING.md)
- [README.md Principal](../../../README.md)
