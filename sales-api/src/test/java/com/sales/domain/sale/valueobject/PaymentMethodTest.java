package com.sales.domain.sale.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PaymentMethodTest {

    @Test
    void shouldGetDescription() {
        assertThat(PaymentMethod.DINHEIRO.getDescription()).isEqualTo("Dinheiro");
        assertThat(PaymentMethod.CARTAO_CREDITO.getDescription()).isEqualTo("Cartão de Crédito");
        assertThat(PaymentMethod.CARTAO_DEBITO.getDescription()).isEqualTo("Cartão de Débito");
        assertThat(PaymentMethod.PIX.getDescription()).isEqualTo("PIX");
        assertThat(PaymentMethod.TRANSFERENCIA_BANCARIA.getDescription()).isEqualTo("Transferência Bancária");
    }

    @Test
    void shouldCreateFromStringByName() {
        assertThat(PaymentMethod.fromString("DINHEIRO")).isEqualTo(PaymentMethod.DINHEIRO);
        assertThat(PaymentMethod.fromString("dinheiro")).isEqualTo(PaymentMethod.DINHEIRO);
        assertThat(PaymentMethod.fromString("PIX")).isEqualTo(PaymentMethod.PIX);
        assertThat(PaymentMethod.fromString("pix")).isEqualTo(PaymentMethod.PIX);
    }

    @Test
    void shouldCreateFromStringByDescription() {
        assertThat(PaymentMethod.fromString("Dinheiro")).isEqualTo(PaymentMethod.DINHEIRO);
        assertThat(PaymentMethod.fromString("dinheiro")).isEqualTo(PaymentMethod.DINHEIRO);
        assertThat(PaymentMethod.fromString("Cartão de Crédito")).isEqualTo(PaymentMethod.CARTAO_CREDITO);
        assertThat(PaymentMethod.fromString("cartão de crédito")).isEqualTo(PaymentMethod.CARTAO_CREDITO);
        assertThat(PaymentMethod.fromString("PIX")).isEqualTo(PaymentMethod.PIX);
        assertThat(PaymentMethod.fromString("Transferência Bancária")).isEqualTo(PaymentMethod.TRANSFERENCIA_BANCARIA);
    }

    @Test
    void shouldFailFromStringWithInvalidValue() {
        assertThatThrownBy(() -> PaymentMethod.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Forma de pagamento desconhecida: INVALID");
    }

    @Test
    void shouldFailFromStringWithNull() {
        assertThatThrownBy(() -> PaymentMethod.fromString(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHaveAllExpectedPaymentMethods() {
        PaymentMethod[] methods = PaymentMethod.values();

        assertThat(methods).hasSize(5);
        assertThat(methods).contains(
                PaymentMethod.DINHEIRO,
                PaymentMethod.CARTAO_CREDITO,
                PaymentMethod.CARTAO_DEBITO,
                PaymentMethod.PIX,
                PaymentMethod.TRANSFERENCIA_BANCARIA
        );
    }

    @Test
    void shouldHandleCreditCardVariations() {
        assertThat(PaymentMethod.fromString("CARTAO_CREDITO")).isEqualTo(PaymentMethod.CARTAO_CREDITO);
        assertThat(PaymentMethod.fromString("cartao_credito")).isEqualTo(PaymentMethod.CARTAO_CREDITO);
        assertThat(PaymentMethod.fromString("Cartão de Crédito")).isEqualTo(PaymentMethod.CARTAO_CREDITO);
    }

    @Test
    void shouldHandleDebitCardVariations() {
        assertThat(PaymentMethod.fromString("CARTAO_DEBITO")).isEqualTo(PaymentMethod.CARTAO_DEBITO);
        assertThat(PaymentMethod.fromString("cartao_debito")).isEqualTo(PaymentMethod.CARTAO_DEBITO);
        assertThat(PaymentMethod.fromString("Cartão de Débito")).isEqualTo(PaymentMethod.CARTAO_DEBITO);
    }

    @Test
    void shouldHandleBankTransferVariations() {
        assertThat(PaymentMethod.fromString("TRANSFERENCIA_BANCARIA")).isEqualTo(PaymentMethod.TRANSFERENCIA_BANCARIA);
        assertThat(PaymentMethod.fromString("transferencia_bancaria")).isEqualTo(PaymentMethod.TRANSFERENCIA_BANCARIA);
        assertThat(PaymentMethod.fromString("Transferência Bancária")).isEqualTo(PaymentMethod.TRANSFERENCIA_BANCARIA);
    }
}
