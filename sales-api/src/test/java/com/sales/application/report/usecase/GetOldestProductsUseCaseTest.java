package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.product.entity.ProductEntity;
import com.sales.infrastructure.persistence.product.repository.ProductPanacheRepository;
import com.sales.infrastructure.rest.report.dto.OldestProductData;
import com.sales.infrastructure.rest.report.dto.OldestProductsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetOldestProductsUseCase Tests")
class GetOldestProductsUseCaseTest {

    @Mock
    private ProductPanacheRepository productRepository;

    @InjectMocks
    private GetOldestProductsUseCase getOldestProductsUseCase;

    private List<ProductEntity> mockProducts;

    @BeforeEach
    void setUp() {
        mockProducts = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return oldest products successfully")
    void shouldReturnOldestProductsSuccessfully() {

        ProductEntity product1 = createProductEntity(
                "PROD001",
                "Batom Matte",
                new BigDecimal("0.050"),
                new BigDecimal("18.00"),
                LocalDateTime.of(2023, 1, 15, 10, 0)
        );

        ProductEntity product2 = createProductEntity(
                "PROD002",
                "Base Líquida",
                new BigDecimal("0.120"),
                new BigDecimal("35.00"),
                LocalDateTime.of(2023, 2, 20, 14, 30)
        );

        ProductEntity product3 = createProductEntity(
                "PROD003",
                "Máscara de Cílios",
                new BigDecimal("0.080"),
                new BigDecimal("22.00"),
                LocalDateTime.of(2023, 3, 10, 9, 15)
        );

        mockProducts.add(product1);
        mockProducts.add(product2);
        mockProducts.add(product3);

        when(productRepository.findOldestProducts(3)).thenReturn(mockProducts);

        OldestProductsResponse result = getOldestProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(3);

        OldestProductData firstProduct = result.getProducts().get(0);
        assertThat(firstProduct.getName()).isEqualTo("Batom Matte");
        assertThat(firstProduct.getWeight()).isEqualByComparingTo(new BigDecimal("0.050"));
        assertThat(firstProduct.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("18.00"));
        assertThat(firstProduct.getRegistrationDate()).isEqualTo(LocalDateTime.of(2023, 1, 15, 10, 0));

        verify(productRepository).findOldestProducts(3);
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProductsExist() {

        when(productRepository.findOldestProducts(3)).thenReturn(Collections.emptyList());

        OldestProductsResponse result = getOldestProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getProducts()).isEmpty();

        verify(productRepository).findOldestProducts(3);
    }

    @Test
    @DisplayName("Should limit results to 3 oldest products")
    void shouldLimitResultsTo3OldestProducts() {

        for (int i = 1; i <= 3; i++) {
            ProductEntity product = createProductEntity(
                    "PROD00" + i,
                    "Product " + i,
                    new BigDecimal("0.100"),
                    new BigDecimal("20.00"),
                    LocalDateTime.of(2023, i, 1, 10, 0)
            );
            mockProducts.add(product);
        }

        when(productRepository.findOldestProducts(3)).thenReturn(mockProducts);

        OldestProductsResponse result = getOldestProductsUseCase.execute();

        assertThat(result.getProducts()).hasSize(3);
        verify(productRepository).findOldestProducts(3);
    }

    @Test
    @DisplayName("Should verify repository interaction")
    void shouldVerifyRepositoryInteraction() {

        when(productRepository.findOldestProducts(3)).thenReturn(Collections.emptyList());

        getOldestProductsUseCase.execute();

        verify(productRepository, times(1)).findOldestProducts(3);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("Should map all product fields correctly")
    void shouldMapAllProductFieldsCorrectly() {

        LocalDateTime registrationDate = LocalDateTime.of(2023, 6, 15, 14, 30);
        ProductEntity product = createProductEntity(
                "PROD999",
                "Paleta de Sombras",
                new BigDecimal("0.200"),
                new BigDecimal("45.00"),
                registrationDate
        );
        mockProducts.add(product);

        when(productRepository.findOldestProducts(3)).thenReturn(mockProducts);

        OldestProductsResponse result = getOldestProductsUseCase.execute();

        OldestProductData productData = result.getProducts().get(0);
        assertThat(productData.getName()).isEqualTo("Paleta de Sombras");
        assertThat(productData.getWeight()).isEqualByComparingTo(new BigDecimal("0.200"));
        assertThat(productData.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(productData.getRegistrationDate()).isEqualTo(registrationDate);
    }

    private ProductEntity createProductEntity(String code, String name, BigDecimal weight,
                                             BigDecimal purchasePrice, LocalDateTime createdAt) {
        ProductEntity entity = new ProductEntity();
        entity.setCode(code);
        entity.setName(name);
        entity.setWeight(weight);
        entity.setPurchasePrice(purchasePrice);
        entity.setCreatedAt(createdAt);
        entity.setType("Maquiagem");
        entity.setDetails("Produto de teste");
        entity.setSalePrice(purchasePrice.multiply(new BigDecimal("2")));
        entity.setStockQuantity(100);
        return entity;
    }
}
