# Docker - Produção (Frontend)

Configuração Docker para deploy em produção do frontend.

## O que este setup contém

- **Frontend** (Angular + Nginx) - Aplicação web otimizada
- **Backend** (Quarkus) - API REST
- Resource limits configurados
- Restart policies
- Health checks (backend)

**IMPORTANTE**: PostgreSQL não está incluso neste setup. Você deve usar um banco gerenciado (AWS RDS, DigitalOcean, etc).

## Pré-requisitos

### 1. Build e Publish das Imagens

**Frontend**:

```bash
cd sales-web

# Build
docker build -f docker/prod/Dockerfile -t sales-web:latest .

# Tag para registry
docker tag sales-web:latest ghcr.io/seu-usuario/sales-api-web:latest

# Login
echo $GITHUB_TOKEN | docker login ghcr.io -u seu-usuario --password-stdin

# Push
docker push ghcr.io/seu-usuario/sales-api-web:latest
```

**Backend** (se ainda não fez):

```bash
cd sales-api

# Build
docker build -f docker/dockerfiles/Dockerfile.simple -t sales-api:latest .

# Tag e push
docker tag sales-api:latest ghcr.io/seu-usuario/sales-api:latest
docker push ghcr.io/seu-usuario/sales-api:latest
```

### 2. Configurar Variáveis de Ambiente

Crie `.env` na pasta `docker/prod/`:

```bash
# .env

# GitHub Container Registry
GITHUB_REPOSITORY=seu-usuario/sales-api

# Frontend
API_URL=https://api.suaempresa.com/api

# Backend Database (use banco gerenciado!)
DB_HOST=seu-banco.rds.amazonaws.com
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=SenhaForteDoBanco@123

# JWT
JWT_ISSUER=sales-api-production
JWT_EXPIRATION_HOURS=24
JWT_INACTIVITY_TIMEOUT_MINUTES=15

# Email (SMTP)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_FROM=noreply@suaempresa.com
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app-gmail
SMTP_MOCK=false

# Application
APP_URL=https://suaempresa.com
```

**IMPORTANTE**: Adicione `.env` ao `.gitignore`!

### 3. Banco de Dados Externo

**Recomendações**:
- AWS RDS PostgreSQL
- DigitalOcean Managed Database
- Google Cloud SQL
- Azure Database for PostgreSQL
- Render PostgreSQL

**Não use PostgreSQL em Docker para produção!** Use um serviço gerenciado.

## Deploy

### 1. Subir os serviços

```bash
cd sales-web/docker/prod

# Pull das imagens
docker-compose pull

# Start
docker-compose up -d

# Ver logs
docker-compose logs -f
```

### 2. Verificar status

```bash
docker-compose ps
```

Saída esperada:
```
NAME              STATUS          PORTS
frontend          Up              0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp
backend           Up (healthy)    0.0.0.0:8080->8080/tcp
```

### 3. Testar aplicação

```bash
# Frontend
curl http://localhost

# Backend health
curl http://localhost:8080/q/health

# Backend API
curl http://localhost:8080/api/customers
```

## Acessos

### Frontend

- **HTTP**: http://localhost:80
- **HTTPS**: https://localhost:443 (se SSL configurado)

### Backend

- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/q/swagger-ui
- **Health**: http://localhost:8080/q/health
- **Metrics**: http://localhost:8080/q/metrics

## SSL/HTTPS (Opcional)

### Opção 1: Let's Encrypt com Certbot

```bash
# Instalar certbot
sudo apt-get install certbot

# Gerar certificados
sudo certbot certonly --standalone -d suaempresa.com -d www.suaempresa.com

# Certificados estarão em:
# /etc/letsencrypt/live/suaempresa.com/fullchain.pem
# /etc/letsencrypt/live/suaempresa.com/privkey.pem

# Criar pasta certs
mkdir certs
sudo cp /etc/letsencrypt/live/suaempresa.com/fullchain.pem certs/
sudo cp /etc/letsencrypt/live/suaempresa.com/privkey.pem certs/
```

