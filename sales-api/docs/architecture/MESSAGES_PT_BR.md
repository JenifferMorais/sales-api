# 🇧🇷 Mensagens em Português - Vendas API

## ✅ Atualização Completa

Todas as mensagens de erro, validação e retorno da API foram traduzidas para **português brasileiro**.

---

## 📋 Validações de DTOs

### Customer (Cliente)
- ✅ "Código é obrigatório"
- ✅ "Nome completo é obrigatório"
- ✅ "Nome da mãe é obrigatório"
- ✅ "CPF é obrigatório" / "CPF inválido"
- ✅ "RG é obrigatório"
- ✅ "Endereço é obrigatório"
- ✅ "Data de nascimento é obrigatória" / "deve estar no passado"
- ✅ "Celular é obrigatório" / "Celular inválido"
- ✅ "Email é obrigatório" / "Email inválido"

### Address (Endereço)
- ✅ "CEP é obrigatório" / "CEP inválido. Formato: 12345-678"
- ✅ "Rua é obrigatória"
- ✅ "Número é obrigatório"
- ✅ "Bairro é obrigatório"
- ✅ "Cidade é obrigatória"
- ✅ "Estado é obrigatório" / "deve ter 2 letras maiúsculas (ex: SP, RJ)"

### Product (Produto)
- ✅ "Código é obrigatório"
- ✅ "Nome é obrigatório"
- ✅ "Tipo é obrigatório"
- ✅ "Peso é obrigatório" / "deve ser maior que zero"
- ✅ "Preço de compra é obrigatório" / "deve ser maior que zero"
- ✅ "Preço de venda é obrigatório" / "deve ser maior que zero"
- ✅ "Altura é obrigatória" / "deve ser maior que zero"
- ✅ "Largura é obrigatória" / "deve ser maior que zero"
- ✅ "Profundidade é obrigatória" / "deve ser maior que zero"

### Sale (Venda)
- ✅ "Código é obrigatório"
- ✅ "Código do cliente é obrigatório"
- ✅ "Código do vendedor é obrigatório"
- ✅ "Forma de pagamento é obrigatória"
- ✅ "Valor pago deve ser maior que zero"
- ✅ "Itens são obrigatórios"
- ✅ "Venda deve ter pelo menos um item"
- ✅ "Código do produto é obrigatório"
- ✅ "Quantidade é obrigatória" / "deve ser no mínimo 1"

---

## 🏛️ Mensagens do Domínio

### Customer Entity
- "Código não pode estar vazio"
- "Nome completo não pode estar vazio"
- "Nome da mãe não pode estar vazio"
- "Documento não pode ser nulo"
- "Endereço não pode ser nulo"
- "Data de nascimento não pode ser nula"
- "Data de nascimento não pode ser no futuro"
- "Data de nascimento inválida"
- "Celular não pode estar vazio"
- "Celular deve ter 11 dígitos"
- "Email não pode estar vazio"
- "Formato de email inválido"

### Document (CPF/RG)
- "CPF não pode estar vazio"
- "CPF deve ter 11 dígitos"
- "CPF inválido"
- "RG não pode estar vazio"
- "Formato de RG inválido"

### Address
- "CEP não pode estar vazio"
- "CEP deve ter 8 dígitos"
- "Rua não pode estar vazia"
- "Número não pode estar vazio"
- "Bairro não pode estar vazio"
- "Cidade não pode estar vazia"
- "Estado não pode estar vazio"
- "Estado deve ter 2 caracteres"

### Product Entity
- "Código não pode estar vazio"
- "Nome do produto não pode estar vazio"
- "Tipo do produto não pode ser nulo"
- "Peso não pode ser nulo" / "deve ser maior que zero"
- "Preço de compra não pode ser nulo" / "deve ser maior que zero"
- "Preço de venda não pode ser nulo" / "deve ser maior que zero"
- "Dimensões não podem ser nulas"
- "Preço de venda não pode ser menor que o preço de compra"
- "Quantidade deve ser maior que zero"
- "Estoque insuficiente"

### Dimensions
- "Altura não pode ser nula" / "deve ser maior que zero"
- "Largura não pode ser nula" / "deve ser maior que zero"
- "Profundidade não pode ser nula" / "deve ser maior que zero"

### Sale Entity
- "Código não pode estar vazio"
- "Código do cliente não pode estar vazio"
- "Nome do cliente não pode estar vazio"
- "Código do vendedor não pode estar vazio"
- "Forma de pagamento não pode ser nula"
- "Número do cartão é obrigatório para pagamento com cartão"
- "Valor pago não pode ser negativo"
- "Item não pode ser nulo"
- "Venda deve ter pelo menos um item"
- "Valor pago é insuficiente"

