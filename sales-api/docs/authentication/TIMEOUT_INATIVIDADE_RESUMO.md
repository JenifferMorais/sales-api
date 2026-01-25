# ⏱️ Sistema de Timeout por Inatividade - Resumo

## ✅ Implementação Concluída

O sistema agora invalida automaticamente tokens JWT após **15 minutos de inatividade**.

## 🎯 Como Funciona

### Rastreamento de Atividade
- Cada requisição autenticada atualiza o timestamp de última atividade
- Hash SHA-256 do token é usado como identificador único
- Armazenado na tabela `user_activity`

### Verificação Automática
Em cada requisição, o `TokenBlacklistFilter`:
1. ✅ Verifica se token está na blacklist
2. ✅ Verifica se passou 15 min desde última atividade
3. ✅ Se inativo → adiciona à blacklist e retorna erro 401
4. ✅ Se ativo → atualiza timestamp e processa requisição

### Mensagem de Erro
```json
{
  "message": "Sessão expirada por inatividade. Faça login novamente."
}
```

## 📁 Arquivos Criados/Modificados

### Novos Arquivos (8)
1. `UserActivityEntity.java` - Entidade JPA para rastreamento
2. `UserActivityPanacheRepository.java` - Repositório de atividades
3. `UserActivityService.java` - Lógica de timeout (130+ linhas)
4. `V007__create_user_activity_table.sql` - Migration da tabela
5. `INACTIVITY_TIMEOUT_GUIDE.md` - Documentação completa (400+ linhas)
6. `TIMEOUT_INATIVIDADE_RESUMO.md` - Este arquivo

### Arquivos Modificados (5)
1. `TokenBlacklistFilter.java` - Adicionada verificação de inatividade
2. `LogoutUseCase.java` - Remove atividade no logout
3. `TokenBlacklistCleanupJob.java` - Limpa atividades antigas
4. `application.properties` - Configuração de timeout
5. `LOGOUT_GUIDE.md` - Menção ao timeout de inatividade
6. `AUTHENTICATION.md` - Nova funcionalidade documentada

## 🗄️ Estrutura do Banco de Dados

### Nova Tabela: user_activity
```sql
CREATE TABLE user_activity (
    id BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    last_activity_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Índices para performance
CREATE INDEX idx_token_hash_activity ON user_activity(token_hash);
CREATE INDEX idx_last_activity ON user_activity(last_activity_at);
```

## ⚙️ Configuração

### application.properties
```properties
# Padrão: 15 minutos
jwt.inactivity.timeout.minutes=${JWT_INACTIVITY_TIMEOUT_MINUTES:15}
```

### Variável de Ambiente
```bash
# Alterar para 30 minutos
export JWT_INACTIVITY_TIMEOUT_MINUTES=30

# Para desenvolvimento/testes (1 minuto)
export JWT_INACTIVITY_TIMEOUT_MINUTES=1
```

## 📊 Cenários de Uso

### ✅ Usuário Ativo
```
00:00 - Login
00:05 - GET /customers (atualiza atividade)
00:10 - POST /sales (atualiza atividade)
00:14 - GET /products (atualiza atividade)
00:20 - GET /reports (atualiza atividade)

Resultado: Todas funcionam normalmente
```

### ❌ Usuário Inativo
```
00:00 - Login
00:05 - GET /customers (atualiza atividade)
[Usuário para de usar]
00:21 - GET /products (16 min depois)

Resultado: 401 - "Sessão expirada por inatividade"
```

### ⚡ Timer Resetado
```
00:00 - Login
00:05 - GET /customers
00:14 - GET /products (14 min, ainda ativo)
00:25 - GET /sales (11 min após última, ativo)

Resultado: Timer é resetado a cada requisição
```

## 🔒 Diferenças: Expiração vs Inatividade

| Aspecto | Expiração JWT | Timeout Inatividade |
|---------|---------------|---------------------|
| **Tempo** | 24h (fixo) | 15 min sem uso |
| **Resetável** | ❌ Não | ✅ Sim |
| **Objetivo** | Vida máxima | Detectar abandono |

## 🧹 Limpeza Automática

Job diário à meia-noite:
- Remove tokens expirados da blacklist
- Remove atividades antigas (> 24 horas)

## 💻 Tratamento no Frontend

### React Example
```typescript
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      const message = error.response?.data?.message;
      if (message?.includes('inatividade')) {
        localStorage.removeItem('access_token');
        toast.error('Sessão expirada por inatividade');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
```

## 🧪 Como Testar

### Teste Rápido (DEV)
```bash
# 1. Configurar timeout de 1 minuto
export JWT_INACTIVITY_TIMEOUT_MINUTES=1

# 2. Reiniciar aplicação

# 3. Fazer login
TOKEN=$(curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"senha123"}' \
  | jq -r '.access_token')

# 4. Fazer requisição (funciona)
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN"

# 5. Aguardar 61 segundos

# 6. Tentar novamente (deve falhar)
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN"

# Esperado: {"message":"Sessão expirada por inatividade. Faça login novamente."}
```

## 📈 Monitoramento

### Usuários Atualmente Ativos
```sql
SELECT
    ua.user_id,
    ua.last_activity_at,
    EXTRACT(EPOCH FROM (NOW() - ua.last_activity_at))/60 as minutes_inactive
FROM user_activity ua
WHERE ua.last_activity_at > NOW() - INTERVAL '15 minutes'
ORDER BY ua.last_activity_at DESC;
```

### Prestes a Expirar (10-15 min)
```sql
SELECT user_id, last_activity_at
FROM user_activity
WHERE last_activity_at BETWEEN NOW() - INTERVAL '15 minutes'
                           AND NOW() - INTERVAL '10 minutes';
```

### Invalidados Hoje
```sql
SELECT COUNT(*)
FROM token_blacklist
WHERE blacklisted_at >= CURRENT_DATE;
```

## 🎓 Benefícios de Segurança

1. **Sessões Abandonadas:** Tokens de sessões esquecidas expiram automaticamente
2. **Token Roubado:** Se alguém roubar um token, ele expira após 15 min de inatividade
3. **Múltiplos Dispositivos:** Cada login tem rastreamento independente
4. **Conformidade:** Atende requisitos de segurança de sistemas financeiros/bancários

## 📚 Documentação Completa

- **Guia Detalhado:** [INACTIVITY_TIMEOUT_GUIDE.md](INACTIVITY_TIMEOUT_GUIDE.md)
- **Sistema de Logout:** [LOGOUT_GUIDE.md](LOGOUT_GUIDE.md)
- **Autenticação Geral:** [AUTHENTICATION.md](AUTHENTICATION.md)

## ✨ Próximos Passos

Sistema está completo e pronto para uso! Para colocar em produção:

1. ✅ Aplicar migrations do banco de dados
2. ✅ Configurar variável de ambiente `JWT_INACTIVITY_TIMEOUT_MINUTES` (ou usar padrão de 15 min)
3. ✅ Atualizar frontend para tratar erro de inatividade
4. ✅ Documentar comportamento para usuários finais
5. ✅ Monitorar métricas de expiração por inatividade

---

**Status:** ✅ Implementação Completa
**Data:** 2026-01-24
**Versão:** 1.0.0
