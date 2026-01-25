package com.sales.application.sale.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.port.SaleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FindSaleUseCase {

    private final SaleRepository saleRepository;

    @Inject
    public FindSaleUseCase(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com id: " + id));
    }

    public Sale findByCode(String code) {
        return saleRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada com código: " + code));
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public List<Sale> findByCustomerCode(String customerCode) {
        return saleRepository.findByCustomerCode(customerCode);
    }
}
