package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.EmailService;
import com.sales.domain.auth.port.TokenService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForgotPasswordUseCase Tests")
class ForgotPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ForgotPasswordUseCase forgotPasswordUseCase;

    private User validUser;
    private String userEmail = "john.silva@email.com";

    @BeforeEach
    void setUp() {
        Email email = new Email(userEmail);
        Password password = Password.fromPlainText("Test@123");
        validUser = new User("CUST001", email, password);
    }

    @Test
    @DisplayName("Should generate reset token and send email successfully")
    void shouldGenerateResetTokenSuccessfully() {

        String resetToken = "reset-token-123";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateResetPasswordToken()).thenReturn(resetToken);
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        doNothing().when(emailService).sendResetPasswordEmail(any(Email.class), anyString());

        forgotPasswordUseCase.execute(userEmail);

        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService).generateResetPasswordToken();
        verify(userRepository).save(validUser);
        verify(emailService).sendResetPasswordEmail(any(Email.class), eq(resetToken));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> forgotPasswordUseCase.execute("nonexistent@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nenhum usuário encontrado com o email: nonexistent@email.com");

        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService, never()).generateResetPasswordToken();
        verify(emailService, never()).sendResetPasswordEmail(any(), any());
    }

    @Test
    @DisplayName("Should send email with reset token")
    void shouldSendEmailWithResetToken() {

        String resetToken = "reset-token-xyz";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateResetPasswordToken()).thenReturn(resetToken);
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        doNothing().when(emailService).sendResetPasswordEmail(any(Email.class), anyString());

        forgotPasswordUseCase.execute(userEmail);

        verify(emailService).sendResetPasswordEmail(any(Email.class), eq(resetToken));
    }

    @Test
    @DisplayName("Should save user with reset token")
    void shouldSaveUserWithResetToken() {

        String resetToken = "reset-token-abc";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateResetPasswordToken()).thenReturn(resetToken);
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        doNothing().when(emailService).sendResetPasswordEmail(any(Email.class), anyString());

        forgotPasswordUseCase.execute(userEmail);

        verify(userRepository).save(validUser);
    }

    @Test
    @DisplayName("Should verify operations happen in correct order")
    void shouldVerifyOperationsOrder() {

        String resetToken = "reset-token-123";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateResetPasswordToken()).thenReturn(resetToken);
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        doNothing().when(emailService).sendResetPasswordEmail(any(Email.class), anyString());

        forgotPasswordUseCase.execute(userEmail);

        var inOrder = inOrder(userRepository, tokenService, emailService);
        inOrder.verify(userRepository).findByEmail(any(Email.class));
        inOrder.verify(tokenService).generateResetPasswordToken();
        inOrder.verify(userRepository).save(validUser);
        inOrder.verify(emailService).sendResetPasswordEmail(any(Email.class), eq(resetToken));
    }
}
