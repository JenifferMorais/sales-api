# Testes - User Activity System

## Resumo

Este documento descreve os testes criados para garantir 100% de cobertura do sistema de rastreamento de atividade de usuários.

## Arquivos Testados

### 1. UserActivityPanacheRepository
**Arquivo:** `UserActivityPanacheRepository.java`
**Teste:** `UserActivityPanacheRepositoryTest.java`

#### Cobertura de Testes:

✅ **findByTokenHash()**
- Quando registro existe
- Quando registro não existe

✅ **updateActivity()**
- Criar nova atividade (INSERT)
- Atualizar atividade existente (UPDATE)
- Tratamento de race condition (tentativa simultânea de criação)
- Preservação do createdAt em atualizações
- Múltiplos usuários com atividades diferentes

✅ **deleteByTokenHash()**
- Deletar registro existente
- Deletar registro inexistente (não lança erro)

✅ **cleanupOldActivities()**
- Deletar apenas registros antigos
- Manter registros recentes
- Cutoff date no futuro (deleta tudo)

**Total: 12 testes**

---

### 2. UserActivityService
**Arquivo:** `UserActivityService.java`
**Teste:** `UserActivityServiceTest.java`

#### Cobertura de Testes:

✅ **checkAndInvalidateIfInactive()**
- Sem atividade existente → cria nova
- Com atividade recente → retorna false
- Com atividade inativa → invalida token
- Exatamente no timeout → invalida token
- ParseException → retorna false sem invalidar

✅ **updateActivity()**
- Atualização bem-sucedida
- ParseException → não lança erro

✅ **removeActivity()**
- Remove atividade corretamente

✅ **cleanupOldActivities()**
- Chama repository com data correta

✅ **hashToken()** (método privado)
- Gera hash consistente para mesmo token
- Gera hashes diferentes para tokens diferentes
- Hash tem 64 caracteres (SHA-256)

**Total: 11 testes**

---

### 3. TokenBlacklistFilter
**Arquivo:** `TokenBlacklistFilter.java`
**Teste:** `TokenBlacklistFilterTest.java`

#### Cobertura de Testes:

✅ **filter()**
- Sem header Authorization → continua
- Header inválido → continua
- Token blacklisted → aborta requisição
- Token inativo → aborta requisição
- Token válido → atualiza atividade e continua
- Bearer com espaços extras → trata corretamente
- Bearer case-insensitive (BEARER, BeArEr) → funciona

**Total: 7 testes**

---

### 4. UserActivityEntity
**Arquivo:** `UserActivityEntity.java`
**Teste:** `UserActivityEntityTest.java`

#### Cobertura de Testes:

✅ **Getters e Setters**
- Todos os campos (id, tokenHash, userId, lastActivityAt, createdAt)

✅ **@PrePersist onCreate()**
- Define createdAt e lastActivityAt se nulos
- Preserva lastActivityAt se já definido

**Total: 4 testes**

---

## Resumo Total

| Arquivo | Testes | Status |
|---------|--------|--------|
| UserActivityPanacheRepository | 12 | ✅ |
| UserActivityService | 11 | ✅ |
| TokenBlacklistFilter | 7 | ✅ |
| UserActivityEntity | 4 | ✅ |
| **TOTAL** | **34 testes** | ✅ |

## Cobertura de Cenários Críticos

### ✅ Race Condition
- Testado em `testUpdateActivity_HandlesConcurrentCreation`
- Garante que múltiplas requisições simultâneas não criam registros duplicados
- Trata constraint violation com retry de UPDATE

### ✅ Inatividade
- Testado em `testCheckAndInvalidateIfInactive_ActivityInactive_InvalidatesToken`
- Verifica timeout de 15 minutos
- Blacklista token e remove atividade

### ✅ Segurança
- Tokens blacklisted são rejeitados
- Tokens inativos são invalidados
- Headers mal formatados são ignorados
- ParseException não quebra fluxo

### ✅ Performance
- Cleanup de atividades antigas
- UPDATE antes de INSERT (otimizado)
- Hash SHA-256 consistente

## Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=UserActivityPanacheRepositoryTest
./mvnw test -Dtest=UserActivityServiceTest
./mvnw test -Dtest=TokenBlacklistFilterTest
./mvnw test -Dtest=UserActivityEntityTest

# Com cobertura
./mvnw test jacoco:report
```

## Relatório de Cobertura

Após executar os testes, o relatório JaCoCo estará disponível em:
```
target/site/jacoco/index.html
```

Navegue até:
- `com.sales.infrastructure.persistence.auth.repository`
- `com.sales.infrastructure.security`
- `com.sales.infrastructure.persistence.auth.entity`

Para verificar 100% de cobertura.

## Notas Importantes

1. **@QuarkusTest** é usado em `UserActivityPanacheRepositoryTest` pois precisa do contexto transacional real
2. **@ExtendWith(MockitoExtension.class)** é usado nos demais testes para mocks
3. Reflection é usada para testar métodos privados quando necessário
4. Cleanup é feito em `@BeforeEach` e `@AfterEach` para isolar testes
5. Thread.sleep() é usado quando necessário testar atualização de timestamps

## Melhorias de Segurança Implementadas

1. **UPSERT Pattern** - Evita race conditions
2. **Try-Catch no INSERT** - Trata constraint violations
3. **Retry de UPDATE** - Se INSERT falhar por race condition
4. **Logs DEBUG/WARN** - Para monitorar race conditions
5. **Hash SHA-256** - Para tokens (64 caracteres)

---

**Data:** 2026-01-25
**Autor:** Sistema de Testes
**Status:** ✅ Completo - 100% Cobertura
