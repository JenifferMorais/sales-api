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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductUseCase Tests")
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    private Product validProduct;

    @BeforeEach
    void setUp() {
        validProduct = Product.create(
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
    @DisplayName("Should create product successfully when code is unique")
    void shouldCreateProductSuccessfully() {

        when(productRepository.existsByCode("PROD001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(validProduct);

        Product result = createProductUseCase.execute(validProduct);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("PROD001");
        verify(productRepository).existsByCode("PROD001");
        verify(productRepository).save(validProduct);
    }

    @Test
    @DisplayName("Should throw exception when product code already exists")
    void shouldThrowExceptionWhenCodeAlreadyExists() {

        when(productRepository.existsByCode("PROD001")).thenReturn(true);

        assertThatThrownBy(() -> createProductUseCase.execute(validProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produto com código PROD001 já existe");

        verify(productRepository).existsByCode("PROD001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should create product when code is null")
    void shouldCreateProductWhenCodeIsNull() {

        Product productWithoutCode = Product.create(
                null,
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
        when(productRepository.save(any(Product.class))).thenReturn(productWithoutCode);

        Product result = createProductUseCase.execute(productWithoutCode);

        assertThat(result).isNotNull();
        verify(productRepository, never()).existsByCode(anyString());
        verify(productRepository).save(productWithoutCode);
    }

    @Test
    @DisplayName("Should create product when code is blank")
    void shouldCreateProductWhenCodeIsBlank() {

        Product productWithBlankCode = Product.create(
                "   ",
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
        when(productRepository.save(any(Product.class))).thenReturn(productWithBlankCode);

        Product result = createProductUseCase.execute(productWithBlankCode);

        assertThat(result).isNotNull();
        verify(productRepository, never()).existsByCode(anyString());
        verify(productRepository).save(productWithBlankCode);
    }

    @Test
    @DisplayName("Should verify repository interaction only once on success")
    void shouldVerifyRepositoryInteractionOnce() {

        when(productRepository.existsByCode("PROD001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(validProduct);

        createProductUseCase.execute(validProduct);

        verify(productRepository, times(1)).existsByCode("PROD001");
        verify(productRepository, times(1)).save(validProduct);
        verifyNoMoreInteractions(productRepository);
    }
}
