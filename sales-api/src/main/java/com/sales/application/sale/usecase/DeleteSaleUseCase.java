package com.sales.application.sale.usecase;

import com.sales.domain.sale.port.SaleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeleteSaleUseCase {

    private final SaleRepository saleRepository;

    @Inject
    public DeleteSaleUseCase(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public void execute(Long id) {

        saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com id: " + id));

        saleRepository.deleteById(id);
    }
}
