package com.sales.domain.product.entity;

import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateValidProduct() {
        Dimensions dimensions = new Dimensions(
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        Product product = new Product(
                "PROD001",
                "Batom Matte",
                ProductType.LIPS,
                "Batom longa duração",
                new BigDecimal("15.5"),
                new BigDecimal("25.00"),
                new BigDecimal("45.00"),
                dimensions,
                "Sedex"
        );

        assertThat(product.getCode()).isEqualTo("PROD001");
        assertThat(product.getName()).isEqualTo("Batom Matte");
        assertThat(product.getType()).isEqualTo(ProductType.LIPS);
        assertThat(product.getStockQuantity()).isEqualTo(0);
    }

    @Test
    void shouldFailWhenSalePriceIsLowerThanPurchasePrice() {
        Dimensions dimensions = new Dimensions(
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        assertThatThrownBy(() -> new Product(
                "PROD001",
                "Batom Matte",
                ProductType.LIPS,
                "Batom longa duração",
                new BigDecimal("15.5"),
                new BigDecimal("50.00"),
                new BigDecimal("30.00"),
                dimensions,
                "Sedex"
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Preço de venda não pode ser menor que o preço de compra");
    }

    @Test
    void shouldAddStock() {
        Dimensions dimensions = new Dimensions(
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        Product product = new Product(
                "PROD001",
                "Batom Matte",
                ProductType.LIPS,
                "Batom longa duração",
                new BigDecimal("15.5"),
                new BigDecimal("25.00"),
                new BigDecimal("45.00"),
                dimensions,
                "Sedex"
        );

        product.addStock(10);
        assertThat(product.getStockQuantity()).isEqualTo(10);

        product.addStock(5);
        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    @Test
    void shouldRemoveStock() {
        Dimensions dimensions = new Dimensions(
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        Product product = new Product(
                "PROD001",
                "Batom Matte",
                ProductType.LIPS,
                "Batom longa duração",
                new BigDecimal("15.5"),
                new BigDecimal("25.00"),
                new BigDecimal("45.00"),
                dimensions,
                "Sedex"
        );

        product.addStock(10);
        product.removeStock(5);
        assertThat(product.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void shouldFailWhenRemovingMoreStockThanAvailable() {
        Dimensions dimensions = new Dimensions(
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        Product product = new Product(
                "PROD001",
                "Batom Matte",
                ProductType.LIPS,
                "Batom longa duração",
                new BigDecimal("15.5"),
                new BigDecimal("25.00"),
                new BigDecimal("45.00"),
                dimensions,
                "Sedex"
        );

        product.addStock(5);

        assertThatThrownBy(() -> product.removeStock(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    void shouldCalculateProfit() {
        Dimensions dimensions = new Dimensions(
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        Product product = new Product(
                "PROD001",
                "Batom Matte",
                ProductType.LIPS,
                "Batom longa duração",
                new BigDecimal("15.5"),
                new BigDecimal("25.00"),
                new BigDecimal("45.00"),
                dimensions,
                "Sedex"
        );

        assertThat(product.getProfit()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(product.getProfitMargin()).isEqualByComparingTo(new BigDecimal("80.0000"));
    }
}
