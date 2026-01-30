package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.infrastructure.persistence.customer.service.CustomerCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerCodeGenerator codeGenerator;

    @InjectMocks
    private CreateCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateCustomer() {
        Document document = new Document("12345678909", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");
        Customer customer = new Customer(
                null,
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        );

        Customer customerWithCode = new Customer(
                "CUST0001",
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        );

        when(codeGenerator.generateNextCode()).thenReturn("CUST0001");
        when(customerRepository.existsByCpf("12345678909")).thenReturn(false);
        when(customerRepository.existsByEmail("joao@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerWithCode);

        Customer result = useCase.execute(customer);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("CUST0001");
        verify(codeGenerator, times(1)).generateNextCode();
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldFailWhenCPFAlreadyExists() {
        Document document = new Document("12345678909", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");
        Customer customer = new Customer(
                null,
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        );

        when(customerRepository.existsByCpf("12345678909")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(customer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF");

        verify(codeGenerator, never()).generateNextCode();
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        Document document = new Document("12345678909", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");
        Customer customer = new Customer(
                null,
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        );

        when(customerRepository.existsByCpf("12345678909")).thenReturn(false);
        when(customerRepository.existsByEmail("joao@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(customer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(codeGenerator, never()).generateNextCode();
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
