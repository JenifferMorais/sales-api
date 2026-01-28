package com.sales.application.dashboard.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.infrastructure.rest.dashboard.dto.DashboardStatsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@ApplicationScoped
public class GetDashboardStatsUseCase {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Inject
    public GetDashboardStatsUseCase(
            SaleRepository saleRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public DashboardStatsResponse execute() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate firstDayOfLastMonth = firstDayOfCurrentMonth.minusMonths(1);
        LocalDate lastDayOfLastMonth = firstDayOfCurrentMonth.minusDays(1);

        LocalDateTime currentMonthStart = firstDayOfCurrentMonth.atStartOfDay();
        LocalDateTime currentMonthEnd = today.atTime(23, 59, 59);
        List<Sale> currentMonthSales = saleRepository.findByDateRange(currentMonthStart, currentMonthEnd);

        LocalDateTime lastMonthStart = firstDayOfLastMonth.atStartOfDay();
        LocalDateTime lastMonthEnd = lastDayOfLastMonth.atTime(23, 59, 59);
        List<Sale> lastMonthSales = saleRepository.findByDateRange(lastMonthStart, lastMonthEnd);

        Long currentSalesCount = (long) currentMonthSales.size();
        BigDecimal currentRevenue = currentMonthSales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long lastSalesCount = (long) lastMonthSales.size();
        BigDecimal lastRevenue = lastMonthSales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal salesVariation = calculateVariation(currentSalesCount, lastSalesCount);
        BigDecimal revenueVariation = calculateVariation(currentRevenue, lastRevenue);

        List<Customer> allCustomers = customerRepository.findAll();
        long currentMonthCustomers = allCustomers.stream()
                .filter(c -> c.getCreatedAt().isAfter(currentMonthStart) && c.getCreatedAt().isBefore(currentMonthEnd))
                .count();
        long lastMonthCustomers = allCustomers.stream()
                .filter(c -> c.getCreatedAt().isAfter(lastMonthStart) && c.getCreatedAt().isBefore(lastMonthEnd))
                .count();
        BigDecimal customersVariation = calculateVariation(currentMonthCustomers, lastMonthCustomers);

        List<Sale> allSales = saleRepository.findAll();
        Long totalSales = (long) allSales.size();
        BigDecimal totalRevenue = allSales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Long totalCustomers = (long) allCustomers.size();
        Long totalProducts = (long) productRepository.findAll().size();

        return DashboardStatsResponse.builder()
                .totalSales(totalSales)
                .totalRevenue(totalRevenue)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .salesVariation(salesVariation)
                .revenueVariation(revenueVariation)
                .customersVariation(customersVariation)
                .build();
    }

    private BigDecimal calculateVariation(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        BigDecimal currentBD = BigDecimal.valueOf(current);
        BigDecimal previousBD = BigDecimal.valueOf(previous);
        return currentBD.subtract(previousBD)
                .divide(previousBD, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateVariation(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }
}
