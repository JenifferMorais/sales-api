package com.sales.application.sale.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.port.SaleRepository;
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
@DisplayName("FindSaleUseCase Tests")
class FindSaleUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private FindSaleUseCase findSaleUseCase;

    private Sale sale1;
    private Sale sale2;

    @BeforeEach
    void setUp() {
        sale1 = Sale.createCashSale(
                "SALE001",
                "CUST001",
                "John Silva",
                "SELLER001",
                "Vendedor Sistema",
                new BigDecimal("100.00")
        );
        sale1.addItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));

        sale2 = Sale.createCashSale(
                "SALE002",
                "CUST001",
                "John Silva",
                "SELLER001",
                "Vendedor Sistema",
                new BigDecimal("200.00")
        );
        sale2.addItem("PROD002", "Base Líquida", 2, new BigDecimal("89.00"));
    }

    @Test
    @DisplayName("Should find sale by ID successfully")
    void shouldFindSaleById() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale1));

        Sale result = findSaleUseCase.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("SALE001");
        verify(saleRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when sale not found by ID")
    void shouldThrowExceptionWhenNotFoundById() {
        when(saleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findSaleUseCase.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Should find sale by code successfully")
    void shouldFindSaleByCode() {
        when(saleRepository.findByCode("SALE001")).thenReturn(Optional.of(sale1));

        Sale result = findSaleUseCase.findByCode("SALE001");

        assertThat(result).isNotNull();
        assertThat(result.getCustomerName()).isEqualTo("John Silva");
        verify(saleRepository).findByCode("SALE001");
    }

    @Test
    @DisplayName("Should throw exception when sale not found by code")
    void shouldThrowExceptionWhenNotFoundByCode() {
        when(saleRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findSaleUseCase.findByCode("NONEXISTENT"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NONEXISTENT");
    }

    @Test
    @DisplayName("Should find all sales successfully")
    void shouldFindAllSales() {
        when(saleRepository.findAll()).thenReturn(Arrays.asList(sale1, sale2));

        List<Sale> result = findSaleUseCase.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(sale1, sale2);
        verify(saleRepository).findAll();
    }

    @Test
    @DisplayName("Should find sales by customer code")
    void shouldFindSalesByCustomerCode() {
        when(saleRepository.findByCustomerCode("CUST001"))
                .thenReturn(Arrays.asList(sale1, sale2));

        List<Sale> result = findSaleUseCase.findByCustomerCode("CUST001");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(s -> s.getCustomerCode().equals("CUST001"));
        verify(saleRepository).findByCustomerCode("CUST001");
    }

    @Test
    @DisplayName("Should return empty list when customer has no sales")
    void shouldReturnEmptyListWhenNoSalesForCustomer() {
        when(saleRepository.findByCustomerCode("CUST999")).thenReturn(List.of());

        List<Sale> result = findSaleUseCase.findByCustomerCode("CUST999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no sales exist")
    void shouldReturnEmptyListWhenNoSales() {
        when(saleRepository.findAll()).thenReturn(List.of());

        List<Sale> result = findSaleUseCase.findAll();

        assertThat(result).isEmpty();
    }
}
