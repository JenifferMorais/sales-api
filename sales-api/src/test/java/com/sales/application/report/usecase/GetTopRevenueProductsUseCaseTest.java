package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.sale.repository.SalePanacheRepository;
import com.sales.infrastructure.rest.report.dto.TopRevenueProductData;
import com.sales.infrastructure.rest.report.dto.TopRevenueProductsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTopRevenueProductsUseCase Tests")
class GetTopRevenueProductsUseCaseTest {

    @Mock
    private SalePanacheRepository saleRepository;

    @InjectMocks
    private GetTopRevenueProductsUseCase getTopRevenueProductsUseCase;

    private List<Map<String, Object>> mockProductData;

    @BeforeEach
    void setUp() {
        mockProductData = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return top revenue products successfully")
    void shouldReturnTopRevenueProductsSuccessfully() {

        Map<String, Object> product1 = new HashMap<>();
        product1.put("productCode", "PROD001");
        product1.put("productName", "Batom Matte");
        product1.put("salePrice", new BigDecimal("35.00"));
        product1.put("totalRevenue", new BigDecimal("3500.00"));

        Map<String, Object> product2 = new HashMap<>();
        product2.put("productCode", "PROD002");
        product2.put("productName", "Base Líquida");
        product2.put("salePrice", new BigDecimal("65.00"));
        product2.put("totalRevenue", new BigDecimal("3250.00"));

        mockProductData.add(product1);
        mockProductData.add(product2);

        when(saleRepository.getTopRevenueProducts(4)).thenReturn(mockProductData);

        TopRevenueProductsResponse result = getTopRevenueProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(2);

        TopRevenueProductData firstProduct = result.getProducts().get(0);
        assertThat(firstProduct.getProductCode()).isEqualTo("PROD001");
        assertThat(firstProduct.getProductName()).isEqualTo("Batom Matte");
        assertThat(firstProduct.getSalePrice()).isEqualByComparingTo(new BigDecimal("35.00"));
        assertThat(firstProduct.getTotalRevenue()).isEqualByComparingTo(new BigDecimal("3500.00"));

        verify(saleRepository).getTopRevenueProducts(4);
    }

    @Test
    @DisplayName("Should return empty list when no products sold")
    void shouldReturnEmptyListWhenNoProductsSold() {

        when(saleRepository.getTopRevenueProducts(4)).thenReturn(Collections.emptyList());

        TopRevenueProductsResponse result = getTopRevenueProductsUseCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getProducts()).isEmpty();

        verify(saleRepository).getTopRevenueProducts(4);
    }

    @Test
    @DisplayName("Should limit results to top 4 products")
    void shouldLimitResultsToTop4Products() {

        for (int i = 1; i <= 4; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("productCode", "PROD00" + i);
            product.put("productName", "Product " + i);
            product.put("salePrice", new BigDecimal("50.00"));
            product.put("totalRevenue", new BigDecimal(String.valueOf(5000 - (i * 100))));
            mockProductData.add(product);
        }

        when(saleRepository.getTopRevenueProducts(4)).thenReturn(mockProductData);

        TopRevenueProductsResponse result = getTopRevenueProductsUseCase.execute();

        assertThat(result.getProducts()).hasSize(4);
        verify(saleRepository).getTopRevenueProducts(4);
    }

    @Test
    @DisplayName("Should verify repository interaction")
    void shouldVerifyRepositoryInteraction() {

        when(saleRepository.getTopRevenueProducts(4)).thenReturn(Collections.emptyList());

        getTopRevenueProductsUseCase.execute();

        verify(saleRepository, times(1)).getTopRevenueProducts(4);
        verifyNoMoreInteractions(saleRepository);
    }

    @Test
    @DisplayName("Should map all product fields correctly")
    void shouldMapAllProductFieldsCorrectly() {

        Map<String, Object> product = new HashMap<>();
        product.put("productCode", "PROD999");
        product.put("productName", "Máscara de Cílios");
        product.put("salePrice", new BigDecimal("42.50"));
        product.put("totalRevenue", new BigDecimal("4250.00"));
        mockProductData.add(product);

        when(saleRepository.getTopRevenueProducts(4)).thenReturn(mockProductData);

        TopRevenueProductsResponse result = getTopRevenueProductsUseCase.execute();

        TopRevenueProductData productData = result.getProducts().get(0);
        assertThat(productData.getProductCode()).isEqualTo("PROD999");
        assertThat(productData.getProductName()).isEqualTo("Máscara de Cílios");
        assertThat(productData.getSalePrice()).isEqualByComparingTo(new BigDecimal("42.50"));
        assertThat(productData.getTotalRevenue()).isEqualByComparingTo(new BigDecimal("4250.00"));
    }
}
