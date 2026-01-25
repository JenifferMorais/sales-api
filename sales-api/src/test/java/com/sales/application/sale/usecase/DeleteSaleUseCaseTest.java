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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteSaleUseCase Tests")
class DeleteSaleUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private DeleteSaleUseCase deleteSaleUseCase;

    private Sale existingSale;

    @BeforeEach
    void setUp() {
        existingSale = Sale.createCashSale(
                "SALE001",
                "CUST001",
                "John Silva",
                "SELLER001",
                "Vendedor Sistema",
                new BigDecimal("100.00")
        );
        existingSale.addItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));
    }

    @Test
    @DisplayName("Should delete sale successfully when sale exists")
    void shouldDeleteSaleSuccessfully() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        doNothing().when(saleRepository).deleteById(1L);

        deleteSaleUseCase.execute(1L);

        verify(saleRepository).findById(1L);
        verify(saleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when sale not found")
    void shouldThrowExceptionWhenSaleNotFound() {
        when(saleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteSaleUseCase.execute(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sale not found with id: 999");

        verify(saleRepository).findById(999L);
        verify(saleRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should verify repository interactions in correct order")
    void shouldVerifyRepositoryInteractionsInOrder() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        doNothing().when(saleRepository).deleteById(1L);

        deleteSaleUseCase.execute(1L);

        var inOrder = inOrder(saleRepository);
        inOrder.verify(saleRepository).findById(1L);
        inOrder.verify(saleRepository).deleteById(1L);
        verifyNoMoreInteractions(saleRepository);
    }

    @Test
    @DisplayName("Should delete sale only once")
    void shouldDeleteSaleOnlyOnce() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        doNothing().when(saleRepository).deleteById(1L);

        deleteSaleUseCase.execute(1L);

        verify(saleRepository, times(1)).findById(1L);
        verify(saleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if sale exists before deleting")
    void shouldCheckIfSaleExistsBeforeDeleting() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        doNothing().when(saleRepository).deleteById(1L);

        deleteSaleUseCase.execute(1L);

        var inOrder = inOrder(saleRepository);
        inOrder.verify(saleRepository).findById(1L);
        inOrder.verify(saleRepository).deleteById(1L);
    }
}
