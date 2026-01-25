package com.sales.infrastructure.rest.dashboard;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class DashboardControllerTest {

    @Test
    void shouldGetDashboardStats() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/dashboard/stats")
        .then()
            .statusCode(200)
            .body("totalSales", notNullValue())
            .body("totalRevenue", notNullValue())
            .body("totalCustomers", notNullValue())
            .body("totalProducts", notNullValue())
            .body("salesVariation", notNullValue())
            .body("revenueVariation", notNullValue())
            .body("customersVariation", notNullValue());
    }

    @Test
    void shouldGetChartDataWithDefaultRange() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/dashboard/chart-data")
        .then()
            .statusCode(200)
            .body("chartData", notNullValue());
    }

    @Test
    void shouldGetChartDataForWeek() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("range", "week")
        .when()
            .get("/api/v1/dashboard/chart-data")
        .then()
            .statusCode(200)
            .body("chartData", notNullValue())
            .body("chartData.size()", equalTo(7)); // 7 days
    }

    @Test
    void shouldGetChartDataForMonth() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("range", "month")
        .when()
            .get("/api/v1/dashboard/chart-data")
        .then()
            .statusCode(200)
            .body("chartData", notNullValue());
    }

    @Test
    void shouldGetChartDataForQuarter() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("range", "quarter")
        .when()
            .get("/api/v1/dashboard/chart-data")
        .then()
            .statusCode(200)
            .body("chartData", notNullValue())
            .body("chartData.size()", equalTo(3)); // 3 months
    }

    @Test
    void shouldGetChartDataForYear() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("range", "year")
        .when()
            .get("/api/v1/dashboard/chart-data")
        .then()
            .statusCode(200)
            .body("chartData", notNullValue())
            .body("chartData.size()", equalTo(12)); // 12 months
    }

    @Test
    void shouldFailWithInvalidRange() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("range", "invalid")
        .when()
            .get("/api/v1/dashboard/chart-data")
        .then()
            .statusCode(400)
            .body("error", containsString("Período inválido"));
    }

    @Test
    void shouldGetRecentSalesWithDefaultLimit() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/dashboard/recent-sales")
        .then()
            .statusCode(200)
            .body("sales", notNullValue());
    }

    @Test
    void shouldGetRecentSalesWithCustomLimit() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("limit", 10)
        .when()
            .get("/api/v1/dashboard/recent-sales")
        .then()
            .statusCode(200)
            .body("sales", notNullValue());
    }

    @Test
    void shouldUseDefaultLimitWhenInvalidLimit() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("limit", -1)
        .when()
            .get("/api/v1/dashboard/recent-sales")
        .then()
            .statusCode(200)
            .body("sales", notNullValue());
    }

    @Test
    void shouldUseDefaultLimitWhenLimitTooHigh() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("limit", 100)
        .when()
            .get("/api/v1/dashboard/recent-sales")
        .then()
            .statusCode(200)
            .body("sales", notNullValue());
    }
}
