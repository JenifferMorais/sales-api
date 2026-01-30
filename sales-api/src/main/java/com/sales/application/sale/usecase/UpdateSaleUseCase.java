package com.sales.application.sale.usecase;

import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class UpdateSaleUseCase {

    private static final Logger LOG = Logger.getLogger(UpdateSaleUseCase.class);

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
        LOG.infof("Iniciando atualização da venda ID: %d - Vendedor: %s, Itens: %d",
                  id, sellerName, items.size());

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de atualizar venda inexistente - ID: %d", id);
                    return new IllegalArgumentException("Venda não encontrada com id: " + id);
                });

        LOG.debugf("Venda encontrada - Código: %s, Cliente: %s", sale.getCode(), sale.getCustomerCode());

        for (SaleItem item : items) {
            productRepository.findByCode(item.getProductCode())
                    .orElseThrow(() -> {
                        LOG.warnf("Produto não encontrado ao atualizar venda - Código: %s", item.getProductCode());
                        return new IllegalArgumentException(
                                "Produto não encontrado com código: " + item.getProductCode()
                        );
                    });
        }

        sale.update(sellerCode, sellerName, paymentMethod, cardNumber, amountPaid, items);

        Sale updatedSale = saleRepository.save(sale);

        LOG.infof("Venda atualizada com sucesso - ID: %d, Código: %s, Novo valor: R$ %.2f",
                  updatedSale.getId(), updatedSale.getCode(), updatedSale.getTotalAmount());

        return updatedSale;
    }
}
