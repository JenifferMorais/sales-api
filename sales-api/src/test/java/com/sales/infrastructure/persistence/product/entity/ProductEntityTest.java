package com.sales.infrastructure.persistence.product.entity;

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
@DisplayName("ProductEntity Tests")
class ProductEntityTest {

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void setUp() {
        entityManager.createQuery("DELETE FROM ProductEntity").executeUpdate();
    }

    @AfterEach
    @Transactional
    void tearDown() {
        entityManager.createQuery("DELETE FROM ProductEntity").executeUpdate();
    }

    @Test
    @Transactional
    @DisplayName("Should set createdAt on persist")
    void shouldSetCreatedAtOnPersist() {
        ProductEntity entity = createTestProduct();

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @Transactional
    @DisplayName("Should persist and retrieve product entity")
    void shouldPersistAndRetrieveProductEntity() {
        ProductEntity entity = createTestProduct();

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        ProductEntity retrieved = entityManager.find(ProductEntity.class, entity.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCode()).isEqualTo("PROD001");
        assertThat(retrieved.getName()).isEqualTo("Product Test");
        assertThat(retrieved.getType()).isEqualTo("INDUSTRIAL");
    }

    @Test
    @DisplayName("Should have correct table name")
    void shouldHaveCorrectTableName() {
        assertThat(ProductEntity.class.getAnnotation(jakarta.persistence.Table.class).name())
                .isEqualTo("products");
    }

    @Test
    @Transactional
    @DisplayName("Should validate price constraints")
    void shouldValidatePriceConstraints() {
        ProductEntity entity = createTestProduct();
        entity.setPurchasePrice(BigDecimal.valueOf(100.00));
        entity.setSalePrice(BigDecimal.valueOf(150.00));

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getPurchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(entity.getSalePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    @Test
    @Transactional
    @DisplayName("Should validate dimension constraints")
    void shouldValidateDimensionConstraints() {
        ProductEntity entity = createTestProduct();
        entity.setHeight(BigDecimal.valueOf(10.0));
        entity.setWidth(BigDecimal.valueOf(20.0));
        entity.setDepth(BigDecimal.valueOf(30.0));

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getHeight()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
        assertThat(entity.getWidth()).isEqualByComparingTo(BigDecimal.valueOf(20.0));
        assertThat(entity.getDepth()).isEqualByComparingTo(BigDecimal.valueOf(30.0));
    }

    @Test
    @Transactional
    @DisplayName("Should handle stock quantity")
    void shouldHandleStockQuantity() {
        ProductEntity entity = createTestProduct();
        entity.setStockQuantity(100);

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getStockQuantity()).isEqualTo(100);
    }

    private ProductEntity createTestProduct() {
        ProductEntity entity = new ProductEntity();
        entity.setCode("PROD001");
        entity.setName("Product Test");
        entity.setType("INDUSTRIAL");
        entity.setDetails("Test details");
        entity.setWeight(BigDecimal.valueOf(5.5));
        entity.setPurchasePrice(BigDecimal.valueOf(100.00));
        entity.setSalePrice(BigDecimal.valueOf(150.00));
        entity.setHeight(BigDecimal.valueOf(10.0));
        entity.setWidth(BigDecimal.valueOf(20.0));
        entity.setDepth(BigDecimal.valueOf(30.0));
        entity.setDestinationVehicle("Truck");
        entity.setStockQuantity(50);
        return entity;
    }
}
