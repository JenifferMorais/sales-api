package com.sales.application.auth.usecase;

import com.sales.infrastructure.security.TokenBlacklistService;
import com.sales.infrastructure.security.UserActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutUseCase Tests")
class LogoutUseCaseTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private UserActivityService userActivityService;

    @InjectMocks
    private LogoutUseCase logoutUseCase;

    private String validToken = "jwt.token.here";
    private String tokenWithBearer = "Bearer jwt.token.here";

    @BeforeEach
    void setUp() {

        doNothing().when(tokenBlacklistService).blacklistToken(anyString());
        doNothing().when(userActivityService).removeActivity(anyString());
    }

    @Test
    @DisplayName("Should logout successfully with valid token")
    void shouldLogoutSuccessfully() {

        logoutUseCase.execute(validToken);

        verify(tokenBlacklistService).blacklistToken(validToken);
        verify(userActivityService).removeActivity(validToken);
    }

    @Test
    @DisplayName("Should remove Bearer prefix before blacklisting")
    void shouldRemoveBearerPrefix() {

        logoutUseCase.execute(tokenWithBearer);

        verify(tokenBlacklistService).blacklistToken(validToken);
        verify(userActivityService).removeActivity(validToken);
    }

    @Test
    @DisplayName("Should throw exception when token is null")
    void shouldThrowExceptionWhenTokenIsNull() {

        assertThatThrownBy(() -> logoutUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token não pode estar vazio");

        verify(tokenBlacklistService, never()).blacklistToken(anyString());
        verify(userActivityService, never()).removeActivity(anyString());
    }

    @Test
    @DisplayName("Should throw exception when token is blank")
    void shouldThrowExceptionWhenTokenIsBlank() {

        assertThatThrownBy(() -> logoutUseCase.execute("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token não pode estar vazio");

        verify(tokenBlacklistService, never()).blacklistToken(anyString());
        verify(userActivityService, never()).removeActivity(anyString());
    }

    @Test
    @DisplayName("Should throw exception when token is empty")
    void shouldThrowExceptionWhenTokenIsEmpty() {

        assertThatThrownBy(() -> logoutUseCase.execute(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token não pode estar vazio");

        verify(tokenBlacklistService, never()).blacklistToken(anyString());
        verify(userActivityService, never()).removeActivity(anyString());
    }

    @Test
    @DisplayName("Should verify both services are called in correct order")
    void shouldVerifyServicesCalledInOrder() {

        logoutUseCase.execute(validToken);

        var inOrder = inOrder(tokenBlacklistService, userActivityService);
        inOrder.verify(tokenBlacklistService).blacklistToken(validToken);
        inOrder.verify(userActivityService).removeActivity(validToken);
    }

    @Test
    @DisplayName("Should handle token with mixed case Bearer prefix")
    void shouldHandleMixedCaseBearerPrefix() {

        String mixedCaseBearer = "BeArEr jwt.token.here";

        logoutUseCase.execute(mixedCaseBearer);

        verify(tokenBlacklistService).blacklistToken(validToken);
        verify(userActivityService).removeActivity(validToken);
    }
}
