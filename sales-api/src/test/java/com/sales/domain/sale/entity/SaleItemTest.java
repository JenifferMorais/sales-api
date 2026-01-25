package com.sales.domain.sale.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class SaleItemTest {

    @Test
    void shouldCreateValidSaleItem() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThat(item.getProductCode()).isEqualTo("PROD001");
        assertThat(item.getProductName()).isEqualTo("Batom Vermelho");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
    }

    @Test
    void shouldCreateSaleItemWithId() {
        SaleItem item = new SaleItem(1L, "PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getProductCode()).isEqualTo("PROD001");
    }

    @Test
    void shouldCalculateTotalPrice() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 3, BigDecimal.valueOf(30.00));

        BigDecimal totalPrice = item.getTotalPrice();

        assertThat(totalPrice).isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }

    @Test
    void shouldCalculateTotalPriceWithDecimals() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(29.99));

        BigDecimal totalPrice = item.getTotalPrice();

        assertThat(totalPrice).isEqualByComparingTo(BigDecimal.valueOf(59.98));
    }

    @Test
    void shouldFailWhenProductCodeIsNull() {
        assertThatThrownBy(() -> new SaleItem(null, "Batom Vermelho", 2, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do produto não pode estar vazio");
    }

    @Test
    void shouldFailWhenProductCodeIsEmpty() {
        assertThatThrownBy(() -> new SaleItem("", "Batom Vermelho", 2, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do produto não pode estar vazio");
    }

    @Test
    void shouldFailWhenProductCodeIsBlank() {
        assertThatThrownBy(() -> new SaleItem("   ", "Batom Vermelho", 2, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do produto não pode estar vazio");
    }

    @Test
    void shouldFailWhenProductNameIsNull() {
        assertThatThrownBy(() -> new SaleItem("PROD001", null, 2, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome do produto não pode estar vazio");
    }

    @Test
    void shouldFailWhenProductNameIsEmpty() {
        assertThatThrownBy(() -> new SaleItem("PROD001", "", 2, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome do produto não pode estar vazio");
    }

    @Test
    void shouldFailWhenQuantityIsNull() {
        assertThatThrownBy(() -> new SaleItem("PROD001", "Batom Vermelho", null, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");
    }

    @Test
    void shouldFailWhenQuantityIsZero() {
        assertThatThrownBy(() -> new SaleItem("PROD001", "Batom Vermelho", 0, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");
    }

    @Test
    void shouldFailWhenQuantityIsNegative() {
        assertThatThrownBy(() -> new SaleItem("PROD001", "Batom Vermelho", -1, BigDecimal.valueOf(30.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");
    }

    @Test
    void shouldFailWhenUnitPriceIsNull() {
        assertThatThrownBy(() -> new SaleItem("PROD001", "Batom Vermelho", 2, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Preço não pode ser nulo");
    }

    @Test
    void shouldFailWhenUnitPriceIsZero() {
        assertThatThrownBy(() -> new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Preço deve ser maior que zero");
    }

    @Test
    void shouldFailWhenUnitPriceIsNegative() {
        assertThatThrownBy(() -> new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(-10.00)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Preço deve ser maior que zero");
    }

    @Test
    void shouldUpdateQuantity() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        item.updateQuantity(5);

        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    @Test
    void shouldFailUpdateQuantityWhenNull() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThatThrownBy(() -> item.updateQuantity(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");
    }

    @Test
    void shouldFailUpdateQuantityWhenZero() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThatThrownBy(() -> item.updateQuantity(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");
    }

    @Test
    void shouldFailUpdateQuantityWhenNegative() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThatThrownBy(() -> item.updateQuantity(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");
    }

    @Test
    void shouldBeEqualWhenSameProductCode() {
        SaleItem item1 = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));
        SaleItem item2 = new SaleItem("PROD001", "Batom Rosa", 3, BigDecimal.valueOf(25.00));

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentProductCode() {
        SaleItem item1 = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));
        SaleItem item2 = new SaleItem("PROD002", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldBeEqualToItself() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThat(item).isEqualTo(item);
    }

    @Test
    void shouldNotBeEqualToNull() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThat(item).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));

        assertThat(item).isNotEqualTo("Not a SaleItem");
    }
}
