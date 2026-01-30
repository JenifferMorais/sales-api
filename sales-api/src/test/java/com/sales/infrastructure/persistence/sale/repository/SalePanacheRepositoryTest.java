package com.sales.infrastructure.persistence.sale.repository;

import com.sales.infrastructure.persistence.sale.entity.SaleEntity;
import com.sales.infrastructure.persistence.sale.entity.SaleItemEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("SalePanacheRepository Tests")
class SalePanacheRepositoryTest {

    @Inject
    SalePanacheRepository repository;

    @Inject
    EntityManager entityManager;

    private SaleEntity testSale;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar base de dados (delete sale_items first to avoid FK constraint violation)
        entityManager.createQuery("DELETE FROM SaleItemEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM SaleEntity").executeUpdate();

        // Criar venda de teste
        testSale = new SaleEntity();
        testSale.setCode("SALE0001");
        testSale.setCustomerCode("CUST0001");
        testSale.setCustomerName("João Silva");
        testSale.setSellerCode("SELLER0001");
        testSale.setSellerName("Maria Vendedora");
        testSale.setPaymentMethod("CREDIT_CARD");
        testSale.setCardNumber("1234****5678");
        testSale.setAmountPaid(BigDecimal.valueOf(200.00));

        SaleItemEntity item1 = new SaleItemEntity();
        item1.setProductCode("PROD001");
        item1.setProductName("Product 1");
        item1.setQuantity(2);
        item1.setUnitPrice(BigDecimal.valueOf(50.00));

        SaleItemEntity item2 = new SaleItemEntity();
        item2.setProductCode("PROD002");
        item2.setProductName("Product 2");
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(100.00));

        testSale.addItem(item1);
        testSale.addItem(item2);

        repository.persist(testSale);
    }

    @Test
    @DisplayName("Should find sale by code")
    void shouldFindSaleByCode() {
        Optional<SaleEntity> result = repository.findByCode("SALE0001");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("SALE0001");
        assertThat(result.get().getCustomerName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Should return empty when sale not found by code")
    void shouldReturnEmptyWhenSaleNotFoundByCode() {
        Optional<SaleEntity> result = repository.findByCode("SALE9999");

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should find sales by customer code")
    void shouldFindSalesByCustomerCode() {
        List<SaleEntity> result = repository.findByCustomerCode("CUST0001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerCode()).isEqualTo("CUST0001");
    }

    @Test
    @Transactional
    @DisplayName("Should find sales by seller code")
    void shouldFindSalesBySellerCode() {
        List<SaleEntity> result = repository.findBySellerCode("SELLER0001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSellerCode()).isEqualTo("SELLER0001");
    }

    @Test
    @Transactional
    @DisplayName("Should find sales by payment method")
    void shouldFindSalesByPaymentMethod() {
        List<SaleEntity> result = repository.findByPaymentMethod("CREDIT_CARD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPaymentMethod()).isEqualTo("CREDIT_CARD");
    }

    @Test
    @Transactional
    @DisplayName("Should find sales by date range")
    void shouldFindSalesByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        List<SaleEntity> result = repository.findByDateRange(start, end);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should check if sale exists by code")
    void shouldCheckIfSaleExistsByCode() {
        boolean exists = repository.existsByCode("SALE0001");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when sale does not exist by code")
    void shouldReturnFalseWhenSaleDoesNotExistByCode() {
        boolean exists = repository.existsByCode("SALE9999");

        assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("Should get monthly revenue")
    void shouldGetMonthlyRevenue() {
        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now().plusMonths(1);

        List<Map<String, Object>> result = repository.getMonthlyRevenue(start, end);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).containsKeys("month", "year", "subtotal");
    }

    @Test
    @Transactional
    @DisplayName("Should get top revenue products")
    void shouldGetTopRevenueProducts() {
        List<Map<String, Object>> result = repository.getTopRevenueProducts(10);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).containsKeys("productCode", "productName", "salePrice", "totalRevenue");
    }

    @Test
    @Transactional
    @DisplayName("Should search sales with filter")
    void shouldSearchSalesWithFilter() {
        List<SaleEntity> result = repository.search("Silva", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).contains("Silva");
    }

    @Test
    @Transactional
    @DisplayName("Should search sales without filter")
    void shouldSearchSalesWithoutFilter() {
        List<SaleEntity> result = repository.search("", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should search sales with pagination")
    void shouldSearchSalesWithPagination() {
        repository.persist(createTestSale("SALE0002", "CUST0002", "Pedro Santos"));
        repository.persist(createTestSale("SALE0003", "CUST0003", "Ana Costa"));

        List<SaleEntity> page1 = repository.search("", 0, 2);
        List<SaleEntity> page2 = repository.search("", 1, 2);

        assertThat(page1).hasSize(2);
        assertThat(page2).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should count search results with filter")
    void shouldCountSearchResultsWithFilter() {
        long count = repository.countSearch("Silva");

        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Should count search results without filter")
    void shouldCountSearchResultsWithoutFilter() {
        long count = repository.countSearch("");

        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Should search by code in filter")
    void shouldSearchByCodeInFilter() {
        List<SaleEntity> result = repository.search("SALE0001", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("SALE0001");
    }

    @Test
    @Transactional
    @DisplayName("Should search by customer code in filter")
    void shouldSearchByCustomerCodeInFilter() {
        List<SaleEntity> result = repository.search("CUST0001", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerCode()).isEqualTo("CUST0001");
    }

    @Test
    @Transactional
    @DisplayName("Should search by seller name in filter")
    void shouldSearchBySellerNameInFilter() {
        List<SaleEntity> result = repository.search("Maria", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSellerName()).contains("Maria");
    }

    @Test
    @Transactional
    @DisplayName("Should return empty list when search has no results")
    void shouldReturnEmptyListWhenSearchHasNoResults() {
        List<SaleEntity> result = repository.search("NonExistent", 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should return zero count when search has no results")
    void shouldReturnZeroCountWhenSearchHasNoResults() {
        long count = repository.countSearch("NonExistent");

        assertThat(count).isZero();
    }

    @Test
    @Transactional
    @DisplayName("Should limit top revenue products by parameter")
    void shouldLimitTopRevenueProductsByParameter() {
        List<Map<String, Object>> result = repository.getTopRevenueProducts(1);

        assertThat(result).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should calculate total revenue in top products")
    void shouldCalculateTotalRevenueInTopProducts() {
        List<Map<String, Object>> result = repository.getTopRevenueProducts(10);

        assertThat(result).isNotEmpty();
        BigDecimal totalRevenue = (BigDecimal) result.get(0).get("totalRevenue");
        assertThat(totalRevenue).isPositive();
    }

    // Helper method
    private SaleEntity createTestSale(String code, String customerCode, String customerName) {
        SaleEntity sale = new SaleEntity();
        sale.setCode(code);
        sale.setCustomerCode(customerCode);
        sale.setCustomerName(customerName);
        sale.setSellerCode("SELLER0001");
        sale.setSellerName("Maria Vendedora");
        sale.setPaymentMethod("CREDIT_CARD");
        sale.setCardNumber("1234****5678");
        sale.setAmountPaid(BigDecimal.valueOf(100.00));

        SaleItemEntity item = new SaleItemEntity();
        item.setProductCode("PROD001");
        item.setProductName("Product 1");
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.valueOf(100.00));

        sale.addItem(item);
        return sale;
    }
}
