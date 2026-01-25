# 🔐 Guia de Segurança - Vendas API

## 📋 Índice

1. [Gerenciamento de Credenciais](#gerenciamento-de-credenciais)
2. [Variáveis de Ambiente](#variáveis-de-ambiente)
3. [GitHub Secrets](#github-secrets)
4. [Boas Práticas](#boas-práticas)
5. [Secrets em Produção](#secrets-em-produção)
6. [Checklist de Segurança](#checklist-de-segurança)

---

## 🔑 Gerenciamento de Credenciais

### ❌ NUNCA Faça Isso:

```properties
# ❌ ERRADO - Credenciais hardcoded
quarkus.datasource.username=admin
quarkus.datasource.password=senha123
jwt.secret=minha-chave-secreta
```

### ✅ SEMPRE Faça Isso:

```properties
# ✅ CORRETO - Usando variáveis de ambiente
quarkus.datasource.username=${DB_USERNAME}
quarkus.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET_KEY}
```

---

## 🌍 Variáveis de Ambiente

### Configuração Local (Desenvolvimento)

#### Opção 1: Arquivo .env (Recomendado)

1. **Copie o template:**
```bash
cp .env.example .env
```

2. **Edite o .env com valores reais:**
```bash
DB_USERNAME=vendas_user
DB_PASSWORD=SenhaForte123!@#
JWT_SECRET_KEY=minha-chave-jwt-super-secreta-min-32-chars
```

3. **Configure o Quarkus para ler .env:**

Adicione ao `pom.xml`:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-config-yaml</artifactId>
</dependency>
```

Ou use dotenv manualmente:
```bash
# Linux/Mac
export $(cat .env | xargs)
./mvnw quarkus:dev

# Windows PowerShell
Get-Content .env | ForEach-Object {
    $name, $value = $_.split('=')
    Set-Content env:\$name $value
}
.\mvnw quarkus:dev
```

#### Opção 2: Exportar Variáveis Manualmente

```bash
# Linux/Mac
export DB_USERNAME=vendas_user
export DB_PASSWORD=SenhaForte123
export JWT_SECRET_KEY=minha-chave-jwt-super-secreta

# Windows CMD
set DB_USERNAME=vendas_user
set DB_PASSWORD=SenhaForte123
set JWT_SECRET_KEY=minha-chave-jwt-super-secreta

# Windows PowerShell
$env:DB_USERNAME="vendas_user"
$env:DB_PASSWORD="SenhaForte123"
$env:JWT_SECRET_KEY="minha-chave-jwt-super-secreta"
```

#### Opção 3: IDE Configuration

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Environment Variables → Add
3. Adicione: `DB_USERNAME=sales;DB_PASSWORD=senha123`

**VS Code (launch.json):**
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "Quarkus Dev",
      "env": {
        "DB_USERNAME": "vendas_user",
        "DB_PASSWORD": "senha123",
        "JWT_SECRET_KEY": "chave-secreta"
      }
    }
  ]
}
```

---

## 🐙 GitHub Secrets

### Configurando Secrets no GitHub

1. **Acesse o repositório no GitHub**

2. **Navegue para Settings → Secrets and variables → Actions**

3. **Clique em "New repository secret"**

4. **Adicione cada secret:**

| Name | Example Value | Description |
|------|---------------|-------------|
| `DB_USERNAME` | `vendas_prod` | Username do banco de dados |
| `DB_PASSWORD` | `P@ssw0rd!Strong123` | Senha do banco de dados |
| `DB_HOST` | `db.example.com` | Host do banco de dados |
| `JWT_SECRET_KEY` | `32-char-random-string-here` | Chave secreta JWT |
| `PAYMENT_API_KEY` | `pk_live_abc123...` | API Key do gateway de pagamento |

### Usando Secrets no GitHub Actions

**`.github/workflows/ci-cd.yml`:**
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Run Tests
        env:
          DB_USERNAME: ${{ secrets.DB_USERNAME }}
          DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
          DB_HOST: ${{ secrets.DB_HOST }}
          DB_PORT: ${{ secrets.DB_PORT }}
          DB_NAME: ${{ secrets.DB_NAME }}
          JWT_SECRET_KEY: ${{ secrets.JWT_SECRET_KEY }}
        run: ./mvnw clean verify

      - name: Build Application
        run: ./mvnw package -DskipTests

      - name: Deploy to Production
        if: github.ref == 'refs/heads/main'
        env:
          DEPLOY_KEY: ${{ secrets.DEPLOY_KEY }}
          AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
          AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        run: |
          # Script de deploy aqui
          echo "Deploying to production..."
```

### Environment-Specific Secrets

**GitHub Environments:**
1. Settings → Environments → New environment
2. Crie: `development`, `staging`, `production`
3. Adicione secrets específicos para cada ambiente

**Uso no workflow:**
```yaml
jobs:
  deploy-prod:
    runs-on: ubuntu-latest
    environment: production  # Usa secrets do ambiente 'production'
    steps:
      - name: Deploy
        env:
          DB_PASSWORD: ${{ secrets.DB_PASSWORD }}  # Pega do ambiente 'production'
        run: ./deploy.sh
```

---

## 🛡️ Boas Práticas

### 1. Princípio do Menor Privilégio

```bash
# ❌ EVITE: Usuário com privilégios administrativos
DB_USERNAME=postgres
DB_PASSWORD=admin123

# ✅ PREFIRA: Usuário com permissões mínimas necessárias
DB_USERNAME=vendas_app_user  # Apenas SELECT, INSERT, UPDATE, DELETE
DB_PASSWORD=strong_password_here
```

### 2. Senhas Fortes

```bash
# ❌ Senhas Fracas
password=123456
password=admin
password=sales

# ✅ Senhas Fortes (mínimo 16 caracteres)
password=V3nd@s!2024$Pr0d#Secure
jwt_secret=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0
```

**Gerando senhas seguras:**
```bash
# Linux/Mac
openssl rand -base64 32

# PowerShell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 32 | % {[char]$_})

# Online (use com cuidado)
# https://passwordsgenerator.net/
```

### 3. Rotação de Credenciais

```yaml
# Rotação Recomendada:
- Senhas de banco de dados: A cada 90 dias
- API Keys: A cada 6 meses
- JWT Secrets: Anualmente ou após incidentes
- Certificados SSL: Antes da expiração (monitorar)
```

### 4. Separação de Ambientes

```properties
# application-dev.properties
quarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST:localhost}:5432/vendas_dev

# application-test.properties
quarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST:localhost}:5432/vendas_test

# application-prod.properties
quarkus.datasource.jdbc.url=jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
```

Ativar perfil:
```bash
# Desenvolvimento
./mvnw quarkus:dev

# Teste
./mvnw test -Dquarkus.profile=test

# Produção
java -Dquarkus.profile=prod -jar target/quarkus-app/quarkus-run.jar
```

### 5. Nunca Logue Credenciais

```java
// ❌ NUNCA FAÇA ISSO
logger.info("Connecting to database with password: " + password);
logger.debug("JWT Token: " + jwtToken);

// ✅ FAÇA ISSO
logger.info("Connecting to database at: " + dbHost);
logger.debug("User authenticated successfully");
```

---

## 🏢 Secrets em Produção

### AWS Secrets Manager

**1. Criar Secret:**
```bash
aws secretsmanager create-secret \
    --name sales-api/prod/database \
    --secret-string '{
        "username":"vendas_prod",
        "password":"SuperSecurePassword123!",
        "host":"sales-db.cluster-abc.us-east-1.rds.amazonaws.com",
        "port":"5432",
        "dbname":"vendas_production"
    }'
```

**2. Integração com Quarkus:**

Adicione ao `pom.xml`:
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>secretsmanager</artifactId>
    <version>2.20.0</version>
</dependency>
```

**3. Carregar Secrets:**
```java
@ApplicationScoped
public class SecretsManager {

    public Map<String, String> getDatabaseCredentials() {
        SecretsManagerClient client = SecretsManagerClient.create();
        GetSecretValueRequest request = GetSecretValueRequest.builder()
            .secretId("sales-api/prod/database")
            .build();

        GetSecretValueResponse response = client.getSecretValue(request);
        return new Gson().fromJson(response.secretString(), Map.class);
    }
}
```

### Azure Key Vault

```xml
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-security-keyvault-secrets</artifactId>
    <version>4.6.0</version>
</dependency>
```

```java
SecretClient secretClient = new SecretClientBuilder()
    .vaultUrl("https://sales-api-vault.vault.azure.net/")
    .credential(new DefaultAzureCredentialBuilder().build())
    .buildClient();

String dbPassword = secretClient.getSecret("db-password").getValue();
```

### Google Secret Manager

```xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-secretmanager</artifactId>
    <version>2.20.0</version>
</dependency>
```

```java
try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
    SecretVersionName secretName = SecretVersionName.of(
        "sales-api-project",
        "db-password",
        "latest"
    );
    String password = client.accessSecretVersion(secretName)
        .getPayload()
        .getData()
        .toStringUtf8();
}
```

### HashiCorp Vault

```bash
# Armazenar secret
vault kv put secret/sales-api/database \
    username=vendas_prod \
    password=SecurePassword123

# Ler secret
vault kv get secret/sales-api/database
```

**Integração com Quarkus:**
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-vault</artifactId>
</dependency>
```

```properties
quarkus.vault.url=http://localhost:8200
quarkus.vault.authentication.app-role.role-id=${VAULT_ROLE_ID}
quarkus.vault.authentication.app-role.secret-id=${VAULT_SECRET_ID}
quarkus.vault.secret-config-kv-path=secret/sales-api
```

---

## ✅ Checklist de Segurança

### Antes de Commitar

- [ ] Nenhuma senha ou API key no código
- [ ] Arquivo `.env` está no `.gitignore`
- [ ] Valores sensíveis usam `${VAR}` ou `${VAR:default}`
- [ ] `.env.example` está atualizado (sem valores reais)
- [ ] Nenhum arquivo `application-prod.properties` com credenciais

### Antes de Fazer Deploy

- [ ] Secrets configurados no GitHub Actions/GitLab CI
- [ ] Credenciais de produção diferentes de desenvolvimento
- [ ] Secrets em serviço gerenciado (AWS/Azure/GCP)
- [ ] SSL/TLS configurado para conexões de banco
- [ ] Logs não expõem informações sensíveis

### Revisão Periódica

- [ ] Rotação de senhas a cada 90 dias
- [ ] Auditoria de acessos e permissões
- [ ] Remoção de credenciais não utilizadas
- [ ] Verificação de dependências vulneráveis (`mvn dependency-check`)
- [ ] Scan de secrets no repositório (`git-secrets`, `truffleHog`)

---

## 🔍 Ferramentas de Segurança

### Detectar Secrets no Código

**1. git-secrets:**
```bash
# Instalar
brew install git-secrets  # Mac
apt-get install git-secrets  # Linux

# Configurar
git secrets --install
git secrets --register-aws

# Escanear
git secrets --scan
```

**2. TruffleHog:**
```bash
# Instalar
pip install truffleHog

# Escanear repositório
trufflehog git https://github.com/seu-usuario/sales-api
```

**3. GitHub Secret Scanning:**
- Habilitado automaticamente em repositórios públicos
- Para privados: Settings → Code security and analysis → Enable

### SAST (Static Application Security Testing)

```yaml
# .github/workflows/security-scan.yml
name: Security Scan

on: [push, pull_request]

jobs:
  sast:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: OWASP Dependency Check
        uses: dependency-check/Dependency-Check_Action@main
        with:
          project: 'sales-api'
          path: '.'
          format: 'HTML'

      - name: SonarQube Scan
        uses: sonarsource/sonarqube-scan-action@master
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
```

---

## 📚 Referências

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [AWS Secrets Manager Best Practices](https://docs.aws.amazon.com/secretsmanager/latest/userguide/best-practices.html)
- [Quarkus Configuration Reference](https://quarkus.io/guides/config-reference)
- [12 Factor App - Config](https://12factor.net/config)

---

## 🆘 Em Caso de Vazamento

### Se uma credencial foi exposta:

1. **IMEDIATAMENTE:**
   - Rotacione a credencial comprometida
   - Revogue tokens/API keys afetados
   - Notifique a equipe de segurança

2. **Remova do histórico Git:**
```bash
# Usando BFG Repo-Cleaner
bfg --replace-text passwords.txt repo.git
cd repo.git
git reflog expire --expire=now --all
git gc --prune=now --aggressive
git push --force
```

3. **Auditoria:**
   - Verifique logs de acesso
   - Identifique uso não autorizado
   - Documente o incidente

4. **Prevenção:**
   - Configure git-secrets
   - Ative GitHub Secret Scanning
   - Treine a equipe

---

**🔐 Segurança não é opcional - é essencial!**
