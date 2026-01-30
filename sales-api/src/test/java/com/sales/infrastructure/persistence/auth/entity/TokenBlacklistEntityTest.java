package com.sales.infrastructure.persistence.auth.entity;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("TokenBlacklistEntity Tests")
class TokenBlacklistEntityTest {

    @Inject
    EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("Should set blacklistedAt on persist")
    void shouldSetBlacklistedAtOnPersist() {
        TokenBlacklistEntity entity = new TokenBlacklistEntity();
        entity.setTokenHash("hash123");
        entity.setUserId(1L);
        entity.setExpiresAt(LocalDateTime.now().plusHours(1));

        entityManager.persist(entity);
        entityManager.flush();

        assertThat(entity.getBlacklistedAt()).isNotNull();
        assertThat(entity.getBlacklistedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @Transactional
    @DisplayName("Should persist and retrieve token blacklist entity")
    void shouldPersistAndRetrieveTokenBlacklistEntity() {
        TokenBlacklistEntity entity = new TokenBlacklistEntity();
        entity.setTokenHash("uniquehash456");
        entity.setUserId(2L);
        entity.setExpiresAt(LocalDateTime.now().plusHours(2));

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        TokenBlacklistEntity retrieved = entityManager.find(TokenBlacklistEntity.class, entity.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTokenHash()).isEqualTo("uniquehash456");
        assertThat(retrieved.getUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should have correct table name")
    void shouldHaveCorrectTableName() {
        assertThat(TokenBlacklistEntity.class.getAnnotation(jakarta.persistence.Table.class).name())
                .isEqualTo("token_blacklist");
    }

    @Test
    @DisplayName("Should have indexes defined")
    void shouldHaveIndexesDefined() {
        jakarta.persistence.Table tableAnnotation = TokenBlacklistEntity.class
                .getAnnotation(jakarta.persistence.Table.class);

        assertThat(tableAnnotation.indexes()).hasSize(2);
    }

    @Test
    @Transactional
    @DisplayName("Should set all properties correctly")
    void shouldSetAllPropertiesCorrectly() {
        TokenBlacklistEntity entity = new TokenBlacklistEntity();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        entity.setTokenHash("testhash");
        entity.setUserId(123L);
        entity.setExpiresAt(expiresAt);

        assertThat(entity.getTokenHash()).isEqualTo("testhash");
        assertThat(entity.getUserId()).isEqualTo(123L);
        assertThat(entity.getExpiresAt()).isEqualTo(expiresAt);
    }
}
