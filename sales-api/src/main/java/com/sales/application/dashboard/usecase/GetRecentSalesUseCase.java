package com.sales.application.dashboard.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.infrastructure.rest.dashboard.dto.RecentSaleData;
import com.sales.infrastructure.rest.dashboard.dto.RecentSalesResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetRecentSalesUseCase {

    private final SaleRepository saleRepository;

    @Inject
    public GetRecentSalesUseCase(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public RecentSalesResponse execute(int limit) {
        List<Sale> allSales = saleRepository.findAll();

        List<RecentSaleData> recentSales = allSales.stream()
                .sorted(Comparator.comparing(Sale::getCreatedAt).reversed())
                .limit(limit)
                .map(this::mapToRecentSaleData)
                .collect(Collectors.toList());

        return RecentSalesResponse.builder()
                .sales(recentSales)
                .build();
    }

    private RecentSaleData mapToRecentSaleData(Sale sale) {
        String productName = sale.getItems().stream()
                .findFirst()
                .map(SaleItem::getProductName)
                .orElse("N/A");

        if (sale.getItems().size() > 1) {
            productName += " (+" + (sale.getItems().size() - 1) + " itens)";
        }

        return RecentSaleData.builder()
                .id(sale.getId())
                .code(sale.getCode())
                .customerName(sale.getCustomerName())
                .productName(productName)
                .totalAmount(sale.getTotalAmount())
                .saleDate(sale.getCreatedAt())
                .build();
    }
}