### SaleItem
- "Código do produto não pode estar vazio"
- "Nome do produto não pode estar vazio"
- "Quantidade deve ser maior que zero"
- "Preço não pode ser nulo"
- "Preço deve ser maior que zero"

---

## 🎯 Use Cases

### Customer Use Cases
- "Cliente com código {code} já existe"
- "Cliente com CPF {cpf} já existe"
- "Cliente com email {email} já existe"
- "Cliente não encontrado com id: {id}"
- "Cliente não encontrado com código: {code}"
- "Email {email} já está em uso"

### Product Use Cases
- "Produto com código {code} já existe"
- "Produto não encontrado com id: {id}"
- "Produto não encontrado com código: {code}"

### Sale Use Cases
- "Venda com código {code} já existe"
- "Cliente não encontrado com código: {code}"
- "Produto não encontrado com código: {code}"
- "Estoque insuficiente para produto: {name}. Disponível: {available}, Solicitado: {requested}"
- "Venda não encontrada com id: {id}"
- "Venda não encontrada com código: {code}"

---

## 🔧 Exemplos de Resposta

### ✅ Sucesso (201 Created)
```json
{
  "id": 1,
  "code": "CUST001",
  "fullName": "João Silva",
  "email": "joao@email.com"
}
```

### ❌ Erro de Validação (400 Bad Request)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "CPF inválido. Formato: 123.456.789-09"
}
```

### ❌ Não Encontrado (404 Not Found)
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Cliente não encontrado com id: 123"
}
```

### ❌ Conflito (400 Bad Request)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Cliente com CPF 123.456.789-09 já existe"
}
```

### ❌ Regra de Negócio (400 Bad Request)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Estoque insuficiente para produto: Mesa. Disponível: 5, Solicitado: 10"
}
```

---

## 📊 Cobertura

| Camada | Status |
|--------|--------|
| **DTOs** | ✅ 100% em português |
| **Domain Entities** | ✅ 100% em português |
| **Value Objects** | ✅ 100% em português |
| **Use Cases** | ✅ 100% em português |
| **Exception Handler** | ✅ 100% em português |

---

## 🎯 Padrões Utilizados

### Campos Obrigatórios
- "{Campo} é obrigatório"
- Exemplo: "Nome é obrigatório"

### Validação de Formato
- "{Campo} inválido. Formato: {exemplo}"
- Exemplo: "CPF inválido. Formato: 123.456.789-09"

### Valores Numéricos
- "{Campo} deve ser maior que zero"
- "{Campo} deve ser no mínimo {valor}"

### Não Encontrado
- "{Entidade} não encontrado com {campo}: {valor}"
- Exemplo: "Cliente não encontrado com id: 123"

### Duplicação
- "{Entidade} com {campo} {valor} já existe"
- Exemplo: "Cliente com CPF 123.456.789-09 já existe"

### Regras de Negócio
- Mensagens descritivas e contextuais
- Exemplo: "Preço de venda não pode ser menor que o preço de compra"

---

## ✅ Benefícios

1. **Melhor UX** - Usuários entendem as mensagens
2. **Profissionalismo** - API em português para mercado brasileiro
3. **Clareza** - Mensagens objetivas e diretas
4. **Consistência** - Padrões uniformes em toda API
5. **Facilita Debug** - Desenvolvedores entendem os erros

---

## 🚀 Testando

### Exemplo 1: CPF Inválido
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"cpf": "111.111.111-11", ...}'
```

**Resposta:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "CPF inválido"
}
```

### Exemplo 2: Email Já Existe
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"email": "joao@email.com", ...}'
```

**Resposta:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Cliente com email joao@email.com já existe"
}
```

### Exemplo 3: Estoque Insuficiente
```bash
curl -X POST http://localhost:8080/api/v1/sales \
  -H "Content-Type: application/json" \
  -d '{"items": [{"productCode": "PROD001", "quantity": 999}], ...}'
```

**Resposta:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Estoque insuficiente para produto: Mesa. Disponível: 5, Solicitado: 999"
}
```

---

## 📝 Conclusão

**100% das mensagens agora estão em português!**

A API está completamente localizada para o mercado brasileiro, proporcionando:
- ✅ Melhor experiência para desenvolvedores
- ✅ Mensagens claras para usuários finais
- ✅ Profissionalismo e consistência
- ✅ Facilita integração e debug

🇧🇷 **Made in Brazil, for Brazil!**
