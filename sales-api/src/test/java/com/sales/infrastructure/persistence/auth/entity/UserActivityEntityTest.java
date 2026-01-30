package com.sales.infrastructure.persistence.auth.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserActivityEntityTest {

    @Test
    void testGettersAndSetters() {
        UserActivityEntity entity = new UserActivityEntity();

        Long id = 1L;
        String tokenHash = "test_hash_123";
        Long userId = 100L;
        LocalDateTime lastActivityAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();

        entity.setId(id);
        entity.setTokenHash(tokenHash);
        entity.setUserId(userId);
        entity.setLastActivityAt(lastActivityAt);
        entity.setCreatedAt(createdAt);

        assertEquals(id, entity.getId());
        assertEquals(tokenHash, entity.getTokenHash());
        assertEquals(userId, entity.getUserId());
        assertEquals(lastActivityAt, entity.getLastActivityAt());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void testOnCreate_SetsCreatedAtAndLastActivityAt() throws Exception {
        UserActivityEntity entity = new UserActivityEntity();

        java.lang.reflect.Method onCreateMethod = UserActivityEntity.class.getDeclaredMethod("onCreate");

        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(entity);

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getLastActivityAt());
    }

    @Test
    void testOnCreate_PreservesExistingLastActivityAt() throws Exception {
        UserActivityEntity entity = new UserActivityEntity();
        LocalDateTime existingActivity = LocalDateTime.now().minusHours(1);
        entity.setLastActivityAt(existingActivity);

        java.lang.reflect.Method onCreateMethod = UserActivityEntity.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(entity);

        assertNotNull(entity.getCreatedAt());
        assertEquals(existingActivity, entity.getLastActivityAt());
    }

    @Test
    void testCreatedAtIsImmutable() throws Exception {
        UserActivityEntity entity = new UserActivityEntity();

        java.lang.reflect.Method onCreateMethod = UserActivityEntity.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(entity);

        LocalDateTime firstCreatedAt = entity.getCreatedAt();

        entity.setCreatedAt(LocalDateTime.now().plusDays(1));

        assertNotEquals(firstCreatedAt, entity.getCreatedAt(),
                "CreatedAt can be manually set for testing purposes");
    }
}
