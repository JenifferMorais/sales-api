package com.sales.infrastructure.rest.customer.dto;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerMapper();
    }

    @Test
    void shouldMapCreateRequestToDomain() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setCode("CUST001");
        request.setFullName("João Silva");
        request.setMotherName("Maria Silva");
        request.setCpf("123.456.789-09");
        request.setRg("123456789");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCellPhone("(11) 98765-4321");
        request.setEmail("joao@example.com");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setZipCode("12345-678");
        addressDTO.setStreet("Rua Teste");
        addressDTO.setNumber("100");
        addressDTO.setComplement("Apto 1");
        addressDTO.setNeighborhood("Centro");
        addressDTO.setCity("São Paulo");
        addressDTO.setState("SP");
        request.setAddress(addressDTO);

        Customer customer = mapper.toDomain(request);

        assertThat(customer.getCode()).isEqualTo("CUST001");
        assertThat(customer.getFullName()).isEqualTo("João Silva");
        assertThat(customer.getMotherName()).isEqualTo("Maria Silva");
        assertThat(customer.getDocument().getCpf()).isEqualTo("12345678909");
        assertThat(customer.getDocument().getRg()).isEqualTo("123456789");
        assertThat(customer.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(customer.getCellPhone()).isEqualTo("11987654321");
        assertThat(customer.getEmail()).isEqualTo("joao@example.com");
        assertThat(customer.getAddress().getCity()).isEqualTo("São Paulo");
    }

    @Test
    void shouldMapDomainToResponse() {
        Document document = new Document("12345678909", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");
        Customer customer = new Customer("CUST001", "João Silva", "Maria Silva",
                document, address, LocalDate.of(1990, 1, 1), "11987654321", "joao@example.com");

        CustomerResponse response = mapper.toResponse(customer);

        assertThat(response.getCode()).isEqualTo("CUST001");
        assertThat(response.getFullName()).isEqualTo("João Silva");
        assertThat(response.getCpf()).contains("123.456.789-09");
        assertThat(response.getEmail()).isEqualTo("joao@example.com");
        assertThat(response.getAddress().getCity()).isEqualTo("São Paulo");
    }

    @Test
    void shouldMapAddressDTOToAddress() {
        AddressDTO dto = new AddressDTO();
        dto.setZipCode("12345-678");
        dto.setStreet("Rua Teste");
        dto.setNumber("100");
        dto.setComplement("Apto 1");
        dto.setNeighborhood("Centro");
        dto.setCity("São Paulo");
        dto.setState("SP");

        Address address = mapper.toAddress(dto);

        assertThat(address.getStreet()).isEqualTo("Rua Teste");
        assertThat(address.getNumber()).isEqualTo("100");
        assertThat(address.getCity()).isEqualTo("São Paulo");
        assertThat(address.getState()).isEqualTo("SP");
    }
}
