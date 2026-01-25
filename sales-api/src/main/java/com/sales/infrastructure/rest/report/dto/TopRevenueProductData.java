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
public class TopRevenueProductData {
    private String productCode;
    private String productName;
    private BigDecimal salePrice;
    private BigDecimal totalRevenue;
}
