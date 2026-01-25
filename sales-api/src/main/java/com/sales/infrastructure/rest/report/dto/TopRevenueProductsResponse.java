package com.sales.infrastructure.rest.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopRevenueProductsResponse {
    private List<TopRevenueProductData> products;
}
