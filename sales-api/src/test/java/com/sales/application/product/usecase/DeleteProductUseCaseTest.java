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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteProductUseCase Tests")
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

    private Product existingProduct;

    @BeforeEach
    void setUp() {
        existingProduct = Product.create(
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
    }

    @Test
    @DisplayName("Should delete product successfully when product exists")
    void shouldDeleteProductSuccessfully() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productRepository).deleteById(1L);

        deleteProductUseCase.execute(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProductUseCase.execute(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");

        verify(productRepository).findById(999L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should verify repository interactions in correct order")
    void shouldVerifyRepositoryInteractionsInOrder() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productRepository).deleteById(1L);

        deleteProductUseCase.execute(1L);

        var inOrder = inOrder(productRepository);
        inOrder.verify(productRepository).findById(1L);
        inOrder.verify(productRepository).deleteById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should delete product only once")
    void shouldDeleteProductOnlyOnce() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productRepository).deleteById(1L);

        deleteProductUseCase.execute(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if product exists before deleting")
    void shouldCheckIfProductExistsBeforeDeleting() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productRepository).deleteById(1L);

        deleteProductUseCase.execute(1L);

        var inOrder = inOrder(productRepository);
        inOrder.verify(productRepository).findById(1L);
        inOrder.verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should not delete when product ID is null")
    void shouldNotDeleteWhenProductIdIsNull() {

        when(productRepository.findById(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteProductUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");

        verify(productRepository, never()).deleteById(anyLong());
    }
}
