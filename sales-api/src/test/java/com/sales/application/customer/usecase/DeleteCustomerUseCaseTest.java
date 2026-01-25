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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCustomerUseCase Tests")
class DeleteCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DeleteCustomerUseCase deleteCustomerUseCase;

    private Customer existingCustomer;

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
    }

    @Test
    @DisplayName("Should delete customer successfully when customer exists")
    void shouldDeleteCustomerSuccessfully() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        doNothing().when(customerRepository).deleteById(1L);

        deleteCustomerUseCase.execute(1L);

        verify(customerRepository).findById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteCustomerUseCase.execute(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found with id: 999");

        verify(customerRepository).findById(999L);
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should verify repository interactions in correct order")
    void shouldVerifyRepositoryInteractionsInOrder() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        doNothing().when(customerRepository).deleteById(1L);

        deleteCustomerUseCase.execute(1L);

        var inOrder = inOrder(customerRepository);
        inOrder.verify(customerRepository).findById(1L);
        inOrder.verify(customerRepository).deleteById(1L);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    @DisplayName("Should delete customer only once")
    void shouldDeleteCustomerOnlyOnce() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        doNothing().when(customerRepository).deleteById(1L);

        deleteCustomerUseCase.execute(1L);

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if customer exists before deleting")
    void shouldCheckIfCustomerExistsBeforeDeleting() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        doNothing().when(customerRepository).deleteById(1L);

        deleteCustomerUseCase.execute(1L);

        var inOrder = inOrder(customerRepository);
        inOrder.verify(customerRepository).findById(1L);
        inOrder.verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should not delete when customer ID is null")
    void shouldNotDeleteWhenCustomerIdIsNull() {

        when(customerRepository.findById(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteCustomerUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found with id: null");

        verify(customerRepository, never()).deleteById(anyLong());
    }
}
