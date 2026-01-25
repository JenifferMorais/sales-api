# ⚡ Quick Start Guide

## 🚀 Start the Application (3 steps)

### 1. Start Database
```bash
docker-compose up -d
```

### 2. Run Application
```bash
./mvnw quarkus:dev
```
Or on Windows:
```cmd
mvnw.cmd quarkus:dev
```

### 3. Open Swagger
```
http://localhost:8080/swagger-ui
```

## 🧪 Run Tests

```bash
# All tests
./mvnw test

# With coverage report
./mvnw verify

# View coverage
open target/site/jacoco/index.html
```

## 📡 Quick API Examples

### Create a Customer
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
    "email": "joao@email.com"
  }'
```

### Create a Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "code": "PROD001",
    "name": "Mesa de Escritório",
    "type": "FURNITURE",
    "details": "Mesa com gavetas",
    "weight": 25.5,
    "purchasePrice": 300.00,
    "salePrice": 500.00,
    "height": 75.0,
    "width": 140.0,
    "depth": 70.0,
    "destinationVehicle": "Caminhão"
  }'
```

### List All Customers
```bash
curl http://localhost:8080/api/v1/customers
```

### List Products (Sorted)
```bash
curl http://localhost:8080/api/v1/products?sorted=true
```

## 🔧 Useful Commands

### Maven
```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw package

# Skip tests
./mvnw package -DskipTests
```

### Docker
```bash
# Start containers
docker-compose up -d

# Stop containers
docker-compose down

# View logs
docker-compose logs -f

# Restart database
docker-compose restart postgres
```

### Quarkus Dev Mode
```bash
# Start dev mode
./mvnw quarkus:dev

# Access Dev UI
http://localhost:8080/q/dev

# Hot reload is automatic - just save files!
```

## 🐘 PostgreSQL Access

### Via Command Line
```bash
docker exec -it sales-postgres psql -U sales -d sales_db
```

### Via PgAdmin
```
URL: http://localhost:5050
Email: admin@sales.com
Password: admin123

Add Server:
  Host: postgres
  Database: sales_db
  Username: sales
  Password: sales123
```

## 📊 Database Queries

```sql
-- List all customers
SELECT * FROM customers;

-- List all products
SELECT * FROM products ORDER BY name;

-- List all sales
SELECT * FROM sales;

-- Sales with items
SELECT s.code, s.customer_name, si.product_name, si.quantity
FROM sales s
JOIN sale_items si ON s.id = si.sale_id;
```

## 🛠️ Troubleshooting

### Port 8080 already in use
```bash
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

### Database connection error
```bash
# Check if database is running
docker ps

# Restart database
docker-compose restart postgres
```

### Clean everything
```bash
./mvnw clean
docker-compose down -v
docker-compose up -d
./mvnw quarkus:dev
```

## 📁 Project Files

- `README.md` - Main documentation
- `INSTRUCTIONS.md` - Detailed instructions
- `ARCHITECTURE.md` - Architecture details
- `PROJECT_SUMMARY.md` - Complete project summary
- `api-examples.http` - API examples (use with VS Code REST Client)
- `docker-compose.yml` - Database setup
- `pom.xml` - Maven dependencies

## 🎯 Important URLs

| Service | URL |
|---------|-----|
| Swagger UI | http://localhost:8080/swagger-ui |
| OpenAPI Spec | http://localhost:8080/q/openapi |
| Dev UI | http://localhost:8080/q/dev |
| Health Check | http://localhost:8080/q/health |
| Metrics | http://localhost:8080/q/metrics |
| PgAdmin | http://localhost:5050 |

## 📝 Product Types Available

- `OUTDOOR_FINISHING`
- `INDOOR_FINISHING`
- `FURNITURE`
- `BANK`
- `ELECTRICAL`
- `SANITARY`
- `DECORATION`
- `OTHER`

## 💳 Payment Methods Available

- `CASH`
- `CREDIT_CARD`
- `DEBIT_CARD`
- `PIX`
- `BANK_TRANSFER`

## ✅ Validation Rules

### Customer
- CPF must be valid (with checksum)
- Email must be unique
- CPF must be unique
- Birth date must be in the past
- Phone must be 11 digits

### Product
- Sale price ≥ Purchase price
- All dimensions must be > 0
- Weight must be > 0

### Sale
- Must have at least 1 item
- Customer must exist
- Products must exist
- Products must have stock
- Cash payment: amount paid ≥ total

## 🎓 Next Steps

1. Explore Swagger UI
2. Try API examples
3. Check test coverage
4. Read architecture documentation
5. Modify and extend!
