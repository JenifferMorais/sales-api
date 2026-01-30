package com.sales.infrastructure.persistence.auth.entity;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("UserEntity Tests")
class UserEntityTest {

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void setUp() {
        entityManager.createQuery("DELETE FROM UserEntity").executeUpdate();
    }

    @AfterEach
    @Transactional
    void tearDown() {
        entityManager.createQuery("DELETE FROM UserEntity").executeUpdate();
    }

    @Test
    @Transactional
    @DisplayName("Should set createdAt on persist")
    void shouldSetCreatedAtOnPersist() {
        UserEntity entity = new UserEntity();
        entity.setCustomerCode("CUST001");
        entity.setEmail("test@example.com");
        entity.setPassword("hashedpassword");
        entity.setActive(true);

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @Transactional
    @DisplayName("Should persist and retrieve user entity")
    void shouldPersistAndRetrieveUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setCustomerCode("CUST002");
        entity.setEmail("user@example.com");
        entity.setPassword("hashedpass");
        entity.setActive(true);

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        UserEntity retrieved = entityManager.find(UserEntity.class, entity.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getEmail()).isEqualTo("user@example.com");
        assertThat(retrieved.getCustomerCode()).isEqualTo("CUST002");
        assertThat(retrieved.getActive()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("Should handle reset password token")
    void shouldHandleResetPasswordToken() {
        UserEntity entity = new UserEntity();
        entity.setCustomerCode("CUST003");
        entity.setEmail("reset@example.com");
        entity.setPassword("hashedpass");
        entity.setActive(true);
        entity.setResetPasswordToken("reset-token-123");
        entity.setResetPasswordTokenExpiresAt(LocalDateTime.now().plusHours(1));

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getResetPasswordToken()).isEqualTo("reset-token-123");
        assertThat(entity.getResetPasswordTokenExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should have correct table name")
    void shouldHaveCorrectTableName() {
        assertThat(UserEntity.class.getAnnotation(jakarta.persistence.Table.class).name())
                .isEqualTo("users");
    }

    @Test
    @Transactional
    @DisplayName("Should handle inactive user")
    void shouldHandleInactiveUser() {
        UserEntity entity = new UserEntity();
        entity.setCustomerCode("CUST004");
        entity.setEmail("inactive@example.com");
        entity.setPassword("hashedpass");
        entity.setActive(false);

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getActive()).isFalse();
    }
}
