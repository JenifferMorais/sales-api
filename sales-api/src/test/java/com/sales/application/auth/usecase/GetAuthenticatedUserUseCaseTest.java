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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAuthenticatedUserUseCase Tests")
class GetAuthenticatedUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    private User validUser;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        Email email = new Email("john.silva@email.com");
        Password password = Password.fromPlainText("Test@123");
        validUser = new User("CUST001", email, password);
    }

    @Test
    @DisplayName("Should get authenticated user successfully when user exists")
    void shouldGetAuthenticatedUserSuccessfully() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));

        User result = getAuthenticatedUserUseCase.execute(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(validUser);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {

        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getAuthenticatedUserUseCase.execute(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário não encontrado com id: " + nonExistentId);

        verify(userRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should verify repository interaction")
    void shouldVerifyRepositoryInteraction() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));

        getAuthenticatedUserUseCase.execute(userId);

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}
