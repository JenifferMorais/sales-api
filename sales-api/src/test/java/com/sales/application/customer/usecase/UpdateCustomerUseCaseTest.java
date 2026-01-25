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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCustomerUseCase Tests")
class UpdateCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private UpdateCustomerUseCase updateCustomerUseCase;

    private Customer existingCustomer;
    private Address newAddress;

    @BeforeEach
    void setUp() {
        Document document = Document.create("12345678901", "MG1234567");
        Address address = Address.create(
                "30130100",
                "Av. Afonso Pena",
                "1500",
                "Apt 101",
                "Centro",
                "Belo Horizonte",
                "MG"
        );
        existingCustomer = Customer.create(
                "CUST001",
                "John Silva Santos",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 5, 15),
                "31987654321",
                "john.silva@email.com"
        );

        newAddress = Address.create(
                "30140000",
                "Rua da Bahia",
                "2000",
                "Apt 202",
                "Centro",
                "Belo Horizonte",
                "MG"
        );
    }

    @Test
    @DisplayName("Should update customer successfully when customer exists")
    void shouldUpdateCustomerSuccessfully() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        Customer result = updateCustomerUseCase.execute(
                1L,
                "John Silva Santos Updated",
                "Maria Silva Updated",
                newAddress,
                LocalDate.of(1990, 5, 15),
                "31999999999",
                "john.silva@email.com"
        );

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("John Silva Santos Updated");
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateCustomerUseCase.execute(
                999L,
                "New Name",
                "New Mother",
                newAddress,
                LocalDate.of(1990, 1, 1),
                "31999999999",
                "new@email.com"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found with id: 999");

        verify(customerRepository).findById(999L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when email already in use by another customer")
    void shouldThrowExceptionWhenEmailAlreadyInUse() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByEmail("other@email.com")).thenReturn(true);

        assertThatThrownBy(() -> updateCustomerUseCase.execute(
                1L,
                "John Silva Santos",
                "Maria Silva",
                newAddress,
                LocalDate.of(1990, 5, 15),
                "31987654321",
                "other@email.com"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email other@email.com is already in use");

        verify(customerRepository).findById(1L);
        verify(customerRepository).existsByEmail("other@email.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should allow same email when updating same customer")
    void shouldAllowSameEmailForSameCustomer() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        Customer result = updateCustomerUseCase.execute(
                1L,
                "John Silva Santos Updated",
                "Maria Silva",
                newAddress,
                LocalDate.of(1990, 5, 15),
                "31987654321",
                "john.silva@email.com"
        );

        assertThat(result).isNotNull();
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).existsByEmail(anyString());
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    @DisplayName("Should update customer address successfully")
    void shouldUpdateCustomerAddress() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        Customer result = updateCustomerUseCase.execute(
                1L,
                existingCustomer.getFullName(),
                existingCustomer.getMotherName(),
                newAddress,
                existingCustomer.getBirthDate(),
                existingCustomer.getCellPhone(),
                existingCustomer.getEmail()
        );

        assertThat(result.getAddress()).isEqualTo(newAddress);
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    @DisplayName("Should verify repository interactions in correct order")
    void shouldVerifyRepositoryInteractionsInOrder() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        updateCustomerUseCase.execute(
                1L,
                "Updated Name",
                "Updated Mother",
                newAddress,
                LocalDate.of(1990, 5, 15),
                "31999999999",
                "john.silva@email.com"
        );

        var inOrder = inOrder(customerRepository);
        inOrder.verify(customerRepository).findById(1L);
        inOrder.verify(customerRepository).save(existingCustomer);
    }
}
