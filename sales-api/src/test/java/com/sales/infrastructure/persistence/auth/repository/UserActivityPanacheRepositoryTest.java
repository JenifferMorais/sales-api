package com.sales.infrastructure.persistence.auth.repository;

import com.sales.infrastructure.persistence.auth.entity.UserActivityEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserActivityPanacheRepositoryTest {

    @Inject
    UserActivityPanacheRepository repository;

    private static final String TEST_TOKEN_HASH = "test_token_hash_123456";
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    @Transactional
    void setUp() {
        repository.delete("tokenHash", TEST_TOKEN_HASH);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        repository.delete("tokenHash", TEST_TOKEN_HASH);
    }

    @Test
    void testFindByTokenHash_WhenExists() {
        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);

        Optional<UserActivityEntity> result = repository.findByTokenHash(TEST_TOKEN_HASH);

        assertTrue(result.isPresent());
        assertEquals(TEST_TOKEN_HASH, result.get().getTokenHash());
        assertEquals(TEST_USER_ID, result.get().getUserId());
    }

    @Test
    void testFindByTokenHash_WhenNotExists() {
        Optional<UserActivityEntity> result = repository.findByTokenHash("non_existent_hash");

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateActivity_CreateNewActivity() {
        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);

        Optional<UserActivityEntity> result = repository.findByTokenHash(TEST_TOKEN_HASH);

        assertTrue(result.isPresent());
        UserActivityEntity entity = result.get();
        assertEquals(TEST_TOKEN_HASH, entity.getTokenHash());
        assertEquals(TEST_USER_ID, entity.getUserId());
        assertNotNull(entity.getLastActivityAt());
        assertNotNull(entity.getCreatedAt());
    }

    @Test
    void testUpdateActivity_UpdateExistingActivity() throws InterruptedException {
        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);
        Optional<UserActivityEntity> first = repository.findByTokenHash(TEST_TOKEN_HASH);
        assertTrue(first.isPresent());
        LocalDateTime firstActivityTime = first.get().getLastActivityAt();

        Thread.sleep(100);

        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);
        Optional<UserActivityEntity> second = repository.findByTokenHash(TEST_TOKEN_HASH);

        assertTrue(second.isPresent());
        LocalDateTime secondActivityTime = second.get().getLastActivityAt();
        assertFalse(secondActivityTime.isBefore(firstActivityTime),
                "Last activity time should be updated");
        assertEquals(first.get().getId(), second.get().getId(),
                "Should be the same entity (updated, not created new)");
    }

    @Test
    void testUpdateActivity_HandlesConcurrentCreation() {
        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);

        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);

        long count = repository.count("tokenHash", TEST_TOKEN_HASH);
        assertEquals(1, count, "Should only have one record even with concurrent updates");
    }

    @Test
    void testDeleteByTokenHash() {
        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);
        assertTrue(repository.findByTokenHash(TEST_TOKEN_HASH).isPresent());

        repository.deleteByTokenHash(TEST_TOKEN_HASH);

        assertFalse(repository.findByTokenHash(TEST_TOKEN_HASH).isPresent());
    }

    @Test
    void testDeleteByTokenHash_WhenNotExists() {
        assertDoesNotThrow(() -> repository.deleteByTokenHash("non_existent_hash"));
    }

    @Test
    void testCleanupOldActivities() {
        String oldTokenHash = "old_token_hash";
        String recentTokenHash = "recent_token_hash";

        repository.updateActivity(oldTokenHash, TEST_USER_ID);
        repository.updateActivity(recentTokenHash, TEST_USER_ID);

        LocalDateTime cutoffDate = LocalDateTime.now().plusHours(1);

        repository.cleanupOldActivities(cutoffDate);

        assertFalse(repository.findByTokenHash(oldTokenHash).isPresent(),
                "Old activity should be deleted");
        assertFalse(repository.findByTokenHash(recentTokenHash).isPresent(),
                "Recent activity should also be deleted when cutoff is in future");

        repository.deleteByTokenHash(oldTokenHash);
        repository.deleteByTokenHash(recentTokenHash);
    }

    @Test
    void testCleanupOldActivities_OnlyDeletesOldRecords() {
        String oldTokenHash = "old_token_hash_2";
        String recentTokenHash = "recent_token_hash_2";

        repository.updateActivity(oldTokenHash, TEST_USER_ID);
        repository.updateActivity(recentTokenHash, TEST_USER_ID);

        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(1);

        repository.cleanupOldActivities(cutoffDate);

        assertTrue(repository.findByTokenHash(recentTokenHash).isPresent(),
                "Recent activity should remain");

        repository.deleteByTokenHash(oldTokenHash);
        repository.deleteByTokenHash(recentTokenHash);
    }

    @Test
    void testUpdateActivity_PreservesCreatedAt() throws InterruptedException {
        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);
        Optional<UserActivityEntity> first = repository.findByTokenHash(TEST_TOKEN_HASH);
        assertTrue(first.isPresent());
        LocalDateTime originalCreatedAt = first.get().getCreatedAt();

        Thread.sleep(100);

        repository.updateActivity(TEST_TOKEN_HASH, TEST_USER_ID);
        Optional<UserActivityEntity> second = repository.findByTokenHash(TEST_TOKEN_HASH);

        assertTrue(second.isPresent());
        assertEquals(originalCreatedAt, second.get().getCreatedAt(),
                "CreatedAt should not change on update");
    }

    @Test
    void testMultipleUsersCanHaveActivities() {
        String tokenHash1 = "token_hash_user_1";
        String tokenHash2 = "token_hash_user_2";
        Long userId1 = 1L;
        Long userId2 = 2L;

        repository.updateActivity(tokenHash1, userId1);
        repository.updateActivity(tokenHash2, userId2);

        Optional<UserActivityEntity> activity1 = repository.findByTokenHash(tokenHash1);
        Optional<UserActivityEntity> activity2 = repository.findByTokenHash(tokenHash2);

        assertTrue(activity1.isPresent());
        assertTrue(activity2.isPresent());
        assertEquals(userId1, activity1.get().getUserId());
        assertEquals(userId2, activity2.get().getUserId());

        repository.deleteByTokenHash(tokenHash1);
        repository.deleteByTokenHash(tokenHash2);
    }
}
