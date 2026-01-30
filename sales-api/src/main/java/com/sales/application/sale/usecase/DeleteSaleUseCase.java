package com.sales.application.sale.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.port.SaleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DeleteSaleUseCase {

    private static final Logger LOG = Logger.getLogger(DeleteSaleUseCase.class);

    private final SaleRepository saleRepository;

    @Inject
    public DeleteSaleUseCase(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public void execute(Long id) {
        LOG.infof("Iniciando exclusão da venda ID: %d", id);

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de excluir venda inexistente - ID: %d", id);
                    return new IllegalArgumentException("Venda não encontrada com id: " + id);
                });

        LOG.debugf("Venda encontrada para exclusão - Código: %s, Cliente: %s, Valor: R$ %.2f",
                   sale.getCode(), sale.getCustomerCode(), sale.getTotalAmount());

        saleRepository.deleteById(id);

        LOG.infof("Venda excluída com sucesso - ID: %d, Código: %s",
                  id, sale.getCode());
    }
}
