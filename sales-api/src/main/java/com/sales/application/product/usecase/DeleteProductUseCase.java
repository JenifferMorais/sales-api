package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DeleteProductUseCase {

    private static final Logger LOG = Logger.getLogger(DeleteProductUseCase.class);

    private final ProductRepository productRepository;

    @Inject
    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(Long id) {
        LOG.infof("Iniciando exclusão do produto ID: %d", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de excluir produto inexistente - ID: %d", id);
                    return new IllegalArgumentException("Produto não encontrado com id: " + id);
                });

        LOG.debugf("Produto encontrado para exclusão - Código: %s, Nome: %s",
                   product.getCode(), product.getName());

        productRepository.deleteById(id);

        LOG.infof("Produto excluído com sucesso - ID: %d, Código: %s, Nome: %s",
                  id, product.getCode(), product.getName());
    }
}
