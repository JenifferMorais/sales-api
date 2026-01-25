# Multi-stage build otimizado para produção

# Stage 1: Build do Angular
FROM node:18-alpine AS builder

WORKDIR /app

# Copiar arquivos de dependências
COPY package*.json ./

# Instalar dependências (com cache)
RUN npm ci --legacy-peer-deps

# Copiar código fonte
COPY . .

# Build de produção com otimizações
RUN npm run build -- --configuration production

# Stage 2: Nginx para servir a aplicação
FROM nginx:1.25-alpine

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Remover configuração padrão do nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copiar configuração customizada do nginx
COPY docker/prod/nginx.conf /etc/nginx/nginx.conf

# Copiar aplicação buildada
COPY --from=builder /app/dist/sales-web/browser /usr/share/nginx/html

# Criar script de substituição de variáveis de ambiente
COPY docker/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Expor porta 80
EXPOSE 80

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# Usar script de entrypoint
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
