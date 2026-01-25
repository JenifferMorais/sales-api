package com.sales.infrastructure.rest.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRevenueData {
    private Integer month;
    private Integer year;
    private String monthName;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
}
