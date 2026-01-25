# 📚 Documentação - API de Vendas

Bem-vindo à documentação completa da API de Vendas. Esta documentação está organizada por categorias para facilitar a navegação.

## 📑 Índice

### 🔐 Autenticação e Segurança
- [**AUTHENTICATION.md**](authentication/AUTHENTICATION.md) - Guia completo do sistema de autenticação
- [**AUTH_QUICKSTART.md**](authentication/AUTH_QUICKSTART.md) - Início rápido para autenticação
- [**AUTH_SUMMARY.md**](authentication/AUTH_SUMMARY.md) - Resumo do sistema de autenticação
- [**LOGOUT_GUIDE.md**](authentication/LOGOUT_GUIDE.md) - Guia detalhado de logout
- [**INACTIVITY_TIMEOUT_GUIDE.md**](authentication/INACTIVITY_TIMEOUT_GUIDE.md) - Timeout por inatividade (15 min)
- [**TIMEOUT_INATIVIDADE_RESUMO.md**](authentication/TIMEOUT_INATIVIDADE_RESUMO.md) - Resumo do timeout de inatividade
- [**SECURITY_GUIDE.md**](authentication/SECURITY_GUIDE.md) - Guia de segurança
- [**SECRETS_SETUP.md**](authentication/SECRETS_SETUP.md) - Configuração de secrets
- [**README_SECRETS.md**](authentication/README_SECRETS.md) - Gerenciamento de credenciais

### 📊 Relatórios Gerenciais
- [**RELATORIOS_GERENCIAIS.md**](reports/RELATORIOS_GERENCIAIS.md) - Documentação dos 4 relatórios implementados
  - Faturamento Mensal
  - Maior Faturamento
  - Produtos Encalhados
  - Novos Clientes

### 🚀 Deployment e CI/CD
- [**CI_CD_COMPLETO.md**](deployment/CI_CD_COMPLETO.md) - CI/CD completo (Build + Registry + Deploy)
- [**RENDER_DEPLOY.md**](deployment/RENDER_DEPLOY.md) - Deploy automático no Render
- [**DOCKER_CI_CD_RESUMO.md**](deployment/DOCKER_CI_CD_RESUMO.md) - Resumo executivo
- [**DOCKER_GUIDE.md**](deployment/DOCKER_GUIDE.md) - Guia completo de Docker
- [**CI_CD_SETUP.md**](deployment/CI_CD_SETUP.md) - Setup GitHub Actions
- [**deployment/README.md**](deployment/README.md) - Índice de deployment

### 🏗️ Arquitetura e Padrões
- [**ARCHITECTURE.md**](architecture/ARCHITECTURE.md) - Arquitetura hexagonal do projeto
- [**LOMBOK_GUIDE.md**](architecture/LOMBOK_GUIDE.md) - Guia de uso do Lombok
- [**LOMBOK_BENEFITS.md**](architecture/LOMBOK_BENEFITS.md) - Benefícios do Lombok
- [**LOMBOK_CHEATSHEET.md**](architecture/LOMBOK_CHEATSHEET.md) - Cheat sheet do Lombok
- [**LOMBOK_FINAL_SUMMARY.md**](architecture/LOMBOK_FINAL_SUMMARY.md) - Resumo final do Lombok
- [**SETTER_BEST_PRACTICES.md**](architecture/SETTER_BEST_PRACTICES.md) - Boas práticas de setters
- [**MESSAGES_PT_BR.md**](architecture/MESSAGES_PT_BR.md) - Mensagens em português

### 📋 Projeto e Validação
- [**PROJECT_SUMMARY.md**](project/PROJECT_SUMMARY.md) - Resumo do projeto
- [**VALIDATION_REPORT.md**](project/VALIDATION_REPORT.md) - Relatório de validação de requisitos
- [**SALES_SYSTEM_UPDATE.md**](project/SALES_SYSTEM_UPDATE.md) - Atualização do sistema de sales

## 🚀 Início Rápido

### 1. Configurar Ambiente

