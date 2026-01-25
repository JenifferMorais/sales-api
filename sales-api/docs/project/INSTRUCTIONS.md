# 📘 Instruções de Execução - Vendas API

## 🎯 Requisitos Atendidos

✅ **Arquitetura Hexagonal** (Ports and Adapters)
✅ **TDD** (Test-Driven Development)
✅ **100% Cobertura de Testes** (unitários e integração)
✅ **Princípios SOLID**
✅ **Quarkus** com Java 21
✅ **PostgreSQL** como banco de dados
✅ **OpenAPI/Swagger** para documentação

## 🚀 Como Executar

### Passo 1: Iniciar o Banco de Dados

```bash
cd sales-api
docker-compose up -d
```

Aguarde alguns segundos para o PostgreSQL inicializar.

### Passo 2: Executar a Aplicação

**Modo Desenvolvimento (com hot reload):**
```bash
./mvnw quarkus:dev
```

Ou no Windows:
```cmd
mvnw.cmd quarkus:dev
```

A aplicação estará disponível em **http://localhost:8080**

### Passo 3: Acessar a Documentação

Abra o navegador em: **http://localhost:8080/swagger-ui**

## 🧪 Executar Testes

### Todos os testes:
```bash
./mvnw test
```

### Com relatório de cobertura:
```bash
./mvnw verify
```

O relatório será gerado em: `target/site/jacoco/index.html`

## 📊 Estrutura do Projeto

```
sales-api/
├── src/main/java/com/sales/
│   ├── domain/              # Regras de negócio (núcleo)
│   │   ├── customer/
│   │   ├── product/
│   │   └── sale/
│   ├── application/         # Casos de uso
│   └── infrastructure/      # Adaptadores (REST, Persistence)
└── src/test/java/           # Testes (100% cobertura)
```

## 🔍 Principais Funcionalidades

### 1. Gestão de Clientes
- CRUD completo
- Validação de CPF
- Endereço completo com CEP
- Validação de e-mail único

### 2. Gestão de Produtos
- CRUD completo
- Controle de estoque
- Tipos de produto (acabamento, mobiliário, etc.)
- Cálculo automático de margem de lucro

### 3. Gestão de Vendas
- Criação de sales com múltiplos itens
- Produtos ordenados alfabeticamente
- Validação de estoque
- Formas de pagamento (dinheiro, cartão)
- Cálculo de troco
- Mascaramento de número do cartão

## 💡 Exemplos de Uso

### Criar Cliente
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
      "complement": "Apto 101",
      "neighborhood": "Bela Vista",
      "city": "São Paulo",
      "state": "SP"
    },
    "birthDate": "1990-05-15",
    "cellPhone": "(11) 98765-4321",
    "email": "joao.silva@email.com"
  }'
```

### Criar Produto
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "code": "PROD001",
    "name": "Mesa de Escritório",
    "type": "FURNITURE",
    "details": "Mesa de madeira com 4 gavetas",
    "weight": 25.5,
    "purchasePrice": 300.00,
    "salePrice": 500.00,
    "height": 75.0,
    "width": 140.0,
    "depth": 70.0,
    "destinationVehicle": "Caminhão"
  }'
```

### Listar Produtos (Ordenados Alfabeticamente)
```bash
curl http://localhost:8080/api/v1/products?sorted=true
```

### Criar Venda
```bash
curl -X POST http://localhost:8080/api/v1/sales \
  -H "Content-Type: application/json" \
  -d '{
    "code": "SALE001",
    "customerCode": "CUST001",
    "sellerCode": "SELLER001",
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "1234567890123456",
    "items": [
      {
        "productCode": "PROD001",
        "quantity": 2
      }
    ]
  }'
```

## 🧩 Arquitetura Hexagonal

### Domain (Núcleo)
- **Entities**: Customer, Product, Sale, SaleItem
- **Value Objects**: Address, Document, Dimensions, PaymentMethod, ProductType
- **Ports**: CustomerRepository, ProductRepository, SaleRepository (interfaces)

### Application (Casos de Uso)
- CreateCustomerUseCase, UpdateCustomerUseCase, FindCustomerUseCase, DeleteCustomerUseCase
- CreateProductUseCase, UpdateProductUseCase, FindProductUseCase, DeleteProductUseCase
- CreateSaleUseCase, FindSaleUseCase

### Infrastructure (Adaptadores)
- **Persistence**: Implementação dos repositórios com Panache
- **REST**: Controllers, DTOs, Mappers

## ✅ Princípios SOLID Aplicados

1. **SRP**: Cada classe tem uma única responsabilidade
2. **OCP**: Entidades abertas para extensão, fechadas para modificação
3. **LSP**: Entidades de domínio podem ser substituídas por suas implementações
4. **ISP**: Interfaces específicas para cada repositório
5. **DIP**: Use cases dependem de abstrações (ports), não de implementações

## 🎯 TDD - Cobertura de Testes

- ✅ Testes unitários de entidades
- ✅ Testes unitários de value objects
- ✅ Testes unitários de use cases (com mocks)
- ✅ Testes de integração dos controllers
- ✅ Testes de validação (cenários de sucesso e erro)

## 🔧 Troubleshooting

### Porta 8080 em uso
```bash
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

### Banco de dados não conecta
Verifique se o Docker está rodando:
```bash
docker ps
```

Reinicie o container:
```bash
docker-compose restart
```

### Limpar e recompilar
```bash
./mvnw clean install
```

## 📞 Suporte

Para dúvidas sobre a implementação, consulte:
- README.md (visão geral)
- Swagger UI (documentação da API)
- Código-fonte (comentado e seguindo padrões)
