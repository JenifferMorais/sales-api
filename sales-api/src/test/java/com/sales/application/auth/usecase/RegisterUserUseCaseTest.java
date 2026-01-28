package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.EmailService;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserUseCase Tests")
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private Customer validCustomer;
    private String customerCode = "CUST001";
    private String email = "john.silva@email.com";
    private String validPassword = "Test@123";

    @BeforeEach
    void setUp() {
        Document document = Document.create("12345678909", "MG1234567");
        Address address = Address.create(
                "30130100",
                "Av. Afonso Pena",
                "1500",
                "Apt 101",
                "Centro",
                "Belo Horizonte",
                "MG"
        );
        validCustomer = Customer.create(
                customerCode,
                "John Silva Santos",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 5, 15),
                "31987654321",
                email
        );
    }

    @Test
    @DisplayName("Should register user successfully with valid data")
    void shouldRegisterUserSuccessfully() {

        when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(validCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByCustomerCode(customerCode)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendWelcomeEmail(any(Email.class), anyString());

        User result = registerUserUseCase.execute(customerCode, email, validPassword);

        assertThat(result).isNotNull();
        verify(customerRepository).findByCode(customerCode);
        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).existsByCustomerCode(customerCode);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(any(Email.class), eq("John Silva Santos"));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registerUserUseCase.execute("NONEXISTENT", email, validPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente não encontrado com código: NONEXISTENT");

        verify(customerRepository).findByCode("NONEXISTENT");
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendWelcomeEmail(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when email already in use")
    void shouldThrowExceptionWhenEmailAlreadyInUse() {

        when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(validCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThatThrownBy(() -> registerUserUseCase.execute(customerCode, email, validPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email john.silva@email.com já está em uso");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when customer already has user")
    void shouldThrowExceptionWhenCustomerAlreadyHasUser() {

        when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(validCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByCustomerCode(customerCode)).thenReturn(true);

        assertThatThrownBy(() -> registerUserUseCase.execute(customerCode, email, validPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Já existe usuário cadastrado para o cliente: CUST001");

        verify(userRepository).existsByCustomerCode(customerCode);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should send welcome email after registration")
    void shouldSendWelcomeEmail() {

        when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(validCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByCustomerCode(customerCode)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendWelcomeEmail(any(Email.class), anyString());

        registerUserUseCase.execute(customerCode, email, validPassword);

        verify(emailService).sendWelcomeEmail(any(Email.class), eq("John Silva Santos"));
    }

    @Test
    @DisplayName("Should verify all validations happen in correct order")
    void shouldVerifyValidationsOrder() {

        when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(validCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByCustomerCode(customerCode)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendWelcomeEmail(any(Email.class), anyString());

        registerUserUseCase.execute(customerCode, email, validPassword);

        var inOrder = inOrder(customerRepository, userRepository, emailService);
        inOrder.verify(customerRepository).findByCode(customerCode);
        inOrder.verify(userRepository).existsByEmail(any(Email.class));
        inOrder.verify(userRepository).existsByCustomerCode(customerCode);
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(emailService).sendWelcomeEmail(any(Email.class), anyString());
    }
}
