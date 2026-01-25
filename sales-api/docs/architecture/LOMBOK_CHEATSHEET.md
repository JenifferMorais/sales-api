# 📋 Lombok Cheat Sheet - Referência Rápida

## 🎯 Regra de Ouro

```
É um DTO?              → @Data
É uma Entidade JPA?    → @Getter + @Setter(AccessLevel.PACKAGE)
É uma Entidade Domain? → @Getter (SEM @Setter!)
É um Value Object?     → @Value
```

---

## 📦 Por Tipo de Classe

### DTO (Request/Response)
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private String name;
    private BigDecimal price;
}

// Uso:
ProductDTO dto = ProductDTO.builder()
    .name("Mesa")
    .price(new BigDecimal("100.00"))
    .build();
```

### JPA Entity
```java
@Getter
@Setter(AccessLevel.PACKAGE)  // ⭐ Package-private
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    private Long id;
    private String code;
    private BigDecimal price;
}
```

### Domain Entity
```java
@Getter  // APENAS Getter
public class Product {
    private String code;
    private BigDecimal price;

    public void updatePrice(BigDecimal newPrice) {
        this.price = validate(newPrice);
    }
}
```

### Value Object
```java
@Value  // Imutável
public class Money {
    BigDecimal amount;
    String currency;
}

// Uso:
Money price = new Money(new BigDecimal("100"), "BRL");
// price.setAmount(...);  // ❌ ERRO - é imutável!
```

---

## 🔧 Anotações Principais

| Anotação | O Que Gera | Quando Usar |
|----------|------------|-------------|
| `@Getter` | getters | Sempre que precisar de getters |
| `@Setter` | setters públicos | DTOs |
| `@Setter(PACKAGE)` | setters package-private | JPA Entities ⭐ |
| `@ToString` | toString() | Debug |
| `@EqualsAndHashCode` | equals() e hashCode() | Quando precisar comparar |
| `@NoArgsConstructor` | construtor vazio | DTOs, JPA |
| `@AllArgsConstructor` | construtor com todos campos | DTOs, testes |
| `@RequiredArgsConstructor` | construtor com campos final/@NonNull | Injeção de dependência |
| `@Data` | Tudo acima (menos @RequiredArgsConstructor) | DTOs |
| `@Value` | Classe imutável | Value Objects |
| `@Builder` | Builder pattern | Objetos complexos |

---

## 🎨 Combinações Comuns

### DTO de Request
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
}
```

### DTO de Response
```java
@Data
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
```

### JPA Entity (Melhor Prática)
```java
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor  // JPA precisa
@Entity
public class ProductEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private BigDecimal price;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Service com Injeção
```java
@Service
@RequiredArgsConstructor  // Gera construtor para campos final
public class ProductService {
    private final ProductRepository repository;  // Injetado automaticamente
    private final ProductMapper mapper;

    public ProductDTO create(ProductDTO dto) {
        // ...
    }
}
```

---

## 🔒 Access Levels

```java
@Setter(AccessLevel.PUBLIC)     // public void setName(...)     - Padrão
@Setter(AccessLevel.PROTECTED)  // protected void setName(...)  - Herança
@Setter(AccessLevel.PACKAGE)    // void setName(...)            - Mesmo pacote ⭐
@Setter(AccessLevel.PRIVATE)    // private void setName(...)    - Apenas classe
@Setter(AccessLevel.NONE)       // Sem setter                   - Imutável
```

**Para JPA Entities, use PACKAGE!**

---

## ⚡ Atalhos IDE

### IntelliJ IDEA
- **Gerar Delombok**: Right-click → Refactor → Delombok
- **Ver código gerado**: Navigate → Show Lombok Generated Code

### VS Code
- Instalar: Lombok Annotations Support for VS Code
- Restart: Reload Window após instalação

### Eclipse
- Download: lombok.jar
- Executar: `java -jar lombok.jar`
- Seguir wizard de instalação

---

## 🚫 Quando NÃO Usar

### ❌ NÃO use @Data em:
```java
@Data  // ❌
@Entity
public class UserEntity {
    private String password;  // Setter público expõe senha!
}
```

### ❌ NÃO use @Data em Domain:
```java
@Data  // ❌
public class BankAccount {
    private BigDecimal balance;  // setBalance() sem validação!
}
```

### ❌ NÃO use @ToString com relações:
```java
@ToString  // ❌ StackOverflowError!
@Entity
public class Parent {
    @OneToMany
    private List<Child> children;  // Child também tem @ToString para Parent
}
```

**Solução:**
```java
@ToString(exclude = "children")  // ✅
@Entity
public class Parent {
    @OneToMany
    private List<Child> children;
}
```

---

## ✅ Boas Práticas

### 1. DTOs - Use @Data Livremente
```java
@Data
@Builder
public class CustomerDTO { }
```

### 2. JPA - Proteja Setters
```java
@Getter
@Setter(AccessLevel.PACKAGE)  // ⭐
@Entity
public class CustomerEntity { }
```

### 3. Domain - Comportamento, não Setters
```java
@Getter
public class Customer {
    private String email;

    public void updateEmail(String newEmail) {
        this.email = validateEmail(newEmail);
    }
}
```

### 4. Value Objects - Imutabilidade
```java
@Value
public class Email {
    String address;

    public Email(String address) {
        this.address = validate(address);
    }
}
```

### 5. Services - Constructor Injection
```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final MyRepository repository;  // final = required
    private final MyMapper mapper;
}
```

---

## 🎯 Decisão Rápida

```
┌─────────────────────────────────────────────┐
│  Preciso de getters e setters?              │
└──────────────┬──────────────────────────────┘
               │
       ┌───────┴────────┐
       │                │
    É DTO?         É Entity?
       │                │
       ↓                ↓
     @Data       ┌──────┴─────┐
                 │            │
              JPA?       Domain?
                 │            │
                 ↓            ↓
         @Setter(PACKAGE)  @Getter
                           (sem setter)
```

---

## 📚 Referências Rápidas

| Dúvida | Arquivo |
|--------|---------|
| Quando usar cada anotação? | [LOMBOK_GUIDE.md](LOMBOK_GUIDE.md) |
| Por que PACKAGE em JPA? | [SETTER_BEST_PRACTICES.md](SETTER_BEST_PRACTICES.md) |
| Quanto código economizei? | [LOMBOK_BENEFITS.md](LOMBOK_BENEFITS.md) |
| Resumo completo | [LOMBOK_FINAL_SUMMARY.md](LOMBOK_FINAL_SUMMARY.md) |
| Referência rápida | Este arquivo |

---

## 💡 Dicas Finais

1. **@Data é seu amigo em DTOs**
2. **@Setter(AccessLevel.PACKAGE) protege JPA Entities**
3. **Domain entities: sem @Data, com comportamento**
4. **@Value para imutabilidade**
5. **@Builder torna testes mais limpos**
6. **@RequiredArgsConstructor simplifica injeção**

---

## 🎉 TL;DR

```java
// DTO
@Data public class MyDTO { }

// JPA
@Getter @Setter(PACKAGE) @Entity public class MyEntity { }

// Domain
@Getter public class MyDomain { public void update() { } }

// ValueObject
@Value public class MyVO { }
```

**Done!** 🚀
