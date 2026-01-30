package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateProductUseCase {

    private static final Logger LOG = Logger.getLogger(CreateProductUseCase.class);

    private final ProductRepository productRepository;

    @Inject
    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Product product) {
        LOG.infof("Iniciando criação de produto: %s", product.getName());

        if (product.getCode() != null && !product.getCode().isBlank()) {
            LOG.debugf("Validando código do produto: %s", product.getCode());

            if (productRepository.existsByCode(product.getCode())) {
                LOG.warnf("Tentativa de criar produto com código duplicado: %s", product.getCode());
                throw new IllegalArgumentException("Produto com código " + product.getCode() + " já existe");
            }
        }

        Product savedProduct = productRepository.save(product);

        LOG.infof("Produto criado com sucesso - ID: %d, Código: %s, Nome: %s, Preço: R$ %.2f",
                  savedProduct.getId(), savedProduct.getCode(), savedProduct.getName(),
                  savedProduct.getSalePrice());

        return savedProduct;
    }
}
