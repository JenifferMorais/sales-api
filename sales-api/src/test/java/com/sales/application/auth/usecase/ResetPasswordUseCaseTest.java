package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResetPasswordUseCase Tests")
class ResetPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResetPasswordUseCase resetPasswordUseCase;

    private User validUser;
    private String validResetToken = "valid-reset-token-123";
    private String newPassword = "NewPassword@123";

    @BeforeEach
    void setUp() {
        Email email = new Email("john.silva@email.com");
        Password password = Password.fromPlainText("OldPassword@123");
        validUser = new User("CUST001", email, password);

        validUser.setResetPasswordToken(validResetToken);
    }

    @Test
    @DisplayName("Should reset password successfully with valid token")
    void shouldResetPasswordSuccessfully() {

        when(userRepository.findByResetPasswordToken(validResetToken)).thenReturn(Optional.of(validUser));
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        when(validUser.isResetTokenValid(validResetToken)).thenReturn(true);

        resetPasswordUseCase.execute(validResetToken, newPassword);

        verify(userRepository).findByResetPasswordToken(validResetToken);
        verify(userRepository).save(validUser);
    }

    @Test
    @DisplayName("Should throw exception when token not found")
    void shouldThrowExceptionWhenTokenNotFound() {

        when(userRepository.findByResetPasswordToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordUseCase.execute("invalid-token", newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token inválido ou expirado");

        verify(userRepository).findByResetPasswordToken("invalid-token");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when token is invalid or expired")
    void shouldThrowExceptionWhenTokenInvalidOrExpired() {

        when(userRepository.findByResetPasswordToken(validResetToken)).thenReturn(Optional.of(validUser));

        when(validUser.isResetTokenValid(validResetToken)).thenReturn(false);

        assertThatThrownBy(() -> resetPasswordUseCase.execute(validResetToken, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token inválido ou expirado");

        verify(userRepository).findByResetPasswordToken(validResetToken);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new password is invalid")
    void shouldThrowExceptionWhenPasswordInvalid() {

        when(userRepository.findByResetPasswordToken(validResetToken)).thenReturn(Optional.of(validUser));
        when(validUser.isResetTokenValid(validResetToken)).thenReturn(true);

        assertThatThrownBy(() -> resetPasswordUseCase.execute(validResetToken, "weak"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository).findByResetPasswordToken(validResetToken);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should verify operations happen in correct order")
    void shouldVerifyOperationsOrder() {

        when(userRepository.findByResetPasswordToken(validResetToken)).thenReturn(Optional.of(validUser));
        when(validUser.isResetTokenValid(validResetToken)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        resetPasswordUseCase.execute(validResetToken, newPassword);

        var inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).findByResetPasswordToken(validResetToken);
        inOrder.verify(userRepository).save(validUser);
    }

    @Test
    @DisplayName("Should save user after password change")
    void shouldSaveUserAfterPasswordChange() {

        when(userRepository.findByResetPasswordToken(validResetToken)).thenReturn(Optional.of(validUser));
        when(validUser.isResetTokenValid(validResetToken)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        resetPasswordUseCase.execute(validResetToken, newPassword);

        verify(userRepository).save(validUser);
    }
}
