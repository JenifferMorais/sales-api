package com.sales.domain.product.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class DimensionsTest {

    @Test
    void shouldCreateValidDimensions() {
        Dimensions dimensions = new Dimensions(
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(3.0)
        );

        assertThat(dimensions.getHeight()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
        assertThat(dimensions.getWidth()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(dimensions.getDepth()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
    }

    @Test
    void shouldCreateDimensionsWithCreateMethod() {
        Dimensions dimensions = Dimensions.create(
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(3.0)
        );

        assertThat(dimensions.getHeight()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
    }

    @Test
    void shouldCalculateVolume() {
        Dimensions dimensions = new Dimensions(
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(2.0)
        );

        BigDecimal volume = dimensions.getVolume();

        assertThat(volume).isEqualByComparingTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void shouldFailWhenHeightIsNull() {
        assertThatThrownBy(() -> new Dimensions(null, BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Altura não pode ser nulo");
    }

    @Test
    void shouldFailWhenHeightIsZero() {
        assertThatThrownBy(() -> new Dimensions(BigDecimal.ZERO, BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Altura deve ser maior que zero");
    }

    @Test
    void shouldFailWhenHeightIsNegative() {
        assertThatThrownBy(() -> new Dimensions(BigDecimal.valueOf(-10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Altura deve ser maior que zero");
    }

    @Test
    void shouldFailWhenWidthIsNull() {
        assertThatThrownBy(() -> new Dimensions(BigDecimal.valueOf(10.0), null, BigDecimal.valueOf(3.0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Largura não pode ser nulo");
    }

    @Test
    void shouldFailWhenWidthIsZero() {
        assertThatThrownBy(() -> new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.ZERO, BigDecimal.valueOf(3.0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Largura deve ser maior que zero");
    }

    @Test
    void shouldFailWhenDepthIsNull() {
        assertThatThrownBy(() -> new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profundidade não pode ser nulo");
    }

    @Test
    void shouldFailWhenDepthIsZero() {
        assertThatThrownBy(() -> new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profundidade deve ser maior que zero");
    }

    @Test
    void shouldBeEqualWhenSameDimensions() {
        Dimensions dimensions1 = new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0));
        Dimensions dimensions2 = new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0));

        assertThat(dimensions1).isEqualTo(dimensions2);
        assertThat(dimensions1.hashCode()).isEqualTo(dimensions2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentDimensions() {
        Dimensions dimensions1 = new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0));
        Dimensions dimensions2 = new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(4.0));

        assertThat(dimensions1).isNotEqualTo(dimensions2);
    }

    @Test
    void shouldFormatToString() {
        Dimensions dimensions = new Dimensions(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0));

        String formatted = dimensions.toString();

        assertThat(formatted).contains("10");
        assertThat(formatted).contains("5");
        assertThat(formatted).contains("3");
        assertThat(formatted).contains("cm");
    }
}
