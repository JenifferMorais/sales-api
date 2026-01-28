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
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 200.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "quantity": 2
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
                .body("id", notNullValue())
                .body("code", equalTo("SALETEST001"))
                .body("customerCode", equalTo("CUST001"))
                .body("paymentMethod", containsString("Dinheiro"))
                .body("items.size()", equalTo(1));
    }

    @Test
    @DisplayName("Should create sale with credit card payment successfully")
    void shouldCreateSaleWithCreditCard() {
        String saleJson = """
                {
                    "code": "SALETEST002",
                    "customerCode": "CUST001",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "CARTAO_CREDITO",
                    "cardNumber": "1234567890123456",
                    "items": [
                        {
                            "productCode": "PROD002",
                            "quantity": 1
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
                .body("paymentMethod", containsString("Cr"))
                .body("cardNumber", containsString("****"));
    }

    @Test
    @DisplayName("Should return 400 when creating sale with duplicate code")
    void shouldReturn400WhenDuplicateCode() {
        String saleJson = """
                {
                    "code": "SALEDUP001",
                    "customerCode": "CUST001",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 200.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "quantity": 1
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
                .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 when customer not found")
    void shouldReturn400WhenCustomerNotFound() {
        String saleJson = """
                {
                    "code": "SALETEST003",
                    "customerCode": "NONEXISTENT",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 200.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "quantity": 1
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
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Should return 400 when product not found")
    void shouldReturn400WhenProductNotFound() {
        String saleJson = """
                {
                    "code": "SALETEST004",
                    "customerCode": "CUST001",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 200.00,
                    "items": [
                        {
                            "productCode": "NONEXISTENT",
                            "quantity": 1
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
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Should get sale by code successfully")
    void shouldGetSaleByCode() {
        String saleJson = """
                {
                    "code": "SALEGET001",
                    "customerCode": "CUST001",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "PIX",
                    "items": [
                        {
                            "productCode": "PROD001",
                            "quantity": 1
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
                .get("/api/v1/sales/code/SALEGET001")
                .then()
                .statusCode(200)
                .body("code", equalTo("SALEGET001"))
                .body("customerCode", equalTo("CUST001"));
    }

    @Test
    @DisplayName("Should return 400 when sale not found")
    void shouldReturn400WhenSaleNotFound() {
        given()
                .when()
                .get("/api/v1/sales/code/NONEXISTENT")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should delete sale successfully")
    void shouldDeleteSaleSuccessfully() {
        String saleJson = """
                {
                    "code": "SALEDEL001",
                    "customerCode": "CUST001",
                    "sellerCode": "SELLER001",
                    "sellerName": "Vendedor Sistema",
                    "paymentMethod": "DINHEIRO",
                    "amountPaid": 200.00,
                    "items": [
                        {
                            "productCode": "PROD001",
                            "quantity": 1
                        }
                    ]
                }
                """;

        Number id = given()
                .contentType(ContentType.JSON)
                .body(saleJson)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1/sales/" + id.longValue())
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1/sales/" + id.longValue())
                .then()
                .statusCode(400);
    }
}
