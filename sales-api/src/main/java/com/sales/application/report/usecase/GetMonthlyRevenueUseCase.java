package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.sale.repository.SalePanacheRepository;
import com.sales.infrastructure.rest.report.dto.MonthlyRevenueData;
import com.sales.infrastructure.rest.report.dto.MonthlyRevenueResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@ApplicationScoped
public class GetMonthlyRevenueUseCase {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.09");

    private final SalePanacheRepository saleRepository;

    @Inject
    public GetMonthlyRevenueUseCase(SalePanacheRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public MonthlyRevenueResponse execute(LocalDate referenceDate) {

        LocalDateTime endDate = referenceDate.atTime(23, 59, 59);
        LocalDateTime startDate = referenceDate.minusMonths(11).withDayOfMonth(1).atStartOfDay();

        List<Map<String, Object>> rawData = saleRepository.getMonthlyRevenue(startDate, endDate);

        Map<String, BigDecimal> revenueByMonth = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            Integer month = ((Number) row.get("month")).intValue();
            Integer year = ((Number) row.get("year")).intValue();
            BigDecimal subtotal = (BigDecimal) row.get("subtotal");
            String key = year + "-" + String.format("%02d", month);
            revenueByMonth.put(key, subtotal);
        }

        List<MonthlyRevenueData> monthlyDataList = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(referenceDate.minusMonths(i));
            String key = yearMonth.getYear() + "-" + String.format("%02d", yearMonth.getMonthValue());

            BigDecimal subtotal = revenueByMonth.getOrDefault(key, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

            totalRevenue = totalRevenue.add(subtotal);
            totalTax = totalTax.add(taxAmount);

            MonthlyRevenueData data = MonthlyRevenueData.builder()
                    .month(yearMonth.getMonthValue())
                    .year(yearMonth.getYear())
                    .monthName(yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")))
                    .subtotal(subtotal)
                    .taxAmount(taxAmount)
                    .total(total)
                    .build();

            monthlyDataList.add(data);
        }

        BigDecimal grandTotal = totalRevenue.add(totalTax).setScale(2, RoundingMode.HALF_UP);

        return MonthlyRevenueResponse.builder()
                .monthlyData(monthlyDataList)
                .totalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_UP))
                .totalTax(totalTax.setScale(2, RoundingMode.HALF_UP))
                .grandTotal(grandTotal)
                .build();
    }
}
