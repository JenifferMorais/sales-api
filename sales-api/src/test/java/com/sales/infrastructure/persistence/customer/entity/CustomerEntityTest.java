package com.sales.infrastructure.persistence.customer.entity;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("CustomerEntity Tests")
class CustomerEntityTest {

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void setUp() {
        entityManager.createQuery("DELETE FROM CustomerEntity").executeUpdate();
    }

    @AfterEach
    @Transactional
    void tearDown() {
        entityManager.createQuery("DELETE FROM CustomerEntity").executeUpdate();
    }

    @Test
    @Transactional
    @DisplayName("Should set createdAt on persist")
    void shouldSetCreatedAtOnPersist() {
        CustomerEntity entity = createTestCustomer();

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @Transactional
    @DisplayName("Should persist and retrieve customer entity")
    void shouldPersistAndRetrieveCustomerEntity() {
        CustomerEntity entity = createTestCustomer();

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        CustomerEntity retrieved = entityManager.find(CustomerEntity.class, entity.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCode()).isEqualTo("CUST001");
        assertThat(retrieved.getFullName()).isEqualTo("João Silva");
        assertThat(retrieved.getCpf()).isEqualTo("12345678901");
    }

    @Test
    @Transactional
    @DisplayName("Should use setPhone method")
    void shouldUseSetPhoneMethod() {
        CustomerEntity entity = new CustomerEntity();
        entity.setPhone("11987654321");

        assertThat(entity.getCellPhone()).isEqualTo("11987654321");
    }

    @Test
    @DisplayName("Should have correct table name")
    void shouldHaveCorrectTableName() {
        assertThat(CustomerEntity.class.getAnnotation(jakarta.persistence.Table.class).name())
                .isEqualTo("customers");
    }

    @Test
    @DisplayName("Should have unique constraints")
    void shouldHaveUniqueConstraints() {
        jakarta.persistence.Table tableAnnotation = CustomerEntity.class
                .getAnnotation(jakarta.persistence.Table.class);

        assertThat(tableAnnotation.uniqueConstraints()).hasSize(3);
    }

    @Test
    @Transactional
    @DisplayName("Should handle optional complement field")
    void shouldHandleOptionalComplementField() {
        CustomerEntity entity = createTestCustomer();
        entity.setComplement(null);

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getComplement()).isNull();
    }

    private CustomerEntity createTestCustomer() {
        CustomerEntity entity = new CustomerEntity();
        entity.setCode("CUST001");
        entity.setFullName("João Silva");
        entity.setMotherName("Maria Silva");
        entity.setCpf("12345678901");
        entity.setRg("123456789");
        entity.setZipCode("12345678");
        entity.setStreet("Rua Teste");
        entity.setNumber("123");
        entity.setComplement("Apto 1");
        entity.setNeighborhood("Centro");
        entity.setCity("São Paulo");
        entity.setState("SP");
        entity.setBirthDate(LocalDate.of(1990, 1, 1));
        entity.setCellPhone("11987654321");
        entity.setEmail("joao@example.com");
        return entity;
    }
}
