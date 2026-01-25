# 🚀 Quick Start - Autenticação

## Setup Rápido (5 minutos)

### 1️⃣ Gerar Chaves JWT

**Linux/Mac:**
```bash
chmod +x generate-jwt-keys.sh
./generate-jwt-keys.sh
```

**Windows PowerShell:**
```powershell
.\generate-jwt-keys.ps1
```

**Ou manualmente:**
```bash
openssl genpkey -algorithm RSA -out src/main/resources/META-INF/resources/privateKey.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/META-INF/resources/privateKey.pem -out src/main/resources/META-INF/resources/publicKey.pem
```

### 2️⃣ Configurar Email (Opcional)

**Para desenvolvimento (modo mock):**
```bash
# Emails serão logados no console, não enviados
export SMTP_MOCK=true
```

**Para produção (Gmail):**
```bash
export SMTP_MOCK=false
export SMTP_USERNAME=seu-email@gmail.com
export SMTP_PASSWORD=sua-senha-app  # Gere em: https://myaccount.google.com/apppasswords
```

### 3️⃣ Iniciar Aplicação

```bash
./mvnw quarkus:dev
```

### 4️⃣ Testar Endpoints

#### a) Criar Cliente

```bash
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
```

#### b) Registrar Usuário

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "customerCode": "CUST001",
    "email": "joao@email.com",
    "password": "Senha@123",
    "confirmPassword": "Senha@123"
  }'
```

**Resposta:**
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

#### c) Fazer Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "password": "Senha@123"
  }'
```

#### d) Usar Token em Requisições

```bash
# Salve o token
TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."

# Use em requisições autenticadas (quando implementado)
curl -X GET http://localhost:8080/api/v1/sales \
  -H "Authorization: Bearer $TOKEN"
```

#### e) Esqueci Minha Senha

```bash
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com"
  }'
```

**Se SMTP_MOCK=true, veja o email no console:**
```
📧 Email de reset de senha enviado para: joao@email.com
Link de reset: http://localhost:8080/auth/reset-password?token=abc-123-def
```

#### f) Redefinir Senha

```bash
# Pegue o token do console ou email
curl -X POST http://localhost:8080/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "abc-123-def",
    "newPassword": "NovaSenha@456",
    "confirmPassword": "NovaSenha@456"
  }'
```

---

## 🎯 Próximos Passos

1. **Proteger Endpoints:**
   - Adicione `@RolesAllowed("USER")` nos controllers
   - Veja exemplo em: [AUTHENTICATION.md](./AUTHENTICATION.md#proteger-endpoints)

2. **Frontend:**
   - Salve o token no localStorage
   - Envie em todas as requisições: `Authorization: Bearer {token}`

3. **Produção:**
   - Configure SMTP real
   - Use HTTPS
   - Configure CORS restrito
   - Implemente rate limiting

---

## 📚 Documentação Completa

- [AUTHENTICATION.md](./AUTHENTICATION.md) - Guia completo de autenticação
- [SECURITY_GUIDE.md](./SECURITY_GUIDE.md) - Guia de segurança
- [Swagger UI](http://localhost:8080/swagger-ui) - Documentação interativa

---

**✅ Sistema de autenticação pronto para uso!**
