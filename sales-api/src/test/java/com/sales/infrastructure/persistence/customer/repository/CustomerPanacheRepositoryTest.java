package com.sales.infrastructure.persistence.customer.repository;

import com.sales.infrastructure.persistence.customer.entity.CustomerEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("CustomerPanacheRepository Tests")
class CustomerPanacheRepositoryTest {

    @Inject
    CustomerPanacheRepository repository;

    private CustomerEntity testCustomer;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar base de dados
        repository.deleteAll();

        // Criar cliente de teste
        testCustomer = new CustomerEntity();
        testCustomer.setCode("CUST0001");
        testCustomer.setFullName("João Silva");
        testCustomer.setMotherName("Maria Silva");
        testCustomer.setCpf("12345678901");
        testCustomer.setRg("123456789");
        testCustomer.setZipCode("12345678");
        testCustomer.setStreet("Rua Teste");
        testCustomer.setNumber("123");
        testCustomer.setComplement("Apto 1");
        testCustomer.setNeighborhood("Centro");
        testCustomer.setCity("São Paulo");
        testCustomer.setState("SP");
        testCustomer.setBirthDate(LocalDate.of(1990, 1, 1));
        testCustomer.setCellPhone("11987654321");
        testCustomer.setEmail("joao@example.com");

        repository.persist(testCustomer);
    }

    @Test
    @DisplayName("Should find customer by code")
    void shouldFindCustomerByCode() {
        Optional<CustomerEntity> result = repository.findByCode("CUST0001");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("CUST0001");
        assertThat(result.get().getFullName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Should return empty when customer not found by code")
    void shouldReturnEmptyWhenCustomerNotFoundByCode() {
        Optional<CustomerEntity> result = repository.findByCode("CUST9999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find customer by CPF")
    void shouldFindCustomerByCpf() {
        Optional<CustomerEntity> result = repository.findByCpf("12345678901");

        assertThat(result).isPresent();
        assertThat(result.get().getCpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Should return empty when customer not found by CPF")
    void shouldReturnEmptyWhenCustomerNotFoundByCpf() {
        Optional<CustomerEntity> result = repository.findByCpf("99999999999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find customer by email")
    void shouldFindCustomerByEmail() {
        Optional<CustomerEntity> result = repository.findByEmail("joao@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    @DisplayName("Should return empty when customer not found by email")
    void shouldReturnEmptyWhenCustomerNotFoundByEmail() {
        Optional<CustomerEntity> result = repository.findByEmail("notfound@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should find customers by name containing")
    void shouldFindCustomersByNameContaining() {
        List<CustomerEntity> result = repository.findByNameContaining("Silva");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).contains("Silva");
    }

    @Test
    @Transactional
    @DisplayName("Should find customers by name containing case insensitive")
    void shouldFindCustomersByNameContainingCaseInsensitive() {
        List<CustomerEntity> result = repository.findByNameContaining("silva");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).containsIgnoringCase("silva");
    }

    @Test
    @DisplayName("Should check if customer exists by code")
    void shouldCheckIfCustomerExistsByCode() {
        boolean exists = repository.existsByCode("CUST0001");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when customer does not exist by code")
    void shouldReturnFalseWhenCustomerDoesNotExistByCode() {
        boolean exists = repository.existsByCode("CUST9999");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should check if customer exists by CPF")
    void shouldCheckIfCustomerExistsByCpf() {
        boolean exists = repository.existsByCpf("12345678901");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when customer does not exist by CPF")
    void shouldReturnFalseWhenCustomerDoesNotExistByCpf() {
        boolean exists = repository.existsByCpf("99999999999");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should check if customer exists by email")
    void shouldCheckIfCustomerExistsByEmail() {
        boolean exists = repository.existsByEmail("joao@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when customer does not exist by email")
    void shouldReturnFalseWhenCustomerDoesNotExistByEmail() {
        boolean exists = repository.existsByEmail("notfound@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find last customer code")
    void shouldFindLastCustomerCode() {
        Optional<String> lastCode = repository.findLastCode();

        assertThat(lastCode).isPresent();
        assertThat(lastCode.get()).isEqualTo("CUST0001");
    }

    @Test
    @Transactional
    @DisplayName("Should find last code among multiple customers")
    void shouldFindLastCodeAmongMultipleCustomers() {
        CustomerEntity customer2 = createTestCustomer("CUST0002", "Pedro Santos", "98765432100", "pedro@example.com");
        repository.persist(customer2);

        Optional<String> lastCode = repository.findLastCode();

        assertThat(lastCode).isPresent();
        assertThat(lastCode.get()).isEqualTo("CUST0002");
    }

    @Test
    @DisplayName("Should count all customers")
    void shouldCountAllCustomers() {
        long count = repository.countAll();

        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Should search customers with filter")
    void shouldSearchCustomersWithFilter() {
        List<CustomerEntity> result = repository.search("Silva", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).contains("Silva");
    }

    @Test
    @Transactional
    @DisplayName("Should search customers without filter")
    void shouldSearchCustomersWithoutFilter() {
        List<CustomerEntity> result = repository.search("", 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should search customers with pagination")
    void shouldSearchCustomersWithPagination() {
        // Criar mais clientes
        repository.persist(createTestCustomer("CUST0002", "Pedro Santos", "98765432100", "pedro@example.com"));
        repository.persist(createTestCustomer("CUST0003", "Ana Costa", "11122233344", "ana@example.com"));

        List<CustomerEntity> page1 = repository.search("", 0, 2);
        List<CustomerEntity> page2 = repository.search("", 1, 2);

        assertThat(page1).hasSize(2);
        assertThat(page2).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("Should count search results with filter")
    void shouldCountSearchResultsWithFilter() {
        long count = repository.countSearch("Silva");

        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Should count search results without filter")
    void shouldCountSearchResultsWithoutFilter() {
        long count = repository.countSearch("");

        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Should search by email in filter")
    void shouldSearchByEmailInFilter() {
        List<CustomerEntity> result = repository.search("joao@example.com", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    @Transactional
    @DisplayName("Should search by code in filter")
    void shouldSearchByCodeInFilter() {
        List<CustomerEntity> result = repository.search("CUST0001", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("CUST0001");
    }

    @Test
    @Transactional
    @DisplayName("Should search by CPF in filter")
    void shouldSearchByCpfInFilter() {
        List<CustomerEntity> result = repository.search("12345678901", 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCpf()).isEqualTo("12345678901");
    }

    @Test
    @Transactional
    @DisplayName("Should find customers by registration year")
    void shouldFindCustomersByRegistrationYear() {
        int currentYear = LocalDateTime.now().getYear();
        List<CustomerEntity> result = repository.findByRegistrationYear(currentYear);

        assertThat(result).isNotEmpty();
    }

    // Helper method
    private CustomerEntity createTestCustomer(String code, String name, String cpf, String email) {
        CustomerEntity customer = new CustomerEntity();
        customer.setCode(code);
        customer.setFullName(name);
        customer.setMotherName("Mother Name");
        customer.setCpf(cpf);
        customer.setRg("987654321");
        customer.setZipCode("87654321");
        customer.setStreet("Rua Teste");
        customer.setNumber("456");
        customer.setNeighborhood("Bairro");
        customer.setCity("Rio de Janeiro");
        customer.setState("RJ");
        customer.setBirthDate(LocalDate.of(1985, 5, 15));
        customer.setCellPhone("21987654321");
        customer.setEmail(email);
        return customer;
    }
}
