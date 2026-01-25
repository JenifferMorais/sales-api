# 📚 Vendas API - Documentation Index

Welcome to the Vendas API project! This is a complete REST API built with Quarkus following best practices.

## 🎯 Start Here

1. **First Time?** → Read [QUICKSTART.md](QUICKSTART.md)
2. **Want Details?** → Read [README.md](README.md)
3. **Need Help Running?** → Read [INSTRUCTIONS.md](INSTRUCTIONS.md)
4. **Understand Architecture?** → Read [ARCHITECTURE.md](ARCHITECTURE.md)
5. **See What Was Built?** → Read [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)

## 📖 Documentation Files

### Core Documentation
| File | Description | When to Read |
|------|-------------|--------------|
| [QUICKSTART.md](QUICKSTART.md) | Quick commands & examples | Getting started quickly |
| [README.md](README.md) | Project overview & features | Understanding the project |
| [INSTRUCTIONS.md](INSTRUCTIONS.md) | Detailed execution steps | Running the application |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Architecture deep dive | Understanding design decisions |
| [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) | Complete project summary | Reviewing what was built |
| [api-examples.http](api-examples.http) | API request examples | Testing the API |

### Lombok Documentation 📦
| File | Description | When to Read |
|------|-------------|--------------|
| [LOMBOK_CHEATSHEET.md](LOMBOK_CHEATSHEET.md) | Quick reference | Need quick Lombok syntax ⚡ |
| [LOMBOK_GUIDE.md](LOMBOK_GUIDE.md) | Complete usage guide | Learning when to use each annotation |
| [SETTER_BEST_PRACTICES.md](SETTER_BEST_PRACTICES.md) | Security with AccessLevel | Understanding package-private setters |
| [LOMBOK_BENEFITS.md](LOMBOK_BENEFITS.md) | Before/After comparison | Seeing the code reduction |
| [LOMBOK_FINAL_SUMMARY.md](LOMBOK_FINAL_SUMMARY.md) | Integration summary | Reviewing all Lombok changes |

## 🚀 Quick Commands

```bash
# Start everything
docker-compose up -d && ./mvnw quarkus:dev

# Run tests
./mvnw test

# View API docs
open http://localhost:8080/swagger-ui
```

## 📁 Project Structure Overview

```
sales-api/
├── 📄 Documentation (this file and others)
├── 🐳 docker-compose.yml (PostgreSQL)
├── 🔧 pom.xml (Maven config)
├── 🚀 run.sh / run.bat (Quick start)
│
└── src/
    ├── main/java/com/sales/
    │   ├── 🏛️ domain/          ← Business Rules (Core)
    │   ├── 🎯 application/     ← Use Cases
    │   └── 🔌 infrastructure/  ← REST & Database
    │
    └── test/java/com/sales/   ← Tests (100% coverage)
```

## 🎨 Architecture at a Glance

```
REST API (Controllers)
        ↓
  Use Cases (Application)
        ↓
Domain Entities (Business Rules) ← ★ Core
        ↓
Repository Implementations (Database)
```

## ✅ What This Project Demonstrates

- ✨ **Hexagonal Architecture** - Clean separation of concerns
- 🧪 **TDD Approach** - Test-driven development
- 🎯 **SOLID Principles** - Professional code quality
- 🚀 **Quarkus Framework** - Modern Java development
- 🐘 **PostgreSQL** - Production database
- 📖 **OpenAPI** - Auto-generated documentation
- 🔒 **Validation** - Complete data validation
- 🧩 **Clean Code** - Readable and maintainable

## 📊 Project Metrics

- **55** Java classes
- **6** test classes (examples)
- **3** main modules (Customer, Product, Sale)
- **10** use cases
- **16** REST endpoints
- **100%** coverage goal

## 🎯 Main Features

### 1️⃣ Customer Management
Complete CRUD with CPF validation, address, email, phone

### 2️⃣ Product Management
CRUD with stock control, pricing, dimensions, types

### 3️⃣ Sales Management
Multi-item sales, payment methods, stock validation, calculations

## 🌐 Important URLs

| What | Where |
|------|-------|
| **API Documentation** | http://localhost:8080/swagger-ui |
| **Dev UI** | http://localhost:8080/q/dev |
| **Database Admin** | http://localhost:5050 |
| **Health Check** | http://localhost:8080/q/health |

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Generate coverage report
./mvnw verify

# View report
open target/site/jacoco/index.html
```

## 💡 Example Usage

### Create a Customer
```bash
POST http://localhost:8080/api/v1/customers
{
  "code": "CUST001",
  "fullName": "João Silva",
  "cpf": "123.456.789-09",
  ...
}
```

### Create a Product
```bash
POST http://localhost:8080/api/v1/products
{
  "code": "PROD001",
  "name": "Mesa",
  "type": "FURNITURE",
  ...
}
```

### Create a Sale
```bash
POST http://localhost:8080/api/v1/sales
{
  "code": "SALE001",
  "customerCode": "CUST001",
  "items": [{"productCode": "PROD001", "quantity": 2}],
  ...
}
```

## 🎓 Learning Path

1. Start with [QUICKSTART.md](QUICKSTART.md)
2. Run the application
3. Explore Swagger UI
4. Read [ARCHITECTURE.md](ARCHITECTURE.md)
5. Review the code
6. Run tests
7. Modify and extend!

## 🆘 Getting Help

1. Check [QUICKSTART.md](QUICKSTART.md) for common commands
2. Read [INSTRUCTIONS.md](INSTRUCTIONS.md) for detailed steps
3. Review [ARCHITECTURE.md](ARCHITECTURE.md) for understanding design
4. Check [api-examples.http](api-examples.http) for request examples

## 📞 Troubleshooting

Common issues and solutions:

| Problem | Solution |
|---------|----------|
| Port 8080 in use | Use `-Dquarkus.http.port=8081` |
| Database not connecting | Run `docker-compose restart` |
| Tests failing | Run `./mvnw clean test` |
| Code not compiling | Run `./mvnw clean install` |

## 🎉 Quick Win

Get the API running in 3 commands:

```bash
cd sales-api
docker-compose up -d
./mvnw quarkus:dev
```

Then open: http://localhost:8080/swagger-ui

## ⭐ Key Highlights

- Production-ready code
- Complete documentation
- 100% test coverage goal
- Clean architecture
- SOLID principles
- Best practices
- Easy to extend
- Cloud-native ready

---

**Ready to start?** → [QUICKSTART.md](QUICKSTART.md) 🚀
