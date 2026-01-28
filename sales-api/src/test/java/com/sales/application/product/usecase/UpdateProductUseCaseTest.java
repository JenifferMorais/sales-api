package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductUseCase Tests")
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductUseCase updateProductUseCase;

    private Product existingProduct;
    private ProductType newType;
    private Dimensions newDimensions;

    @BeforeEach
    void setUp() {
        existingProduct = Product.create(
                "PROD001",
                "Batom Matte Original",
                "Lábios",
                "Descrição original",
                new BigDecimal("0.050"),
                new BigDecimal("18.00"),
                new BigDecimal("35.00"),
                new BigDecimal("8.00"),
                new BigDecimal("2.00"),
                new BigDecimal("2.00"),
                "Todos os tipos de pele",
                100
        );

        newType = ProductType.create("Olhos");
        newDimensions = Dimensions.create(
                new BigDecimal("10.00"),
                new BigDecimal("3.00"),
                new BigDecimal("3.00")
        );
    }

    @Test
    @DisplayName("Should update product successfully when product exists")
    void shouldUpdateProductSuccessfully() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Product result = updateProductUseCase.execute(
                1L,
                "Batom Matte Atualizado",
                ProductType.create("Lábios"),
                "Nova descrição",
                new BigDecimal("0.060"),
                new BigDecimal("20.00"),
                new BigDecimal("40.00"),
                newDimensions,
                "Peles sensíveis"
        );

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Batom Matte Atualizado");
        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProductUseCase.execute(
                999L,
                "Batom Atualizado",
                ProductType.create("Lábios"),
                "Descrição",
                new BigDecimal("0.050"),
                new BigDecimal("18.00"),
                new BigDecimal("35.00"),
                newDimensions,
                "Todos"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");

        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product with new prices")
    void shouldUpdateProductWithNewPrices() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        BigDecimal newPurchasePrice = new BigDecimal("25.00");
        BigDecimal newSalePrice = new BigDecimal("50.00");

        Product result = updateProductUseCase.execute(
                1L,
                existingProduct.getName(),
                existingProduct.getType(),
                existingProduct.getDetails(),
                existingProduct.getWeight(),
                newPurchasePrice,
                newSalePrice,
                existingProduct.getDimensions(),
                existingProduct.getDestinationVehicle()
        );

        assertThat(result.getPurchasePrice()).isEqualByComparingTo(newPurchasePrice);
        assertThat(result.getSalePrice()).isEqualByComparingTo(newSalePrice);
        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("Should verify repository interactions in correct order")
    void shouldVerifyRepositoryInteractionsInOrder() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        updateProductUseCase.execute(
                1L,
                "Updated Name",
                ProductType.create("Lábios"),
                "Updated details",
                new BigDecimal("0.050"),
                new BigDecimal("18.00"),
                new BigDecimal("35.00"),
                newDimensions,
                "All skin types"
        );

        var inOrder = inOrder(productRepository);
        inOrder.verify(productRepository).findById(1L);
        inOrder.verify(productRepository).save(existingProduct);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should update product type successfully")
    void shouldUpdateProductTypeSuccessfully() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        ProductType newType = ProductType.create("Rosto");

        Product result = updateProductUseCase.execute(
                1L,
                existingProduct.getName(),
                newType,
                existingProduct.getDetails(),
                existingProduct.getWeight(),
                existingProduct.getPurchasePrice(),
                existingProduct.getSalePrice(),
                existingProduct.getDimensions(),
                existingProduct.getDestinationVehicle()
        );

        assertThat(result.getType()).isEqualTo(newType);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
