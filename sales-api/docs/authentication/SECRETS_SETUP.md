# 🔐 Configuração de Secrets - Guia Passo a Passo

## 📋 Índice

1. [Configuração Local](#configuração-local)
2. [GitHub Secrets](#github-secrets)
3. [AWS Secrets Manager](#aws-secrets-manager)
4. [Docker Secrets](#docker-secrets)
5. [Kubernetes Secrets](#kubernetes-secrets)

---

## 🏠 Configuração Local

### Passo 1: Criar Arquivo .env

```bash
# Copie o template
cp .env.example .env

# Edite com seu editor favorito
nano .env
# ou
code .env
```

### Passo 2: Preencher Credenciais

**`.env`:**
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sales_db
DB_USERNAME=vendas_user
DB_PASSWORD=sua_senha_forte_aqui

# JWT
JWT_SECRET_KEY=gere-uma-chave-de-32-caracteres-ou-mais
JWT_ISSUER=sales-api
JWT_EXPIRATION_HOURS=24

# Email (opcional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=seu-email@gmail.com
SMTP_PASSWORD=sua-senha-app-gmail
```

### Passo 3: Carregar Variáveis

**Opção A - Usando direnv (Recomendado):**
```bash
# Instalar direnv
# Mac: brew install direnv
# Ubuntu: sudo apt install direnv

# Configurar shell (adicione ao ~/.bashrc ou ~/.zshrc)
eval "$(direnv hook bash)"  # para bash
eval "$(direnv hook zsh)"   # para zsh

# Criar .envrc
echo "dotenv" > .envrc

# Permitir direnv
direnv allow

# Agora as variáveis são carregadas automaticamente!
```

**Opção B - Carregar Manualmente:**
```bash
# Linux/Mac
export $(cat .env | xargs)
./mvnw quarkus:dev

# Windows PowerShell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
.\mvnw quarkus:dev
```

**Opção C - IntelliJ IDEA:**
1. Run → Edit Configurations
2. Environment Variables → 📁 (folder icon)
3. Clique em + e carregue o arquivo .env
4. Ou adicione manualmente:
```
DB_HOST=localhost;DB_PORT=5432;DB_NAME=sales_db;DB_USERNAME=vendas_user;DB_PASSWORD=senha123;JWT_SECRET_KEY=sua-chave-secreta-aqui
```

**Opção D - VS Code (launch.json):**
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Quarkus Dev Mode",
      "request": "launch",
      "mainClass": "io.quarkus.bootstrap.runner.QuarkusEntryPoint",
      "envFile": "${workspaceFolder}/.env"
    }
  ]
}
```

---

## 🐙 GitHub Secrets

### Passo 1: Acessar Configurações

1. Vá para seu repositório no GitHub
2. Clique em **Settings** (⚙️)
3. No menu lateral, clique em **Secrets and variables** → **Actions**

### Passo 2: Criar Secrets

Clique em **New repository secret** e adicione cada um:

#### Secrets de Desenvolvimento/Teste

| Secret Name | Example Value | Descrição |
|-------------|---------------|-----------|
| `JWT_SECRET_KEY_TEST` | `test-secret-key-for-ci-pipeline-32-chars` | Chave JWT para testes |
| `CODECOV_TOKEN` | `abc123...` | Token do Codecov |
| `SONAR_TOKEN` | `sqp_abc123...` | Token do SonarQube |
| `SONAR_HOST_URL` | `https://sonarcloud.io` | URL do SonarQube |

#### Secrets de Staging

| Secret Name | Value | Descrição |
|-------------|-------|-----------|
| `AWS_ACCESS_KEY_ID_STAGING` | `AKIA...` | AWS Access Key (Staging) |
| `AWS_SECRET_ACCESS_KEY_STAGING` | `xyz...` | AWS Secret Key (Staging) |
| `DB_HOST_STAGING` | `staging-db.example.com` | Host do banco (Staging) |
| `DB_USERNAME_STAGING` | `vendas_staging` | Usuário do banco (Staging) |
| `DB_PASSWORD_STAGING` | `senha-forte-staging` | Senha do banco (Staging) |
| `JWT_SECRET_KEY_STAGING` | `staging-jwt-secret-32-chars-min` | Chave JWT (Staging) |

#### Secrets de Produção

| Secret Name | Value | Descrição |
|-------------|-------|-----------|
| `AWS_ACCESS_KEY_ID_PROD` | `AKIA...` | AWS Access Key (Produção) |
| `AWS_SECRET_ACCESS_KEY_PROD` | `xyz...` | AWS Secret Key (Produção) |
| `DB_HOST_PROD` | `prod-db.example.com` | Host do banco (Produção) |
| `DB_USERNAME_PROD` | `vendas_prod` | Usuário do banco (Produção) |
| `DB_PASSWORD_PROD` | `senha-forte-producao` | Senha do banco (Produção) |
| `JWT_SECRET_KEY_PROD` | `prod-jwt-secret-min-32-characters` | Chave JWT (Produção) |

#### Outros Secrets

| Secret Name | Value | Descrição |
|-------------|-------|-----------|
| `DOCKER_USERNAME` | `seu-usuario-docker` | Usuário Docker Hub |
| `DOCKER_PASSWORD` | `sua-senha-docker` | Senha Docker Hub |
| `SLACK_WEBHOOK_URL` | `https://hooks.slack.com/...` | Webhook do Slack |

### Passo 3: Criar Environments

1. Settings → Environments → **New environment**
2. Crie 3 ambientes: `development`, `staging`, `production`
3. Para `production`:
   - Environment protection rules → ✅ Required reviewers (adicione revisores)
   - ✅ Wait timer → 5 minutes (tempo de espera antes do deploy)
4. Adicione secrets específicos em cada ambiente

### Passo 4: Usar Secrets no Workflow

**Exemplo de uso:**
```yaml
jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production  # Usa secrets do ambiente 'production'
    steps:
      - name: Deploy
        env:
          DB_PASSWORD: ${{ secrets.DB_PASSWORD_PROD }}
          JWT_SECRET: ${{ secrets.JWT_SECRET_KEY_PROD }}
        run: ./deploy.sh
```

---

## ☁️ AWS Secrets Manager

### Passo 1: Criar Secret no AWS

**Via AWS Console:**
1. Acesse [AWS Secrets Manager Console](https://console.aws.amazon.com/secretsmanager)
2. **Store a new secret**
3. Secret type: **Other type of secret**
4. Key/value pairs:
```json
{
  "username": "vendas_prod",
  "password": "SenhaForte123!@#",
  "host": "sales-db.cluster-abc.us-east-1.rds.amazonaws.com",
  "port": "5432",
  "dbname": "vendas_production"
}
```
5. Secret name: `sales-api/prod/database`
6. Configure rotation: **Enable automatic rotation** (90 days)

**Via AWS CLI:**
```bash
aws secretsmanager create-secret \
    --name sales-api/prod/database \
    --description "Database credentials for Vendas API Production" \
    --secret-string '{
        "username":"vendas_prod",
        "password":"SenhaForte123!@#",
        "host":"sales-db.cluster-abc.us-east-1.rds.amazonaws.com",
        "port":"5432",
        "dbname":"vendas_production"
    }' \
    --region us-east-1
```

### Passo 2: Criar Secret para JWT

```bash
aws secretsmanager create-secret \
    --name sales-api/prod/jwt \
    --secret-string '{
        "secret_key":"sua-chave-jwt-super-secreta-min-32-chars",
        "issuer":"sales-api",
        "expiration_hours":"24"
    }' \
    --region us-east-1
```

### Passo 3: Configurar IAM Policy

**Criar política:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret"
      ],
      "Resource": [
        "arn:aws:secretsmanager:us-east-1:123456789012:secret:sales-api/*"
      ]
    }
  ]
}
```

**Anexar à role do ECS Task:**
```bash
aws iam put-role-policy \
    --role-name sales-api-ecs-task-role \
    --policy-name SecretsManagerAccess \
    --policy-document file://secrets-policy.json
```

### Passo 4: Integrar com Quarkus

**Adicionar dependência:**
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>secretsmanager</artifactId>
    <version>2.20.0</version>
</dependency>
```

**Criar classe de configuração:**
```java
@ApplicationScoped
public class AwsSecretsConfig {

    private final SecretsManagerClient secretsClient;

    public AwsSecretsConfig() {
        this.secretsClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }

    public String getSecret(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse response = secretsClient.getSecretValue(request);
        return response.secretString();
    }

    public Map<String, String> getDatabaseCredentials() {
        String json = getSecret("sales-api/prod/database");
        return new Gson().fromJson(json, Map.class);
    }
}
```

### Passo 5: Usar em ECS Task Definition

**ecs-task-definition-prod.json:**
```json
{
  "family": "sales-api-task-prod",
  "containerDefinitions": [
    {
      "name": "sales-api",
      "image": "seu-usuario/sales-api:latest",
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:sales-api/prod/database:password::"
        },
        {
          "name": "DB_USERNAME",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:sales-api/prod/database:username::"
        },
        {
          "name": "JWT_SECRET_KEY",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:sales-api/prod/jwt:secret_key::"
        }
      ]
    }
  ]
}
```

---

## 🐋 Docker Secrets

### Passo 1: Criar Secrets

```bash
# Criar secrets no Docker Swarm
echo "vendas_prod" | docker secret create db_username -
echo "SenhaForte123" | docker secret create db_password -
echo "chave-jwt-secreta" | docker secret create jwt_secret -
```

### Passo 2: Docker Compose com Secrets

**docker-compose.yml:**
```yaml
version: '3.8'

services:
  sales-api:
    image: sales-api:latest
    secrets:
      - db_password
      - jwt_secret
    environment:
      DB_HOST: postgres
      DB_USERNAME: vendas_prod
      DB_PASSWORD_FILE: /run/secrets/db_password
      JWT_SECRET_FILE: /run/secrets/jwt_secret
    deploy:
      replicas: 3

secrets:
  db_password:
    external: true
  jwt_secret:
    external: true
```

### Passo 3: Ler Secrets na Aplicação

```java
public class SecretReader {

    public static String readSecret(String secretName) {
        String secretPath = "/run/secrets/" + secretName;
        try {
            return Files.readString(Path.of(secretPath)).trim();
        } catch (IOException e) {
            // Fallback para variável de ambiente
            return System.getenv(secretName.toUpperCase());
        }
    }
}
```

---

## ☸️ Kubernetes Secrets

### Passo 1: Criar Secret

**Opção A - Kubectl:**
```bash
kubectl create secret generic sales-api-secrets \
    --from-literal=db-password='SenhaForte123' \
    --from-literal=jwt-secret='chave-jwt-secreta' \
    --namespace=production
```

**Opção B - YAML:**
```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: sales-api-secrets
  namespace: production
type: Opaque
data:
  # Base64 encoded values
  db-password: U2VuaGFGb3J0ZTEyMw==
  jwt-secret: Y2hhdmUtand0LXNlY3JldGE=
```

```bash
# Aplicar
kubectl apply -f secret.yaml
```

**Gerar valores base64:**
```bash
echo -n 'SenhaForte123' | base64
echo -n 'chave-jwt-secreta' | base64
```

### Passo 2: Usar em Deployment

**deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sales-api
  namespace: production
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: sales-api
        image: sales-api:latest
        env:
          # Opção 1: Injetar como variável de ambiente
          - name: DB_PASSWORD
            valueFrom:
              secretKeyRef:
                name: sales-api-secrets
                key: db-password

          - name: JWT_SECRET_KEY
            valueFrom:
              secretKeyRef:
                name: sales-api-secrets
                key: jwt-secret

        # Opção 2: Montar como arquivo
        volumeMounts:
        - name: secrets
          mountPath: "/etc/secrets"
          readOnly: true

      volumes:
      - name: secrets
        secret:
          secretName: sales-api-secrets
```

### Passo 3: External Secrets Operator (Recomendado)

**Integração com AWS Secrets Manager:**

```bash
# Instalar External Secrets Operator
helm repo add external-secrets https://charts.external-secrets.io
helm install external-secrets external-secrets/external-secrets
```

**SecretStore:**
```yaml
apiVersion: external-secrets.io/v1beta1
kind: SecretStore
metadata:
  name: aws-secrets-manager
  namespace: production
spec:
  provider:
    aws:
      service: SecretsManager
      region: us-east-1
      auth:
        secretRef:
          accessKeyIDSecretRef:
            name: aws-credentials
            key: access-key-id
          secretAccessKeySecretRef:
            name: aws-credentials
            key: secret-access-key
```

**ExternalSecret:**
```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: sales-api-external-secret
  namespace: production
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: aws-secrets-manager
    kind: SecretStore
  target:
    name: sales-api-secrets
    creationPolicy: Owner
  data:
  - secretKey: db-password
    remoteRef:
      key: sales-api/prod/database
      property: password
  - secretKey: jwt-secret
    remoteRef:
      key: sales-api/prod/jwt
      property: secret_key
```

---

## ✅ Verificação Final

### Checklist de Segurança

- [ ] `.env` está no `.gitignore`
- [ ] `.env.example` não contém valores reais
- [ ] Secrets configurados no GitHub Actions
- [ ] Environments criados (dev, staging, prod)
- [ ] AWS Secrets Manager configurado (produção)
- [ ] IAM policies com least privilege
- [ ] Rotação automática de secrets ativada
- [ ] Logs não expõem credenciais
- [ ] TLS/SSL ativado em todas as conexões

### Teste de Validação

```bash
# 1. Verifique se não há credenciais no código
git grep -i "password\s*=\s*['\"]" -- '*.java' '*.properties'

# 2. Verifique se .env está ignorado
git check-ignore .env  # Deve retornar: .env

# 3. Verifique se secrets estão carregados
./mvnw quarkus:dev
# No log, deve aparecer: Datasource connected to: jdbc:postgresql://...
```

---

## 📚 Recursos Adicionais

- [GitHub Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [AWS Secrets Manager Best Practices](https://docs.aws.amazon.com/secretsmanager/latest/userguide/best-practices.html)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)
- [External Secrets Operator](https://external-secrets.io/)
- [12 Factor App - Config](https://12factor.net/config)

---

**🔐 Lembre-se: Segurança é responsabilidade de todos!**
