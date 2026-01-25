package com.sales.domain.customer.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AddressTest {

    @Test
    void shouldCreateValidAddress() {
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");

        assertThat(address.getZipCode()).isEqualTo("12345678");
        assertThat(address.getStreet()).isEqualTo("Rua Teste");
        assertThat(address.getNumber()).isEqualTo("100");
        assertThat(address.getComplement()).isEqualTo("Apto 1");
        assertThat(address.getNeighborhood()).isEqualTo("Centro");
        assertThat(address.getCity()).isEqualTo("São Paulo");
        assertThat(address.getState()).isEqualTo("SP");
    }

    @Test
    void shouldCreateAddressWithCreateMethod() {
        Address address = Address.create("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");

        assertThat(address.getStreet()).isEqualTo("Rua Teste");
    }

    @Test
    void shouldCleanZipCode() {
        Address address = new Address("12345-678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");

        assertThat(address.getZipCode()).isEqualTo("12345678");
    }

    @Test
    void shouldConvertStateToUpperCase() {
        Address address = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "sp");

        assertThat(address.getState()).isEqualTo("SP");
    }

    @Test
    void shouldFormatZipCode() {
        Address address = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");

        assertThat(address.getFormattedZipCode()).isEqualTo("12345-678");
    }

    @Test
    void shouldFailWhenZipCodeIsNull() {
        assertThatThrownBy(() -> new Address(null, "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CEP não pode estar vazio");
    }

    @Test
    void shouldFailWhenZipCodeIsInvalid() {
        assertThatThrownBy(() -> new Address("123", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CEP deve ter 8 dígitos");
    }

    @Test
    void shouldFailWhenStreetIsNull() {
        assertThatThrownBy(() -> new Address("12345678", null, "100", "",
                "Centro", "São Paulo", "SP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rua não pode estar vazia");
    }

    @Test
    void shouldFailWhenNumberIsNull() {
        assertThatThrownBy(() -> new Address("12345678", "Rua Teste", null, "",
                "Centro", "São Paulo", "SP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número não pode estar vazio");
    }

    @Test
    void shouldFailWhenNeighborhoodIsNull() {
        assertThatThrownBy(() -> new Address("12345678", "Rua Teste", "100", "",
                null, "São Paulo", "SP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bairro não pode estar vazio");
    }

    @Test
    void shouldFailWhenCityIsNull() {
        assertThatThrownBy(() -> new Address("12345678", "Rua Teste", "100", "",
                "Centro", null, "SP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cidade não pode estar vazia");
    }

    @Test
    void shouldFailWhenStateIsNull() {
        assertThatThrownBy(() -> new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado não pode estar vazio");
    }

    @Test
    void shouldFailWhenStateIsInvalid() {
        assertThatThrownBy(() -> new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SPP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado deve ter 2 caracteres");
    }

    @Test
    void shouldBeEqualWhenSameZipCodeStreetAndNumber() {
        Address address1 = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");
        Address address2 = new Address("12345678", "Rua Teste", "100", "Apto 2",
                "Centro", "São Paulo", "SP");

        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void shouldFormatToStringCorrectly() {
        Address address = new Address("12345678", "Rua Teste", "100", "Apto 1",
                "Centro", "São Paulo", "SP");

        String formatted = address.toString();

        assertThat(formatted).contains("Rua Teste");
        assertThat(formatted).contains("100");
        assertThat(formatted).contains("Apto 1");
        assertThat(formatted).contains("Centro");
        assertThat(formatted).contains("São Paulo");
        assertThat(formatted).contains("SP");
        assertThat(formatted).contains("12345-678");
    }

    @Test
    void shouldFormatToStringWithoutComplement() {
        Address address = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");

        String formatted = address.toString();

        assertThat(formatted).contains("Rua Teste");
        assertThat(formatted).doesNotContain("()");
    }
}
