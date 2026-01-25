package com.sales.infrastructure.rest.product.dto;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    void shouldMapRequestToDomain() {
        ProductRequest request = new ProductRequest();
        request.setCode("PROD001");
        request.setName("Batom Vermelho");
        request.setType("LIPS");
        request.setDetails("Batom matte");
        request.setWeight(BigDecimal.valueOf(0.5));
        request.setPurchasePrice(BigDecimal.valueOf(15.00));
        request.setSalePrice(BigDecimal.valueOf(30.00));
        request.setHeight(BigDecimal.valueOf(10.0));
        request.setWidth(BigDecimal.valueOf(5.0));
        request.setDepth(BigDecimal.valueOf(3.0));
        request.setDestinationVehicle("Correios");

        Product product = mapper.toDomain(request);

        assertThat(product.getCode()).isEqualTo("PROD001");
        assertThat(product.getName()).isEqualTo("Batom Vermelho");
        assertThat(product.getType()).isEqualTo(ProductType.LIPS);
        assertThat(product.getDetails()).isEqualTo("Batom matte");
        assertThat(product.getWeight()).isEqualByComparingTo(BigDecimal.valueOf(0.5));
        assertThat(product.getPurchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(15.00));
        assertThat(product.getSalePrice()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
        assertThat(product.getDimensions().getHeight()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
    }

    @Test
    void shouldMapDomainToResponse() {
        Dimensions dimensions = new Dimensions(
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0));
        Product product = new Product("PROD001", "Batom Vermelho", ProductType.LIPS, "Batom matte",
                BigDecimal.valueOf(0.5), BigDecimal.valueOf(15.00), BigDecimal.valueOf(30.00),
                dimensions, "Correios");

        ProductResponse response = mapper.toResponse(product);

        assertThat(response.getCode()).isEqualTo("PROD001");
        assertThat(response.getName()).isEqualTo("Batom Vermelho");
        assertThat(response.getType()).isEqualTo("Lábios");
        assertThat(response.getDetails()).isEqualTo("Batom matte");
        assertThat(response.getPurchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(15.00));
        assertThat(response.getSalePrice()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
    }
}