### Opção 2: Usar Traefik ou Nginx Proxy Manager

Adicione um reverse proxy:

```yaml
# docker-compose.yml
services:
  traefik:
    image: traefik:v2.10
    command:
      - "--providers.docker=true"
      - "--entrypoints.web.address=:80"
      - "--entrypoints.websecure.address=:443"
      - "--certificatesresolvers.letsencrypt.acme.email=seu@email.com"
      - "--certificatesresolvers.letsencrypt.acme.storage=/letsencrypt/acme.json"
      - "--certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint=web"
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./letsencrypt:/letsencrypt
```

### Opção 3: Cloudflare (Mais fácil)

1. Configure DNS para apontar para seu servidor
2. Habilite proxy Cloudflare (nuvem laranja)
3. SSL automático configurado!

## Comandos de Produção

### Atualizar aplicação

**Frontend**:
```bash
# Pull da nova imagem
docker-compose pull frontend

# Restart
docker-compose up -d frontend

# Verificar
docker-compose logs -f frontend
```

**Backend**:
```bash
docker-compose pull backend
docker-compose up -d backend
docker-compose logs -f backend
```

### Zero-downtime deployment

```bash
# Para o frontend (estático, restart rápido)
docker-compose pull frontend
docker-compose up -d frontend

# Para o backend (mais crítico)
# 1. Escalar para 2 instâncias
docker-compose up -d --scale backend=2

# 2. Aguardar nova instância ficar healthy
sleep 30

# 3. Voltar para 1 (remove a antiga)
docker-compose up -d --scale backend=1
```

### Monitoramento

```bash
# Ver uso de recursos
docker stats

# Logs em tempo real
docker-compose logs -f

# Últimas 1000 linhas
docker-compose logs --tail=1000 > logs.txt

# Logs de hoje
docker-compose logs --since $(date +%Y-%m-%d) > logs-today.txt
```

### Backup (se usar nginx para arquivos)

```bash
# Backup de arquivos estáticos
docker cp frontend:/usr/share/nginx/html ./backup-frontend-$(date +%Y%m%d)

# Backup de configuração
docker cp frontend:/etc/nginx/nginx.conf ./backup-nginx.conf
```

## Resource Limits

Configurados no `docker-compose.yml`:

**Frontend**:
```yaml
deploy:
  resources:
    limits:
      cpus: '0.5'
      memory: 512M
    reservations:
      cpus: '0.25'
      memory: 256M
```

**Backend**:
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

Ajuste conforme necessário para seu servidor.

## Troubleshooting

### Frontend não carrega

```bash
# Verificar status
docker-compose ps frontend

# Ver logs
docker-compose logs frontend

# Verificar se Nginx está servindo arquivos
docker-compose exec frontend ls -la /usr/share/nginx/html

# Testar Nginx diretamente
docker-compose exec frontend wget -O- http://localhost
```

### Backend não conecta ao banco

```bash
# Verificar variáveis
docker-compose config | grep DB_

# Testar conexão manualmente
docker-compose exec backend telnet $DB_HOST $DB_PORT

# Ver logs do backend
docker-compose logs backend | grep -i "database\|postgres\|connection"
```

### Erro 502 Bad Gateway

```bash
# Backend não está respondendo

# Verificar se está rodando
docker-compose ps backend

# Verificar health
curl http://localhost:8080/q/health

# Ver logs
docker-compose logs backend

# Restart
docker-compose restart backend
```

### Erro "Cannot find module"

```bash
# Problema no build do frontend

# Rebuild sem cache
cd sales-web
docker build -f docker/prod/Dockerfile --no-cache -t sales-web:latest .

# Push nova imagem
docker tag sales-web:latest ghcr.io/seu-usuario/sales-api-web:latest
docker push ghcr.io/seu-usuario/sales-api-web:latest

# Pull e restart
cd docker/prod
docker-compose pull frontend
docker-compose up -d frontend
```

