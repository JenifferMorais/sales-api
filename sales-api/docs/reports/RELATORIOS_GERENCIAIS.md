# Relatórios Gerenciais - API de Vendas

Este documento descreve os 4 relatórios gerenciais implementados no sistema de sales.

## Visão Geral

Todos os relatórios estão disponíveis através do endpoint base `/api/reports` e seguem a arquitetura hexagonal do projeto.

## 1. Relatório de Faturamento Mensal

**Endpoint:** `POST /api/reports/monthly-revenue`

**Descrição:** Exibe o faturamento dos últimos 12 meses a partir de uma data de referência informada pelo usuário.

**Request:**
```json
{
  "referenceDate": "2026-01-24"
}
```

**Response:**
```json
{
  "monthlyData": [
    {
      "month": "janeiro",
      "year": 2025,
      "revenue": 15000.00,
      "tax": 1350.00,
      "total": 16350.00
    },
    {
      "month": "fevereiro",
      "year": 2025,
      "revenue": 18500.00,
      "tax": 1665.00,
      "total": 20165.00
    },
    // ... mais 10 meses
  ],
  "totalRevenue": 185000.00,
  "totalTax": 16650.00,
  "grandTotal": 201650.00
}
```

**Características:**
- Calcula faturamento (subtotal) de cada mês
- Aplica taxa de 9% de imposto sobre o faturamento
- Exibe total mensal (faturamento + imposto)
- Ao final, exibe totais consolidados dos 12 meses
- Ordena do mês mais antigo para o mais recente
- Meses sem sales aparecem com valores zerados

**Implementação:**
- Use Case: `GetMonthlyRevenueUseCase`
- Query: `SalePanacheRepository.getMonthlyRevenue()`
- Utiliza SQL nativo para agregação por mês/ano

---

## 2. Relatório de Maior Faturamento

**Endpoint:** `GET /api/reports/top-revenue-products`

**Descrição:** Exibe os 4 produtos que mais trouxeram faturamento para a loja desde o início dos registros.

**Request:** Não requer parâmetros

**Response:**
```json
{
  "products": [
    {
      "productCode": "PROD0015",
      "productName": "Amortecedor Dianteiro",
      "salePrice": 350.00,
      "totalRevenue": 28000.00
    },
    {
      "productCode": "PROD0008",
      "productName": "Kit Embreagem",
      "salePrice": 450.00,
      "totalRevenue": 22500.00
    },
    {
      "productCode": "PROD0023",
      "productName": "Pastilha de Freio",
      "salePrice": 120.00,
      "totalRevenue": 18000.00
    },
    {
      "productCode": "PROD0012",
      "productName": "Filtro de Óleo",
      "salePrice": 35.00,
      "totalRevenue": 14000.00
    }
  ]
}
```

**Características:**
- Lista os 4 produtos com maior faturamento total
- Exibe código, nome e preço de venda do produto
- Exibe o faturamento total gerado pelo produto
- Ordenado do maior para o menor faturamento
- Considera todas as sales desde o início dos registros

**Implementação:**
- Use Case: `GetTopRevenueProductsUseCase`
- Query: `SalePanacheRepository.getTopRevenueProducts(4)`
- Utiliza SQL nativo para agregação por produto

---

## 3. Relatório de Produtos Encalhados

**Endpoint:** `GET /api/reports/oldest-products`

**Descrição:** Exibe os 3 produtos mais antigos cadastrados na loja, ordenados do mais caro (preço de compra) para o mais barato.

**Request:** Não requer parâmetros

**Response:**
```json
{
  "products": [
    {
      "name": "Motor Completo 1.6",
      "weight": 85.500,
      "registrationDate": "2024-01-15T10:30:00",
      "purchasePrice": 4500.00
    },
    {
      "name": "Caixa de Câmbio Manual",
      "weight": 42.300,
      "registrationDate": "2024-01-15T14:20:00",
      "purchasePrice": 2800.00
    },
    {
      "name": "Radiador Alumínio",
      "weight": 12.750,
      "registrationDate": "2024-01-16T09:15:00",
      "purchasePrice": 850.00
    }
  ]
}
```

**Características:**
- Lista os 3 produtos mais antigos (por data de cadastro)
- Ordenado primeiro por data de cadastro (mais antigo primeiro)
- Depois ordenado por preço de compra (mais caro primeiro)
- Exibe: nome, peso, data de cadastro e preço de compra
- Útil para identificar produtos parados em estoque

**Implementação:**
- Use Case: `GetOldestProductsUseCase`
- Query: `ProductPanacheRepository.findOldestProducts(3)`
- Utiliza ordenação composta: `createdAt ASC, purchasePrice DESC`

---

## 4. Relatório de Novos Clientes

**Endpoint:** `POST /api/reports/new-customers`

**Descrição:** Exibe informações sobre clientes cadastrados em um determinado ano informado pelo usuário.

**Request:**
```json
{
  "year": 2025
}
```

**Response:**
```json
{
  "year": 2025,
  "customers": [
    {
      "code": "CUST0025",
      "fullName": "João Silva Santos",
      "birthDate": "1985-03-15"
    },
    {
      "code": "CUST0026",
      "fullName": "Maria Oliveira Costa",
      "birthDate": "1990-07-22"
    },
    {
      "code": "CUST0027",
      "fullName": "Pedro Henrique Souza",
      "birthDate": "1988-11-08"
    }
  ]
}
```

