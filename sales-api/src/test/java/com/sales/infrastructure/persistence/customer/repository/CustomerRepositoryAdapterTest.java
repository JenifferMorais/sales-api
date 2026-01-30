package com.sales.infrastructure.persistence.customer.repository;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.domain.shared.PageResult;
import com.sales.infrastructure.persistence.customer.entity.CustomerEntity;
import com.sales.infrastructure.persistence.customer.service.CustomerCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRepositoryAdapter Tests")
class CustomerRepositoryAdapterTest {

    @Mock
    private CustomerPanacheRepository panacheRepository;

    @Mock
    private CustomerCodeGenerator codeGenerator;

    @InjectMocks
    private CustomerRepositoryAdapter repositoryAdapter;

    private Customer testCustomer;
    private CustomerEntity testEntity;
    private Document testDocument;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testDocument = Document.fromDatabase("12345678901", "123456789");
        testAddress = new Address(
                "12345678",
                "Rua Teste",
                "123",
                "Apto 1",
                "Centro",
                "São Paulo",
                "SP"
        );

        testCustomer = new Customer(
                null,
                null,
                "João Silva",
                "Maria Silva",
                testDocument,
                testAddress,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com",
                null
        );

        testEntity = createCustomerEntity(1L, "CUST001");
    }

    @Test
    @DisplayName("Should save new customer and generate code")
    void shouldSaveNewCustomerAndGenerateCode() {
        when(codeGenerator.generateNextCode()).thenReturn("CUST001");
        doNothing().when(panacheRepository).persist(any(CustomerEntity.class));

        Customer result = repositoryAdapter.save(testCustomer);

        verify(codeGenerator).generateNextCode();
        verify(panacheRepository).persist(any(CustomerEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should save new customer with provided code")
    void shouldSaveNewCustomerWithProvidedCode() {
        Customer customerWithCode = new Customer(
                null,
                "CUST999",
                "João Silva",
                "Maria Silva",
                testDocument,
                testAddress,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com",
                null
        );

        doNothing().when(panacheRepository).persist(any(CustomerEntity.class));

        Customer result = repositoryAdapter.save(customerWithCode);

        verify(codeGenerator, never()).generateNextCode();
        verify(panacheRepository).persist(any(CustomerEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should update existing customer")
    void shouldUpdateExistingCustomer() {
        Customer existingCustomer = new Customer(
                1L,
                "CUST001",
                "João Silva Updated",
                "Maria Silva",
                testDocument,
                testAddress,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao.updated@example.com",
                LocalDateTime.now()
        );

        when(panacheRepository.findById(1L)).thenReturn(testEntity);

        Customer result = repositoryAdapter.save(existingCustomer);

        verify(panacheRepository).findById(1L);
        verify(panacheRepository, never()).persist(any(CustomerEntity.class));
        assertThat(result).isNotNull();
        assertThat(testEntity.getFullName()).isEqualTo("João Silva Updated");
        assertThat(testEntity.getEmail()).isEqualTo("joao.updated@example.com");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent customer")
    void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
        Customer customerWithId = new Customer(
                999L,
                "CUST999",
                "João Silva",
                "Maria Silva",
                testDocument,
                testAddress,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com",
                LocalDateTime.now()
        );

        when(panacheRepository.findById(999L)).thenReturn(null);

        assertThatThrownBy(() -> repositoryAdapter.save(customerWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente não encontrado com id: 999");

        verify(panacheRepository).findById(999L);
        verify(panacheRepository, never()).persist(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should find customer by ID")
    void shouldFindCustomerById() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Customer> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCode()).isEqualTo("CUST001");
        verify(panacheRepository).findByIdOptional(1L);
    }

    @Test
    @DisplayName("Should return empty when customer not found by ID")
    void shouldReturnEmptyWhenCustomerNotFoundById() {
        when(panacheRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Optional<Customer> result = repositoryAdapter.findById(999L);

        assertThat(result).isEmpty();
        verify(panacheRepository).findByIdOptional(999L);
    }

    @Test
    @DisplayName("Should find customer by code")
    void shouldFindCustomerByCode() {
        when(panacheRepository.findByCode("CUST001")).thenReturn(Optional.of(testEntity));

        Optional<Customer> result = repositoryAdapter.findByCode("CUST001");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("CUST001");
        verify(panacheRepository).findByCode("CUST001");
    }

    @Test
    @DisplayName("Should find customer by CPF")
    void shouldFindCustomerByCpf() {
        when(panacheRepository.findByCpf("12345678901")).thenReturn(Optional.of(testEntity));

        Optional<Customer> result = repositoryAdapter.findByCpf("12345678901");

        assertThat(result).isPresent();
        assertThat(result.get().getDocument().getCpf()).isEqualTo("12345678901");
        verify(panacheRepository).findByCpf("12345678901");
    }

    @Test
    @DisplayName("Should find customer by email")
    void shouldFindCustomerByEmail() {
        when(panacheRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(testEntity));

        Optional<Customer> result = repositoryAdapter.findByEmail("joao@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("joao@example.com");
        verify(panacheRepository).findByEmail("joao@example.com");
    }

    @Test
    @DisplayName("Should find all customers")
    void shouldFindAllCustomers() {
        CustomerEntity entity2 = createCustomerEntity(2L, "CUST002");
        when(panacheRepository.listAll()).thenReturn(Arrays.asList(testEntity, entity2));

        List<Customer> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("CUST001");
        assertThat(result.get(1).getCode()).isEqualTo("CUST002");
        verify(panacheRepository).listAll();
    }

    @Test
    @DisplayName("Should find customers by name containing")
    void shouldFindCustomersByNameContaining() {
        when(panacheRepository.findByNameContaining("Silva")).thenReturn(Arrays.asList(testEntity));

        List<Customer> result = repositoryAdapter.findByNameContaining("Silva");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).contains("Silva");
        verify(panacheRepository).findByNameContaining("Silva");
    }

    @Test
    @DisplayName("Should delete customer by ID")
    void shouldDeleteCustomerById() {
        when(panacheRepository.deleteById(1L)).thenReturn(true);

        repositoryAdapter.deleteById(1L);

        verify(panacheRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if customer exists by code")
    void shouldCheckIfCustomerExistsByCode() {
        when(panacheRepository.existsByCode("CUST001")).thenReturn(true);

        boolean result = repositoryAdapter.existsByCode("CUST001");

        assertThat(result).isTrue();
        verify(panacheRepository).existsByCode("CUST001");
    }

    @Test
    @DisplayName("Should check if customer exists by CPF")
    void shouldCheckIfCustomerExistsByCpf() {
        when(panacheRepository.existsByCpf("12345678901")).thenReturn(true);

        boolean result = repositoryAdapter.existsByCpf("12345678901");

        assertThat(result).isTrue();
        verify(panacheRepository).existsByCpf("12345678901");
    }

    @Test
    @DisplayName("Should check if customer exists by email")
    void shouldCheckIfCustomerExistsByEmail() {
        when(panacheRepository.existsByEmail("joao@example.com")).thenReturn(true);

        boolean result = repositoryAdapter.existsByEmail("joao@example.com");

        assertThat(result).isTrue();
        verify(panacheRepository).existsByEmail("joao@example.com");
    }

    @Test
    @DisplayName("Should search customers with pagination")
    void shouldSearchCustomersWithPagination() {
        CustomerEntity entity2 = createCustomerEntity(2L, "CUST002");
        when(panacheRepository.search("Silva", 0, 10)).thenReturn(Arrays.asList(testEntity, entity2));
        when(panacheRepository.countSearch("Silva")).thenReturn(2L);

        PageResult<Customer> result = repositoryAdapter.search("Silva", 0, 10);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        verify(panacheRepository).search("Silva", 0, 10);
        verify(panacheRepository).countSearch("Silva");
    }

    @Test
    @DisplayName("Should convert entity to domain correctly")
    void shouldConvertEntityToDomainCorrectly() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Customer> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        Customer customer = result.get();
        assertThat(customer.getId()).isEqualTo(testEntity.getId());
        assertThat(customer.getCode()).isEqualTo(testEntity.getCode());
        assertThat(customer.getFullName()).isEqualTo(testEntity.getFullName());
        assertThat(customer.getMotherName()).isEqualTo(testEntity.getMotherName());
        assertThat(customer.getDocument().getCpf()).isEqualTo(testEntity.getCpf());
        assertThat(customer.getDocument().getRg()).isEqualTo(testEntity.getRg());
        assertThat(customer.getAddress().getZipCode()).isEqualTo(testEntity.getZipCode());
        assertThat(customer.getAddress().getStreet()).isEqualTo(testEntity.getStreet());
        assertThat(customer.getAddress().getCity()).isEqualTo(testEntity.getCity());
        assertThat(customer.getBirthDate()).isEqualTo(testEntity.getBirthDate());
        assertThat(customer.getCellPhone()).isEqualTo(testEntity.getCellPhone());
        assertThat(customer.getEmail()).isEqualTo(testEntity.getEmail());
    }

    @Test
    @DisplayName("Should update all customer fields")
    void shouldUpdateAllCustomerFields() {
        Document newDocument = Document.fromDatabase("98765432109", "987654321");
        Address newAddress = new Address(
                "87654321",
                "Rua Nova",
                "456",
                "Casa",
                "Bairro Novo",
                "Rio de Janeiro",
                "RJ"
        );

        Customer updatedCustomer = new Customer(
                1L,
                "CUST001",
                "João Silva Atualizado",
                "Maria Silva Atualizada",
                newDocument,
                newAddress,
                LocalDate.of(1991, 2, 2),
                "21987654321",
                "joao.novo@example.com",
                LocalDateTime.now()
        );

        when(panacheRepository.findById(1L)).thenReturn(testEntity);

        repositoryAdapter.save(updatedCustomer);

        assertThat(testEntity.getFullName()).isEqualTo("João Silva Atualizado");
        assertThat(testEntity.getMotherName()).isEqualTo("Maria Silva Atualizada");
        assertThat(testEntity.getCpf()).isEqualTo("98765432109");
        assertThat(testEntity.getRg()).isEqualTo("987654321");
        assertThat(testEntity.getZipCode()).isEqualTo("87654321");
        assertThat(testEntity.getStreet()).isEqualTo("Rua Nova");
        assertThat(testEntity.getNumber()).isEqualTo("456");
        assertThat(testEntity.getComplement()).isEqualTo("Casa");
        assertThat(testEntity.getNeighborhood()).isEqualTo("Bairro Novo");
        assertThat(testEntity.getCity()).isEqualTo("Rio de Janeiro");
        assertThat(testEntity.getState()).isEqualTo("RJ");
        assertThat(testEntity.getBirthDate()).isEqualTo(LocalDate.of(1991, 2, 2));
        assertThat(testEntity.getCellPhone()).isEqualTo("21987654321");
        assertThat(testEntity.getEmail()).isEqualTo("joao.novo@example.com");
    }

    // Helper method para criar CustomerEntity
    private CustomerEntity createCustomerEntity(Long id, String code) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setFullName("João Silva");
        entity.setMotherName("Maria Silva");
        entity.setCpf("12345678901");
        entity.setRg("123456789");
        entity.setZipCode("12345678");
        entity.setStreet("Rua Teste");
        entity.setNumber("123");
        entity.setComplement("Apto 1");
        entity.setNeighborhood("Centro");
        entity.setCity("São Paulo");
        entity.setState("SP");
        entity.setBirthDate(LocalDate.of(1990, 1, 1));
        entity.setCellPhone("11987654321");
        entity.setEmail("joao@example.com");
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
