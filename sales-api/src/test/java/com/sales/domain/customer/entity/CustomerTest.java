package com.sales.domain.customer.entity;

import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateValidCustomer() {
        Document document = new Document("12345678901", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");

        Customer customer = new Customer(
                "CUST001",
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        );

        assertThat(customer.getCode()).isEqualTo("CUST001");
        assertThat(customer.getFullName()).isEqualTo("João Silva");
        assertThat(customer.getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    void shouldFailWhenCodeIsEmpty() {
        Document document = new Document("12345678901", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");

        assertThatThrownBy(() -> new Customer(
                "",
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Code cannot be empty");
    }

    @Test
    void shouldFailWhenBirthDateIsInFuture() {
        Document document = new Document("12345678901", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");

        assertThatThrownBy(() -> new Customer(
                "CUST001",
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.now().plusDays(1),
                "11987654321",
                "joao@example.com"
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Birth date cannot be in the future");
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        Document document = new Document("12345678901", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");

        assertThatThrownBy(() -> new Customer(
                "CUST001",
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "invalid-email"
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid email format");
    }

    @Test
    void shouldUpdateCustomerInfo() {
        Document document = new Document("12345678901", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");

        Customer customer = new Customer(
                "CUST001",
                "João Silva",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 1, 1),
                "11987654321",
                "joao@example.com"
        );

        Address newAddress = new Address("87654321", "Rua Nova", "200", "",
                "Bairro Novo", "Rio de Janeiro", "RJ");
        customer.updateInfo(
                "João Santos",
                "Maria Santos",
                newAddress,
                LocalDate.of(1990, 1, 1),
                "21987654321",
                "joao.santos@example.com"
        );

        assertThat(customer.getFullName()).isEqualTo("João Santos");
        assertThat(customer.getEmail()).isEqualTo("joao.santos@example.com");
        assertThat(customer.getAddress().getCity()).isEqualTo("Rio de Janeiro");
    }
}
