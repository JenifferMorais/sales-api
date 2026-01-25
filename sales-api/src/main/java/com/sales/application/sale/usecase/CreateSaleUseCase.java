package com.sales.application.sale.usecase;

import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateSaleUseCase {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Inject
    public CreateSaleUseCase(SaleRepository saleRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Sale execute(Sale sale) {
        validateSale(sale);
        return saleRepository.save(sale);
    }

    private void validateSale(Sale sale) {

        if (saleRepository.existsByCode(sale.getCode())) {
            throw new IllegalArgumentException("Venda com código " + sale.getCode() + " já existe");
        }

        customerRepository.findByCode(sale.getCustomerCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente não encontrado com código: " + sale.getCustomerCode()
                ));

        sale.validateSale();

        for (SaleItem item : sale.getItems()) {
            productRepository.findByCode(item.getProductCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado com código: " + item.getProductCode()
                    ));
        }
    }
}
