package com.sales.infrastructure.security;

import com.sales.infrastructure.persistence.auth.entity.UserActivityEntity;
import com.sales.infrastructure.persistence.auth.repository.UserActivityPanacheRepository;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {

    @Mock
    private UserActivityPanacheRepository activityRepository;

    @Mock
    private JWTParser jwtParser;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private JsonWebToken jsonWebToken;

    @InjectMocks
    private UserActivityService userActivityService;

    private static final String TEST_TOKEN = "test.jwt.token";
    private static final String TOKEN_HASH = "2a782332eb4f6f9230c5f26a8eb0d2f838d7c1f1749f27b24d3983bc30b49324";
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() throws Exception {
        userActivityService = new UserActivityService();

        java.lang.reflect.Field repoField = UserActivityService.class.getDeclaredField("activityRepository");
        repoField.setAccessible(true);
        repoField.set(userActivityService, activityRepository);

        java.lang.reflect.Field parserField = UserActivityService.class.getDeclaredField("jwtParser");
        parserField.setAccessible(true);
        parserField.set(userActivityService, jwtParser);

        java.lang.reflect.Field blacklistField = UserActivityService.class.getDeclaredField("tokenBlacklistService");
        blacklistField.setAccessible(true);
        blacklistField.set(userActivityService, tokenBlacklistService);

        java.lang.reflect.Field timeoutField = UserActivityService.class.getDeclaredField("inactivityTimeoutMinutes");
        timeoutField.setAccessible(true);
        timeoutField.set(userActivityService, 15L);
    }

    @Test
    void testCheckAndInvalidateIfInactive_NoActivityExists_ReturnsFalse() throws ParseException {
        when(activityRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        boolean result = userActivityService.checkAndInvalidateIfInactive(TEST_TOKEN);

        assertFalse(result);
        verify(activityRepository, never()).updateActivity(anyString(), anyLong());
        verify(jwtParser, never()).parse(anyString());
        verify(tokenBlacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void testCheckAndInvalidateIfInactive_ActivityExistsAndActive_ReturnsFalse() {
        UserActivityEntity activity = new UserActivityEntity();
        activity.setUserId(USER_ID);
        activity.setLastActivityAt(LocalDateTime.now().minusMinutes(5));

        when(activityRepository.findByTokenHash(anyString())).thenReturn(Optional.of(activity));

        boolean result = userActivityService.checkAndInvalidateIfInactive(TEST_TOKEN);

        assertFalse(result);
        verify(tokenBlacklistService, never()).blacklistToken(anyString());
        verify(activityRepository, never()).deleteByTokenHash(anyString());
    }

    @Test
    void testCheckAndInvalidateIfInactive_ActivityInactive_InvalidatesToken() {
        UserActivityEntity activity = new UserActivityEntity();
        activity.setUserId(USER_ID);
        activity.setLastActivityAt(LocalDateTime.now().minusMinutes(20));

        when(activityRepository.findByTokenHash(anyString())).thenReturn(Optional.of(activity));

        boolean result = userActivityService.checkAndInvalidateIfInactive(TEST_TOKEN);

        assertTrue(result);
        verify(tokenBlacklistService).blacklistToken(TEST_TOKEN);
        verify(activityRepository).deleteByTokenHash(anyString());
    }

    @Test
    void testCheckAndInvalidateIfInactive_ExactlyAtTimeout_InvalidatesToken() {
        UserActivityEntity activity = new UserActivityEntity();
        activity.setUserId(USER_ID);
        activity.setLastActivityAt(LocalDateTime.now().minusMinutes(15));

        when(activityRepository.findByTokenHash(anyString())).thenReturn(Optional.of(activity));

        boolean result = userActivityService.checkAndInvalidateIfInactive(TEST_TOKEN);

        assertTrue(result);
        verify(tokenBlacklistService).blacklistToken(TEST_TOKEN);
    }

    @Test
    void testCheckAndInvalidateIfInactive_NoActivityExists_DoesNotThrow() throws ParseException {
        when(activityRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userActivityService.checkAndInvalidateIfInactive(TEST_TOKEN));
        verify(tokenBlacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void testUpdateActivity_Success() throws ParseException {
        when(jwtParser.parse(TEST_TOKEN)).thenReturn(jsonWebToken);
        when(jsonWebToken.getSubject()).thenReturn(USER_ID.toString());

        userActivityService.updateActivity(TEST_TOKEN);

        verify(activityRepository).updateActivity(anyString(), eq(USER_ID));
    }

    @Test
    void testUpdateActivity_ParseException_DoesNotThrow() throws ParseException {
        when(jwtParser.parse(TEST_TOKEN)).thenThrow(new ParseException("Invalid token"));

        assertDoesNotThrow(() -> userActivityService.updateActivity(TEST_TOKEN));
        verify(activityRepository, never()).updateActivity(anyString(), anyLong());
    }

    @Test
    void testRemoveActivity() {
        userActivityService.removeActivity(TEST_TOKEN);

        verify(activityRepository).deleteByTokenHash(anyString());
    }

    @Test
    void testCleanupOldActivities() {
        userActivityService.cleanupOldActivities();

        verify(activityRepository).cleanupOldActivities(any(LocalDateTime.class));
    }

    @Test
    void testHashToken_GeneratesConsistentHash() throws Exception {
        java.lang.reflect.Method hashMethod = UserActivityService.class.getDeclaredMethod("hashToken", String.class);
        hashMethod.setAccessible(true);

        String hash1 = (String) hashMethod.invoke(userActivityService, TEST_TOKEN);
        String hash2 = (String) hashMethod.invoke(userActivityService, TEST_TOKEN);

        assertEquals(hash1, hash2, "Same token should generate same hash");
        assertEquals(64, hash1.length(), "SHA-256 hash should be 64 characters");
    }

    @Test
    void testHashToken_DifferentTokensDifferentHashes() throws Exception {
        java.lang.reflect.Method hashMethod = UserActivityService.class.getDeclaredMethod("hashToken", String.class);
        hashMethod.setAccessible(true);

        String hash1 = (String) hashMethod.invoke(userActivityService, "token1");
        String hash2 = (String) hashMethod.invoke(userActivityService, "token2");

        assertNotEquals(hash1, hash2, "Different tokens should generate different hashes");
    }
}
