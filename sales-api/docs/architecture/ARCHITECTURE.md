# 🏛️ Architecture Documentation

## Hexagonal Architecture (Ports and Adapters)

This project implements the Hexagonal Architecture pattern, also known as Ports and Adapters, which provides a clean separation between the business logic and external dependencies.

## Layer Structure

```
┌─────────────────────────────────────────────────────────┐
│                    REST Controllers                      │
│                      (Input Adapter)                     │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                     │
│                      (Use Cases)                         │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                     Domain Layer                         │
│           (Entities, Value Objects, Ports)               │
│                    ★ Business Rules ★                    │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                Repository Implementations                │
│                    (Output Adapter)                      │
└─────────────────────────────────────────────────────────┘
```

## Domain Layer (Core)

The **Domain Layer** is the heart of the application. It contains:

### Entities
Business objects with identity and lifecycle:
- **Customer**: Represents a customer with personal information
- **Product**: Represents a product with pricing and stock
- **Sale**: Represents a sales transaction
- **SaleItem**: Represents an item within a sale

### Value Objects
Immutable objects representing domain concepts:
- **Address**: Complete address with validation
- **Document**: CPF and RG with validation logic
- **Dimensions**: Product dimensions (height, width, depth)
- **PaymentMethod**: Enum for payment types
- **ProductType**: Enum for product categories

### Ports (Interfaces)
Contracts that define how the domain interacts with the outside world:
- **CustomerRepository**: Customer persistence operations
- **ProductRepository**: Product persistence operations
- **SaleRepository**: Sale persistence operations

## Application Layer

The **Application Layer** orchestrates the domain logic through use cases:

### Customer Use Cases
- `CreateCustomerUseCase`: Creates a new customer with validation
- `UpdateCustomerUseCase`: Updates customer information
- `FindCustomerUseCase`: Retrieves customer data
- `DeleteCustomerUseCase`: Removes a customer

### Product Use Cases
- `CreateProductUseCase`: Creates a new product
- `UpdateProductUseCase`: Updates product information
- `FindProductUseCase`: Retrieves product data
- `DeleteProductUseCase`: Removes a product

### Sale Use Cases
- `CreateSaleUseCase`: Creates a sale with stock validation
- `FindSaleUseCase`: Retrieves sale information

## Infrastructure Layer

The **Infrastructure Layer** implements the technical details:

### Persistence Adapters
- **CustomerRepositoryAdapter**: Implements CustomerRepository using Panache
- **ProductRepositoryAdapter**: Implements ProductRepository using Panache
- **SaleRepositoryAdapter**: Implements SaleRepository using Panache

### REST Adapters
- **Controllers**: Handle HTTP requests and responses
- **DTOs**: Data Transfer Objects for API communication
- **Mappers**: Convert between domain entities and DTOs

## SOLID Principles Application

### Single Responsibility Principle (SRP)
Each class has one reason to change:
- Entities handle business rules
- Use cases handle application logic
- Controllers handle HTTP communication
- Repositories handle persistence

### Open/Closed Principle (OCP)
Entities are open for extension but closed for modification:
- New payment methods can be added without changing Sale entity
- New product types can be added without changing Product entity

### Liskov Substitution Principle (LSP)
Repository implementations can be substituted:
- Any implementation of `CustomerRepository` can replace another
- Tests use mocks that implement the same interfaces

### Interface Segregation Principle (ISP)
Specific interfaces for each repository:
- `CustomerRepository` only has customer-related methods
- `ProductRepository` only has product-related methods
- No fat interfaces forcing unnecessary implementations

### Dependency Inversion Principle (DIP)
High-level modules don't depend on low-level modules:
- Use cases depend on repository interfaces (ports)
- Infrastructure implements these interfaces
- Domain has zero dependencies on infrastructure

## Data Flow

### Creating a Sale (Example)

```
1. REST Request
   ↓
2. SaleController receives SaleRequest DTO
   ↓
3. SaleMapper converts DTO to Domain Entity
   ↓
4. CreateSaleUseCase orchestrates the operation:
   - Validates customer exists (via CustomerRepository port)
   - Validates products exist (via ProductRepository port)
   - Validates stock availability
   - Decreases stock for each item
   - Saves sale (via SaleRepository port)
   ↓
5. SaleMapper converts Domain Entity to Response DTO
   ↓
6. SaleController returns HTTP Response
```

## Testing Strategy

### Unit Tests
- **Domain Entities**: Test business rules in isolation
- **Value Objects**: Test validation logic
- **Use Cases**: Test with mocked repositories

### Integration Tests
- **Controllers**: Test full HTTP request/response cycle
- **Repositories**: Test database interactions

## Benefits of This Architecture

1. **Independence**: Domain logic is independent of frameworks
2. **Testability**: Easy to test with mocks
3. **Flexibility**: Easy to change database or web framework
4. **Maintainability**: Clear separation of concerns
5. **Scalability**: Easy to add new features
6. **Clean Code**: Following SOLID principles

## Package Structure

```
com.sales
├── domain                          # Domain Layer (Core)
│   ├── customer
│   │   ├── entity                 # Customer entity
│   │   ├── valueobject            # Address, Document
│   │   └── port                   # CustomerRepository interface
│   ├── product
│   │   ├── entity                 # Product entity
│   │   ├── valueobject            # Dimensions, ProductType
│   │   └── port                   # ProductRepository interface
│   ├── sale
│   │   ├── entity                 # Sale, SaleItem entities
│   │   ├── valueobject            # PaymentMethod
│   │   └── port                   # SaleRepository interface
│   └── shared                     # Base Entity class
│
├── application                     # Application Layer
│   ├── customer.usecase           # Customer use cases
│   ├── product.usecase            # Product use cases
│   └── sale.usecase               # Sale use cases
│
└── infrastructure                  # Infrastructure Layer
    ├── persistence                # Database adapters
    │   ├── customer
    │   │   ├── entity             # JPA entities
    │   │   └── repository         # Repository implementations
    │   ├── product
    │   └── sale
    │
    └── rest                       # REST adapters
        ├── customer
        │   ├── dto                # DTOs
        │   └── controller         # REST controllers
        ├── product
        ├── sale
        └── exception              # Global exception handling
```

## Key Design Decisions

1. **Separate Domain and Persistence Models**: Domain entities are pure Java objects, JPA entities are in infrastructure
2. **Immutable Value Objects**: Ensures thread safety and prevents unintended modifications
3. **Repository Pattern**: Abstracts data access behind interfaces
4. **Use Case Pattern**: Each business operation is a separate class
5. **Mapper Pattern**: Converts between layers without coupling them

## Future Extensibility

The architecture makes it easy to:
- Add new payment gateways
- Change database (PostgreSQL → MongoDB)
- Add event-driven features
- Implement CQRS pattern
- Add caching layer
- Create GraphQL API alongside REST