Consulte o [README principal](../README.md) para:
- Requisitos do sistema
- Instalação de dependências
- Configuração do banco de dados

### 2. Segurança e Autenticação

**Leitura obrigatória:**
1. [SECRETS_SETUP.md](authentication/SECRETS_SETUP.md) - Configure secrets primeiro!
2. [AUTH_QUICKSTART.md](authentication/AUTH_QUICKSTART.md) - Entenda o fluxo de autenticação
3. [SECURITY_GUIDE.md](authentication/SECURITY_GUIDE.md) - Melhores práticas de segurança

### 3. Funcionalidades Principais

- **Autenticação JWT:** Sistema completo com login, registro, logout e reset de senha
- **Timeout de Inatividade:** Tokens expiram após 15 minutos sem uso
- **Relatórios Gerenciais:** 4 relatórios prontos para uso
- **CRUD Completo:** Clientes, Produtos e Vendas

## 📖 Guias por Papel

### Para Desenvolvedores Backend

1. [ARCHITECTURE.md](architecture/ARCHITECTURE.md) - Entenda a arquitetura hexagonal
2. [LOMBOK_GUIDE.md](architecture/LOMBOK_GUIDE.md) - Use Lombok corretamente
3. [AUTHENTICATION.md](authentication/AUTHENTICATION.md) - Implemente autenticação
4. [RELATORIOS_GERENCIAIS.md](reports/RELATORIOS_GERENCIAIS.md) - Crie novos relatórios

### Para Desenvolvedores Frontend

1. [AUTH_QUICKSTART.md](authentication/AUTH_QUICKSTART.md) - Integre com autenticação
2. [LOGOUT_GUIDE.md](authentication/LOGOUT_GUIDE.md) - Implemente logout
3. [INACTIVITY_TIMEOUT_GUIDE.md](authentication/INACTIVITY_TIMEOUT_GUIDE.md) - Trate timeout de inatividade
4. [RELATORIOS_GERENCIAIS.md](reports/RELATORIOS_GERENCIAIS.md) - Consuma os endpoints

### Para DevOps/SRE

1. [CI_CD_SETUP.md](deployment/CI_CD_SETUP.md) - Configure pipeline CI/CD
2. [DOCKER_GUIDE.md](deployment/DOCKER_GUIDE.md) - Deploy com Docker
3. [SECRETS_SETUP.md](authentication/SECRETS_SETUP.md) - Configure secrets em produção
4. [SECURITY_GUIDE.md](authentication/SECURITY_GUIDE.md) - Checklist de segurança

### Para Gestores de Produto

1. [PROJECT_SUMMARY.md](project/PROJECT_SUMMARY.md) - Visão geral do sistema
2. [VALIDATION_REPORT.md](project/VALIDATION_REPORT.md) - Conformidade com requisitos
3. [RELATORIOS_GERENCIAIS.md](reports/RELATORIOS_GERENCIAIS.md) - Relatórios disponíveis

## 🔍 Busca Rápida

### Preciso configurar...
- **CI/CD:** [CI_CD_SETUP.md](deployment/CI_CD_SETUP.md)
- **Docker:** [DOCKER_GUIDE.md](deployment/DOCKER_GUIDE.md)
- **Banco de dados:** [README principal](../README.md)
- **JWT/Secrets:** [SECRETS_SETUP.md](authentication/SECRETS_SETUP.md)
- **Email:** [SECURITY_GUIDE.md](authentication/SECURITY_GUIDE.md)
- **Timeout de inatividade:** [TIMEOUT_INATIVIDADE_RESUMO.md](authentication/TIMEOUT_INATIVIDADE_RESUMO.md)

