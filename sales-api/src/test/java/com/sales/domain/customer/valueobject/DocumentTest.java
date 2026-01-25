package com.sales.domain.customer.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DocumentTest {

    @Test
    void shouldCreateValidDocument() {
        Document document = new Document("12345678901", "123456789");
        assertThat(document.getCpf()).isEqualTo("12345678901");
        assertThat(document.getRg()).isEqualTo("123456789");
    }

    @Test
    void shouldAcceptCPFWithMask() {
        Document document = new Document("123.456.789-01", "123456789");
        assertThat(document.getCpf()).isEqualTo("12345678901");
        assertThat(document.getFormattedCPF()).isEqualTo("123.456.789-01");
    }

    @Test
    void shouldFailWhenCPFIsEmpty() {
        assertThatThrownBy(() -> new Document("", "123456789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF cannot be empty");
    }

    @Test
    void shouldFailWhenCPFHasInvalidLength() {
        assertThatThrownBy(() -> new Document("123456789", "123456789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF must have 11 digits");
    }

    @Test
    void shouldFailWhenCPFIsInvalid() {
        assertThatThrownBy(() -> new Document("11111111111", "123456789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid CPF");
    }

    @Test
    void shouldValidateCorrectCPF() {
        assertThatCode(() -> new Document("12345678909", "123456789"))
                .doesNotThrowAnyException();
    }
}
