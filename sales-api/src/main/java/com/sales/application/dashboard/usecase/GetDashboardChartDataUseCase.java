package com.sales.application.dashboard.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.infrastructure.rest.dashboard.dto.DashboardChartDataPoint;
import com.sales.infrastructure.rest.dashboard.dto.DashboardChartResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetDashboardChartDataUseCase {

    private final SaleRepository saleRepository;

    private static final Map<String, String> MONTH_NAMES = Map.ofEntries(
            Map.entry("01", "Janeiro"),
            Map.entry("02", "Fevereiro"),
            Map.entry("03", "Março"),
            Map.entry("04", "Abril"),
            Map.entry("05", "Maio"),
            Map.entry("06", "Junho"),
            Map.entry("07", "Julho"),
            Map.entry("08", "Agosto"),
            Map.entry("09", "Setembro"),
            Map.entry("10", "Outubro"),
            Map.entry("11", "Novembro"),
            Map.entry("12", "Dezembro")
    );

    private static final Map<String, String> SHORT_MONTH_NAMES = Map.ofEntries(
            Map.entry("01", "Jan"),
            Map.entry("02", "Fev"),
            Map.entry("03", "Mar"),
            Map.entry("04", "Abr"),
            Map.entry("05", "Mai"),
            Map.entry("06", "Jun"),
            Map.entry("07", "Jul"),
            Map.entry("08", "Ago"),
            Map.entry("09", "Set"),
            Map.entry("10", "Out"),
            Map.entry("11", "Nov"),
            Map.entry("12", "Dez")
    );

    private static final String[] DAY_NAMES = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};

    @Inject
    public GetDashboardChartDataUseCase(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public DashboardChartResponse execute(String range) {
        LocalDateTime now = LocalDateTime.now();
        List<DashboardChartDataPoint> chartData;

        switch (range) {
            case "week":
                chartData = getWeekData(now);
                break;
            case "month":
                chartData = getMonthData(now);
                break;
            case "quarter":
                chartData = getQuarterData(now);
                break;
            case "year":
                chartData = getYearData(now);
                break;
            default:
                chartData = getMonthData(now);
        }

        return DashboardChartResponse.builder()
                .chartData(chartData)
                .build();
    }

    private List<DashboardChartDataPoint> getWeekData(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        LocalDate weekStart = today.minusDays(6);

        List<DashboardChartDataPoint> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            List<Sale> sales = saleRepository.findByDateRange(startOfDay, endOfDay);

            String dayName = DAY_NAMES[date.getDayOfWeek().getValue() % 7];

            result.add(DashboardChartDataPoint.builder()
                    .label(dayName)
                    .shortLabel(dayName)
                    .date(date)
                    .salesCount((long) sales.size())
                    .revenue(calculateTotalRevenue(sales))
                    .build());
        }

        return result;
    }

    private List<DashboardChartDataPoint> getMonthData(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());

        List<DashboardChartDataPoint> result = new ArrayList<>();

        LocalDate currentWeekStart = monthStart;
        int weekNumber = 1;

        while (currentWeekStart.isBefore(today.plusDays(1))) {
            LocalDate weekEnd = currentWeekStart.plusDays(6);
            if (weekEnd.isAfter(today)) {
                weekEnd = today;
            }

            LocalDateTime startOfWeek = currentWeekStart.atStartOfDay();
            LocalDateTime endOfWeek = weekEnd.atTime(23, 59, 59);

            List<Sale> sales = saleRepository.findByDateRange(startOfWeek, endOfWeek);

            String monthAbbr = SHORT_MONTH_NAMES.get(String.format("%02d", monthStart.getMonthValue()));

            result.add(DashboardChartDataPoint.builder()
                    .label("Semana " + weekNumber)
                    .shortLabel("Semana " + weekNumber)
                    .date(currentWeekStart)
                    .salesCount((long) sales.size())
                    .revenue(calculateTotalRevenue(sales))
                    .build());

            currentWeekStart = currentWeekStart.plusDays(7);
            weekNumber++;

            if (weekNumber > 4) break;
        }

        return result;
    }

    private List<DashboardChartDataPoint> getQuarterData(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        List<DashboardChartDataPoint> result = new ArrayList<>();

        for (int i = 2; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            LocalDate monthStart = monthDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate monthEnd = monthDate.with(TemporalAdjusters.lastDayOfMonth());

            LocalDateTime start = monthStart.atStartOfDay();
            LocalDateTime end = monthEnd.atTime(23, 59, 59);

            List<Sale> sales = saleRepository.findByDateRange(start, end);

            String monthKey = String.format("%02d", monthDate.getMonthValue());
            String monthName = MONTH_NAMES.get(monthKey);
            String monthShort = SHORT_MONTH_NAMES.get(monthKey);

            result.add(DashboardChartDataPoint.builder()
                    .label(monthName)
                    .shortLabel(monthShort)
                    .date(monthStart)
                    .salesCount((long) sales.size())
                    .revenue(calculateTotalRevenue(sales))
                    .build());
        }

        return result;
    }

    private List<DashboardChartDataPoint> getYearData(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        List<DashboardChartDataPoint> result = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            LocalDate monthStart = monthDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate monthEnd = monthDate.with(TemporalAdjusters.lastDayOfMonth());

            LocalDateTime start = monthStart.atStartOfDay();
            LocalDateTime end = monthEnd.atTime(23, 59, 59);

            List<Sale> sales = saleRepository.findByDateRange(start, end);

            String monthKey = String.format("%02d", monthDate.getMonthValue());
            String monthName = MONTH_NAMES.get(monthKey);
            String monthShort = SHORT_MONTH_NAMES.get(monthKey);

            result.add(DashboardChartDataPoint.builder()
                    .label(monthName)
                    .shortLabel(monthShort)
                    .date(monthStart)
                    .salesCount((long) sales.size())
                    .revenue(calculateTotalRevenue(sales))
                    .build());
        }

        return result;
    }

    private BigDecimal calculateTotalRevenue(List<Sale> sales) {
        return sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
