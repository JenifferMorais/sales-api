package com.sales.infrastructure.rest.report;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class ReportControllerTest {

    @Test
    void shouldGetMonthlyRevenue() {
        String requestBody = """
        {
            "referenceDate": "%s"
        }
        """.formatted(LocalDate.now().toString());

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/monthly-revenue")
        .then()
            .statusCode(200)
            .body("monthlyData", notNullValue());
    }

    @Test
    void shouldFailMonthlyRevenueWithInvalidDate() {
        String requestBody = """
        {
            "referenceDate": "invalid-date"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/monthly-revenue")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailMonthlyRevenueWithMissingReferenceDate() {
        String requestBody = "{}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/monthly-revenue")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldGetTopRevenueProducts() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/reports/top-revenue-products")
        .then()
            .statusCode(200)
            .body("products", notNullValue());
    }

    @Test
    void shouldGetOldestProducts() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/reports/oldest-products")
        .then()
            .statusCode(200)
            .body("products", notNullValue());
    }

    @Test
    void shouldGetNewCustomers() {
        String requestBody = """
        {
            "year": 2024
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/new-customers")
        .then()
            .statusCode(200)
            .body("customers", notNullValue());
    }

    @Test
    void shouldFailNewCustomersWithInvalidYear() {
        String requestBody = """
        {
            "year": "invalid"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/new-customers")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldFailNewCustomersWithMissingYear() {
        String requestBody = "{}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/new-customers")
        .then()
            .statusCode(400);
    }

    @Test
    void shouldGetNewCustomersForCurrentYear() {
        int currentYear = LocalDate.now().getYear();
        String requestBody = """
        {
            "year": %d
        }
        """.formatted(currentYear);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/new-customers")
        .then()
            .statusCode(200)
            .body("customers", notNullValue())
            .body("year", equalTo(currentYear));
    }

    @Test
    void shouldGetNewCustomersForPastYear() {
        int pastYear = LocalDate.now().getYear() - 1;
        String requestBody = """
        {
            "year": %d
        }
        """.formatted(pastYear);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/reports/new-customers")
        .then()
            .statusCode(200)
            .body("customers", notNullValue())
            .body("year", equalTo(pastYear));
    }
}
