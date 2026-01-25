package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import com.sales.domain.shared.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SearchProductsUseCase useCase;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Dimensions dimensions1 = new Dimensions(
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(3.0)
        );
        product1 = new Product(
                "PROD001",
                "Batom Vermelho",
                ProductType.LIPS,
                "Batom vermelho matte",
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(15.00),
                BigDecimal.valueOf(30.00),
                dimensions1,
                null
        );

        Dimensions dimensions2 = new Dimensions(
                BigDecimal.valueOf(8.0),
                BigDecimal.valueOf(4.0),
                BigDecimal.valueOf(2.0)
        );
        product2 = new Product(
                "PROD002",
                "Batom Rosa",
                ProductType.LIPS,
                "Batom rosa cremoso",
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(12.00),
                BigDecimal.valueOf(25.00),
                dimensions2,
                null
        );
    }

    @Test
    void shouldSearchProductsWithFilter() {
        String filter = "Vermelho";
        PageResult<Product> expectedPage = new PageResult<>(
                List.of(product1),
                1L,
                0,
                10
        );

        when(productRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Product> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Batom Vermelho");
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(productRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldSearchProductsWithoutFilter() {
        String filter = "";
        PageResult<Product> expectedPage = new PageResult<>(
                List.of(product1, product2),
                2L,
                0,
                10
        );

        when(productRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Product> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        verify(productRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductsFound() {
        String filter = "NonExistent";
        PageResult<Product> expectedPage = new PageResult<>(
                List.of(),
                0L,
                0,
                10
        );

        when(productRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Product> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
        verify(productRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldHandlePagination() {
        String filter = "Batom";
        PageResult<Product> expectedPage = new PageResult<>(
                List.of(product2),
                2L,
                1,
                1
        );

        when(productRepository.search(filter, 1, 1)).thenReturn(expectedPage);

        PageResult<Product> result = useCase.execute(filter, 1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        verify(productRepository, times(1)).search(filter, 1, 1);
    }
}
