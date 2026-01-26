# Sales Web - Frontend

Frontend Angular para o Sistema de Vendas com interface responsiva, componentes reutilizáveis e integração REST completa.

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Formas de Executar](#formas-de-executar)
- [Configuração](#configuração)
- [Funcionalidades](#funcionalidades)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Componentes](#componentes)
- [Services](#services)
- [Guias de Estilo](#guias-de-estilo)
- [Testes](#testes)
- [Build & Deploy](#build--deploy)
- [Troubleshooting](#troubleshooting)

## 🚀 Tecnologias

### Core
- **Angular 19** - Framework SPA com Signals
- **TypeScript 5.7** - JavaScript tipado
- **RxJS** - Programação reativa
- **Signals** - Gerenciamento de estado moderno

### UI Components
- **PrimeNG 17.18.12** - Biblioteca de componentes UI
- **PrimeIcons** - Ícones
- **PrimeFlex** - Utilitários CSS
- **Angular Material 19** - Componentes Material Design

### Ferramentas
- **Angular CLI** - Scaffolding e build
- **Vite** - Build tool rápido
- **ESLint** - Linting
- **Prettier** - Formatação de código

### HTTP & Auth
- **HttpClient** - Cliente HTTP
- **JWT Interceptor** - Autenticação automática
- **Route Guards** - Proteção de rotas

## 🏗️ Arquitetura

### Padrões Arquiteturais

```
src/app/
│
├── core/                           # Singleton services, guards, interceptors
│   ├── guards/                    # Route guards
│   │   ├── auth.guard.ts
│   │   └── no-auth.guard.ts
│   │
│   ├── interceptors/              # HTTP interceptors
│   │   ├── auth.interceptor.ts   # Adiciona JWT token
│   │   ├── error.interceptor.ts  # Tratamento de erros
│   │   └── loading.interceptor.ts
│   │
│   ├── services/                  # Serviços singleton
│   │   ├── auth.service.ts
│   │   ├── customer.service.ts
│   │   ├── product.service.ts
│   │   ├── sale.service.ts
│   │   └── inactivity.service.ts
│   │
│   └── models/                    # Interfaces e tipos
│       ├── customer.model.ts
│       ├── product.model.ts
│       ├── sale.model.ts
│       └── auth.model.ts
│
├── shared/                        # Componentes compartilhados
│   ├── components/
│   │   ├── loading/
│   │   ├── error-message/
│   │   └── confirmation-dialog/
│   │
│   ├── directives/                # Diretivas customizadas
│   │   ├── auto-focus.directive.ts
│   │   └── currency-mask.directive.ts
│   │
│   └── pipes/                     # Pipes customizados
│       ├── currency-format.pipe.ts
│       └── date-format.pipe.ts
│
├── layout/                        # Componentes de layout
│   ├── header/
│   ├── sidebar/
│   ├── footer/
│   └── main-layout/
│
├── public/                        # Módulos públicos (sem auth)
│   ├── login/
│   ├── register/
│   ├── forgot-password/
│   └── reset-password/
│
├── private/                       # Módulos privados (com auth)
│   ├── dashboard/                # Dashboard principal
│   │   ├── dashboard.component.ts
│   │   ├── dashboard.component.html
│   │   └── dashboard.component.scss
│   │
│   ├── customers/                # Gestão de clientes
│   │   ├── customer-list/
│   │   ├── customer-form/
│   │   └── customer-detail/
│   │
│   ├── products/                 # Gestão de produtos
│   │   ├── product-list/
│   │   ├── product-form/
│   │   └── product-detail/
│   │
│   ├── sales/                    # Gestão de vendas
│   │   ├── sale-list/
│   │   ├── sale-form/
│   │   └── sale-detail/
│   │
│   └── reports/                  # Relatórios
│       ├── monthly-revenue/
│       ├── top-products/
│       ├── customer-purchases/
│       └── sales-period/
│
└── environments/                  # Configurações por ambiente
    ├── environment.ts            # Desenvolvimento
    └── environment.prod.ts       # Produção
```

### Princípios Aplicados

- **Lazy Loading**: Módulos carregados sob demanda
- **Smart & Dumb Components**: Separação de lógica e apresentação
- **Reactive Programming**: RxJS para operações assíncronas
- **Signals**: Estado reativo e performático
- **Dependency Injection**: Serviços injetáveis
- **Type Safety**: TypeScript strict mode

## 🎯 Formas de Executar

### Opção 1: Desenvolvimento Local (Recomendado)

Servidor de desenvolvimento com hot reload.

**Pré-requisitos:**
- Node.js 18+
- npm 9+

**Passos:**

```bash
# 1. Instalar dependências
npm install

# 2. Iniciar servidor dev
npm start

# Ou especificar porta
npm start -- --port 4300
```

**Acesso:**
- App: http://localhost:4200
- Auto-reload: Mudanças detectadas automaticamente

**Ferramentas de dev:**
- Angular DevTools (extensão Chrome/Firefox)
- Redux DevTools (se usar NgRx)

---

### Opção 2: Build de Desenvolvimento

Build rápido sem otimizações.

```bash
# Build
npm run build

# Servir arquivos buildados
npx http-server dist/sales-web/browser -p 4200
```

---

### Opção 3: Build de Produção

Build otimizado com minificação e tree-shaking.

```bash
# Build produção
npm run build -- --configuration production

# Ou
npm run build:prod

# Servir localmente
npx http-server dist/sales-web/browser -p 80
```

**Otimizações aplicadas:**
- ✅ Minificação JS/CSS
- ✅ Tree-shaking
- ✅ Ahead-of-Time (AOT) compilation
- ✅ Bundle optimization
- ✅ Source maps (opcional)

---

### Opção 4: Docker Development

```bash
# Build imagem
docker build -f docker/dev/Dockerfile -t sales-web:dev .

# Executar
docker run -p 4200:4200 sales-web:dev
```

---

### Opção 5: Docker Production

```bash
# Build imagem otimizada
docker build -f docker/prod/Dockerfile -t sales-web:prod .

# Executar com Nginx
docker run -d \
  -p 80:80 \
  -e API_URL=https://api.example.com/api \
  sales-web:prod
```

**Acesso:** http://localhost

---

### Opção 6: Docker Compose (com Backend)

```bash
# Subir stack completa
cd ../sales-api/docker/dev
docker-compose up -d

# Frontend em outro terminal
cd ../../../sales-web
npm start
```

---

## ⚙️ Configuração

### Variáveis de Ambiente

**environment.ts (Desenvolvimento):**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  inactivityTimeoutMinutes: 15,
  defaultTheme: 'light' as Theme,
  enableDebugMode: true,
  logLevel: 'debug'
};
```

**environment.prod.ts (Produção):**
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com/api',
  inactivityTimeoutMinutes: 15,
  defaultTheme: 'light' as Theme,
  enableDebugMode: false,
  logLevel: 'error'
};
```

### Configuração do Angular

**angular.json:**
```json
{
  "projects": {
    "sales-web": {
      "architect": {
        "build": {
          "configurations": {
            "production": {
              "optimization": true,
              "sourceMap": false,
              "namedChunks": false,
              "aot": true,
              "buildOptimizer": true
            }
          }
        }
      }
    }
  }
}
```

### Proxy para API (Desenvolvimento)

Evita problemas de CORS em desenvolvimento.

**proxy.conf.json:**
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

**Usar proxy:**
```bash
npm start -- --proxy-config proxy.conf.json
```

---

## 🎨 Funcionalidades

### Autenticação & Segurança
- ✅ Login com email/senha
- ✅ Registro de novos usuários
- ✅ Recuperação de senha por email
- ✅ JWT token automático (interceptor)
- ✅ Timeout de inatividade (15 min configurável)
- ✅ Auto-logout ao expirar token
- ✅ Route guards para proteção de rotas

### Dashboard
- ✅ Métricas em tempo real
- ✅ Gráficos interativos
- ✅ Total de vendas do mês
- ✅ Produtos mais vendidos
- ✅ Últimas vendas
- ✅ Atalhos rápidos

### Gestão de Clientes
- ✅ Listagem paginada
- ✅ Busca e filtros
- ✅ Criar/Editar/Excluir
- ✅ Validação de formulários
- ✅ Histórico de compras

### Gestão de Produtos
- ✅ Listagem paginada
- ✅ Controle de estoque
- ✅ Preços e descrições
- ✅ Upload de imagens (futuro)
- ✅ Alertas de estoque baixo

### Gestão de Vendas
- ✅ Nova venda com múltiplos itens
- ✅ Seleção de cliente e produtos
- ✅ Cálculo automático de totais
- ✅ Histórico de vendas
- ✅ Cancelamento de vendas

### Relatórios
- ✅ Receita mensal (gráfico + tabela)
- ✅ Top produtos vendidos
- ✅ Compras por cliente
- ✅ Vendas por período
- ✅ Export PDF/Excel (futuro)

### UX/UI
- ✅ Design responsivo (mobile-first)
- ✅ Temas claro/escuro
- ✅ Mensagens de feedback (toast)
- ✅ Loading states
- ✅ Confirmações de ações
- ✅ Tratamento de erros amigável

---

## 📁 Estrutura de Pastas Detalhada

```
src/
├── app/
│   ├── app.component.ts              # Root component
│   ├── app.routes.ts                 # Configuração de rotas
│   ├── app.config.ts                 # Providers e configurações
│   │
│   ├── core/                         # Módulo core (singleton)
│   │   ├── guards/
│   │   │   ├── auth.guard.ts        # Protege rotas privadas
│   │   │   └── no-auth.guard.ts     # Redireciona autenticados
│   │   │
│   │   ├── interceptors/
│   │   │   ├── auth.interceptor.ts   # Adiciona Bearer token
│   │   │   └── error.interceptor.ts  # Captura erros HTTP
│   │   │
│   │   ├── services/
│   │   │   ├── auth.service.ts       # Autenticação
│   │   │   ├── customer.service.ts   # CRUD clientes
│   │   │   ├── product.service.ts    # CRUD produtos
│   │   │   ├── sale.service.ts       # CRUD vendas
│   │   │   ├── report.service.ts     # Relatórios
│   │   │   ├── theme.service.ts      # Temas
│   │   │   └── inactivity.service.ts # Timeout
│   │   │
│   │   └── models/
│   │       ├── customer.model.ts
│   │       ├── product.model.ts
│   │       ├── sale.model.ts
│   │       ├── auth.model.ts
│   │       └── api-response.model.ts
│   │
│   ├── shared/
│   │   ├── components/
│   │   │   ├── loading-spinner/
│   │   │   ├── error-message/
│   │   │   ├── confirm-dialog/
│   │   │   └── data-table/
│   │   │
│   │   ├── directives/
│   │   │   └── auto-focus.directive.ts
│   │   │
│   │   └── pipes/
│   │       ├── currency-br.pipe.ts
│   │       └── date-br.pipe.ts
│   │
│   ├── layout/
│   │   ├── header/
│   │   │   ├── header.component.ts
│   │   │   ├── header.component.html
│   │   │   └── header.component.scss
│   │   │
│   │   ├── sidebar/
│   │   └── main-layout/
│   │
│   ├── public/
│   │   ├── login/
│   │   ├── register/
│   │   └── forgot-password/
│   │
│   └── private/
│       ├── dashboard/
│       ├── customers/
│       ├── products/
│       ├── sales/
│       └── reports/
│
├── assets/
│   ├── images/                       # Imagens estáticas
│   ├── icons/                        # Ícones SVG
│   └── i18n/                         # Traduções (futuro)
│
├── styles/
│   ├── _variables.scss               # Variáveis SCSS
│   ├── _mixins.scss                  # Mixins
│   ├── _themes.scss                  # Temas
│   └── styles.scss                   # Estilos globais
│
└── environments/
    ├── environment.ts
    └── environment.prod.ts
```

---

## 🧩 Componentes

### Smart Components (Container)

Gerenciam estado e lógica:

```typescript
// customer-list.component.ts
@Component({
  selector: 'app-customer-list',
  template: `
    <app-customer-table
      [customers]="customers()"
      [loading]="loading()"
      (edit)="onEdit($event)"
      (delete)="onDelete($event)">
    </app-customer-table>
  `
})
export class CustomerListComponent {
  customers = signal<Customer[]>([]);
  loading = signal(false);

  constructor(private customerService: CustomerService) {}

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading.set(true);
    this.customerService.getAll().subscribe({
      next: (data) => this.customers.set(data),
      complete: () => this.loading.set(false)
    });
  }
}
```

### Dumb Components (Presentational)

Apenas apresentação:

```typescript
// customer-table.component.ts
@Component({
  selector: 'app-customer-table',
  template: `
    <p-table [value]="customers" [loading]="loading">
      <!-- Template -->
    </p-table>
  `
})
export class CustomerTableComponent {
  @Input() customers: Customer[] = [];
  @Input() loading = false;
  @Output() edit = new EventEmitter<Customer>();
  @Output() delete = new EventEmitter<number>();
}
```

---

## 🔧 Services

### AuthService

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'auth_token';
  currentUser = signal<User | null>(null);

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/v1/auth/login', credentials)
      .pipe(
        tap(response => {
          localStorage.setItem(this.tokenKey, response.token);
          this.currentUser.set(response.user);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
```

### HTTP Service Base

```typescript
export abstract class BaseService<T> {
  constructor(
    protected http: HttpClient,
    protected endpoint: string
  ) {}

  getAll(): Observable<T[]> {
    return this.http.get<T[]>(this.endpoint);
  }

  getById(id: number): Observable<T> {
    return this.http.get<T>(`${this.endpoint}/${id}`);
  }

  create(item: Partial<T>): Observable<T> {
    return this.http.post<T>(this.endpoint, item);
  }

  update(id: number, item: Partial<T>): Observable<T> {
    return this.http.put<T>(`${this.endpoint}/${id}`, item);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }
}
```

---

## 🎨 Guias de Estilo

### Nomenclatura

```typescript
// Components
customer-list.component.ts      // Kebab-case
CustomerListComponent          // PascalCase

// Services
customer.service.ts            // Kebab-case
CustomerService                // PascalCase

// Models
customer.model.ts              // Kebab-case
export interface Customer {}   // PascalCase

// Variáveis
const customerName = '';       // camelCase
const MAX_ITEMS = 100;         // UPPER_SNAKE_CASE
```

### Code Style

```typescript
// ✅ BOM
export class CustomerService {
  private readonly apiUrl = `${environment.apiUrl}/customers`;

  constructor(private http: HttpClient) {}

  getCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiUrl)
      .pipe(
        catchError(this.handleError)
      );
  }
}

// ❌ RUIM
export class CustomerService {
  apiUrl = environment.apiUrl + '/customers'; // não readonly

  constructor(private http: HttpClient) {}

  getCustomers() { // sem tipo de retorno
    return this.http.get(this.apiUrl); // sem tipagem genérica
  }
}
```

---

## 🧪 Testes

### Testes Unitários

```bash
# Executar todos os testes
npm test

# Testes em watch mode
npm run test:watch

# Com cobertura
npm run test:coverage

# Ver relatório
open coverage/index.html
```

### Estrutura de Testes

```typescript
// customer.service.spec.ts
describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomerService]
    });

    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch customers', () => {
    const mockCustomers: Customer[] = [
      { id: 1, name: 'Test' }
    ];

    service.getAll().subscribe(customers => {
      expect(customers).toEqual(mockCustomers);
    });

    const req = httpMock.expectOne('/api/v1/customers');
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomers);
  });
});
```

### E2E Tests (Cypress - futuro)

```bash
npm run e2e
```

---

## 📦 Build & Deploy

### Build Produção

```bash
# Build otimizado
npm run build:prod

