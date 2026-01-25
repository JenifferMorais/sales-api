package com.sales.application.sale.usecase;

import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class UpdateSaleUseCase {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Inject
    public UpdateSaleUseCase(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Sale execute(Long id, String sellerCode, String sellerName, PaymentMethod paymentMethod,
                       String cardNumber, BigDecimal amountPaid, List<SaleItem> items) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Venda não encontrada com id: " + id
                ));

        for (SaleItem item : items) {
            productRepository.findByCode(item.getProductCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado com código: " + item.getProductCode()
                    ));
        }

        sale.update(sellerCode, sellerName, paymentMethod, cardNumber, amountPaid, items);

        return saleRepository.save(sale);
    }
}
