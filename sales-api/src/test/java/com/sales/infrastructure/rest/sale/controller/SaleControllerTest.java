package com.sales.infrastructure.rest.sale.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("SaleController Integration Tests")
class SaleControllerTest {

    @Test
    @DisplayName("Should create sale with cash payment successfully")
    void shouldCreateSaleWithCashPayment() {
        String saleJson = """
                {
                    "code": "SALETEST001",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 100.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "productName": "Batom Matte Longa Duração",
                            "quantity": 2,
                            "unitPrice": 35.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("code", equalTo("SALETEST001"))
                .body("customerCode", equalTo("CUST001"))
                .body("paymentMethod", equalTo("DINHEIRO"))
                .body("items.size()", equalTo(1));
    }

    @Test
    @DisplayName("Should create sale with credit card payment successfully")
    void shouldCreateSaleWithCreditCard() {
        String saleJson = """
                {
                    "code": "SALETEST002",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "CARTAO_CREDITO",
                    "cardNumber": "1234567890123456",
                    "items": [
                        {
                            "productCode": "PROD002",
                            "productName": "Base Líquida HD Cobertura Total",
                            "quantity": 1,
                            "unitPrice": 89.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("code", equalTo("SALETEST002"))
                .body("paymentMethod", equalTo("CARTAO_CREDITO"))
                .body("cardNumber", containsString("****"));
    }

    @Test
    @DisplayName("Should return 400 when creating sale with duplicate code")
    void shouldReturn400WhenDuplicateCode() {
        String saleJson = """
                {
                    "code": "SALEDUP001",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 100.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "productName": "Batom Matte",
                            "quantity": 1,
                            "unitPrice": 35.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .post("/api/v1/sales");

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(400)
                .body("message", containsString("já existe"));
    }

    @Test
    @DisplayName("Should return 400 when customer not found")
    void shouldReturn400WhenCustomerNotFound() {
        String saleJson = """
                {
                    "code": "SALETEST003",
                    "customerCode": "NONEXISTENT",
                    "customerName": "Invalid Customer",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 100.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "productName": "Batom Matte",
                            "quantity": 1,
                            "unitPrice": 35.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(400)
                .body("message", containsString("não encontrado"));
    }

    @Test
    @DisplayName("Should return 400 when product not found")
    void shouldReturn400WhenProductNotFound() {
        String saleJson = """
                {
                    "code": "SALETEST004",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 100.00,
                    "items": [
                        {
                            "productCode": "NONEXISTENT",
                            "productName": "Invalid Product",
                            "quantity": 1,
                            "unitPrice": 35.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(400)
                .body("message", containsString("não encontrado"));
    }

    @Test
    @DisplayName("Should return 400 when sale has no items")
    void shouldReturn400WhenNoItems() {
        String saleJson = """
                {
                    "code": "SALETEST005",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 0.00,
                    "items": []
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should list all sales successfully")
    void shouldListAllSales() {
        given()
                .when()
                .get("/api/v1/sales")
                .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Should get sale by code successfully")
    void shouldGetSaleByCode() {

        String saleJson = """
                {
                    "code": "SALEGET001",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "PIX",
                    "items": [
                        {
                            "productCode": "PROD001",
                            "productName": "Batom Matte",
                            "quantity": 1,
                            "unitPrice": 35.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .post("/api/v1/sales");

        given()
                .when()
                .get("/api/v1/sales/SALEGET001")
                .then()
                .statusCode(200)
                .body("code", equalTo("SALEGET001"))
                .body("customerName", equalTo("John Silva Santos"));
    }

    @Test
    @DisplayName("Should return 404 when sale not found")
    void shouldReturn404WhenSaleNotFound() {
        given()
                .when()
                .get("/api/v1/sales/NONEXISTENT")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should create sale with multiple items")
    void shouldCreateSaleWithMultipleItems() {
        String saleJson = """
                {
                    "code": "SALEMULTI001",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 200.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "productName": "Batom Matte",
                            "quantity": 2,
                            "unitPrice": 35.00
                        },
                        {
                            "productCode": "PROD002",
                            "productName": "Base Líquida",
                            "quantity": 1,
                            "unitPrice": 89.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("items.size()", equalTo(2))
                .body("code", equalTo("SALEMULTI001"));
    }

    @Test
    @DisplayName("Should delete sale successfully")
    void shouldDeleteSaleSuccessfully() {

        String saleJson = """
                {
                    "code": "SALEDEL001",
                    "customerCode": "CUST001",
                    "customerName": "John Silva Santos",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 50.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "productName": "Batom Matte",
                            "quantity": 1,
                            "unitPrice": 35.00
                        }
                    ]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .post("/api/v1/sales");

        given()
                .when()
                .delete("/api/v1/sales/SALEDEL001")
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1/sales/SALEDEL001")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when creating sale with missing required fields")
    void shouldReturn400WhenMissingRequiredFields() {
        String invalidSaleJson = """
                {
                    "code": "SALEINV001"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidSaleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(400);
    }
}
