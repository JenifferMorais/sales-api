package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindProductUseCase Tests")
class FindProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FindProductUseCase findProductUseCase;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.create(
                "PROD001",
                "Batom Matte",
                "Lábios",
                "Batom matte de longa duração",
                new BigDecimal("0.050"),
                new BigDecimal("18.00"),
                new BigDecimal("35.00"),
                new BigDecimal("8.00"),
                new BigDecimal("2.00"),
                new BigDecimal("2.00"),
                "Todos os tipos de pele",
                100
        );

        product2 = Product.create(
                "PROD002",
                "Base Líquida",
                "Rosto",
                "Base líquida alta cobertura",
                new BigDecimal("0.120"),
                new BigDecimal("45.00"),
                new BigDecimal("89.00"),
                new BigDecimal("12.00"),
                new BigDecimal("4.00"),
                new BigDecimal("4.00"),
                "Pele mista a oleosa",
                150
        );
    }

    @Test
    @DisplayName("Should find product by ID successfully")
    void shouldFindProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product result = findProductUseCase.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("PROD001");
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void shouldThrowExceptionWhenNotFoundById() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findProductUseCase.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Should find product by code successfully")
    void shouldFindProductByCode() {
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(product1));

        Product result = findProductUseCase.findByCode("PROD001");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Batom Matte");
        verify(productRepository).findByCode("PROD001");
    }

    @Test
    @DisplayName("Should throw exception when product not found by code")
    void shouldThrowExceptionWhenNotFoundByCode() {
        when(productRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findProductUseCase.findByCode("NONEXISTENT"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NONEXISTENT");
    }

    @Test
    @DisplayName("Should find all products successfully")
    void shouldFindAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = findProductUseCase.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(product1, product2);
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should find all products sorted by name")
    void shouldFindAllProductsSortedByName() {
        when(productRepository.findAllSortedByName()).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = findProductUseCase.findAllSortedByName();

        assertThat(result).hasSize(2);
        verify(productRepository).findAllSortedByName();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = findProductUseCase.findAll();

        assertThat(result).isEmpty();
    }
}
