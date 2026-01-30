package com.sales.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Entity Tests")
class EntityTest {

    @Test
    @DisplayName("Should create entity with default constructor and set createdAt")
    void shouldCreateEntityWithDefaultConstructorAndSetCreatedAt() {
        TestEntity entity = new TestEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.getCode()).isNull();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create entity with all parameters")
    void shouldCreateEntityWithAllParameters() {
        Long id = 1L;
        String code = "TEST001";
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);

        TestEntity entity = new TestEntity(id, code, createdAt);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getCode()).isEqualTo(code);
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Should set createdAt to now when null in constructor")
    void shouldSetCreatedAtToNowWhenNullInConstructor() {
        TestEntity entity = new TestEntity(1L, "TEST001", null);

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should be equal when codes are equal")
    void shouldBeEqualWhenCodesAreEqual() {
        TestEntity entity1 = new TestEntity(1L, "TEST001", LocalDateTime.now());
        TestEntity entity2 = new TestEntity(2L, "TEST001", LocalDateTime.now());

        assertThat(entity1).isEqualTo(entity2);
    }

    @Test
    @DisplayName("Should not be equal when codes are different")
    void shouldNotBeEqualWhenCodesAreDifferent() {
        TestEntity entity1 = new TestEntity(1L, "TEST001", LocalDateTime.now());
        TestEntity entity2 = new TestEntity(1L, "TEST002", LocalDateTime.now());

        assertThat(entity1).isNotEqualTo(entity2);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        TestEntity entity = new TestEntity(1L, "TEST001", LocalDateTime.now());

        assertThat(entity).isEqualTo(entity);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        TestEntity entity = new TestEntity(1L, "TEST001", LocalDateTime.now());

        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        TestEntity entity = new TestEntity(1L, "TEST001", LocalDateTime.now());
        String differentClass = "Not an entity";

        assertThat(entity).isNotEqualTo(differentClass);
    }

    @Test
    @DisplayName("Should have same hashCode when codes are equal")
    void shouldHaveSameHashCodeWhenCodesAreEqual() {
        TestEntity entity1 = new TestEntity(1L, "TEST001", LocalDateTime.now());
        TestEntity entity2 = new TestEntity(2L, "TEST001", LocalDateTime.now());

        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    @DisplayName("Should have different hashCode when codes are different")
    void shouldHaveDifferentHashCodeWhenCodesAreDifferent() {
        TestEntity entity1 = new TestEntity(1L, "TEST001", LocalDateTime.now());
        TestEntity entity2 = new TestEntity(1L, "TEST002", LocalDateTime.now());

        assertThat(entity1.hashCode()).isNotEqualTo(entity2.hashCode());
    }

    @Test
    @DisplayName("Should handle null code in equals")
    void shouldHandleNullCodeInEquals() {
        TestEntity entity1 = new TestEntity(1L, null, LocalDateTime.now());
        TestEntity entity2 = new TestEntity(2L, null, LocalDateTime.now());

        assertThat(entity1).isEqualTo(entity2);
    }

    @Test
    @DisplayName("Should handle null code in hashCode")
    void shouldHandleNullCodeInHashCode() {
        TestEntity entity = new TestEntity(1L, null, LocalDateTime.now());

        assertThat(entity.hashCode()).isNotNull();
    }

    @Test
    @DisplayName("Should not be equal when one code is null and other is not")
    void shouldNotBeEqualWhenOneCodeIsNullAndOtherIsNot() {
        TestEntity entity1 = new TestEntity(1L, null, LocalDateTime.now());
        TestEntity entity2 = new TestEntity(1L, "TEST001", LocalDateTime.now());

        assertThat(entity1).isNotEqualTo(entity2);
    }

    // Concrete implementation for testing
    private static class TestEntity extends Entity {
        public TestEntity() {
            super();
        }

        public TestEntity(Long id, String code, LocalDateTime createdAt) {
            super(id, code, createdAt);
        }
    }
}
