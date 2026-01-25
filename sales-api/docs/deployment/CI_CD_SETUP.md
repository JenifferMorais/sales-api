# 🚀 Setup de CI/CD - GitHub Actions

Guia rápido para configurar o CI/CD automático com GitHub Actions.

## ✅ O Que Foi Configurado

O projeto possui CI/CD automático que:

1. **Trigger:** A cada push na branch `main`
2. **Build:** Compila com Maven (Java 21)
3. **Testes:** Executa todos os testes
4. **Docker:** Cria imagem Docker otimizada
5. **Push:** Publica no GitHub Container Registry (ghcr.io)
6. **Tags:** Gera múltiplas tags automaticamente

## 📋 Pré-requisitos

- ✅ Repositório no GitHub
- ✅ Branch `main` configurada
- ✅ Código com testes passando

## 🔧 Passo a Passo

### 1. Habilitar GitHub Packages

No seu repositório GitHub:

1. Acesse **Settings** → **Actions** → **General**
2. Em **Workflow permissions**, selecione:
   - ✅ **Read and write permissions**
   - ✅ **Allow GitHub Actions to create and approve pull requests**
3. Clique em **Save**

### 2. Fazer Push do Código

```bash
# Adicionar arquivos do CI/CD
git add .github/workflows/docker-build.yml
git add src/main/docker/
git add .dockerignore
git add docker-compose.prod.yml

# Commit
git commit -m "ci: adicionar pipeline CI/CD com Docker"

# Push para main
git push origin main
```

### 3. Acompanhar Execução

1. Acesse: `https://github.com/SEU-USUARIO/sales-api/actions`
2. Clique no workflow em execução
3. Acompanhe os logs em tempo real

**Etapas do Pipeline:**
```
✅ Checkout repository
✅ Set up JDK 21
✅ Build with Maven
✅ Run tests
✅ Log in to GitHub Container Registry
✅ Extract metadata (tags, labels)
✅ Build and push Docker image
✅ Image digest
```

### 4. Verificar Imagem Publicada

Após sucesso, a imagem estará disponível em:
```
ghcr.io/SEU-USUARIO/sales-api:latest
ghcr.io/SEU-USUARIO/sales-api:main
ghcr.io/SEU-USUARIO/sales-api:main-SHA
```

Acesse: `https://github.com/SEU-USUARIO/sales-api/pkgs/container/sales-api`

### 5. Tornar Imagem Pública (Opcional)

1. Acesse o package publicado
2. Clique em **Package settings**
3. Role até **Danger Zone**
4. Clique em **Change visibility**
5. Selecione **Public**
6. Confirme digitando o nome do repositório

## 🐳 Usar a Imagem

### Pull da Imagem

```bash
# Logar no GitHub Container Registry (se privada)
echo $GITHUB_TOKEN | docker login ghcr.io -u SEU-USUARIO --password-stdin

# Pull
docker pull ghcr.io/SEU-USUARIO/sales-api:latest
```

### Executar Localmente

```bash
docker run -it --rm -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=sales_db \
  -e DB_USERNAME=sales \
  -e DB_PASSWORD=sales123 \
  ghcr.io/SEU-USUARIO/sales-api:latest
```

### Deploy com Docker Compose

```bash
# Criar .env
cat > .env << EOF
GITHUB_REPOSITORY=SEU-USUARIO/sales-api
DB_NAME=sales_db
DB_USERNAME=sales
DB_PASSWORD=sua-senha
EOF

# Deploy
docker compose -f docker-compose.prod.yml up -d
```

## 🏷️ Sistema de Tags

O pipeline gera automaticamente as seguintes tags:

| Tag | Exemplo | Quando é criada |
|-----|---------|-----------------|
| `latest` | `ghcr.io/.../sales-api:latest` | A cada push em `main` |
| `branch` | `ghcr.io/.../sales-api:main` | Nome da branch |
| `sha` | `ghcr.io/.../sales-api:main-abc1234` | SHA do commit |
| `semver` | `ghcr.io/.../sales-api:v1.0.0` | Ao criar tag Git |
| `major.minor` | `ghcr.io/.../sales-api:1.0` | Ao criar tag Git |

