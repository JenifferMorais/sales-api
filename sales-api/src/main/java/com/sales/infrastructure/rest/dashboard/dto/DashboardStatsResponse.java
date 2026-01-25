package com.sales.infrastructure.rest.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private Long totalSales;
    private BigDecimal totalRevenue;
    private Long totalCustomers;
    private Long totalProducts;

    private BigDecimal salesVariation;
    private BigDecimal revenueVariation;
    private BigDecimal customersVariation;
}
