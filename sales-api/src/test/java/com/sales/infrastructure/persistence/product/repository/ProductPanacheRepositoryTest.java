package com.sales.infrastructure.persistence.product.repository;

import com.sales.infrastructure.persistence.product.entity.ProductEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("ProductPanacheRepository Tests")
class ProductPanacheRepositoryTest {

    @Inject
    ProductPanacheRepository repository;

    private ProductEntity testProduct;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar base de dados
        repository.deleteAll();

        // Criar produto de teste
        testProduct = new ProductEntity();
        testProduct.setCode("PROD0001");
        testProduct.setName("Product Test");
        testProduct.setType("INDUSTRIAL");
        testProduct.setDetails("Test product details");
        testProduct.setWeight(BigDecimal.valueOf(5.5));
        testProduct.setPurchasePrice(BigDecimal.valueOf(100.00));
        testProduct.setSalePrice(BigDecimal.valueOf(150.00));
        testProduct.setHeight(BigDecimal.valueOf(10.0));
        testProduct.setWidth(BigDecimal.valueOf(20.0));
        testProduct.setDepth(BigDecimal.valueOf(30.0));
        testProduct.setDestinationVehicle("Truck");
        testProduct.setStockQuantity(100);

        repository.persist(testProduct);
    }

    @Test
    @DisplayName("Should find product by code")
    void shouldFindProductByCode() {
        Optional<ProductEntity> result = repository.findByCode("PROD0001");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("PROD0001");
        assertThat(result.get().getName()).isEqualTo("Product Test");
    }

    @Test
    @DisplayName("Should return empty when product not found by code")
    void shouldReturnEmptyWhenProductNotFoundByCode() {
        Optional<ProductEntity> result = repository.findByCode("PROD9999");

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should find all products sorted by name")
    void shouldFindAllProductsSortedByName() {
        repository.persist(createTestProduct("PROD0002", "Zebra Product"));
        repository.persist(createTestProduct("PROD0003", "Alpha Product"));

        List<ProductEntity> result = repository.findAllSortedByName();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("Alpha Product");
        assertThat(result.get(2).getName()).isEqualTo("Zebra Product");
    }

    @Test
    @Transactional
    @DisplayName("Should find products by type")
    void shouldFindProductsByType() {
        repository.persist(createTestProduct("PROD0002", "Another Industrial", "INDUSTRIAL"));
        repository.persist(createTestProduct("PROD0003", "Automotive Product", "AUTOMOTIVE"));

        List<ProductEntity> result = repository.findByType("INDUSTRIAL");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getType().equals("INDUSTRIAL"));
    }

    @Test
    @Transactional
    @DisplayName("Should find products by name containing")
    void shouldFindProductsByNameContaining() {
        List<ProductEntity> result = repository.findByNameContaining("Test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");
    }

    @Test
    @Transactional
    @DisplayName("Should find products by name containing case insensitive")
    void shouldFindProductsByNameContainingCaseInsensitive() {
        List<ProductEntity> result = repository.findByNameContaining("test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).containsIgnoringCase("test");
    }

    @Test
    @DisplayName("Should check if product exists by code")
    void shouldCheckIfProductExistsByCode() {
        boolean exists = repository.existsByCode("PROD0001");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when product does not exist by code")
    void shouldReturnFalseWhenProductDoesNotExistByCode() {
        boolean exists = repository.existsByCode("PROD9999");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find last product code")
    void shouldFindLastProductCode() {
        Optional<String> lastCode = repository.findLastCode();

        assertThat(lastCode).isPresent();
        assertThat(lastCode.get()).isEqualTo("PROD0001");
    }

    @Test
    @Transactional
    @DisplayName("Should find last code among multiple products")
    void shouldFindLastCodeAmongMultipleProducts() {
        repository.persist(createTestProduct("PROD0002", "Product 2"));
        repository.persist(createTestProduct("PROD0003", "Product 3"));

        Optional<String> lastCode = repository.findLastCode();

        assertThat(lastCode).isPresent();
        assertThat(lastCode.get()).isEqualTo("PROD0003");
    }

    @Test
    @DisplayName("Should count all products")
    void shouldCountAllProducts() {
        long count = repository.countAll();

        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Should find oldest products with limit")
    void shouldFindOldestProductsWithLimit() {
        repository.persist(createTestProduct("PROD0002", "Product 2"));
        repository.persist(createTestProduct("PROD0003", "Product 3"));

        List<ProductEntity> result = repository.findOldestProducts(2);

        assertThat(result).hasSize(2);
    }

    @Test
    @Transactional
    @DisplayName("Should search products with filter")
    void shouldSearchProductsWithFilter() {
        List<ProductEntity> result = repository.search("Test", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");
    }

    @Test
    @Transactional
    @DisplayName("Should search products without filter")
    void shouldSearchProductsWithoutFilter() {
        List<ProductEntity> result = repository.search("", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should search products with pagination")
    void shouldSearchProductsWithPagination() {
        repository.persist(createTestProduct("PROD0002", "Product 2"));
        repository.persist(createTestProduct("PROD0003", "Product 3"));

        List<ProductEntity> page1 = repository.search("", 0, 2);
        List<ProductEntity> page2 = repository.search("", 1, 2);

        assertThat(page1).hasSize(2);
        assertThat(page2).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should count search results with filter")
    void shouldCountSearchResultsWithFilter() {
        long count = repository.countSearch("Test");

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
        List<ProductEntity> result = repository.search("PROD0001", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("PROD0001");
    }

    @Test
    @Transactional
    @DisplayName("Should search by details in filter")
    void shouldSearchByDetailsInFilter() {
        List<ProductEntity> result = repository.search("details", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDetails()).containsIgnoringCase("details");
    }

    @Test
    @Transactional
    @DisplayName("Should search by type in filter")
    void shouldSearchByTypeInFilter() {
        List<ProductEntity> result = repository.search("INDUSTRIAL", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("INDUSTRIAL");
    }

    @Test
    @Transactional
    @DisplayName("Should return empty list when search has no results")
    void shouldReturnEmptyListWhenSearchHasNoResults() {
        List<ProductEntity> result = repository.search("NonExistent", 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should return zero count when search has no results")
    void shouldReturnZeroCountWhenSearchHasNoResults() {
        long count = repository.countSearch("NonExistent");

        assertThat(count).isZero();
    }

    // Helper methods
    private ProductEntity createTestProduct(String code, String name) {
        return createTestProduct(code, name, "INDUSTRIAL");
    }

    private ProductEntity createTestProduct(String code, String name, String type) {
        ProductEntity product = new ProductEntity();
        product.setCode(code);
        product.setName(name);
        product.setType(type);
        product.setDetails("Product details");
        product.setWeight(BigDecimal.valueOf(5.0));
        product.setPurchasePrice(BigDecimal.valueOf(100.00));
        product.setSalePrice(BigDecimal.valueOf(150.00));
        product.setHeight(BigDecimal.valueOf(10.0));
        product.setWidth(BigDecimal.valueOf(20.0));
        product.setDepth(BigDecimal.valueOf(30.0));
        product.setDestinationVehicle("Truck");
        product.setStockQuantity(50);
        return product;
    }
}
