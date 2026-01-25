package com.sales.application.sale.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.shared.PageResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SearchSalesUseCase {

    private final SaleRepository saleRepository;

    @Inject
    public SearchSalesUseCase(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public PageResult<Sale> execute(String filter, int page, int size) {
        return saleRepository.search(filter, page, size);
    }
}