# Analisar bundle size
npm run analyze
```

### Deploy Render

```bash
# Via GitHub Actions (automático)
git push origin main

# Deploy manual
render deploy
```

### Deploy Vercel

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel --prod
```

### Deploy Netlify

```bash
# Install Netlify CLI
npm i -g netlify-cli

# Deploy
netlify deploy --prod --dir=dist/sales-web/browser
```

---

## 🔍 Troubleshooting

### Problema: Erro de CORS

```typescript
// Usar proxy em desenvolvimento
// proxy.conf.json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}

// npm start -- --proxy-config proxy.conf.json
```

---

### Problema: Token expirado

O AuthInterceptor captura 401 e faz logout automático.

```typescript
// Verificar em auth.interceptor.ts
if (error.status === 401) {
  this.authService.logout();
}
```

---

### Problema: Porta 4200 em uso

```bash
# Usar outra porta
npm start -- --port 4300

# Ou matar processo
lsof -i :4200                    # Linux/Mac
netstat -ano | findstr :4200     # Windows
```

---

### Problema: Build falha

```bash
# Limpar cache
npm cache clean --force
rm -rf node_modules package-lock.json
npm install

# Rebuild
npm run build
```

---

## 📚 Scripts Disponíveis

```json
{
  "scripts": {
    "start": "ng serve",
    "build": "ng build",
    "build:prod": "ng build --configuration production",
    "test": "ng test",
    "test:coverage": "ng test --code-coverage",
    "lint": "ng lint",
    "e2e": "ng e2e",
    "analyze": "ng build --stats-json && webpack-bundle-analyzer dist/stats.json"
  }
}
```

---

## 📖 Documentação Adicional

- [Angular Docs](https://angular.dev)
- [PrimeNG Components](https://primeng.org)
- [RxJS Documentation](https://rxjs.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

---

## 📝 Licença

MIT

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie sua feature branch: `git checkout -b feature/MinhaFeature`
3. Commit suas mudanças: `git commit -m 'Add MinhaFeature'`
4. Push para a branch: `git push origin feature/MinhaFeature`
5. Abra um Pull Request

### Padrão de Commits

```bash
feat: Nova funcionalidade
fix: Correção de bug
docs: Documentação
style: Formatação
refactor: Refatoração
test: Testes
chore: Manutenção
```
