# 🔒 Setter Best Practices - Access Control com Lombok

## ⚠️ Por que Setters Públicos são Problemáticos?

### Problema 1: Quebra de Encapsulamento
```java
@Data  // ❌ Gera setters públicos
@Entity
public class ProductEntity {
    private Integer stock;
}

// Em QUALQUER lugar do código:
product.setStock(-100);  // ❌ Sem validação! Estado inválido!
```

### Problema 2: Perda de Controle
```java
@Data  // ❌
public class SaleEntity {
    private BigDecimal totalAmount;
    private List<SaleItemEntity> items;
}

// Permite fazer:
sale.setTotalAmount(BigDecimal.ZERO);  // ❌ Mas tem items!
sale.setItems(null);  // ❌ NPE garantido!
```

---

## ✅ Solução: AccessLevel no Lombok

### Para Entidades JPA

```java
@Getter
@Setter(AccessLevel.PACKAGE)  // ⭐ PERFEITO para JPA
@Entity
public class CustomerEntity {
    private String code;
    private String email;
}
```

**Benefícios:**
- ✅ Hibernate acessa (mesmo pacote: `infrastructure.persistence.customer`)
- ✅ Repository acessa (mesmo pacote)
- ❌ Controllers NÃO podem modificar
- ❌ Use cases NÃO podem modificar
- ✅ Apenas adapters de persistência têm acesso

### Para DTOs

```java
@Data  // ✅ OK para DTOs - são apenas dados
public class ProductRequest {
    private String name;
    private BigDecimal price;
}
```

**Por que OK aqui?**
- DTOs não têm regras de negócio
- São só transporte de dados
- Validação está nas anotações (@NotNull, etc.)

### Para Entidades de Domínio

```java
@Getter  // ✅ APENAS Getter
public class Product {
    private String code;
    private Integer stock;

    // ✅ Comportamento controlado
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        this.stock += quantity;
    }
}
```

**NUNCA @Setter em domínio!**

---

## 📊 Níveis de Acesso Explicados

### PUBLIC (Padrão)
```java
@Setter  // Mesmo que @Setter(AccessLevel.PUBLIC)
public class Example {
    private String field;
}

// Qualquer classe pode fazer:
example.setField("value");
```

### PROTECTED
```java
@Setter(AccessLevel.PROTECTED)
public class Example {
    private String field;
}

// Apenas subclasses e mesmo pacote podem:
this.setField("value");  // Dentro da classe ou subclasse
```

### PACKAGE (Package-Private) ⭐
```java
@Setter(AccessLevel.PACKAGE)
public class Example {
    private String field;
}

// Apenas classes no MESMO PACOTE podem:
example.setField("value");  // Se estiver no mesmo pacote
```

### PRIVATE
```java
@Setter(AccessLevel.PRIVATE)
public class Example {
    private String field;
}

// Apenas a PRÓPRIA CLASSE pode:
private void internalMethod() {
    this.setField("value");  // OK
}
```

### NONE
```java
@Setter(AccessLevel.NONE)  // Sem setter
public class Example {
    private final String field;  // Imutável
}
```

---

## 🎯 Regras de Uso

| Tipo de Classe | Access Level | Razão |
|----------------|--------------|-------|
| **DTO** | PUBLIC (@Data) | Apenas dados, sem comportamento |
| **JPA Entity** | PACKAGE | Hibernate precisa, mas controla acesso |
| **Domain Entity** | NONE | Comportamento via métodos |
| **Value Object** | NONE (@Value) | Imutável por definição |
| **Builder** | PUBLIC | Fluent API precisa de acesso |

---

## 💡 Exemplos Práticos

### ❌ RUIM - Setter Público em JPA

```java
@Getter
@Setter  // ❌ Público!
@Entity
public class ProductEntity {
    private Integer stock;
}

// Em um Controller (FORA da camada de persistência):
@PUT("/products/{id}/hack")
public void hackStock(Long id) {
    ProductEntity product = repository.findById(id);
    product.setStock(999999);  // ❌ Bypass das regras de negócio!
    repository.save(product);
}
```

### ✅ BOM - Setter Package em JPA

```java
@Getter
@Setter(AccessLevel.PACKAGE)  // ✅ Package-private
@Entity
public class ProductEntity {
    private Integer stock;
}

// No Controller:
@PUT("/products/{id}/hack")
public void hackStock(Long id) {
    ProductEntity product = repository.findById(id);
    product.setStock(999999);  // ❌ ERRO DE COMPILAÇÃO!
    // setStock() is not visible
}

// No Repository (mesmo pacote):
@Transactional
void updateStock(Product domain) {
    ProductEntity entity = findById(domain.getId());
    entity.setStock(domain.getStock());  // ✅ OK - mesmo pacote
    persist(entity);
}
```

### ✅ MELHOR - Sem Setter em Domínio

```java
@Getter
public class Product {
    private String code;
    private Integer stock;

    // Construtor
    public Product(String code) {
        this.code = code;
        this.stock = 0;
    }

    // ✅ Comportamento controlado
    public void addStock(int quantity) {
        validateQuantity(quantity);
        this.stock += quantity;
    }

    public void removeStock(int quantity) {
        validateQuantity(quantity);
        if (this.stock < quantity) {
            throw new InsufficientStockException();
        }
        this.stock -= quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}

// Uso:
Product product = new Product("PROD001");
product.addStock(10);      // ✅ Validado
product.removeStock(5);    // ✅ Validado
product.setStock(-100);    // ❌ ERRO DE COMPILAÇÃO - método não existe!
```

---

## 🔐 Proteção em Camadas

```
┌─────────────────────────────────────┐
│  REST Controllers                    │
│  ❌ NÃO pode usar setters JPA       │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  Use Cases                           │
│  ❌ NÃO pode usar setters JPA       │
│  ✅ USA métodos de domínio          │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  Domain Entities                     │
│  ✅ Comportamento com validação     │
│  ❌ SEM setters públicos            │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  Repository Adapters                 │
│  ✅ PODE usar setters JPA           │
│  (mesmo pacote - PACKAGE access)    │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  JPA Entities                        │
│  ✅ @Setter(AccessLevel.PACKAGE)    │
└─────────────────────────────────────┘
```

---

## ✅ Checklist de Código Seguro

Antes de fazer commit, verifique:

- [ ] DTOs usam `@Data` (OK - apenas dados)
- [ ] Entidades JPA usam `@Setter(AccessLevel.PACKAGE)` (proteção)
- [ ] Entidades de Domínio SEM `@Setter` (comportamento)
- [ ] Value Objects usam `@Value` (imutabilidade)
- [ ] Nenhum setter público em entidades JPA
- [ ] Regras de negócio em métodos de domínio, não em setters

---

## 🎓 Resumo

### ✅ FAZER
```java
// DTOs
@Data
public class ProductDTO { }

// JPA Entities
@Getter
@Setter(AccessLevel.PACKAGE)  // ⭐
@Entity
public class ProductEntity { }

// Domain Entities
@Getter  // Apenas Getter
public class Product {
    public void updatePrice(BigDecimal price) { /* validação */ }
}
```

### ❌ NÃO FAZER
```java
// JPA com setter público
@Data  // ❌
@Entity
public class ProductEntity { }

// Domain com setter
@Data  // ❌
public class Product { }
```

---

## 🎯 Conclusão

**@Setter(AccessLevel.PACKAGE)** é o sweet spot para JPA:
- ✅ Hibernate funciona
- ✅ Repositórios funcionam
- ✅ Encapsulamento mantido
- ✅ Código mais seguro

**Use wisely!** 🛡️
