package com.sales.infrastructure.rest.report;

import com.sales.application.report.usecase.GetMonthlyRevenueUseCase;
import com.sales.application.report.usecase.GetNewCustomersUseCase;
import com.sales.application.report.usecase.GetOldestProductsUseCase;
import com.sales.application.report.usecase.GetTopRevenueProductsUseCase;
import com.sales.infrastructure.rest.report.dto.MonthlyRevenueResponse;
import com.sales.infrastructure.rest.report.dto.NewCustomersResponse;
import com.sales.infrastructure.rest.report.dto.OldestProductsResponse;
import com.sales.infrastructure.rest.report.dto.TopRevenueProductsResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;

@QuarkusTest
class ReportControllerTest {

    @InjectMock
    GetMonthlyRevenueUseCase getMonthlyRevenueUseCase;

    @InjectMock
    GetTopRevenueProductsUseCase getTopRevenueProductsUseCase;

    @InjectMock
    GetOldestProductsUseCase getOldestProductsUseCase;

    @InjectMock
    GetNewCustomersUseCase getNewCustomersUseCase;

    @Test
    void shouldGetMonthlyRevenue() {
        when(getMonthlyRevenueUseCase.execute(org.mockito.ArgumentMatchers.any()))
                .thenReturn(MonthlyRevenueResponse.builder()
                        .monthlyData(List.of())
                        .totalRevenue(BigDecimal.ZERO)
                        .totalTax(BigDecimal.ZERO)
                        .grandTotal(BigDecimal.ZERO)
                        .build());

        String requestBody = """
                {
                    "referenceDate": "%s"
                }
                """.formatted(LocalDate.now());

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
    void shouldGetTopRevenueProducts() {
        when(getTopRevenueProductsUseCase.execute())
                .thenReturn(TopRevenueProductsResponse.builder().products(List.of()).build());

        given()
                .when()
                .get("/api/reports/top-revenue-products")
                .then()
                .statusCode(200)
                .body("products", notNullValue());
    }

    @Test
    void shouldGetOldestProducts() {
        when(getOldestProductsUseCase.execute())
                .thenReturn(OldestProductsResponse.builder().products(List.of()).build());

        given()
                .when()
                .get("/api/reports/oldest-products")
                .then()
                .statusCode(200)
                .body("products", notNullValue());
    }

    @Test
    void shouldGetNewCustomers() {
        when(getNewCustomersUseCase.execute(org.mockito.ArgumentMatchers.anyInt()))
                .thenReturn(NewCustomersResponse.builder().customers(List.of()).build());

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
}

