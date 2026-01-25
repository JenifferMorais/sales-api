package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.domain.shared.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchCustomersUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SearchCustomersUseCase useCase;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Document document1 = new Document("12345678909", "123456789");
        Address address1 = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");
        customer1 = new Customer(
                "CUST001",
                "João Silva",
                "Maria Silva",
                document1,
                address1,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        );

        Document document2 = new Document("98765432100", "987654321");
        Address address2 = new Address("87654321", "Av. Principal", "200", "Apto 10",
                "Jardim", "Rio de Janeiro", "RJ");
        customer2 = new Customer(
                "CUST002",
                "Maria Santos",
                "Ana Santos",
                document2,
                address2,
                LocalDate.of(1985, 5, 15),
                "21987654321",
                "maria@example.com"
        );
    }

    @Test
    void shouldSearchCustomersWithFilter() {
        String filter = "João";
        PageResult<Customer> expectedPage = new PageResult<>(
                List.of(customer1),
                1L,
                0,
                10
        );

        when(customerRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Customer> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("João Silva");
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(customerRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldSearchCustomersWithoutFilter() {
        String filter = "";
        PageResult<Customer> expectedPage = new PageResult<>(
                List.of(customer1, customer2),
                2L,
                0,
                10
        );

        when(customerRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Customer> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        verify(customerRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldReturnEmptyPageWhenNoCustomersFound() {
        String filter = "NonExistent";
        PageResult<Customer> expectedPage = new PageResult<>(
                List.of(),
                0L,
                0,
                10
        );

        when(customerRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Customer> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
        verify(customerRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldHandlePagination() {
        String filter = "";
        PageResult<Customer> expectedPage = new PageResult<>(
                List.of(customer2),
                2L,
                1,
                1
        );

        when(customerRepository.search(filter, 1, 1)).thenReturn(expectedPage);

        PageResult<Customer> result = useCase.execute(filter, 1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        verify(customerRepository, times(1)).search(filter, 1, 1);
    }
}
