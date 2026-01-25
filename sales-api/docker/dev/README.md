# Docker - Desenvolvimento Local (Backend)

Configuração Docker para desenvolvimento local do backend.

## O que este setup contém

- PostgreSQL 16 (Alpine)
- pgAdmin 4 (Interface web para gerenciar PostgreSQL)

**IMPORTANTE**: Este setup NÃO roda o backend em Docker. O backend deve rodar localmente com `mvn quarkus:dev` para ter hot reload.

## Como usar

### 1. Subir os serviços

```bash
cd sales-api/docker/dev
docker-compose up -d
```

### 2. Verificar se está rodando

```bash
docker-compose ps
```

Saída esperada:
```
NAME                    STATUS          PORTS
sales-postgres         Up             0.0.0.0:5432->5432/tcp
sales-pgadmin          Up             0.0.0.0:5050->80/tcp
```

### 3. Rodar o backend localmente

```bash
# Voltar para a raiz do projeto backend
cd ../..

# Rodar em modo dev (hot reload ativado)
mvn quarkus:dev
```

## Acessos

### PostgreSQL

- **Host**: localhost
- **Porta**: 5432
- **Database**: sales_db
- **Usuário**: sales
- **Senha**: sales123

**String de conexão JDBC**:
```
jdbc:postgresql://localhost:5432/sales_db
```

### pgAdmin

Acesse: http://localhost:5050

**Credenciais**:
- Email: admin@sales.com
- Senha: admin123

**Primeira vez usando pgAdmin?**

1. Acesse http://localhost:5050
2. Faça login com as credenciais acima
3. Clique com botão direito em "Servers" → "Register" → "Server"
4. Na aba "General":
   - Name: Vendas Local
5. Na aba "Connection":
   - Host: postgres (ou localhost se não funcionar)
   - Port: 5432
   - Maintenance database: sales_db
   - Username: sales
   - Password: sales123
   - ✅ Save password
6. Clique "Save"

## Comandos Úteis

### Ver logs

```bash
# Todos os logs
docker-compose logs -f

# Apenas PostgreSQL
docker-compose logs -f postgres

# Apenas pgAdmin
docker-compose logs -f pgadmin
```

### Parar os serviços

```bash
# Parar (mantém volumes/dados)
docker-compose stop

# Parar e remover containers (mantém volumes/dados)
docker-compose down

# Parar e remover TUDO (incluindo dados)
docker-compose down -v
```

### Restart dos serviços

```bash
# Restart de tudo
docker-compose restart

# Restart só do PostgreSQL
docker-compose restart postgres
```

### Conectar ao PostgreSQL via terminal

```bash
# Usando docker exec
docker-compose exec postgres psql -U sales -d sales_db

# Comandos úteis no psql:
\dt          # Listar tabelas
\d customers # Descrever tabela customers
\q           # Sair
```

### Backup do banco

```bash
# Fazer backup
docker-compose exec postgres pg_dump -U sales sales_db > backup.sql

# Restaurar backup
docker-compose exec -T postgres psql -U sales sales_db < backup.sql
```

### Resetar banco de dados

```bash
# ATENÇÃO: Isso apaga TODOS os dados!
docker-compose down -v
docker-compose up -d

# Aguardar 10 segundos para inicialização
timeout 10

# O Quarkus vai recriar as tabelas automaticamente
```

## Troubleshooting

### Porta 5432 já está em uso

Se você já tem PostgreSQL instalado localmente:

**Opção 1**: Parar o PostgreSQL local
```bash
# Windows
net stop postgresql-x64-16

# Linux
sudo systemctl stop postgresql

# Mac
brew services stop postgresql
```

**Opção 2**: Mudar a porta no docker-compose.yml
```yaml
postgres:
  ports:
    - "5433:5432"  # Usar porta 5433 no host
```

Depois atualizar `application.properties`:
```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5433/sales_db
```

### pgAdmin não abre

```bash
# Verificar logs
docker-compose logs pgadmin

# Verificar se a porta 5050 está livre
netstat -ano | findstr :5050

# Se necessário, mudar a porta
ports:
  - "5051:80"
```

### PostgreSQL não aceita conexões

```bash
# Verificar se o container está rodando
docker-compose ps

# Verificar logs do PostgreSQL
docker-compose logs postgres

# Verificar se o banco foi criado
docker-compose exec postgres psql -U sales -l

# Se não inicializou corretamente, recriar
docker-compose down -v
docker-compose up -d
```

### Erro "database sales_db does not exist"

```bash
# Recriar o container com volumes limpos
docker-compose down -v
docker-compose up -d

# Aguardar inicialização (10 segundos)
timeout 10

# Verificar se o banco foi criado
docker-compose exec postgres psql -U sales -l
```

## Volumes

Os dados do PostgreSQL são armazenados no volume `postgres_data`.

**Verificar volumes**:
```bash
docker volume ls | grep postgres
```

**Remover volume (apaga todos os dados)**:
```bash
docker-compose down -v
```

## Rede

Os containers estão conectados à rede `sales-network`.

**Verificar rede**:
```bash
docker network inspect sales-network
```

## Próximos Passos

Após subir o PostgreSQL:

1. Rodar o backend em modo dev: `mvn quarkus:dev`
2. Acessar Swagger: http://localhost:8080/q/swagger-ui
3. Acessar Dev UI: http://localhost:8080/q/dev
4. Executar testes: `mvn test`

## Documentação Relacionada

- [Docker README Principal](../README.md)
- [Produção](../prod/README.md)
- [TROUBLESHOOTING.md](../../../TROUBLESHOOTING.md)
- [INICIO_RAPIDO.md](../../INICIO_RAPIDO.md)
