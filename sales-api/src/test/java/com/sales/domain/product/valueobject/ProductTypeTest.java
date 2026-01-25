package com.sales.domain.product.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductTypeTest {

    @Test
    void shouldGetDescription() {
        assertThat(ProductType.LIPS.getDescription()).isEqualTo("Lábios");
        assertThat(ProductType.FACE.getDescription()).isEqualTo("Rosto");
        assertThat(ProductType.EYES.getDescription()).isEqualTo("Olhos");
        assertThat(ProductType.NAILS.getDescription()).isEqualTo("Unhas");
        assertThat(ProductType.SKIN_CARE.getDescription()).isEqualTo("Cuidados com a Pele");
        assertThat(ProductType.HAIR.getDescription()).isEqualTo("Cabelos");
        assertThat(ProductType.FRAGRANCE.getDescription()).isEqualTo("Fragrância");
        assertThat(ProductType.OTHER.getDescription()).isEqualTo("Outro");
    }

    @Test
    void shouldCreateFromStringByName() {
        assertThat(ProductType.fromString("LIPS")).isEqualTo(ProductType.LIPS);
        assertThat(ProductType.fromString("lips")).isEqualTo(ProductType.LIPS);
        assertThat(ProductType.fromString("FACE")).isEqualTo(ProductType.FACE);
    }

    @Test
    void shouldCreateFromStringByDescription() {
        assertThat(ProductType.fromString("Lábios")).isEqualTo(ProductType.LIPS);
        assertThat(ProductType.fromString("lábios")).isEqualTo(ProductType.LIPS);
        assertThat(ProductType.fromString("Rosto")).isEqualTo(ProductType.FACE);
    }

    @Test
    void shouldFailFromStringWithInvalidValue() {
        assertThatThrownBy(() -> ProductType.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de produto desconhecido: INVALID");
    }

    @Test
    void shouldCreateFromDatabase() {
        assertThat(ProductType.fromDatabase("LIPS")).isEqualTo(ProductType.LIPS);
        assertThat(ProductType.fromDatabase("Lábios")).isEqualTo(ProductType.LIPS);
    }

    @Test
    void shouldReturnOtherWhenDatabaseValueIsNull() {
        assertThat(ProductType.fromDatabase(null)).isEqualTo(ProductType.OTHER);
    }

    @Test
    void shouldReturnOtherWhenDatabaseValueIsBlank() {
        assertThat(ProductType.fromDatabase("")).isEqualTo(ProductType.OTHER);
        assertThat(ProductType.fromDatabase("   ")).isEqualTo(ProductType.OTHER);
    }

    @Test
    void shouldReturnOtherWhenDatabaseValueIsInvalid() {
        assertThat(ProductType.fromDatabase("INVALID")).isEqualTo(ProductType.OTHER);
    }

    @Test
    void shouldCreateWithCreateMethod() {
        assertThat(ProductType.create("LIPS")).isEqualTo(ProductType.LIPS);
        assertThat(ProductType.create("Lábios")).isEqualTo(ProductType.LIPS);
    }

    @Test
    void shouldHaveAllExpectedTypes() {
        ProductType[] types = ProductType.values();

        assertThat(types).hasSize(8);
        assertThat(types).contains(
                ProductType.LIPS,
                ProductType.FACE,
                ProductType.EYES,
                ProductType.NAILS,
                ProductType.SKIN_CARE,
                ProductType.HAIR,
                ProductType.FRAGRANCE,
                ProductType.OTHER
        );
    }
}
