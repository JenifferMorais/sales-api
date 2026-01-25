package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.sale.repository.SalePanacheRepository;
import com.sales.infrastructure.rest.report.dto.TopRevenueProductData;
import com.sales.infrastructure.rest.report.dto.TopRevenueProductsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetTopRevenueProductsUseCase {

    private final SalePanacheRepository saleRepository;

    @Inject
    public GetTopRevenueProductsUseCase(SalePanacheRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public TopRevenueProductsResponse execute() {
        List<Map<String, Object>> rawData = saleRepository.getTopRevenueProducts(4);

        List<TopRevenueProductData> products = rawData.stream()
                .map(row -> TopRevenueProductData.builder()
                        .productCode((String) row.get("productCode"))
                        .productName((String) row.get("productName"))
                        .salePrice((BigDecimal) row.get("salePrice"))
                        .totalRevenue((BigDecimal) row.get("totalRevenue"))
                        .build())
                .collect(Collectors.toList());

        return TopRevenueProductsResponse.builder()
                .products(products)
                .build();
    }
}
