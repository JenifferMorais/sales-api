package com.sales.domain.sale.entity;

import com.sales.domain.sale.valueobject.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaleTest {

    @Test
    void shouldCreateValidSale() {
        Sale sale = new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Santos",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("200.00")
        );

        sale.addItem(new SaleItem("PROD001", "Mesa", 2, new BigDecimal("100.00")));

        assertThat(sale.getCode()).isEqualTo("SALE001");
        assertThat(sale.getCustomerCode()).isEqualTo("CUST001");
        assertThat(sale.getItems()).hasSize(1);
    }

    @Test
    void shouldCalculateTotalAmount() {
        Sale sale = new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Santos",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("300.00")
        );

        sale.addItem(new SaleItem("PROD001", "Mesa", 2, new BigDecimal("100.00")));
        sale.addItem(new SaleItem("PROD002", "Cadeira", 4, new BigDecimal("50.00")));

        assertThat(sale.getSubtotal()).isEqualByComparingTo(new BigDecimal("400.00"));
        assertThat(sale.getTaxAmount()).isEqualByComparingTo(new BigDecimal("36.00"));
        assertThat(sale.getTotalAmount()).isEqualByComparingTo(new BigDecimal("436.00"));
        assertThat(sale.getTotalItems()).isEqualTo(6);
    }

    @Test
    void shouldCalculateChange() {
        Sale sale = new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Santos",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("250.00")
        );

        sale.addItem(new SaleItem("PROD001", "Mesa", 2, new BigDecimal("100.00")));

        assertThat(sale.getTotalAmount()).isEqualByComparingTo(new BigDecimal("218.00"));
        assertThat(sale.getChange()).isEqualByComparingTo(new BigDecimal("32.00"));
    }

    @Test
    void shouldFailValidationWhenNoItems() {
        Sale sale = new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Santos",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("100.00")
        );

        assertThatThrownBy(sale::validateSale)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFailValidationWhenInsufficientCashPayment() {
        Sale sale = new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Santos",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("150.00")
        );

        sale.addItem(new SaleItem("PROD001", "Mesa", 2, new BigDecimal("100.00")));

        assertThatThrownBy(sale::validateSale)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRequireCardNumberForCardPayment() {
        assertThatThrownBy(() -> new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Santos",
                PaymentMethod.CARTAO_CREDITO,
                null,
                null
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldMaskCardNumber() {
        Sale sale = new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Santos",
                PaymentMethod.CARTAO_CREDITO,
                "1234567890123456",
                null
        );

        assertThat(sale.getMaskedCardNumber()).isEqualTo("**** **** **** 3456");
    }
}
