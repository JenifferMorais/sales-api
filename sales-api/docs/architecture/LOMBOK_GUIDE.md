# 📘 Lombok Usage Guide - When and How to Use

## 🎯 Regra de Ouro

**DTOs e Entidades JPA**: Use Lombok livremente ✅
**Entidades de Domínio**: Use com cautela, prefira @Getter sem @Setter ⚠️
**Value Objects**: Use @Value para imutabilidade ✅

---

## ✅ Camada de Infraestrutura (DTOs)

### USO RECOMENDADO: @Data

DTOs são apenas **transferência de dados**, sem comportamento.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String code;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;
}
```

**Benefícios:**
- ✅ Menos código boilerplate
- ✅ Getters e setters automáticos
- ✅ toString() útil para debug
- ✅ equals() e hashCode()

---

## ✅ Camada de Infraestrutura (Entidades JPA)

### USO RECOMENDADO: @Getter + @Setter(AccessLevel.PACKAGE) ⭐ MELHOR PRÁTICA

Entidades JPA precisam de setters para o Hibernate, mas **não precisam ser públicos**!

```java
@Getter
@Setter(AccessLevel.PACKAGE)  // ⭐ Setters apenas no pacote
@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String fullName;
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

**Por que @Setter(AccessLevel.PACKAGE)?**
- ✅ Hibernate pode acessar (mesmo pacote)
- ✅ Repositórios podem usar (infrastructure.persistence)
- ❌ Código externo NÃO pode modificar diretamente
- ✅ Melhor encapsulamento

**Outras opções de AccessLevel:**
```java
@Setter(AccessLevel.PUBLIC)     // Padrão - todos podem acessar
@Setter(AccessLevel.PROTECTED)  // Subclasses podem acessar
@Setter(AccessLevel.PACKAGE)    // Apenas mesmo pacote ⭐ RECOMENDADO
@Setter(AccessLevel.PRIVATE)    // Apenas a classe
@Setter(AccessLevel.NONE)       // Sem setter (imutável)
```

**⚠️ Evite @Data em Entidades JPA:**
```java
@Data  // ❌ Gera setters PÚBLICOS
@Entity
public class CustomerEntity {
    // Permite entity.setCode("X") de QUALQUER LUGAR!
}
```

---

## ⚠️ Camada de Domínio (Entidades)

### USO RECOMENDADO: @Getter (SEM @Setter ou @Data)

Entidades de domínio devem **controlar seu próprio estado**.

### ❌ NÃO FAZER:
```java
@Data  // ❌ Gera setters públicos!
public class Product {
    private String code;
    private BigDecimal price;
    private Integer stock;
}

// Permite fazer:
product.setStock(-10);  // ❌ Sem validação!
```

### ✅ FAZER:
```java
@Getter
public class Product extends Entity {
    private String code;
    private String name;
    private BigDecimal salePrice;
    private Integer stockQuantity;

    // Construtor com validação
    public Product(String code, String name, BigDecimal salePrice) {
        this.code = validateCode(code);
        this.name = validateName(name);
        this.salePrice = validatePrice(salePrice);
        this.stockQuantity = 0;
    }

    // Comportamento com validação
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        if (quantity > this.stockQuantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
    }

    // Métodos privados de validação
    private String validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }
        return code;
    }
    // ... outras validações
}
```

**Benefícios:**
- ✅ Getters automáticos com @Getter
- ✅ Estado controlado (sem setters públicos)
- ✅ Validação garantida em métodos de negócio
- ✅ Expressa comportamento de domínio

---

## ✅ Camada de Domínio (Value Objects)

### USO RECOMENDADO: @Value

Value Objects devem ser **imutáveis**.