### Criar Release com Tag Semântica

```bash
# Tag e push
git tag v1.0.0
git push origin v1.0.0

# Pipeline criará automaticamente:
# - ghcr.io/.../sales-api:v1.0.0
# - ghcr.io/.../sales-api:1.0
# - ghcr.io/.../sales-api:latest
```

## 🔄 Workflow em Pull Requests

Quando criar um PR:
- ✅ Build é executado
- ✅ Testes são executados
- ✅ Imagem Docker é buildada (mas NÃO publicada)
- ✅ Resultados aparecem no PR

Isso garante que o código está funcionando antes do merge.

## 🛠️ Troubleshooting

### Pipeline Falha no Build

**Erro comum:** Testes falhando

**Solução:**
```bash
# Testar localmente primeiro
./mvnw clean test

# Verificar se todos os testes passam
./mvnw verify
```

### Pipeline Falha no Push

**Erro:** `permission denied`

**Solução:**
1. Verifique **Workflow permissions** (passo 1)
2. Certifique-se de ter permissão de escrita no repositório

### Imagem Não Aparece nos Packages

**Causa:** Pipeline ainda executando ou falhou

**Solução:**
1. Verifique se o workflow completou com sucesso
2. Acesse Actions e verifique logs
3. Aguarde alguns minutos (pode demorar)

### Não Consigo Fazer Pull da Imagem

**Erro:** `unauthorized`

**Solução:**
```bash
# Criar Personal Access Token no GitHub
# Settings → Developer settings → Personal access tokens
# Scope: read:packages

# Login
echo $GITHUB_TOKEN | docker login ghcr.io -u SEU-USUARIO --password-stdin

# Ou tornar o package público (passo 5)
```

## 📊 Monitoramento

### Ver Histórico de Builds

```bash
# Via GitHub CLI
gh run list --workflow=docker-build.yml

# Ver logs de um build específico
gh run view RUN_ID --log
```

### Notificações

Configure notificações no GitHub:
1. **Settings** → **Notifications**
2. Habilite notificações para **Actions**

## 🔐 Segurança

### Secrets Usados no Pipeline

O pipeline usa automaticamente:
- `GITHUB_TOKEN` - Token automático do GitHub Actions
  - Permissões: read packages, write packages
  - Renovado automaticamente
  - Sem necessidade de configuração manual

### Adicionar Secrets Customizados

Se precisar de secrets adicionais (ex: Docker Hub):

1. **Settings** → **Secrets and variables** → **Actions**
2. Clique em **New repository secret**
3. Adicione:
   - `DOCKERHUB_USERNAME`
   - `DOCKERHUB_TOKEN`

### Scan de Vulnerabilidades (Recomendado)

Adicione ao workflow:

```yaml
- name: Run Trivy vulnerability scanner
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: ghcr.io/${{ github.repository }}:latest
    format: 'sarif'
    output: 'trivy-results.sarif'

- name: Upload Trivy results to GitHub Security
  uses: github/codeql-action/upload-sarif@v2
  with:
    sarif_file: 'trivy-results.sarif'
```

## 🚀 Próximos Passos

1. ✅ **Teste o pipeline** fazendo um push
2. ✅ **Configure deploy automático** em staging/produção
3. ✅ **Adicione badges** no README
4. ✅ **Configure notificações** de build

### Badge de Status (Opcional)

Adicione ao README.md:

```markdown
![Docker Build](https://github.com/SEU-USUARIO/sales-api/actions/workflows/docker-build.yml/badge.svg)
```

## 📚 Arquivos Relacionados

- `.github/workflows/docker-build.yml` - Workflow principal
- `.github/workflows/docker-build-dockerhub.yml.example` - Alternativa Docker Hub
- `src/main/docker/Dockerfile.jvm` - Dockerfile usado no build
- `.dockerignore` - Arquivos excluídos do build
- `docker-compose.prod.yml` - Deploy em produção

## 🔗 Links Úteis

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [Docker Build Action](https://github.com/docker/build-push-action)

---

**Última atualização:** 2026-01-24
**Versão:** 1.0.0
