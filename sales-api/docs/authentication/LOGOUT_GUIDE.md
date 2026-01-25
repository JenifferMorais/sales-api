# Guia de Logout - API de Vendas

Este documento descreve a funcionalidade de logout implementada no sistema.

## Visão Geral

O sistema de logout invalida tokens JWT através de uma **blacklist de tokens**. Quando um usuário faz logout, o token é adicionado a uma lista de tokens invalidados, impedindo seu uso futuro mesmo que ainda não tenha expirado.

### Invalidação Automática por Inatividade

Além do logout manual, o sistema também invalida automaticamente tokens quando o usuário fica **15 minutos sem fazer nenhuma requisição**. Isso aumenta a segurança prevenindo que sessões abandonadas permaneçam ativas.

Para mais detalhes, consulte: [INACTIVITY_TIMEOUT_GUIDE.md](INACTIVITY_TIMEOUT_GUIDE.md)

## Como Funciona

### 1. Processo de Logout

Quando o usuário faz logout:
1. O token JWT é recebido do header `Authorization`
2. Um hash SHA-256 do token é gerado
3. O hash é armazenado na tabela `token_blacklist` junto com:
   - ID do usuário
   - Data/hora do logout
   - Data/hora de expiração original do token

### 2. Validação em Requisições

Para cada requisição autenticada:
1. O Quarkus Security valida a assinatura e expiração do JWT
2. O `TokenBlacklistFilter` verifica se o token está na blacklist
3. Se estiver na blacklist, a requisição é rejeitada com status 401

### 3. Limpeza Automática

Um job agendado (`TokenBlacklistCleanupJob`) executa diariamente à meia-noite para:
- Remover tokens expirados da blacklist
- Evitar crescimento desnecessário da tabela
- Manter performance otimizada

## Endpoint de Logout

### POST /api/v1/auth/logout

**Autenticação:** Requerida (token JWT válido)

**Headers:**
```
Authorization: Bearer {seu-token-jwt}
```

**Request:**
Não requer corpo (body vazio)

**Response Sucesso (200 OK):**
```json
{
  "message": "Logout realizado com sucesso"
}
```

**Response Erro (401 Unauthorized):**
```json
{
  "message": "Token de autenticação não fornecido"
}
```

**Response Erro (400 Bad Request):**
```json
{
  "message": "Token inválido: [detalhes do erro]"
}
```

## Exemplos de Uso

### Exemplo 1: Logout com cURL

