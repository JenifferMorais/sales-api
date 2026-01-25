package com.sales.infrastructure.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistFilterTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private UserActivityService userActivityService;

    @Mock
    private ContainerRequestContext requestContext;

    @InjectMocks
    private TokenBlacklistFilter filter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String BLACKLISTED_TOKEN = "blacklisted.jwt.token";

    @BeforeEach
    void setUp() throws Exception {
        filter = new TokenBlacklistFilter();

        java.lang.reflect.Field blacklistField = TokenBlacklistFilter.class.getDeclaredField("tokenBlacklistService");
        blacklistField.setAccessible(true);
        blacklistField.set(filter, tokenBlacklistService);

        java.lang.reflect.Field activityField = TokenBlacklistFilter.class.getDeclaredField("userActivityService");
        activityField.setAccessible(true);
        activityField.set(filter, userActivityService);
    }

    @Test
    void testFilter_NoAuthHeader_Continues() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(tokenBlacklistService, never()).isBlacklisted(anyString());
    }

    @Test
    void testFilter_InvalidAuthHeader_Continues() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidHeader");

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(tokenBlacklistService, never()).isBlacklisted(anyString());
    }

    @Test
    void testFilter_BlacklistedToken_AbortsRequest() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + BLACKLISTED_TOKEN);
        when(tokenBlacklistService.isBlacklisted(BLACKLISTED_TOKEN)).thenReturn(true);

        filter.filter(requestContext);

        verify(requestContext).abortWith(any(Response.class));
        verify(userActivityService, never()).checkAndInvalidateIfInactive(anyString());
        verify(userActivityService, never()).updateActivity(anyString());
    }

    @Test
    void testFilter_InactiveToken_AbortsRequest() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + VALID_TOKEN);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(userActivityService.checkAndInvalidateIfInactive(VALID_TOKEN)).thenReturn(true);

        filter.filter(requestContext);

        verify(requestContext).abortWith(any(Response.class));
        verify(userActivityService, never()).updateActivity(anyString());
    }

    @Test
    void testFilter_ValidToken_UpdatesActivityAndContinues() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + VALID_TOKEN);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(userActivityService.checkAndInvalidateIfInactive(VALID_TOKEN)).thenReturn(false);

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());
        verify(userActivityService).updateActivity(VALID_TOKEN);
    }

    @Test
    void testFilter_BearerWithExtraSpaces_TrimsCorrectly() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer   " + VALID_TOKEN + "   ");
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(userActivityService.checkAndInvalidateIfInactive(VALID_TOKEN)).thenReturn(false);

        filter.filter(requestContext);

        verify(tokenBlacklistService).isBlacklisted(VALID_TOKEN);
        verify(userActivityService).updateActivity(VALID_TOKEN);
    }

    @Test
    void testFilter_BearerCaseInsensitive() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                .thenReturn("BEARER " + VALID_TOKEN);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(userActivityService.checkAndInvalidateIfInactive(VALID_TOKEN)).thenReturn(false);

        filter.filter(requestContext);

        verify(userActivityService).updateActivity(VALID_TOKEN);
    }

    @Test
    void testFilter_BearerMixedCase() throws IOException {
        when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                .thenReturn("BeArEr " + VALID_TOKEN);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(userActivityService.checkAndInvalidateIfInactive(VALID_TOKEN)).thenReturn(false);

        filter.filter(requestContext);

        verify(userActivityService).updateActivity(VALID_TOKEN);
    }
}
