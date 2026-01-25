# 🔐 Sistema de Autenticação - Vendas API

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Funcionalidades](#funcionalidades)
3. [Endpoints](#endpoints)
4. [Fluxos de Uso](#fluxos-de-uso)
5. [Configuração JWT](#configuração-jwt)
6. [Testes](#testes)
7. [Segurança](#segurança)

---

## 🎯 Visão Geral

O sistema de autenticação foi implementado usando **JWT (JSON Web Tokens)** com as seguintes características:

- ✅ Registro de usuário associado a cliente existente
- ✅ Login com email e senha
- ✅ Logout com invalidação de token (blacklist)
- ✅ Recuperação de senha via email
- ✅ Tokens JWT com expiração configurável
- ✅ Senha hash com BCrypt (custo 12)
- ✅ Validações robustas de senha
- ✅ Emails HTML responsivos

---

## 🚀 Funcionalidades

### 1. Registro de Usuário

- Cria um usuário associado a um cliente existente
- Valida força da senha (8+ caracteres, maiúscula, minúscula, número, especial)
- Verifica se email já está em uso
- Envia email de boas-vindas
- Retorna token JWT automaticamente (auto-login)

### 2. Login

- Autentica usuário com email e senha
- Gera token JWT válido por 24 horas (configurável)
- Retorna informações do usuário

### 3. Esqueci Minha Senha

- Envia email com token de reset
- Token válido por 1 hora
- Não revela se email existe (segurança)

### 4. Redefinir Senha

- Valida token de reset
- Cria nova senha com mesmas validações
- Invalida token após uso

### 5. Logout

- Invalida o token JWT atual
- Adiciona token à blacklist
- Remove rastreamento de atividade
- Impede uso futuro do token
- Limpeza automática de tokens expirados

### 6. Timeout por Inatividade

- Rastreia última atividade do usuário
- Invalida automaticamente após 15 minutos sem requisições
- Timer é resetado a cada nova requisição
- Retorna erro específico: "Sessão expirada por inatividade"
- **Configurável** via `jwt.inactivity.timeout.minutes`

> 📖 Para detalhes completos sobre timeout de inatividade, consulte [INACTIVITY_TIMEOUT_GUIDE.md](INACTIVITY_TIMEOUT_GUIDE.md)

---

## 📡 Endpoints

### POST `/api/v1/auth/register`

Cadastra novo usuário.

**Request:**
```json
{
  "customerCode": "CUST001",
  "email": "joao@email.com",
  "password": "Senha@123",
  "confirmPassword": "Senha@123"
}
```

**Response (201):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "user": {
    "id": 1,
    "email": "joao@email.com",
    "customerCode": "CUST001"
  }
}
```

**Erros:**
- `400`: Email já em uso
- `400`: Cliente não encontrado
- `400`: Senhas não conferem
- `400`: Senha não atende requisitos

---

### POST `/api/v1/auth/login`

Autentica usuário.

**Request:**
```json
{
  "email": "joao@email.com",
  "password": "Senha@123"
}
```

**Response (200):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 86400
}
```

**Erros:**
- `401`: Credenciais inválidas
- `401`: Usuário inativo

---

### POST `/api/v1/auth/forgot-password`

Solicita reset de senha.

**Request:**
```json
{
  "email": "joao@email.com"
}
```

**Response (200):**
```json
{
  "message": "Se o email existir, um link de redefinição foi enviado"
}
```

**Email enviado:**
```
Assunto: [Vendas API] Redefinição de Senha

Olá,

Você solicitou a redefinição de senha para sua conta.

[Botão: Redefinir Senha]

Este link é válido por 1 hora.
```

---

### POST `/api/v1/auth/reset-password`

Redefine a senha.

**Request:**
```json
{
  "token": "abc123-def456-ghi789",
  "newPassword": "NovaSenha@123",
  "confirmPassword": "NovaSenha@123"
}
```

**Response (200):**
```json
{
  "message": "Senha redefinida com sucesso"
}
```

**Erros:**
- `400`: Token inválido ou expirado
- `400`: Senhas não conferem
- `400`: Senha não atende requisitos

---

### POST `/api/v1/auth/logout`

Invalida o token JWT atual (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
Não requer corpo (body vazio)

**Response (200):**
```json
{
  "message": "Logout realizado com sucesso"
}
```

**Erros:**
- `401`: Token não fornecido ou inválido
- `400`: Token mal formatado

**Observações:**
- Após o logout, o token é adicionado à blacklist
- Tentativas de usar o token retornarão erro 401
- O token permanece na blacklist até sua expiração natural
- Limpeza automática de tokens expirados ocorre diariamente

---

## 🔄 Fluxos de Uso

### Fluxo 1: Registro + Login Automático

```mermaid
sequenceDiagram
    Cliente->>Frontend: Preenche formulário
    Frontend->>API: POST /auth/register
    API->>Database: Verifica cliente existe
    API->>Database: Verifica email disponível
    API->>Database: Salva usuário (senha hash)
    API->>Email Service: Envia email boas-vindas
    API->>JWT Service: Gera token
    API->>Frontend: Retorna token + user info
    Frontend->>App: Redireciona para dashboard
```

### Fluxo 2: Login

```mermaid
sequenceDiagram
    Cliente->>Frontend: Email + Senha
    Frontend->>API: POST /auth/login
    API->>Database: Busca usuário por email
    API->>BCrypt: Verifica senha
    API->>JWT Service: Gera token
    API->>Frontend: Retorna token
    Frontend->>LocalStorage: Salva token
    Frontend->>App: Acessa área autenticada
```

### Fluxo 3: Reset de Senha

```mermaid
sequenceDiagram
    Cliente->>Frontend: Esqueci senha
    Frontend->>API: POST /auth/forgot-password
    API->>Database: Busca usuário
    API->>Token Service: Gera token reset
    API->>Database: Salva token + expiração
    API->>Email Service: Envia email
    Email Service->>Cliente: Email com link
    Cliente->>Frontend: Clica no link
    Frontend->>API: POST /auth/reset-password
    API->>Database: Valida token
    API->>Database: Atualiza senha
    API->>Frontend: Confirma sucesso
```

### Fluxo 4: Logout

```mermaid
sequenceDiagram
    Cliente->>Frontend: Clica em Logout
    Frontend->>API: POST /auth/logout (com token)
    API->>JWT Parser: Extrai informações do token
    API->>Hash Service: Gera SHA-256 do token
    API->>Database: Adiciona hash à blacklist
    API->>Frontend: Confirma logout
    Frontend->>LocalStorage: Remove token
    Frontend->>App: Redireciona para login

    Note over Cliente,App: Tentativa de usar token invalidado
    Cliente->>Frontend: Acessa recurso protegido
    Frontend->>API: GET /customers (com token antigo)
    API->>Blacklist Filter: Verifica se token está na blacklist
    Blacklist Filter->>Database: Consulta hash do token
    Database->>Blacklist Filter: Token encontrado na blacklist
    Blacklist Filter->>API: Bloqueia requisição
    API->>Frontend: 401 Unauthorized
    Frontend->>App: Redireciona para login
```

---

## 🔑 Configuração JWT

### Gerar Par de Chaves RSA

```bash
# Gerar chave privada
openssl genpkey -algorithm RSA -out privateKey.pem -pkeyopt rsa_keygen_bits:2048

# Gerar chave pública
openssl rsa -pubout -in privateKey.pem -out publicKey.pem

# Mover para resources
mv privateKey.pem src/main/resources/META-INF/resources/
mv publicKey.pem src/main/resources/META-INF/resources/
```

### Estrutura do Token JWT

**Header:**
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "iss": "sales-api",
  "sub": "1",
  "email": "joao@email.com",
  "customerCode": "CUST001",
  "groups": ["USER"],
  "iat": 1704067200,
  "exp": 1704153600
}
```

### Usar Token nas Requisições

```bash
curl -X GET http://localhost:8080/api/v1/sales \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**JavaScript:**
```javascript
fetch('/api/v1/sales', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

---

## 🧪 Testes

### Teste Manual - Registro

```bash
# 1. Criar um cliente primeiro
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CUST001",
    "fullName": "João Silva",
    "motherName": "Maria Silva",
    "cpf": "123.456.789-09",
    "rg": "123456789",
    "address": {
      "zipCode": "01310-100",
      "street": "Av. Paulista",
      "number": "1000",
      "neighborhood": "Bela Vista",
      "city": "São Paulo",
      "state": "SP"
    },
    "birthDate": "1990-05-15",
    "cellPhone": "(11) 98765-4321",
    "email": "joao@email.com"
  }'

# 2. Registrar usuário
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "customerCode": "CUST001",
    "email": "joao@email.com",
    "password": "Senha@123",
    "confirmPassword": "Senha@123"
  }'
```

### Teste Manual - Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "password": "Senha@123"
  }'
```

### Teste Manual - Forgot Password

```bash
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com"
  }'
```

### Teste Manual - Reset Password

```bash
# Pegue o token do email ou do banco de dados
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "abc123-def456-ghi789",
    "newPassword": "NovaSenha@456",
    "confirmPassword": "NovaSenha@456"
  }'
```

---

## 🛡️ Segurança

### Validação de Senha

**Requisitos mínimos:**
- ✅ 8 caracteres
- ✅ 1 letra maiúscula (A-Z)
- ✅ 1 letra minúscula (a-z)
- ✅ 1 número (0-9)
- ✅ 1 caractere especial (@$!%*?&)

**Exemplos válidos:**
- `Senha@123`
- `MyP@ssw0rd`
- `Secur3!Pass`

**Exemplos inválidos:**
- `senha123` ❌ (sem maiúscula e caractere especial)
- `SENHA@123` ❌ (sem minúscula)
- `Senha@` ❌ (menos de 8 caracteres)
- `SenhaForte123` ❌ (sem caractere especial)

### Hash de Senha

- **Algoritmo:** BCrypt
- **Custo:** 12 rounds
- **Salt:** Gerado automaticamente por hash

```java
// Exemplo de hash gerado:
$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIoSh.aHie
```

### Proteção Contra Ataques

**1. Timing Attack:**
```java
// ❌ Vulnerável
if (user.getPassword().equals(inputPassword)) { ... }

// ✅ Seguro (BCrypt com tempo constante)
if (password.matches(plainPassword)) { ... }
```

**2. Information Disclosure:**
```java
// ❌ Revela se email existe
throw new Exception("Email não encontrado");

// ✅ Mensagem genérica
return "Se o email existir, um link foi enviado";
```

**3. Brute Force:**
- Token de reset válido por apenas 1 hora
- Senha forte obrigatória
- Rate limiting (implementar em produção)

### Boas Práticas Implementadas

- [x] Senhas nunca são logadas
- [x] Hash BCrypt com custo 12
- [x] Tokens JWT com expiração
- [x] Chaves RSA 2048 bits
- [x] Email não exposto em mensagens de erro
- [x] HTTPS obrigatório em produção (configure no reverse proxy)
- [x] CORS configurado apenas para origens confiáveis
- [x] Validação de entrada em todas as camadas

---

## ⚙️ Configuração de Email

### Desenvolvimento (Mock)

```properties
quarkus.mailer.mock=true
```

Emails são logados no console, não são enviados.

### Produção (Gmail)

```properties
quarkus.mailer.mock=false
quarkus.mailer.from=noreply@sales.com
quarkus.mailer.host=smtp.gmail.com
quarkus.mailer.port=587
quarkus.mailer.start-tls=true
quarkus.mailer.username=seu-email@gmail.com
quarkus.mailer.password=sua-senha-app
```

**Gerar senha de app Gmail:**
1. Acesse: https://myaccount.google.com/security
2. Ative verificação em 2 etapas
3. Gere uma "senha de app"
4. Use essa senha no `SMTP_PASSWORD`

### Outros Provedores

**SendGrid:**
```properties
quarkus.mailer.host=smtp.sendgrid.net
quarkus.mailer.port=587
quarkus.mailer.username=apikey
quarkus.mailer.password=SG.xxx
```

**AWS SES:**
```properties
quarkus.mailer.host=email-smtp.us-east-1.amazonaws.com
quarkus.mailer.port=587
quarkus.mailer.username=YOUR_SMTP_USERNAME
quarkus.mailer.password=YOUR_SMTP_PASSWORD
```

---

## 📚 Recursos Adicionais

- [JWT.io](https://jwt.io/) - Debugar tokens JWT
- [BCrypt Calculator](https://bcrypt-generator.com/) - Testar hash BCrypt
- [Quarkus Security Guide](https://quarkus.io/guides/security-jwt)
- [OWASP Password Storage](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)

---

**🔐 Segurança é prioridade!**
