package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase Tests")
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User validUser;
    private Email validEmail;
    private String validPassword = "Test@123";

    @BeforeEach
    void setUp() {
        validEmail = new Email("john.silva@email.com");
        Password password = Password.fromPlainText(validPassword);
        validUser = new User("CUST001", validEmail, password);
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {

        String expectedToken = "jwt.token.here";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateAccessToken(validUser)).thenReturn(expectedToken);

        String token = loginUseCase.execute("john.silva@email.com", validPassword);

        assertThat(token).isEqualTo(expectedToken);
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService).generateAccessToken(validUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute("nonexistent@email.com", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");

        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService, never()).generateAccessToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));

        assertThatThrownBy(() -> loginUseCase.execute("john.silva@email.com", "WrongPassword@123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");

        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenService, never()).generateAccessToken(any(User.class));
    }

    @Test
    @DisplayName("Should verify repository and token service interactions")
    void shouldVerifyInteractions() {

        String expectedToken = "jwt.token.here";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateAccessToken(validUser)).thenReturn(expectedToken);

        loginUseCase.execute("john.silva@email.com", validPassword);

        var inOrder = inOrder(userRepository, tokenService);
        inOrder.verify(userRepository).findByEmail(any(Email.class));
        inOrder.verify(tokenService).generateAccessToken(validUser);
        verifyNoMoreInteractions(userRepository, tokenService);
    }

    @Test
    @DisplayName("Should create email value object from string")
    void shouldCreateEmailValueObject() {

        String expectedToken = "jwt.token.here";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(tokenService.generateAccessToken(validUser)).thenReturn(expectedToken);

        loginUseCase.execute("john.silva@email.com", validPassword);

        verify(userRepository).findByEmail(any(Email.class));
    }
}
