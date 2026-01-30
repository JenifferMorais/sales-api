package com.sales.infrastructure.persistence.sale.entity;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("SaleEntity Tests")
class SaleEntityTest {

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void setUp() {
        entityManager.createQuery("DELETE FROM SaleItemEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM SaleEntity").executeUpdate();
    }

    @AfterEach
    @Transactional
    void tearDown() {
        entityManager.createQuery("DELETE FROM SaleItemEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM SaleEntity").executeUpdate();
    }

    @Test
    @Transactional
    @DisplayName("Should set createdAt on persist")
    void shouldSetCreatedAtOnPersist() {
        SaleEntity entity = createTestSale();

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @Transactional
    @DisplayName("Should persist and retrieve sale entity")
    void shouldPersistAndRetrieveSaleEntity() {
        SaleEntity entity = createTestSale();

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        SaleEntity retrieved = entityManager.find(SaleEntity.class, entity.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCode()).isEqualTo("SALE001");
        assertThat(retrieved.getCustomerCode()).isEqualTo("CUST001");
    }

    @Test
    @Transactional
    @DisplayName("Should add item to sale")
    void shouldAddItemToSale() {
        SaleEntity sale = createTestSale();
        SaleItemEntity item = createTestItem();

        sale.addItem(item);

        assertThat(sale.getItems()).hasSize(1);
        assertThat(item.getSale()).isEqualTo(sale);
    }

    @Test
    @Transactional
    @DisplayName("Should have items with correct quantities and prices")
    void shouldHaveItemsWithCorrectQuantitiesAndPrices() {
        SaleEntity sale = createTestSale();

        SaleItemEntity item1 = createTestItem();
        item1.setQuantity(2);
        item1.setUnitPrice(BigDecimal.valueOf(50.00));

        SaleItemEntity item2 = createTestItem();
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(100.00));

        sale.addItem(item1);
        sale.addItem(item2);

        assertThat(sale.getItems()).hasSize(2);
        assertThat(sale.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(sale.getItems().get(1).getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Should have correct table name")
    void shouldHaveCorrectTableName() {
        assertThat(SaleEntity.class.getAnnotation(jakarta.persistence.Table.class).name())
                .isEqualTo("sales");
    }

    @Test
    @Transactional
    @DisplayName("Should cascade persist items")
    void shouldCascadePersistItems() {
        SaleEntity sale = createTestSale();
        SaleItemEntity item = createTestItem();
        sale.addItem(item);

        entityManager.persist(sale);
        entityManager.flush();
        entityManager.clear();

        SaleEntity retrieved = entityManager.find(SaleEntity.class, sale.getId());

        assertThat(retrieved.getItems()).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should remove all items")
    void shouldRemoveAllItems() {
        SaleEntity sale = createTestSale();
        sale.addItem(createTestItem());
        sale.addItem(createTestItem());

        sale.getItems().clear();

        assertThat(sale.getItems()).isEmpty();
    }

    private SaleEntity createTestSale() {
        SaleEntity entity = new SaleEntity();
        entity.setCode("SALE001");
        entity.setCustomerCode("CUST001");
        entity.setCustomerName("João Silva");
        entity.setSellerCode("SELLER001");
        entity.setSellerName("Maria Vendedora");
        entity.setPaymentMethod("CREDIT_CARD");
        entity.setCardNumber("1234****5678");
        entity.setAmountPaid(BigDecimal.valueOf(200.00));
        return entity;
    }

    private SaleItemEntity createTestItem() {
        SaleItemEntity item = new SaleItemEntity();
        item.setProductCode("PROD001");
        item.setProductName("Product 1");
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.valueOf(100.00));
        return item;
    }
}
