package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.customer.entity.CustomerEntity;
import com.sales.infrastructure.persistence.customer.repository.CustomerPanacheRepository;
import com.sales.infrastructure.rest.report.dto.NewCustomerData;
import com.sales.infrastructure.rest.report.dto.NewCustomersResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetNewCustomersUseCase Tests")
class GetNewCustomersUseCaseTest {

    @Mock
    private CustomerPanacheRepository customerRepository;

    @InjectMocks
    private GetNewCustomersUseCase getNewCustomersUseCase;

    private List<CustomerEntity> mockCustomers;
    private int targetYear = 2024;

    @BeforeEach
    void setUp() {
        mockCustomers = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return new customers for given year successfully")
    void shouldReturnNewCustomersForGivenYearSuccessfully() {

        CustomerEntity customer1 = createCustomerEntity(
                "CUST001",
                "João Silva Santos",
                LocalDate.of(1990, 5, 15)
        );

        CustomerEntity customer2 = createCustomerEntity(
                "CUST002",
                "Maria Oliveira Costa",
                LocalDate.of(1985, 8, 20)
        );

        CustomerEntity customer3 = createCustomerEntity(
                "CUST003",
                "Pedro Santos Lima",
                LocalDate.of(1995, 3, 10)
        );

        mockCustomers.add(customer1);
        mockCustomers.add(customer2);
        mockCustomers.add(customer3);

        when(customerRepository.findByRegistrationYear(targetYear)).thenReturn(mockCustomers);

        NewCustomersResponse result = getNewCustomersUseCase.execute(targetYear);

        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(targetYear);
        assertThat(result.getCustomers()).hasSize(3);

        NewCustomerData firstCustomer = result.getCustomers().get(0);
        assertThat(firstCustomer.getCode()).isEqualTo("CUST001");
        assertThat(firstCustomer.getFullName()).isEqualTo("João Silva Santos");
        assertThat(firstCustomer.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 15));

        verify(customerRepository).findByRegistrationYear(targetYear);
    }

    @Test
    @DisplayName("Should return empty list when no customers registered in year")
    void shouldReturnEmptyListWhenNoCustomersRegisteredInYear() {

        when(customerRepository.findByRegistrationYear(targetYear)).thenReturn(Collections.emptyList());

        NewCustomersResponse result = getNewCustomersUseCase.execute(targetYear);

        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(targetYear);
        assertThat(result.getCustomers()).isEmpty();

        verify(customerRepository).findByRegistrationYear(targetYear);
    }

    @Test
    @DisplayName("Should work with different years")
    void shouldWorkWithDifferentYears() {

        int year2023 = 2023;
        when(customerRepository.findByRegistrationYear(year2023)).thenReturn(Collections.emptyList());

        NewCustomersResponse result = getNewCustomersUseCase.execute(year2023);

        assertThat(result.getYear()).isEqualTo(year2023);
        verify(customerRepository).findByRegistrationYear(year2023);
    }

    @Test
    @DisplayName("Should verify repository interaction")
    void shouldVerifyRepositoryInteraction() {

        when(customerRepository.findByRegistrationYear(targetYear)).thenReturn(Collections.emptyList());

        getNewCustomersUseCase.execute(targetYear);

        verify(customerRepository, times(1)).findByRegistrationYear(targetYear);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    @DisplayName("Should map all customer fields correctly")
    void shouldMapAllCustomerFieldsCorrectly() {

        CustomerEntity customer = createCustomerEntity(
                "CUST999",
                "Ana Paula Rodrigues",
                LocalDate.of(1992, 11, 25)
        );
        mockCustomers.add(customer);

        when(customerRepository.findByRegistrationYear(targetYear)).thenReturn(mockCustomers);

        NewCustomersResponse result = getNewCustomersUseCase.execute(targetYear);

        NewCustomerData customerData = result.getCustomers().get(0);
        assertThat(customerData.getCode()).isEqualTo("CUST999");
        assertThat(customerData.getFullName()).isEqualTo("Ana Paula Rodrigues");
        assertThat(customerData.getBirthDate()).isEqualTo(LocalDate.of(1992, 11, 25));
    }

    @Test
    @DisplayName("Should handle multiple customers correctly")
    void shouldHandleMultipleCustomersCorrectly() {

        for (int i = 1; i <= 5; i++) {
            CustomerEntity customer = createCustomerEntity(
                    "CUST00" + i,
                    "Customer " + i,
                    LocalDate.of(1990, 1, i)
            );
            mockCustomers.add(customer);
        }

        when(customerRepository.findByRegistrationYear(targetYear)).thenReturn(mockCustomers);

        NewCustomersResponse result = getNewCustomersUseCase.execute(targetYear);

        assertThat(result.getCustomers()).hasSize(5);
        assertThat(result.getCustomers().get(0).getCode()).isEqualTo("CUST001");
        assertThat(result.getCustomers().get(4).getCode()).isEqualTo("CUST005");
    }

    private CustomerEntity createCustomerEntity(String code, String fullName, LocalDate birthDate) {
        CustomerEntity entity = new CustomerEntity();
        entity.setCode(code);
        entity.setFullName(fullName);
        entity.setBirthDate(birthDate);
        entity.setMotherName("Mãe de " + fullName);
        entity.setCpf("12345678909");
        entity.setRg("MG1234567");
        entity.setZipCode("30130100");
        entity.setStreet("Rua Teste");
        entity.setNumber("100");
        entity.setNeighborhood("Centro");
        entity.setCity("Belo Horizonte");
        entity.setState("MG");
        entity.setPhone("31987654321");
        entity.setEmail(code.toLowerCase() + "@email.com");
        return entity;
    }
}
