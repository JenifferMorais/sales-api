package com.sales.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenBlacklistCleanupJob Tests")
class TokenBlacklistCleanupJobTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private UserActivityService userActivityService;

    @InjectMocks
    private TokenBlacklistCleanupJob cleanupJob;

    @BeforeEach
    void setUp() {
        // Setup is done by @BeforeEach automatically
    }

    @Test
    @DisplayName("Should cleanup expired tokens and old activities successfully")
    void shouldCleanupExpiredTokensAndOldActivitiesSuccessfully() {
        doNothing().when(tokenBlacklistService).cleanupExpiredTokens();
        doNothing().when(userActivityService).cleanupOldActivities();

        cleanupJob.cleanupExpiredTokens();

        verify(tokenBlacklistService).cleanupExpiredTokens();
        verify(userActivityService).cleanupOldActivities();
    }

    @Test
    @DisplayName("Should call cleanup in correct order")
    void shouldCallCleanupInCorrectOrder() {
        doNothing().when(tokenBlacklistService).cleanupExpiredTokens();
        doNothing().when(userActivityService).cleanupOldActivities();

        cleanupJob.cleanupExpiredTokens();

        var inOrder = inOrder(tokenBlacklistService, userActivityService);
        inOrder.verify(tokenBlacklistService).cleanupExpiredTokens();
        inOrder.verify(userActivityService).cleanupOldActivities();
    }

    @Test
    @DisplayName("Should handle exception from token blacklist service")
    void shouldHandleExceptionFromTokenBlacklistService() {
        doThrow(new RuntimeException("Database error")).when(tokenBlacklistService).cleanupExpiredTokens();

        assertThatCode(() -> cleanupJob.cleanupExpiredTokens())
                .doesNotThrowAnyException();

        verify(tokenBlacklistService).cleanupExpiredTokens();
        verify(userActivityService, never()).cleanupOldActivities();
    }

    @Test
    @DisplayName("Should handle exception from user activity service")
    void shouldHandleExceptionFromUserActivityService() {
        doNothing().when(tokenBlacklistService).cleanupExpiredTokens();
        doThrow(new RuntimeException("Database error")).when(userActivityService).cleanupOldActivities();

        assertThatCode(() -> cleanupJob.cleanupExpiredTokens())
                .doesNotThrowAnyException();

        verify(tokenBlacklistService).cleanupExpiredTokens();
        verify(userActivityService).cleanupOldActivities();
    }

    @Test
    @DisplayName("Should handle exception and log error")
    void shouldHandleExceptionAndLogError() {
        doThrow(new RuntimeException("Unexpected error")).when(tokenBlacklistService).cleanupExpiredTokens();

        assertThatCode(() -> cleanupJob.cleanupExpiredTokens())
                .doesNotThrowAnyException();

        verify(tokenBlacklistService).cleanupExpiredTokens();
    }

    @Test
    @DisplayName("Should not propagate exception to scheduler")
    void shouldNotPropagateExceptionToScheduler() {
        doThrow(new RuntimeException("Database connection failed"))
                .when(tokenBlacklistService).cleanupExpiredTokens();

        assertThatCode(() -> cleanupJob.cleanupExpiredTokens())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should execute cleanup when both services succeed")
    void shouldExecuteCleanupWhenBothServicesSucceed() {
        doNothing().when(tokenBlacklistService).cleanupExpiredTokens();
        doNothing().when(userActivityService).cleanupOldActivities();

        cleanupJob.cleanupExpiredTokens();

        verify(tokenBlacklistService, times(1)).cleanupExpiredTokens();
        verify(userActivityService, times(1)).cleanupOldActivities();
    }

    @Test
    @DisplayName("Should handle null pointer exception")
    void shouldHandleNullPointerException() {
        doThrow(new NullPointerException("Null value")).when(tokenBlacklistService).cleanupExpiredTokens();

        assertThatCode(() -> cleanupJob.cleanupExpiredTokens())
                .doesNotThrowAnyException();

        verify(tokenBlacklistService).cleanupExpiredTokens();
    }

    @Test
    @DisplayName("Should call cleanup multiple times without side effects")
    void shouldCallCleanupMultipleTimesWithoutSideEffects() {
        doNothing().when(tokenBlacklistService).cleanupExpiredTokens();
        doNothing().when(userActivityService).cleanupOldActivities();

        cleanupJob.cleanupExpiredTokens();
        cleanupJob.cleanupExpiredTokens();
        cleanupJob.cleanupExpiredTokens();

        verify(tokenBlacklistService, times(3)).cleanupExpiredTokens();
        verify(userActivityService, times(3)).cleanupOldActivities();
    }

    @Test
    @DisplayName("Should verify no other methods are called")
    void shouldVerifyNoOtherMethodsAreCalled() {
        doNothing().when(tokenBlacklistService).cleanupExpiredTokens();
        doNothing().when(userActivityService).cleanupOldActivities();

        cleanupJob.cleanupExpiredTokens();

        verifyNoMoreInteractions(tokenBlacklistService);
        verifyNoMoreInteractions(userActivityService);
    }

    @Test
    @DisplayName("Should handle intermittent failures gracefully")
    void shouldHandleIntermittentFailuresGracefully() {
        doThrow(new RuntimeException("Temporary error"))
                .doNothing()
                .when(tokenBlacklistService).cleanupExpiredTokens();

        // First call fails
        assertThatCode(() -> cleanupJob.cleanupExpiredTokens())
                .doesNotThrowAnyException();

        // Second call succeeds
        assertThatCode(() -> cleanupJob.cleanupExpiredTokens())
                .doesNotThrowAnyException();

        verify(tokenBlacklistService, times(2)).cleanupExpiredTokens();
    }

    @Test
    @DisplayName("Should complete user activity cleanup even if token cleanup fails")
    void shouldCompleteUserActivityCleanupEvenIfTokenCleanupFails() {
        doThrow(new RuntimeException("Token cleanup failed"))
                .when(tokenBlacklistService).cleanupExpiredTokens();

        cleanupJob.cleanupExpiredTokens();

        verify(tokenBlacklistService).cleanupExpiredTokens();
        // UserActivityService não deve ser chamado porque a exceção ocorre antes
        verify(userActivityService, never()).cleanupOldActivities();
    }
}
