package com.sales.application.dashboard.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.infrastructure.rest.dashboard.dto.DashboardChartDataPoint;
import com.sales.infrastructure.rest.dashboard.dto.DashboardChartResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetDashboardChartDataUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private GetDashboardChartDataUseCase useCase;

    private Sale sale1;
    private Sale sale2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sale1 = new Sale("SALE001", "CUST001", "João Silva", "SELL001", "Vendedor A",
                PaymentMethod.CARTAO_CREDITO, "1234", BigDecimal.valueOf(60.00));
        SaleItem item1 = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));
        sale1.addItem(item1);

        sale2 = new Sale("SALE002", "CUST002", "Maria Santos", "SELL002", "Vendedor B",
                PaymentMethod.PIX, null, null);
        SaleItem item2 = new SaleItem("PROD002", "Batom Rosa", 1, BigDecimal.valueOf(25.00));
        sale2.addItem(item2);
    }

    @Test
    void shouldGetWeekData() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(sale1, sale2));

        DashboardChartResponse result = useCase.execute("week");

        assertThat(result).isNotNull();
        assertThat(result.getChartData()).isNotNull();
        assertThat(result.getChartData()).hasSize(7); // 7 days
        verify(saleRepository, times(7)).findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void shouldGetMonthData() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(sale1));

        DashboardChartResponse result = useCase.execute("month");

        assertThat(result).isNotNull();
        assertThat(result.getChartData()).isNotNull();
        assertThat(result.getChartData()).isNotEmpty();
        // Should have weeks (1-4)
        assertThat(result.getChartData().size()).isLessThanOrEqualTo(4);
    }

    @Test
    void shouldGetQuarterData() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(sale1));

        DashboardChartResponse result = useCase.execute("quarter");

        assertThat(result).isNotNull();
        assertThat(result.getChartData()).isNotNull();
        assertThat(result.getChartData()).hasSize(3); // Last 3 months
        verify(saleRepository, times(3)).findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void shouldGetYearData() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(sale1));

        DashboardChartResponse result = useCase.execute("year");

        assertThat(result).isNotNull();
        assertThat(result.getChartData()).isNotNull();
        assertThat(result.getChartData()).hasSize(12); // Last 12 months
        verify(saleRepository, times(12)).findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void shouldDefaultToMonthDataWhenInvalidRange() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(sale1));

        DashboardChartResponse result = useCase.execute("invalid");

        assertThat(result).isNotNull();
        assertThat(result.getChartData()).isNotNull();
        assertThat(result.getChartData()).isNotEmpty();
    }

    @Test
    void shouldCalculateRevenueCorrectly() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(sale1, sale2));

        DashboardChartResponse result = useCase.execute("week");

        assertThat(result).isNotNull();
        assertThat(result.getChartData()).isNotEmpty();

        // Each day should have the sum of both sales
        BigDecimal expectedRevenue = sale1.getTotalAmount().add(sale2.getTotalAmount());
        result.getChartData().forEach(dataPoint -> {
            assertThat(dataPoint.getRevenue()).isEqualByComparingTo(expectedRevenue);
            assertThat(dataPoint.getSalesCount()).isEqualTo(2L);
        });
    }

    @Test
    void shouldHandleNoSales() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        DashboardChartResponse result = useCase.execute("week");

        assertThat(result).isNotNull();
        assertThat(result.getChartData()).hasSize(7);
        result.getChartData().forEach(dataPoint -> {
            assertThat(dataPoint.getRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dataPoint.getSalesCount()).isEqualTo(0L);
        });
    }
}
