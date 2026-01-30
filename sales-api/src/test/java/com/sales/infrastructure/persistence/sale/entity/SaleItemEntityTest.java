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

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("SaleItemEntity Tests")
class SaleItemEntityTest {

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
    @DisplayName("Should set quantity and unit price correctly")
    void shouldSetQuantityAndUnitPriceCorrectly() {
        SaleItemEntity item = new SaleItemEntity();
        item.setQuantity(3);
        item.setUnitPrice(BigDecimal.valueOf(50.00));

        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
    }

    @Test
    @Transactional
    @DisplayName("Should persist and retrieve sale item entity")
    void shouldPersistAndRetrieveSaleItemEntity() {
        SaleEntity sale = createTestSale();
        SaleItemEntity item = createTestItem();
        sale.addItem(item);

        entityManager.persist(sale);
        entityManager.flush();
        entityManager.clear();

        SaleEntity retrievedSale = entityManager.find(SaleEntity.class, sale.getId());
        SaleItemEntity retrievedItem = retrievedSale.getItems().get(0);

        assertThat(retrievedItem).isNotNull();
        assertThat(retrievedItem.getProductCode()).isEqualTo("PROD001");
        assertThat(retrievedItem.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should have correct table name")
    void shouldHaveCorrectTableName() {
        assertThat(SaleItemEntity.class.getAnnotation(jakarta.persistence.Table.class).name())
                .isEqualTo("sale_items");
    }

    @Test
    @Transactional
    @DisplayName("Should maintain bidirectional relationship with sale")
    void shouldMaintainBidirectionalRelationshipWithSale() {
        SaleEntity sale = createTestSale();
        SaleItemEntity item = createTestItem();

        sale.addItem(item);

        assertThat(item.getSale()).isEqualTo(sale);
        assertThat(sale.getItems()).contains(item);
    }

    @Test
    @Transactional
    @DisplayName("Should handle decimal prices correctly")
    void shouldHandleDecimalPricesCorrectly() {
        SaleItemEntity item = new SaleItemEntity();
        item.setQuantity(3);
        item.setUnitPrice(BigDecimal.valueOf(33.33));

        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(33.33));
    }

    @Test
    @Transactional
    @DisplayName("Should handle single quantity")
    void shouldHandleSingleQuantity() {
        SaleItemEntity item = new SaleItemEntity();
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.valueOf(100.00));

        assertThat(item.getQuantity()).isEqualTo(1);
        assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
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
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.valueOf(50.00));
        return item;
    }
}
