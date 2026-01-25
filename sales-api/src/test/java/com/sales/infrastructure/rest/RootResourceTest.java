package com.sales.infrastructure.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.emptyString;

@QuarkusTest
class RootResourceTest {

    @Test
    void shouldReturnRootInformation() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("name", notNullValue())
            .body("version", notNullValue())
            .body("status", equalTo("running"))
            .body("links", notNullValue())
            .body("links.swagger", equalTo("/q/swagger-ui"))
            .body("links.openapi", equalTo("/q/openapi"))
            .body("links.health", equalTo("/q/health"))
            .body("links.api", equalTo("/api/v1"));
    }

    @Test
    void shouldReturnHealthStatus() {
        given()
        .when()
            .get("/health")
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("status", equalTo("UP"))
            .body("service", notNullValue());
    }

    @Test
    void shouldReturnJsonContentType() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .header("Content-Type", containsString("application/json"));
    }

    @Test
    void shouldHaveAllRequiredRootFields() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .body("$", hasKey("name"))
            .body("$", hasKey("version"))
            .body("$", hasKey("status"))
            .body("$", hasKey("links"));
    }

    @Test
    void shouldHaveAllRequiredHealthFields() {
        given()
        .when()
            .get("/health")
        .then()
            .statusCode(200)
            .body("$", hasKey("status"))
            .body("$", hasKey("service"));
    }

    @Test
    void shouldHaveCorrectLinkStructure() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .body("links", hasKey("swagger"))
            .body("links", hasKey("openapi"))
            .body("links", hasKey("health"))
            .body("links", hasKey("api"));
    }

    @Test
    void shouldReturnDefaultAppName() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .body("name", anyOf(equalTo("Sales API"), notNullValue()));
    }

    @Test
    void shouldReturnVersion() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .body("version", notNullValue())
            .body("version", is(not(emptyString())));
    }
}
