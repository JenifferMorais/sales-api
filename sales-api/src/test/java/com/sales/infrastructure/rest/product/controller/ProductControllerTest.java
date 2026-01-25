package com.sales.infrastructure.rest.product.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("ProductController Integration Tests")
class ProductControllerTest {

    @Test
    @DisplayName("Should create product successfully with valid data")
    void shouldCreateProductSuccessfully() {
        String productJson = """
                {
                    "code": "PRODTEST001",
                    "name": "Batom Matte Test",
                    "type": "Lábios",
                    "details": "Batom matte de longa duração para testes",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .body("code", equalTo("PRODTEST001"))
                .body("name", equalTo("Batom Matte Test"))
                .body("type", equalTo("Lábios"))
                .body("salePrice", is(35.00f))
                .body("stockQuantity", equalTo(100));
    }

    @Test
    @DisplayName("Should return 400 when creating product with duplicate code")
    void shouldReturn400WhenDuplicateCode() {

        String productJson = """
                {
                    "code": "PRODDUP001",
                    "name": "Produto Duplicado",
                    "type": "Lábios",
                    "details": "Produto para teste de duplicação",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(400)
                .body("message", containsString("já existe"));
    }

    @Test
    @DisplayName("Should return 400 when creating product with missing required fields")
    void shouldReturn400WhenMissingRequiredFields() {
        String invalidProductJson = """
                {
                    "code": "PRODINV001"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidProductJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 when creating product with negative price")
    void shouldReturn400WhenNegativePrice() {
        String invalidProductJson = """
                {
                    "code": "PRODINV002",
                    "name": "Produto Inválido",
                    "type": "Lábios",
                    "details": "Produto com preço negativo",
                    "weight": 0.050,
                    "purchasePrice": -10.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidProductJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should list all products successfully")
    void shouldListAllProducts() {
        given()
                .when()
                .get("/api/v1/products")
                .then()
                .statusCode(200)
                .body("$", notNullValue())
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Should get product by code successfully")
    void shouldGetProductByCode() {

        String productJson = """
                {
                    "code": "PRODGET001",
                    "name": "Produto Get Test",
                    "type": "Lábios",
                    "details": "Produto para teste de busca",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .post("/api/v1/products");

        given()
                .when()
                .get("/api/v1/products/PRODGET001")
                .then()
                .statusCode(200)
                .body("code", equalTo("PRODGET001"))
                .body("name", equalTo("Produto Get Test"));
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() {
        given()
                .when()
                .get("/api/v1/products/NONEXISTENT")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {

        String productJson = """
                {
                    "code": "PRODUPD001",
                    "name": "Produto Original",
                    "type": "Lábios",
                    "details": "Descrição original",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .post("/api/v1/products");

        String updateJson = """
                {
                    "name": "Produto Atualizado",
                    "type": "Lábios",
                    "details": "Descrição atualizada",
                    "weight": 0.050,
                    "purchasePrice": 20.00,
                    "salePrice": 40.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 150
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1/products/PRODUPD001")
                .then()
                .statusCode(200)
                .body("name", equalTo("Produto Atualizado"))
                .body("salePrice", is(40.00f))
                .body("stockQuantity", equalTo(150));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {

        String productJson = """
                {
                    "code": "PRODDEL001",
                    "name": "Produto Para Deletar",
                    "type": "Lábios",
                    "details": "Produto para teste de deleção",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .post("/api/v1/products");

        given()
                .when()
                .delete("/api/v1/products/PRODDEL001")
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1/products/PRODDEL001")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when creating product with sale price less than purchase price")
    void shouldReturn400WhenSalePriceLessThanPurchasePrice() {
        String invalidProductJson = """
                {
                    "code": "PRODINV003",
                    "name": "Produto Inválido",
                    "type": "Lábios",
                    "details": "Produto com preço de venda menor que compra",
                    "weight": 0.050,
                    "purchasePrice": 50.00,
                    "salePrice": 30.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele",
                    "stockQuantity": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidProductJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(400);
    }
}