### Uso alto de memória

```bash
# Ver uso
docker stats

# Ajustar limits no docker-compose.yml
deploy:
  resources:
    limits:
      memory: 256M  # Reduzir se necessário

# Aplicar
docker-compose up -d
```

### Logs muito grandes

```bash
# Ver tamanho dos logs
docker inspect --format='{{.LogPath}}' frontend
ls -lh $(docker inspect --format='{{.LogPath}}' frontend)

# Configurar rotação de logs (criar daemon.json)
sudo nano /etc/docker/daemon.json

# Adicionar:
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}

# Restart Docker daemon
sudo systemctl restart docker
```

## Segurança

### 1. Não exponha portas desnecessárias

Remova a porta 8080 do backend se o frontend acessa via rede interna:

```yaml
backend:
  # ports:
  #   - "8080:8080"  # Comentar
  expose:
    - "8080"  # Apenas interno
```

### 2. Use HTTPS

Configure SSL/TLS conforme seção SSL acima.

### 3. Headers de segurança no Nginx

Edite `nginx.conf`:

```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "no-referrer-when-downgrade" always;
add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
```

### 4. Firewall

```bash
# Permitir apenas 80, 443, SSH
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### 5. Atualizações de segurança

```bash
# Atualizar imagens regularmente
docker-compose pull
docker-compose up -d

# Verificar vulnerabilidades
docker scan sales-web:latest
```

## Deploy em Cloud

### AWS (EC2)

```bash
# 1. Criar EC2 instance (Ubuntu)
# 2. SSH
ssh -i key.pem ubuntu@ec2-ip

# 3. Instalar Docker
sudo apt update
sudo apt install -y docker.io docker-compose
sudo usermod -aG docker ubuntu

# 4. Clone
git clone seu-repo
cd sales-web/docker/prod

# 5. Configurar .env

# 6. Deploy
docker-compose up -d
```

### DigitalOcean (Droplet)

```bash
# 1. Criar Droplet com Docker pre-instalado
# 2. SSH
ssh root@droplet-ip

# 3. Clone e deploy
git clone seu-repo
cd sales-web/docker/prod
nano .env  # Configurar
docker-compose up -d
```

### Render / Vercel / Netlify

Para frontend apenas:

**Render**:
1. Conecte repositório
2. Escolha "Static Site"
3. Build command: `cd sales-web && npm install && npm run build`
4. Publish directory: `sales-web/dist/sales-web/browser`

**Vercel/Netlify**: Similar ao Render

Para backend: Use Render Web Service com Dockerfile

### Railway

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Deploy frontend
cd sales-web
railway up

# Deploy backend
cd sales-api
railway up
```

## Monitoramento de Produção

### Prometheus + Grafana

```yaml
# Adicionar ao docker-compose.yml
  prometheus:
    image: prom/prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
```

### Logs Centralizados

Use ELK Stack, Grafana Loki, ou serviços como:
- Papertrail
- Loggly
- Datadog
- New Relic

### Uptime Monitoring

Configure em:
- UptimeRobot (gratuito)
- Pingdom
- StatusCake
- Better Uptime

## CI/CD

### GitHub Actions

Crie `.github/workflows/deploy.yml`:

```yaml
name: Deploy Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Build and Push Frontend
        run: |
          cd sales-web
          docker build -f docker/prod/Dockerfile -t ghcr.io/${{ github.repository }}-web:latest .
          echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
          docker push ghcr.io/${{ github.repository }}-web:latest

      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.HOST }}
          username: ${{ secrets.USERNAME }}
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd sales-web/docker/prod
            docker-compose pull
            docker-compose up -d
```

## Documentação Relacionada

- [Frontend Docker README](../README.md)
- [Desenvolvimento](../dev/README.md)
- [Backend Docker Produção](../../../sales-api/docker/prod/README.md)
- [TROUBLESHOOTING.md](../../../TROUBLESHOOTING.md)
- [README.md Principal](../../../README.md)
