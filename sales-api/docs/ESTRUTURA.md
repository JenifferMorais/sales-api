# 📂 Estrutura de Documentação

Este documento mostra a organização completa da documentação do projeto.

## 📊 Estrutura Visual

```
docs/
├── README.md                          # Índice principal da documentação
├── ESTRUTURA.md                       # Este arquivo
│
├── authentication/                    # 🔐 Autenticação e Segurança (9 arquivos)
│   ├── AUTHENTICATION.md              # Guia completo de autenticação
│   ├── AUTH_QUICKSTART.md             # Início rápido
│   ├── AUTH_SUMMARY.md                # Resumo do sistema
│   ├── LOGOUT_GUIDE.md                # Guia de logout
│   ├── INACTIVITY_TIMEOUT_GUIDE.md    # Guia de timeout (15 min)
│   ├── TIMEOUT_INATIVIDADE_RESUMO.md  # Resumo do timeout
│   ├── SECURITY_GUIDE.md              # Guia de segurança
│   ├── SECRETS_SETUP.md               # Configuração de secrets
│   └── README_SECRETS.md              # Gerenciamento de credenciais
│
├── reports/                           # 📊 Relatórios Gerenciais (1 arquivo)
│   └── RELATORIOS_GERENCIAIS.md       # Documentação dos 4 relatórios
│
├── deployment/                        # 🚀 Deployment e CI/CD (4 arquivos)
│   ├── README.md                      # Índice de deployment
│   ├── DOCKER_CI_CD_RESUMO.md         # Resumo executivo
│   ├── DOCKER_GUIDE.md                # Guia completo de Docker
│   └── CI_CD_SETUP.md                 # Setup do pipeline
│
├── architecture/                      # 🏗️ Arquitetura e Padrões (7 arquivos)
│   ├── ARCHITECTURE.md                # Arquitetura hexagonal
│   ├── LOMBOK_GUIDE.md                # Guia completo do Lombok
│   ├── LOMBOK_BENEFITS.md             # Benefícios do Lombok
│   ├── LOMBOK_CHEATSHEET.md           # Cheat sheet
│   ├── LOMBOK_FINAL_SUMMARY.md        # Resumo final
│   ├── SETTER_BEST_PRACTICES.md       # Boas práticas de setters
│   └── MESSAGES_PT_BR.md              # Mensagens em português
│
└── project/                           # 📋 Projeto e Validação (6 arquivos)
    ├── PROJECT_SUMMARY.md             # Resumo do projeto
    ├── VALIDATION_REPORT.md           # Relatório de validação
    ├── SALES_SYSTEM_UPDATE.md         # Atualização do sistema de sales
    ├── INDEX.md                       # Índice de funcionalidades
    ├── INSTRUCTIONS.md                # Instruções gerais
    └── QUICKSTART.md                  # Guia de início rápido
```

## 📈 Estatísticas

- **Total de arquivos:** 28 documentos
- **Categorias:** 5 principais (Autenticação, Relatórios, Arquitetura, Projeto, Deployment)
- **Linhas de documentação:** ~7500+ linhas
- **Idioma:** Português (pt-BR)

## 🎯 Por Categoria

### 🔐 Autenticação e Segurança (9 docs)
Cobre todo o sistema de autenticação JWT, incluindo:
- Login, registro, logout
- Reset de senha
- Timeout de inatividade (15 min)
- Blacklist de tokens
- Configuração de secrets
- Segurança OWASP

### 📊 Relatórios Gerenciais (1 doc)
Documentação completa dos 4 relatórios:
- Faturamento Mensal
- Maior Faturamento (top 4 produtos)
- Produtos Encalhados (3 mais antigos)
- Novos Clientes (por ano)

### 🏗️ Arquitetura e Padrões (7 docs)
Documentação técnica sobre:
- Arquitetura hexagonal
- Uso correto do Lombok
- Boas práticas de código
- Convenções do projeto
- Mensagens em português

### 📋 Projeto (6 docs)
Informações gerais do projeto:
- Resumos e validações
- Instruções de setup
- Guias rápidos
- Histórico de atualizações

### 🚀 Deployment e CI/CD (4 docs)
Deploy e integração contínua:
- Pipeline automático com GitHub Actions
- Build e deploy com Docker
- Dockerfiles otimizados (JVM, Native, Legacy)
- Docker Compose para produção

## 🔍 Arquivos Mais Importantes

### Para Começar
1. [README principal](../README.md)
2. [Índice da documentação](README.md)
3. [Início rápido](project/QUICKSTART.md)

### Para Desenvolvedores
1. [Arquitetura](architecture/ARCHITECTURE.md)
2. [Autenticação](authentication/AUTHENTICATION.md)
3. [Lombok](architecture/LOMBOK_GUIDE.md)

### Para DevOps
1. [Secrets](authentication/SECRETS_SETUP.md)
2. [Segurança](authentication/SECURITY_GUIDE.md)

### Para Produto
1. [Resumo do projeto](project/PROJECT_SUMMARY.md)
2. [Relatórios](reports/RELATORIOS_GERENCIAIS.md)
3. [Validação](project/VALIDATION_REPORT.md)

## 📝 Convenções de Nomenclatura

- **ALL_CAPS.md** - Documentos principais e guias
- **snake_case** - Pastas/diretórios
- **PascalCase** - Raramente usado, apenas em casos específicos

## 🔗 Links Úteis

- [Índice principal](README.md)
- [README do projeto](../README.md)
- [Swagger UI](http://localhost:8080/swagger-ui) (quando executando)

## 📅 Última Atualização

**Data:** 2026-01-24
**Versão:** 1.0.0
**Total de documentos:** 24

---

💡 **Dica:** Use o [README principal da documentação](README.md) para navegar por categoria ou papel (desenvolvedor, DevOps, etc.)
