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
                    "type": "LIPS",
                    "details": "Batom matte de longa duração para testes",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", equalTo("PRODTEST001"))
                .body("name", equalTo("Batom Matte Test"))
                .body("stockQuantity", equalTo(0));
    }

    @Test
    @DisplayName("Should get product by code successfully")
    void shouldGetProductByCode() {
        String productJson = """
                {
                    "code": "PRODGET001",
                    "name": "Produto Get Test",
                    "type": "LIPS",
                    "details": "Produto para teste de busca",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele"
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
                .when()
                .get("/api/v1/products/code/PRODGET001")
                .then()
                .statusCode(200)
                .body("code", equalTo("PRODGET001"))
                .body("name", equalTo("Produto Get Test"));
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        String productJson = """
                {
                    "code": "PRODUPD001",
                    "name": "Produto Original",
                    "type": "LIPS",
                    "details": "Descrição original",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele"
                }
                """;

        Number id = given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateJson = """
                {
                    "name": "Produto Atualizado",
                    "type": "LIPS",
                    "details": "Descrição atualizada",
                    "weight": 0.050,
                    "purchasePrice": 20.00,
                    "salePrice": 40.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when()
                .put("/api/v1/products/" + id.longValue())
                .then()
                .statusCode(200)
                .body("name", equalTo("Produto Atualizado"))
                .body("salePrice", is(40.00f));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        String productJson = """
                {
                    "code": "PRODDEL001",
                    "name": "Produto Para Deletar",
                    "type": "LIPS",
                    "details": "Produto para teste de deleção",
                    "weight": 0.050,
                    "purchasePrice": 18.00,
                    "salePrice": 35.00,
                    "height": 8.00,
                    "width": 2.00,
                    "depth": 2.00,
                    "destinationVehicle": "Todos os tipos de pele"
                }
                """;

        Number id = given()
                .contentType(ContentType.JSON)
                .body(productJson)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/api/v1/products/" + id.longValue())
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v1/products/" + id.longValue())
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 when product not found")
    void shouldReturn400WhenProductNotFound() {
        given()
                .when()
                .get("/api/v1/products/99999999")
                .then()
                .statusCode(400);
    }
}
