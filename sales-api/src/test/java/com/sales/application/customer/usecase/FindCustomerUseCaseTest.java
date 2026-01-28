package com.sales.application.customer.usecase;

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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindCustomerUseCase Tests")
class FindCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private FindCustomerUseCase findCustomerUseCase;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        Document document1 = Document.create("12345678909", "MG1234567");
        Address address1 = Address.create(
                "30130100",
                "Av. Afonso Pena",
                "1500",
                "Apt 101",
                "Centro",
                "Belo Horizonte",
                "MG"
        );
        customer1 = Customer.create(
                "CUST001",
                "John Silva Santos",
                "Maria Silva",
                document1,
                address1,
                LocalDate.of(1990, 5, 15),
                "31987654321",
                "john.silva@email.com"
        );

        Document document2 = Document.create("52998224725", "SP9876543");
        Address address2 = Address.create(
                "01310100",
                "Av. Paulista",
                "1000",
                null,
                "Bela Vista",
                "São Paulo",
                "SP"
        );
        customer2 = Customer.create(
                "CUST002",
                "Maria Oliveira Costa",
                "Ana Oliveira",
                document2,
                address2,
                LocalDate.of(1985, 8, 20),
                "11987654321",
                "maria.oliveira@email.com"
        );
    }

    @Test
    @DisplayName("Should find customer by ID successfully")
    void shouldFindCustomerById() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));

        Customer result = findCustomerUseCase.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("CUST001");
        assertThat(result.getFullName()).isEqualTo("John Silva Santos");
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when customer not found by ID")
    void shouldThrowExceptionWhenCustomerNotFoundById() {

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findCustomerUseCase.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                ;

        verify(customerRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find customer by code successfully")
    void shouldFindCustomerByCode() {

        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(customer1));

        Customer result = findCustomerUseCase.findByCode("CUST001");

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("CUST001");
        assertThat(result.getEmail()).isEqualTo("john.silva@email.com");
        verify(customerRepository).findByCode("CUST001");
    }

    @Test
    @DisplayName("Should throw exception when customer not found by code")
    void shouldThrowExceptionWhenCustomerNotFoundByCode() {

        when(customerRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findCustomerUseCase.findByCode("NONEXISTENT"))
                .isInstanceOf(IllegalArgumentException.class)
                ;

        verify(customerRepository).findByCode("NONEXISTENT");
    }

    @Test
    @DisplayName("Should find all customers successfully")
    void shouldFindAllCustomers() {

        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = findCustomerUseCase.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(customer1, customer2);
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomers() {

        when(customerRepository.findAll()).thenReturn(List.of());

        List<Customer> result = findCustomerUseCase.findAll();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should find customers by name containing search term")
    void shouldFindCustomersByNameContaining() {

        when(customerRepository.findByNameContaining("Silva")).thenReturn(List.of(customer1));

        List<Customer> result = findCustomerUseCase.findByNameContaining("Silva");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).contains("Silva");
        verify(customerRepository).findByNameContaining("Silva");
    }

    @Test
    @DisplayName("Should return empty list when no customers match search term")
    void shouldReturnEmptyListWhenNoMatch() {

        when(customerRepository.findByNameContaining("Nonexistent")).thenReturn(List.of());

        List<Customer> result = findCustomerUseCase.findByNameContaining("Nonexistent");

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(customerRepository).findByNameContaining("Nonexistent");
    }

    @Test
    @DisplayName("Should find multiple customers by name containing")
    void shouldFindMultipleCustomersByName() {

        when(customerRepository.findByNameContaining("Maria"))
                .thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> result = findCustomerUseCase.findByNameContaining("Maria");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(customerRepository).findByNameContaining("Maria");
    }

    @Test
    @DisplayName("Should call repository only once when finding by ID")
    void shouldCallRepositoryOnceForFindById() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));

        findCustomerUseCase.findById(1L);

        verify(customerRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(customerRepository);
    }
}
