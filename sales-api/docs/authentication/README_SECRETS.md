# 🔐 Gerenciamento de Credenciais - Vendas API

## ⚠️ IMPORTANTE

**NUNCA** commite credenciais sensíveis no código-fonte!

Este projeto utiliza **variáveis de ambiente** e **secrets managers** para proteger credenciais.

---

## 🚀 Quick Start

### 1️⃣ Configuração Local (Desenvolvimento)

```bash
# 1. Copie o template de variáveis de ambiente
cp .env.example .env

# 2. Edite o .env com suas credenciais locais
nano .env

# 3. Preencha os valores (mínimo necessário):
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=vendas_user
DB_PASSWORD=sua_senha_aqui
JWT_SECRET_KEY=sua-chave-jwt-min-32-caracteres

# 4. Carregue as variáveis e inicie a aplicação
export $(cat .env | xargs)  # Linux/Mac
./mvnw quarkus:dev
```

**Windows PowerShell:**
```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
.\mvnw quarkus:dev
```

### 2️⃣ GitHub Actions (CI/CD)

Adicione os seguintes **Secrets** no seu repositório GitHub:

**Settings → Secrets and variables → Actions → New repository secret**

| Secret Name | Descrição | Exemplo |
|-------------|-----------|---------|
| `DB_USERNAME` | Usuário do banco de dados | `vendas_user` |
| `DB_PASSWORD` | Senha do banco de dados | `P@ssw0rd!Strong123` |
| `JWT_SECRET_KEY_TEST` | Chave JWT para testes | `test-secret-32-chars-min` |
| `JWT_SECRET_KEY_STAGING` | Chave JWT staging | `staging-secret-32-chars` |
| `JWT_SECRET_KEY_PROD` | Chave JWT produção | `prod-secret-32-chars-min` |

### 3️⃣ Produção (AWS Secrets Manager)

```bash
# Criar secret para database
aws secretsmanager create-secret \
    --name sales-api/prod/database \
    --secret-string '{
        "username":"vendas_prod",
        "password":"SenhaForte123!@#",
        "host":"db.example.com",
        "port":"5432",
        "dbname":"vendas_production"
    }'

# Criar secret para JWT
aws secretsmanager create-secret \
    --name sales-api/prod/jwt \
    --secret-string '{
        "secret_key":"prod-jwt-secret-min-32-chars",
        "issuer":"sales-api",
        "expiration_hours":"24"
    }'
```

---

## 📁 Estrutura de Arquivos

```
sales-api/
├── .env.example          # ✅ Template (sem valores reais) - COMMITAR
├── .env                  # ❌ Valores reais - NÃO COMMITAR
├── .gitignore           # ✅ Ignora .env e outros secrets
├── SECURITY_GUIDE.md    # 📖 Guia completo de segurança
├── SECRETS_SETUP.md     # 📖 Setup passo a passo
└── src/
    └── main/
        └── resources/
            └── application.properties  # Usa ${ENV_VAR}
```

---

## 🔍 Como Funciona

### application.properties

```properties
# ❌ ANTES (Inseguro)
quarkus.datasource.username=sales
quarkus.datasource.password=sales123

# ✅ AGORA (Seguro)
quarkus.datasource.username=${DB_USERNAME:sales}
quarkus.datasource.password=${DB_PASSWORD:sales123}
```

**Formato:** `${VARIAVEL:valor_padrao}`
- **VARIAVEL**: Nome da variável de ambiente
- **valor_padrao**: Usado se a variável não estiver definida

---

## 🛡️ Boas Práticas

### ✅ SEMPRE Faça

1. **Use variáveis de ambiente**
```properties
db.password=${DB_PASSWORD}
```

2. **Adicione .env ao .gitignore**
```gitignore
.env
.env.local
*.env
```

3. **Mantenha .env.example atualizado**
```bash
DB_HOST=localhost  # ✅ Valores de exemplo, não reais
DB_PASSWORD=YOUR_PASSWORD_HERE
```

