package com.sales.infrastructure.rest.sale.controller;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("SaleController Integration Tests")
class SaleControllerTest {

    private static final AtomicInteger CUSTOMER_COUNTER = new AtomicInteger();
    private static final AtomicInteger PRODUCT_COUNTER = new AtomicInteger();
    private static final AtomicInteger SALE_COUNTER = new AtomicInteger();
    private static final String SELLER_CODE = "SELLER001";
    private static final String SELLER_NAME = "Vendedor Sistema";

    @Inject
    CustomerRepository customerRepository;

    private String createCustomer() {
        int idx = CUSTOMER_COUNTER.incrementAndGet();
        String code = String.format("SALECUST%04d", idx);
        String cpfDigits = buildCpfDigits(idx);
        String rg = String.format("%07d", 1000000 + idx);
        Document document = new Document(cpfDigits, rg);
        Address address = new Address(
                "00000000",
                "Rua Teste " + idx,
                String.valueOf(100 + idx),
                "Apto " + idx,
                "Centro",
                "Cidade Teste",
                "SP"
        );
        String cellPhone = String.format("119%08d", idx);
        String email = String.format("sale.customer.%d@example.com", idx);
        Customer customer = new Customer(
                code,
                "Cliente Teste " + idx,
                "Mãe Teste " + idx,
                document,
                address,
                LocalDate.of(1990, 1, 1),
                cellPhone,
                email
        );
        customerRepository.save(customer);
        return code;
    }

    private String buildCpfDigits(int idx) {
        int[] digits = new int[11];
        String nineDigits = String.format("%09d", 100_000_000 + idx);
        for (int i = 0; i < 9; i++) {
            digits[i] = nineDigits.charAt(i) - '0';
        }
        digits[9] = calculateCpfDigit(digits, 10);
        digits[10] = calculateCpfDigit(digits, 11);
        return digitsToString(digits);
    }
    private int calculateCpfDigit(int[] digits, int weightStart) {
        int sum = 0;
        int limit = weightStart - 1;
        for (int i = 0; i < limit; i++) {
            sum += digits[i] * (weightStart - i);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private String digitsToString(int[] digits) {
        StringBuilder builder = new StringBuilder(11);
        for (int digit : digits) {
            builder.append(digit);
        }
        return builder.toString();
    }

    private String createProduct() {
        int idx = PRODUCT_COUNTER.incrementAndGet();
        String code = String.format("SALEPROD%04d", idx);
        Map<String, Object> request = new HashMap<>();
        request.put("code", code);
        request.put("name", "Produto Sale Teste " + idx);
        request.put("type", "LIPS");
        request.put("details", "Descrição de teste " + idx);
        request.put("weight", 0.050);
        request.put("purchasePrice", 18.00);
        request.put("salePrice", 35.00);
        request.put("height", 8.00);
        request.put("width", 2.00);
        request.put("depth", 2.00);
        request.put("destinationVehicle", "Todos os tipos de pele");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201);

        return code;
    }

    private Map<String, Object> buildSalePayload(String saleCode,
                                                 String customerCode,
                                                 String paymentMethod,
                                                 String productCode,
                                                 BigDecimal amountPaid,
                                                 String cardNumber) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", saleCode);
        payload.put("customerCode", customerCode);
        payload.put("sellerCode", SELLER_CODE);
        payload.put("sellerName", SELLER_NAME);
        payload.put("paymentMethod", paymentMethod);
        if (cardNumber != null) {
            payload.put("cardNumber", cardNumber);
        }
        if (amountPaid != null) {
            payload.put("amountPaid", amountPaid);
        }
        Map<String, Object> item = new HashMap<>();
        item.put("productCode", productCode);
        item.put("quantity", 1);
        payload.put("items", Collections.singletonList(item));
        return payload;
    }

    private String nextSaleCode() {
        return "SALE_TEST_" + SALE_COUNTER.incrementAndGet();
    }


    @Test
    @DisplayName("Should create sale with cash payment successfully")
    void shouldCreateSaleWithCashPayment() {
        String customerCode = createCustomer();
        String productCode = createProduct();
        String saleCode = nextSaleCode();
        Map<String, Object> payload = buildSalePayload(
                saleCode,
                customerCode,
                "DINHEIRO",
                productCode,
                BigDecimal.valueOf(200.00),
                null
        );

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", equalTo(saleCode))
                .body("customerCode", equalTo(customerCode))
                .body("paymentMethod", containsString("Dinheiro"))
                .body("items.size()", equalTo(1));
    }

    @Test
    @DisplayName("Should create sale with credit card payment successfully")
    void shouldCreateSaleWithCreditCard() {
        String customerCode = createCustomer();
        String productCode = createProduct();
        String saleCode = nextSaleCode();
        Map<String, Object> payload = buildSalePayload(
                saleCode,
                customerCode,
                "CARTAO_CREDITO",
                productCode,
                null,
                "1234567890123456"
        );

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("code", equalTo(saleCode))
                .body("paymentMethod", containsString("Cr"))
                .body("cardNumber", containsString("****"));
    }

    @Test
    @DisplayName("Should return 400 when creating sale with duplicate code")
    void shouldReturn400WhenDuplicateCode() {
        String customerCode = createCustomer();
        String productCode = createProduct();
        String saleCode = nextSaleCode();
        Map<String, Object> payload = buildSalePayload(
                saleCode,
                customerCode,
                "DINHEIRO",
                productCode,
                BigDecimal.valueOf(200.00),
                null
        );

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/api/v1/sales");

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should return 400 when customer not found")
    void shouldReturn400WhenCustomerNotFound() {
        String productCode = createProduct();
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
                            "productCode": "%s",
                            "quantity": 1
                        }
                    ]
                }
                """.formatted(productCode);

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
        String customerCode = createCustomer();
        String saleJson = """
                {
                    "code": "SALETEST004",
                    "customerCode": "%s",
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
                """.formatted(customerCode);

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
        String customerCode = createCustomer();
        String productCode = createProduct();
        String saleCode = nextSaleCode();
        Map<String, Object> payload = buildSalePayload(
                saleCode,
                customerCode,
                "PIX",
                productCode,
                null,
                null
        );

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/api/v1/sales");

        given()
                .when()
                .get("/api/v1/sales/code/" + saleCode)
                .then()
                .statusCode(200)
                .body("code", equalTo(saleCode))
                .body("customerCode", equalTo(customerCode));
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
        String customerCode = createCustomer();
        String productCode = createProduct();
        String saleCode = nextSaleCode();
        Map<String, Object> payload = buildSalePayload(
                saleCode,
                customerCode,
                "DINHEIRO",
                productCode,
                BigDecimal.valueOf(200.00),
                null
        );

        Number id = given()
                .contentType(ContentType.JSON)
                .body(payload)
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
