package com.sales.infrastructure.rest.customer.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
class CustomerControllerTest {

    @Test
    void shouldCreateCustomer() {
        String requestBody = """
                {
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
                    "email": "joao.cust011@example.com"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", matchesPattern("CUST\\d{4}"))
                .body("fullName", equalTo("João Silva"))
                .body("email", equalTo("joao.cust011@example.com"));
    }

    @Test
    void shouldFailCreateCustomerWithInvalidCPF() {
        String requestBody = """
                {
                    "code": "CUST012",
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
                    "email": "joao.cust012@example.com"
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
                    "code": "CUST013",
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
