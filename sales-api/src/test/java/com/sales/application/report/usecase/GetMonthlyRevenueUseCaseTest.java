package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.sale.repository.SalePanacheRepository;
import com.sales.infrastructure.rest.report.dto.MonthlyRevenueData;
import com.sales.infrastructure.rest.report.dto.MonthlyRevenueResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMonthlyRevenueUseCase Tests")
class GetMonthlyRevenueUseCaseTest {

    @Mock
    private SalePanacheRepository saleRepository;

    @InjectMocks
    private GetMonthlyRevenueUseCase getMonthlyRevenueUseCase;

    private LocalDate referenceDate;
    private List<Map<String, Object>> mockRevenueData;

    @BeforeEach
    void setUp() {
        referenceDate = LocalDate.of(2024, 12, 31);
        mockRevenueData = new ArrayList<>();
    }

    @Test
    @DisplayName("Should calculate monthly revenue with tax correctly")
    void shouldCalculateMonthlyRevenueWithTax() {

        Map<String, Object> januaryData = new HashMap<>();
        januaryData.put("month", 1);
        januaryData.put("year", 2024);
        januaryData.put("subtotal", new BigDecimal("1000.00"));
        mockRevenueData.add(januaryData);

        when(saleRepository.getMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockRevenueData);

        MonthlyRevenueResponse result = getMonthlyRevenueUseCase.execute(referenceDate);

        assertThat(result).isNotNull();
        assertThat(result.getMonthlyData()).hasSize(12);
        assertThat(result.getTotalRevenue()).isEqualByComparingTo(new BigDecimal("1000.00"));

        BigDecimal expectedTax = new BigDecimal("90.00");
        assertThat(result.getTotalTax()).isEqualByComparingTo(expectedTax);

        BigDecimal expectedGrandTotal = new BigDecimal("1090.00");
        assertThat(result.getGrandTotal()).isEqualByComparingTo(expectedGrandTotal);
    }

    @Test
    @DisplayName("Should return empty revenue for months with no sales")
    void shouldReturnEmptyRevenueForMonthsWithNoSales() {

        when(saleRepository.getMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        MonthlyRevenueResponse result = getMonthlyRevenueUseCase.execute(referenceDate);

        assertThat(result).isNotNull();
        assertThat(result.getMonthlyData()).hasSize(12);
        assertThat(result.getTotalRevenue()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        assertThat(result.getTotalTax()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        assertThat(result.getGrandTotal()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Should calculate correct period for 12 months")
    void shouldCalculateCorrectPeriodFor12Months() {

        when(saleRepository.getMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        getMonthlyRevenueUseCase.execute(referenceDate);

        verify(saleRepository).getMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should aggregate multiple months correctly")
    void shouldAggregateMultipleMonths() {

        Map<String, Object> januaryData = new HashMap<>();
        januaryData.put("month", 1);
        januaryData.put("year", 2024);
        januaryData.put("subtotal", new BigDecimal("1000.00"));

        Map<String, Object> februaryData = new HashMap<>();
        februaryData.put("month", 2);
        februaryData.put("year", 2024);
        februaryData.put("subtotal", new BigDecimal("2000.00"));

        mockRevenueData.add(januaryData);
        mockRevenueData.add(februaryData);

        when(saleRepository.getMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockRevenueData);

        MonthlyRevenueResponse result = getMonthlyRevenueUseCase.execute(referenceDate);

        assertThat(result.getTotalRevenue()).isEqualByComparingTo(new BigDecimal("3000.00"));

        assertThat(result.getTotalTax()).isEqualByComparingTo(new BigDecimal("270.00"));

        assertThat(result.getGrandTotal()).isEqualByComparingTo(new BigDecimal("3270.00"));
    }

    @Test
    @DisplayName("Should verify repository interaction")
    void shouldVerifyRepositoryInteraction() {

        when(saleRepository.getMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        getMonthlyRevenueUseCase.execute(referenceDate);

        verify(saleRepository, times(1)).getMonthlyRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