### Como faço para...
- **Deploy da aplicação:** [DOCKER_CI_CD_RESUMO.md](deployment/DOCKER_CI_CD_RESUMO.md)
- **Build Docker local:** [DOCKER_GUIDE.md](deployment/DOCKER_GUIDE.md#build-local)
- **Ativar CI/CD:** [CI_CD_SETUP.md](deployment/CI_CD_SETUP.md)
- **Fazer login:** [AUTH_QUICKSTART.md](authentication/AUTH_QUICKSTART.md)
- **Implementar logout:** [LOGOUT_GUIDE.md](authentication/LOGOUT_GUIDE.md)
- **Criar relatório:** [RELATORIOS_GERENCIAIS.md](reports/RELATORIOS_GERENCIAIS.md)
- **Usar Lombok:** [LOMBOK_GUIDE.md](architecture/LOMBOK_GUIDE.md)

### Troubleshooting
- **Sessão expira rápido:** [INACTIVITY_TIMEOUT_GUIDE.md](authentication/INACTIVITY_TIMEOUT_GUIDE.md#troubleshooting)
- **Token inválido:** [LOGOUT_GUIDE.md](authentication/LOGOUT_GUIDE.md#troubleshooting)
- **Erro de secrets:** [SECRETS_SETUP.md](authentication/SECRETS_SETUP.md)

## 📊 Diagramas e Fluxos

### Fluxos de Autenticação
- Login: [AUTHENTICATION.md](authentication/AUTHENTICATION.md#fluxo-2-login)
- Logout: [LOGOUT_GUIDE.md](authentication/LOGOUT_GUIDE.md#fluxo-4-logout)
- Reset de Senha: [AUTHENTICATION.md](authentication/AUTHENTICATION.md#fluxo-3-reset-de-senha)
- Timeout: [INACTIVITY_TIMEOUT_GUIDE.md](authentication/INACTIVITY_TIMEOUT_GUIDE.md#fluxo-completo)

### Arquitetura
- Hexagonal: [ARCHITECTURE.md](architecture/ARCHITECTURE.md)
- Camadas: [PROJECT_SUMMARY.md](project/PROJECT_SUMMARY.md)

## 🛡️ Segurança

### Funcionalidades Implementadas
- ✅ JWT com RSA 2048
- ✅ BCrypt para senhas (custo 12)
- ✅ Blacklist de tokens
- ✅ Timeout de inatividade (15 min)
- ✅ Secrets via variáveis de ambiente
- ✅ CORS configurado
- ✅ Validação de entrada
- ✅ Rate limiting (planejado)

### Conformidade
- ✅ OWASP Top 10
- ✅ LGPD/GDPR considerações
- ✅ Best practices de JWT

## 📝 Convenções

### Mensagens
- Todas as mensagens de erro/validação em **Português (pt-BR)**
- Ver: [MESSAGES_PT_BR.md](architecture/MESSAGES_PT_BR.md)

### Código
- Código-fonte em **Inglês**
- Comentários em **Inglês**
- Documentação em **Português**

### Lombok
- Ver: [LOMBOK_GUIDE.md](architecture/LOMBOK_GUIDE.md)
- `@Data` para DTOs
- `@Setter(AccessLevel.PACKAGE)` para JPA entities
- Somente `@Getter` para domain entities

## 🔗 Links Externos

- [Documentação Quarkus](https://quarkus.io/guides/)
- [SmallRye JWT](https://smallrye.io/docs/smallrye-jwt/index.html)
- [Lombok](https://projectlombok.org/)
- [PostgreSQL](https://www.postgresql.org/docs/)

## 📅 Histórico de Atualizações

- **2026-01-24:** Implementação de timeout por inatividade
- **2026-01-24:** Sistema de logout com blacklist
- **2026-01-24:** Relatórios gerenciais completos
- **2026-01-24:** Sistema de autenticação completo
- **2026-01-24:** Autogeração de códigos para clientes e produtos

## 📧 Suporte

Para dúvidas ou problemas:
1. Consulte a documentação relevante neste índice
2. Verifique a seção de troubleshooting do documento específico
3. Consulte os logs da aplicação
4. Entre em contato com a equipe de desenvolvimento

---

**Última atualização:** 2026-01-24
**Versão da API:** 1.0.0
**Versão do Quarkus:** 3.17.5
