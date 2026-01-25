# Sales Web - Frontend

Frontend Angular para o Sistema de Vendas com interface responsiva e integração REST.

## Tecnologias

- Angular 19
- TypeScript 5.7
- PrimeNG 17.18.12
- Angular Material 19
- RxJS
- Signals

## Funcionalidades

- Autenticação JWT (login, registro, recuperação de senha)
- CRUD de Clientes
- CRUD de Produtos
- CRUD de Vendas
- Dashboard com métricas
- Relatórios gerenciais (4 tipos)
- Timeout automático de inatividade (15 min)

## Início Rápido

```bash
# Instalar dependências
npm install

# Rodar em desenvolvimento
npm start

# Build de produção
npm run build
```

**URL:** http://localhost:4200

## Estrutura

```
src/app/
├── core/           # Services, guards, interceptors, models
├── private/        # Módulos privados (customers, products, sales, reports)
├── public/         # Módulos públicos (login, registro)
├── shared/         # Componentes compartilhados
└── layout/         # Header, sidebar, footer
```

## Configuração

**environment.ts:**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  inactivityTimeoutMinutes: 15,
  defaultTheme: 'light' as Theme
};
```

## Credenciais de Desenvolvimento

Para desenvolvimento local, use as credenciais de teste criadas automaticamente pelo backend:

```
Email: john.silva@email.com
Senha: Test@123
```

**Outros usuários de teste:**
- maria.oliveira@email.com
- carlos.mendes@email.com
- pedro.lima@email.com
- juliana.alves@email.com

Todos usam a mesma senha: `Test@123`

**AVISO DE SEGURANÇA:**
- Essas credenciais são APENAS para desenvolvimento local
- O formulário de login não possui valores pré-preenchidos por segurança
- **NUNCA** use essas credenciais em produção

## Docker

```bash
# Build
docker build -t sales-web .

# Run
docker run -d -p 80:80 -e API_URL=http://localhost:8080/api sales-web
```

**URL:** http://localhost

## Scripts

```bash
npm start           # Desenvolvimento (porta 4200)
npm run build       # Build de produção
npm test            # Testes unitários
npm run lint        # Linting
```

## Deploy

Ver documentação em:
- [Docker](docker/README.md)
- [CI/CD](docs/CI_CD_COMPLETO.md)

## Licença

MIT
