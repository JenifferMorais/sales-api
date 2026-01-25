package com.sales.infrastructure.rest.customer.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class CustomerControllerTest {

    @Test
    void shouldCreateCustomer() {
        String requestBody = """
        {
            "code": "CUST001",
            "fullName": "João Silva",
            "motherName": "Maria Silva",
            "cpf": "123.456.789-09",
            "rg": "123456789",
            "address": {
                "zipCode": "12345-678",
                "street": "Rua Teste",
                "number": "100",
                "complement": "Apto 1",
                "neighborhood": "Centro",
                "city": "São Paulo",
                "state": "SP"
            },
            "birthDate": "1990-01-01",
            "cellPhone": "(11) 98765-4321",
            "email": "joao@example.com"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/v1/customers")
        .then()
            .statusCode(201)
            .body("code", equalTo("CUST001"))
            .body("fullName", equalTo("João Silva"))
            .body("email", equalTo("joao@example.com"));
    }

    @Test
    void shouldFailCreateCustomerWithInvalidCPF() {
        String requestBody = """
        {
            "code": "CUST002",
            "fullName": "João Silva",
            "motherName": "Maria Silva",
            "cpf": "111.111.111-11",
            "rg": "123456789",
            "address": {
                "zipCode": "12345-678",
                "street": "Rua Teste",
                "number": "100",
                "complement": "",
                "neighborhood": "Centro",
                "city": "São Paulo",
                "state": "SP"
            },
            "birthDate": "1990-01-01",
            "cellPhone": "(11) 98765-4321",
            "email": "joao2@example.com"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/v1/customers")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailCreateCustomerWithMissingFields() {
        String requestBody = """
        {
            "code": "CUST003",
            "fullName": "João Silva"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/v1/customers")
        .then()
            .statusCode(400);
    }
}
