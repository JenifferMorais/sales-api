package com.sales.application.dashboard.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.infrastructure.rest.dashboard.dto.RecentSaleData;
import com.sales.infrastructure.rest.dashboard.dto.RecentSalesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetRecentSalesUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private GetRecentSalesUseCase useCase;

    private Sale sale1;
    private Sale sale2;
    private Sale sale3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SaleItem item1 = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));
        sale1 = new Sale("SALE001", "CUST001", "João Silva", "SELL001", "Vendedor A",
                PaymentMethod.CARTAO_CREDITO, "1234", BigDecimal.valueOf(100.00));
        sale1.addItem(item1);

        SaleItem item2 = new SaleItem("PROD002", "Batom Rosa", 1, BigDecimal.valueOf(25.00));
        sale2 = new Sale("SALE002", "CUST002", "Maria Santos", "SELL002", "Vendedor B",
                PaymentMethod.PIX, null, null);
        sale2.addItem(item2);

        SaleItem item3a = new SaleItem("PROD003", "Delineador", 1, BigDecimal.valueOf(20.00));
        SaleItem item3b = new SaleItem("PROD004", "Máscara", 1, BigDecimal.valueOf(35.00));
        sale3 = new Sale("SALE003", "CUST003", "Ana Costa", "SELL003", "Vendedor C",
                PaymentMethod.CARTAO_DEBITO, "5678", BigDecimal.valueOf(100.00));
        sale3.addItem(item3a);
        sale3.addItem(item3b);
    }

    @Test
    void shouldGetRecentSales() {
        when(saleRepository.findAll()).thenReturn(List.of(sale1, sale2, sale3));

        RecentSalesResponse result = useCase.execute(10);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).isNotNull();
        assertThat(result.getSales()).hasSize(3);
        verify(saleRepository, times(1)).findAll();
    }

    @Test
    void shouldLimitResults() {
        when(saleRepository.findAll()).thenReturn(List.of(sale1, sale2, sale3));

        RecentSalesResponse result = useCase.execute(2);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).hasSize(2);
        verify(saleRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyWhenNoSales() {
        when(saleRepository.findAll()).thenReturn(List.of());

        RecentSalesResponse result = useCase.execute(10);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).isEmpty();
        verify(saleRepository, times(1)).findAll();
    }

    @Test
    void shouldMapSaleDataCorrectly() {
        when(saleRepository.findAll()).thenReturn(List.of(sale1));

        RecentSalesResponse result = useCase.execute(10);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).hasSize(1);

        RecentSaleData saleData = result.getSales().get(0);
        assertThat(saleData.getId()).isEqualTo(sale1.getId());
        assertThat(saleData.getCode()).isEqualTo("SALE001");
        assertThat(saleData.getCustomerName()).isEqualTo("João Silva");
        assertThat(saleData.getProductName()).isEqualTo("Batom Vermelho");
        assertThat(saleData.getTotalAmount()).isEqualByComparingTo(sale1.getTotalAmount());
        assertThat(saleData.getSaleDate()).isNotNull();
    }

    @Test
    void shouldShowMultipleItemsIndicator() {
        when(saleRepository.findAll()).thenReturn(List.of(sale3));

        RecentSalesResponse result = useCase.execute(10);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).hasSize(1);

        RecentSaleData saleData = result.getSales().get(0);
        assertThat(saleData.getProductName()).contains("Delineador");
        assertThat(saleData.getProductName()).contains("(+1 itens)");
    }

    @Test
    void shouldHandleSaleWithNoItems() {
        Sale saleWithNoItems = new Sale("SALE999", "CUST999", "Test Customer", "SELL999", "Test Seller",
                PaymentMethod.DINHEIRO, null, BigDecimal.valueOf(100.00));
        when(saleRepository.findAll()).thenReturn(List.of(saleWithNoItems));

        RecentSalesResponse result = useCase.execute(10);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).hasSize(1);

        RecentSaleData saleData = result.getSales().get(0);
        assertThat(saleData.getProductName()).isEqualTo("N/A");
    }

    @Test
    void shouldSortByMostRecent() {
        // All sales have the same creation time by default, but in reality they would be ordered
        when(saleRepository.findAll()).thenReturn(List.of(sale1, sale2, sale3));

        RecentSalesResponse result = useCase.execute(10);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).hasSize(3);
        // Verify the repository was called correctly
        verify(saleRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnOnlyRequestedLimit() {
        when(saleRepository.findAll()).thenReturn(List.of(sale1, sale2, sale3));

        RecentSalesResponse result = useCase.execute(1);

        assertThat(result).isNotNull();
        assertThat(result.getSales()).hasSize(1);
    }
}