```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Exemplo 2: Logout com JavaScript/Fetch

```javascript
async function logout() {
  const token = localStorage.getItem('access_token');

  const response = await fetch('http://localhost:8080/api/v1/auth/logout', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (response.ok) {
    // Remove o token do armazenamento local
    localStorage.removeItem('access_token');
    // Redireciona para página de login
    window.location.href = '/login';
  } else {
    console.error('Erro ao fazer logout');
  }
}
```

### Exemplo 3: Logout com Axios

```javascript
import axios from 'axios';

async function logout() {
  try {
    await axios.post('/api/v1/auth/logout', {}, {
      headers: {
        'Authorization': `Bearer ${getAccessToken()}`
      }
    });

    // Limpa o token
    clearAccessToken();

    // Redireciona
    router.push('/login');
  } catch (error) {
    console.error('Erro ao fazer logout:', error);
  }
}
```

## Estrutura de Arquivos

### Camada de Domínio/Aplicação
- **Use Case:** `LogoutUseCase` - Lógica de negócio do logout

### Camada de Infraestrutura

**Persistência:**
- `TokenBlacklistEntity` - Entidade JPA para tokens invalidados
- `TokenBlacklistPanacheRepository` - Repositório Panache para operações de blacklist

**Segurança:**
- `TokenBlacklistService` - Serviço para gerenciar blacklist de tokens
- `TokenBlacklistFilter` - Filtro JAX-RS que intercepta requisições
- `TokenBlacklistCleanupJob` - Job agendado para limpeza

**REST:**
- `AuthController.logout()` - Endpoint REST de logout

**Banco de Dados:**
- Migration: `V006__create_token_blacklist_table.sql`

## Modelo de Dados

### Tabela: token_blacklist

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGSERIAL | ID primário |
| token_hash | VARCHAR(64) | Hash SHA-256 do token JWT (único) |
| user_id | BIGINT | ID do usuário que fez logout |
| blacklisted_at | TIMESTAMP | Data/hora do logout |
| expires_at | TIMESTAMP | Data/hora de expiração original do token |

**Índices:**
- `idx_token_hash` - Busca rápida por hash do token
- `idx_expires_at` - Limpeza eficiente de tokens expirados

## Segurança

### Por que usar Hash do Token?

Em vez de armazenar o token JWT completo, armazenamos apenas seu hash SHA-256:

1. **Segurança:** Se o banco de dados for comprometido, tokens não ficam expostos
2. **Performance:** Hash tem tamanho fixo (64 caracteres) vs token completo (~800+ caracteres)
3. **Privacidade:** Não é possível reverter o hash para obter o token original

### Token Lifecycle

```
┌─────────────┐
│ Login       │ Token gerado e retornado
│             │
└──────┬──────┘
       │
       v
┌─────────────┐
│ Uso normal  │ Token válido, não está na blacklist
│             │
└──────┬──────┘
       │
       v
┌─────────────┐
│ Logout      │ Token adicionado à blacklist
│             │
└──────┬──────┘
       │
       v
┌─────────────┐
│ Token inval.│ Requisições são rejeitadas (401)
│             │
└──────┬──────┘
       │
       v
┌─────────────┐
│ Expiração   │ Token removido da blacklist (cleanup job)
│             │
└─────────────┘
```

## Performance

### Otimizações Implementadas

1. **Índices de Banco de Dados:**
   - Busca por hash é O(log n) com índice B-tree
   - Limpeza de expirados usa índice em `expires_at`

2. **Hash SHA-256:**
   - Tamanho fixo de 64 caracteres
   - Busca e comparação muito rápidas

3. **Limpeza Automática:**
   - Remove tokens expirados diariamente
   - Mantém tabela enxuta

4. **Filtro de Requisição:**
   - Executa após validação JWT do Quarkus
   - Apenas uma consulta SQL por requisição autenticada

### Estimativa de Volume

Assumindo:
- 1000 usuários ativos
- Cada usuário faz 2 logins por dia
- Token expira em 24 horas

**Volume diário de blacklist:** ~2000 tokens
**Volume máximo (sem cleanup):** ~2000 tokens (removidos após expiração)

## Considerações de Design

### Alternativas Não Escolhidas

1. **Redis/Cache:** Adiciona dependência externa, complexidade
2. **Apenas Client-side:** Inseguro, token permanece válido
3. **Refresh Tokens:** Mais complexo, não era requisito

### Limitações

1. **Logout não revoga refresh tokens:** Sistema atual não usa refresh tokens
2. **Logout em um dispositivo não afeta outros:** Cada login gera um token único
3. **Tokens expirados permanecem por até 24h na blacklist:** Até o próximo cleanup

### Futuras Melhorias

1. **Cache em memória:** Armazenar blacklist em cache (Caffeine, Redis) para performance
2. **Logout global:** Invalidar todos os tokens de um usuário
3. **Histórico de logins:** Registrar logins/logouts para auditoria
4. **Push notifications:** Notificar outros dispositivos sobre logout

## Troubleshooting

### Problema: "Token inválido ou expirado" após login

**Possível causa:** Token foi adicionado à blacklist por erro

**Solução:**
```sql
-- Verificar se token está na blacklist
SELECT * FROM token_blacklist
WHERE user_id = [ID_DO_USUARIO]
AND expires_at > NOW();

-- Remover entrada incorreta (se necessário)
DELETE FROM token_blacklist WHERE id = [ID];
```

### Problema: Tabela token_blacklist crescendo muito

**Possível causa:** Job de limpeza não está executando

**Solução:**
1. Verificar logs do Quarkus para erros do job
2. Executar limpeza manual:
```sql
DELETE FROM token_blacklist WHERE expires_at < NOW();
```

### Problema: Performance degradada em requisições

**Possível causa:** Índices não criados ou estatísticas desatualizadas

**Solução:**
```sql
-- Verificar índices
\d token_blacklist

-- Atualizar estatísticas
ANALYZE token_blacklist;

-- Recriar índices se necessário
REINDEX TABLE token_blacklist;
```

## Testes

### Teste Manual

1. **Fazer login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"senha123"}'
```

2. **Salvar o token retornado**

3. **Acessar recurso protegido (deve funcionar):**
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer [TOKEN]"
```

4. **Fazer logout:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer [TOKEN]"
```

5. **Tentar acessar recurso novamente (deve falhar com 401):**
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer [TOKEN]"
```

### Teste de Limpeza Automática

```bash
# Verificar quantos tokens expirados existem
SELECT COUNT(*) FROM token_blacklist WHERE expires_at < NOW();

# Executar limpeza manualmente
curl -X POST http://localhost:8080/q/scheduler/test/TokenBlacklistCleanupJob/cleanupExpiredTokens

# Verificar novamente (deve ser 0)
SELECT COUNT(*) FROM token_blacklist WHERE expires_at < NOW();
```

## Documentação OpenAPI

O endpoint de logout está documentado no Swagger UI em:
```
http://localhost:8080/q/swagger-ui
```

Procure pela tag **"Autenticação"** e endpoint **POST /api/v1/auth/logout**.

## Conformidade e Segurança

### OWASP Top 10

- ✅ **A01:2021 – Broken Access Control:** Tokens invalidados não concedem acesso
- ✅ **A02:2021 – Cryptographic Failures:** Hash SHA-256 para proteção de tokens
- ✅ **A07:2021 – Identification and Authentication Failures:** Logout adequado invalida sessões

### LGPD/GDPR

- ✅ Permite que usuários encerrem suas sessões
- ✅ Tokens não armazenam dados pessoais sensíveis
- ✅ Sistema de limpeza automática remove dados desnecessários

---

**Última atualização:** 2026-01-24
**Versão da API:** 1.0.0
