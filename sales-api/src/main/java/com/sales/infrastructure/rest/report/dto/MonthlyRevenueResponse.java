package com.sales.infrastructure.rest.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRevenueResponse {
    private List<MonthlyRevenueData> monthlyData;
    private BigDecimal totalRevenue;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;
}
