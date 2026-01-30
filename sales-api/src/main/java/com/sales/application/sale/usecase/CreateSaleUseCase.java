package com.sales.application.sale.usecase;

import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateSaleUseCase {

    private static final Logger LOG = Logger.getLogger(CreateSaleUseCase.class);

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
        LOG.infof("Iniciando criação de venda - Código: %s, Cliente: %s, Itens: %d",
                  sale.getCode(), sale.getCustomerCode(), sale.getItems().size());

        validateSale(sale);

        Sale savedSale = saleRepository.save(sale);

        LOG.infof("Venda criada com sucesso - ID: %d, Código: %s, Valor total: R$ %.2f",
                  savedSale.getId(), savedSale.getCode(), savedSale.getTotalAmount());

        return savedSale;
    }

    private void validateSale(Sale sale) {
        LOG.debugf("Validando venda - Código: %s", sale.getCode());

        if (saleRepository.existsByCode(sale.getCode())) {
            LOG.warnf("Tentativa de criar venda com código duplicado: %s", sale.getCode());
            throw new IllegalArgumentException("Venda com código " + sale.getCode() + " já existe");
        }

        customerRepository.findByCode(sale.getCustomerCode())
                .orElseThrow(() -> {
                    LOG.warnf("Cliente não encontrado para venda - Código: %s", sale.getCustomerCode());
                    return new IllegalArgumentException(
                            "Cliente não encontrado com código: " + sale.getCustomerCode()
                    );
                });

        sale.validateSale();

        for (SaleItem item : sale.getItems()) {
            productRepository.findByCode(item.getProductCode())
                    .orElseThrow(() -> {
                        LOG.warnf("Produto não encontrado para venda - Código: %s", item.getProductCode());
                        return new IllegalArgumentException(
                                "Produto não encontrado com código: " + item.getProductCode()
                        );
                    });
        }

        LOG.debugf("Validação da venda concluída com sucesso - Total de itens: %d", sale.getItems().size());
    }
}
