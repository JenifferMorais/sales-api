package com.sales.domain.customer.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DocumentTest {

    @Test
    void shouldCreateValidDocument() {
        Document document = new Document("12345678909", "123456789");
        assertThat(document.getCpf()).isEqualTo("12345678909");
        assertThat(document.getRg()).isEqualTo("123456789");
    }

    @Test
    void shouldAcceptCPFWithMask() {
        Document document = new Document("123.456.789-09", "123456789");
        assertThat(document.getCpf()).isEqualTo("12345678909");
        assertThat(document.getFormattedCPF()).isEqualTo("123.456.789-09");
    }

    @Test
    void shouldFailWhenCPFIsEmpty() {
        assertThatThrownBy(() -> new Document("", "123456789"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFailWhenCPFHasInvalidLength() {
        assertThatThrownBy(() -> new Document("123456789", "123456789"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFailWhenCPFIsInvalid() {
        assertThatThrownBy(() -> new Document("11111111111", "123456789"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldValidateCorrectCPF() {
        assertThatCode(() -> new Document("12345678909", "123456789"))
                .doesNotThrowAnyException();
    }
}