4. **Use senhas fortes**
```bash
# Gerar senha segura (32 caracteres)
openssl rand -base64 32
```

5. **Rotacione credenciais regularmente**
- Banco de dados: A cada 90 dias
- API Keys: A cada 6 meses
- JWT Secrets: Anualmente

### ❌ NUNCA Faça

1. **Hardcode credentials**
```java
String password = "senha123";  // ❌ NUNCA!
```

2. **Commite arquivos .env**
```bash
git add .env  # ❌ NUNCA!
```

3. **Logue credenciais**
```java
logger.info("Password: " + password);  // ❌ NUNCA!
```

4. **Use senhas fracas**
```bash
DB_PASSWORD=123456  # ❌ NUNCA!
```

---

## 🔐 Variáveis de Ambiente Necessárias

### Obrigatórias

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=vendas_user
DB_PASSWORD=sua_senha_forte

# JWT
JWT_SECRET_KEY=chave-secreta-min-32-caracteres
```

### Opcionais

```bash
# Email
SMTP_HOST=smtp.gmail.com
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app

# AWS (Produção)
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=xyz...
AWS_REGION=us-east-1

# Monitoring
SENTRY_DSN=https://...
```

---

## 🧪 Testando a Configuração

### 1. Verifique se variáveis estão carregadas

```bash
# Linux/Mac
echo $DB_USERNAME
echo $JWT_SECRET_KEY

# Windows PowerShell
echo $env:DB_USERNAME
echo $env:JWT_SECRET_KEY
```

### 2. Teste a aplicação

```bash
./mvnw quarkus:dev

# Deve conectar ao banco sem erros
# Log: Datasource connected to: jdbc:postgresql://localhost:5432/sales_db
```

### 3. Verifique se .env está ignorado

```bash
git check-ignore .env
# Output esperado: .env

git status
# .env NÃO deve aparecer na lista
```

---

## 🆘 Troubleshooting

### Erro: "Could not resolve placeholder 'DB_PASSWORD'"

**Causa:** Variável de ambiente não está definida

**Solução:**
```bash
# Verifique se .env existe
ls -la .env

# Carregue as variáveis
export $(cat .env | xargs)

# Ou defina manualmente
export DB_PASSWORD=sua_senha
```

### Erro: "Authentication failed for user"

**Causa:** Credenciais incorretas ou banco não acessível

**Solução:**
```bash
# 1. Verifique se o PostgreSQL está rodando
docker ps  # ou
sudo systemctl status postgresql

# 2. Teste a conexão
psql -h localhost -U vendas_user -d sales_db

# 3. Verifique as variáveis
echo $DB_USERNAME
echo $DB_HOST
```

### .env foi commitado acidentalmente

**Solução:**
```bash
# 1. Remova do histórico Git
git rm --cached .env
git commit -m "Remove accidentally committed .env"

# 2. Rotacione TODAS as credenciais expostas imediatamente
# 3. Force push (SE o repositório for privado e você for o único dev)
git push --force
```

---

## 📚 Documentação Completa

Para mais detalhes, consulte:

- **[SECURITY_GUIDE.md](./SECURITY_GUIDE.md)** - Guia completo de segurança
- **[SECRETS_SETUP.md](./SECRETS_SETUP.md)** - Setup detalhado passo a passo
- **.env.example** - Template de variáveis

---

## 🔗 Links Úteis

- [12 Factor App - Config](https://12factor.net/config)
- [GitHub Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/)
- [OWASP Security Guidelines](https://owasp.org/www-project-top-ten/)

---

## 📞 Suporte

Dúvidas sobre segurança? Entre em contato com:
- 📧 Email: security@sales.com
- 🔒 Security Team: #security-team no Slack

---

**🔐 Segurança não é opcional - é essencial!**

*Última atualização: 2024-01-24*