**Características:**
- Filtra clientes por ano de cadastro
- Exibe código, nome completo e data de nascimento
- Ordenado por código do cliente
- Valida que o ano esteja entre 2000 e 2100
- Útil para análise de crescimento da base de clientes

**Implementação:**
- Use Case: `GetNewCustomersUseCase`
- Query: `CustomerPanacheRepository.findByRegistrationYear(year)`
- Filtra por intervalo de datas do ano especificado

---

## Estrutura de Arquivos

### DTOs de Request
- `MonthlyRevenueRequest` - Data de referência para faturamento mensal
- `NewCustomersRequest` - Ano para filtrar novos clientes

### DTOs de Response
- `MonthlyRevenueResponse` + `MonthlyRevenueData` - Dados do faturamento mensal
- `TopRevenueProductsResponse` + `TopRevenueProductData` - Top produtos
- `OldestProductsResponse` + `OldestProductData` - Produtos encalhados
- `NewCustomersResponse` + `NewCustomerData` - Novos clientes

### Use Cases (Application Layer)
- `GetMonthlyRevenueUseCase`
- `GetTopRevenueProductsUseCase`
- `GetOldestProductsUseCase`
- `GetNewCustomersUseCase`

### Repositories (Infrastructure Layer)
Métodos adicionados:
- `SalePanacheRepository`:
  - `getMonthlyRevenue(start, end)` - SQL nativo para agregação mensal
  - `getTopRevenueProducts(limit)` - SQL nativo para top produtos
- `ProductPanacheRepository`:
  - `findOldestProducts(limit)` - Query com ordenação composta
- `CustomerPanacheRepository`:
  - `findByRegistrationYear(year)` - Query por intervalo de datas

### Controller
- `ReportController` - Centraliza todos os 4 endpoints de relatórios

---

## Regras de Negócio

### Taxa de Imposto
- **Valor fixo:** 9% sobre o subtotal de sales
- **Aplicação:** Automática em todos os cálculos de faturamento
- **Constante:** `TAX_RATE = 0.09`

### Cálculos de Faturamento
- **Subtotal:** Soma de (quantidade × preço unitário) de todos os itens
- **Imposto:** Subtotal × 9%
- **Total:** Subtotal + Imposto

### Ordenação
- **Faturamento Mensal:** Do mês mais antigo para o mais recente (cronológico)
- **Top Revenue:** Do maior faturamento para o menor (decrescente)
- **Produtos Encalhados:** Primeiro por data (mais antigo), depois por preço (mais caro)
- **Novos Clientes:** Por código do cliente (alfabético crescente)

---

## Exemplos de Uso

### Exemplo 1: Consultar faturamento dos últimos 12 meses
```bash
curl -X POST http://localhost:8080/api/reports/monthly-revenue \
  -H "Content-Type: application/json" \
  -d '{"referenceDate": "2026-01-24"}'
```

### Exemplo 2: Ver produtos que mais venderam
```bash
curl -X GET http://localhost:8080/api/reports/top-revenue-products
```

### Exemplo 3: Identificar produtos encalhados
```bash
curl -X GET http://localhost:8080/api/reports/oldest-products
```

### Exemplo 4: Listar clientes cadastrados em 2025
```bash
curl -X POST http://localhost:8080/api/reports/new-customers \
  -H "Content-Type: application/json" \
  -d '{"year": 2025}'
```

---

## Documentação OpenAPI

Todos os endpoints estão documentados via OpenAPI/Swagger e podem ser acessados em:
```
http://localhost:8080/q/swagger-ui
```

Procure pela tag **"Relatórios"** na interface Swagger para visualizar e testar todos os endpoints de relatórios gerenciais.

---

## Tratamento de Erros

Todos os endpoints retornam mensagens de erro padronizadas em caso de falha:

```json
{
  "error": "Erro ao gerar relatório: [mensagem de erro]"
}
```

**Status HTTP:**
- `200 OK` - Relatório gerado com sucesso
- `400 BAD REQUEST` - Dados de entrada inválidos
- `500 INTERNAL SERVER ERROR` - Erro ao processar o relatório

---

## Considerações Técnicas

### Performance
- Queries otimizadas com SQL nativo para agregações
- Uso de índices nas colunas `createdAt`, `code`
- Limitação de resultados (top N) para evitar sobrecarga

### Escalabilidade
- Use cases independentes e desacoplados
- Queries podem ser facilmente otimizadas ou movidas para views materializadas
- Possibilidade futura de cache de relatórios

### Manutenibilidade
- Seguem a arquitetura hexagonal do projeto
- Código bem estruturado e documentado
- DTOs separados por responsabilidade
- Fácil adição de novos relatórios seguindo o mesmo padrão

---

## Próximos Passos (Futuras Melhorias)

1. **Cache de Relatórios**: Implementar cache para relatórios que não mudam frequentemente
2. **Exportação**: Adicionar endpoints para exportar relatórios em PDF/Excel
3. **Filtros Avançados**: Permitir mais filtros (por cliente, vendedor, produto, etc.)
4. **Paginação**: Adicionar paginação para relatórios com muitos registros
5. **Gráficos**: Retornar dados formatados para geração de gráficos no frontend
6. **Agendamento**: Permitir agendamento automático de relatórios por email