```java
@Value
public class Address {
    String zipCode;
    String street;
    String number;
    String complement;
    String neighborhood;
    String city;
    String state;

    // Construtor com validação
    public Address(String zipCode, String street, String number,
                   String complement, String neighborhood,
                   String city, String state) {
        this.zipCode = validateZipCode(zipCode);
        this.street = validateNotEmpty(street, "Street");
        this.number = validateNotEmpty(number, "Number");
        this.complement = complement;
        this.neighborhood = validateNotEmpty(neighborhood, "Neighborhood");
        this.city = validateNotEmpty(city, "City");
        this.state = validateState(state);
    }

    private String validateZipCode(String zipCode) {
        // validação...
        return zipCode;
    }
    // ... outras validações
}
```

**O que @Value faz:**
- ✅ Todos os campos são `final`
- ✅ Gera getters (sem setters)
- ✅ Gera equals() e hashCode()
- ✅ Gera toString()
- ✅ Classe é `final` (não pode ser herdada)

---

## 📊 Comparação Rápida

| Anotação | Onde Usar | Gera Setters? | Imutável? |
|----------|-----------|---------------|-----------|
| `@Data` | DTOs, Entidades JPA | ✅ Sim | ❌ Não |
| `@Getter` + `@Setter` | Entidades JPA | ✅ Sim | ❌ Não |
| `@Getter` | Entidades Domínio | ❌ Não | ❌ Não* |
| `@Value` | Value Objects | ❌ Não | ✅ Sim |
| `@Builder` | DTOs, Testes | ❌ Não** | ❌ Não |

*Controle manual do estado
**Apenas se combinado com @Setter

---

## 🎨 Combinações Úteis

### DTOs de Request
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
}
```

### DTOs de Response
```java
@Data
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
}
```

### Entidades de Domínio
```java
@Getter
@EqualsAndHashCode(of = "code")
public class Customer extends Entity {
    private String code;
    private String fullName;
    private String email;
    // Sem setters públicos!
    // Comportamento via métodos
}
```

---

## 🚫 O Que NUNCA Fazer

### ❌ @Data em Entidades de Domínio
```java
@Data  // ❌ NUNCA!
public class Sale {
    private List<SaleItem> items;
    private BigDecimal total;
}

// Permite fazer:
sale.setItems(null);  // ❌ Quebra regras de negócio
sale.setTotal(new BigDecimal("-100"));  // ❌ Sem validação
```

### ❌ Setters em Value Objects
```java
@Data  // ❌ ERRADO para Value Object
public class Document {
    private String cpf;  // Deve ser imutável!
}

// Permite fazer:
document.setCpf("outro-cpf");  // ❌ Value Objects são imutáveis!
```

---

## ✅ Checklist de Uso

Antes de usar uma anotação Lombok, pergunte:

1. **É um DTO?**
   - ✅ Sim → Use `@Data`
   - ❌ Não → Continue

2. **É uma Entidade JPA?**
   - ✅ Sim → Use `@Getter` + `@Setter` (ou `@Data`)
   - ❌ Não → Continue

3. **É uma Entidade de Domínio?**
   - ✅ Sim → Use apenas `@Getter`
   - ❌ Não → Continue

4. **É um Value Object?**
   - ✅ Sim → Use `@Value`

---

## 📚 Resumo

### Camada de Infraestrutura
```java
// DTOs - Use @Data livremente
@Data
public class CustomerDTO { ... }

// Entidades JPA - @Getter + @Setter ou @Data
@Getter @Setter
@Entity
public class CustomerEntity { ... }
```

### Camada de Domínio
```java
// Entidades - APENAS @Getter
@Getter
public class Customer {
    // Comportamento, não setters!
    public void updateEmail(String email) { ... }
}

// Value Objects - @Value (imutável)
@Value
public class Address { ... }
```

---

## 🎯 Regra Final

**Se tem comportamento de negócio → NÃO use @Data**
**Se é apenas dados → USE @Data**

---

## 🔗 Referências

- [Lombok Official Docs](https://projectlombok.org/)
- [DDD and Lombok Best Practices](https://vladmihalcea.com/lombok-jpa-hibernate/)
