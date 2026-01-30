package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;

@ApplicationScoped
public class UpdateProductUseCase {

    private static final Logger LOG = Logger.getLogger(UpdateProductUseCase.class);

    private final ProductRepository productRepository;

    @Inject
    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Long id, String name, ProductType type, String details,
                          BigDecimal weight, BigDecimal purchasePrice, BigDecimal salePrice,
                          Dimensions dimensions, String destinationVehicle) {
        LOG.infof("Iniciando atualização do produto ID: %d", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de atualizar produto inexistente - ID: %d", id);
                    return new IllegalArgumentException("Produto não encontrado com id: " + id);
                });

        LOG.debugf("Produto encontrado - Código: %s, Nome atual: %s", product.getCode(), product.getName());

        product.updateInfo(name, type, details, weight, purchasePrice, salePrice, dimensions, destinationVehicle);
        Product updatedProduct = productRepository.save(product);

        LOG.infof("Produto atualizado com sucesso - ID: %d, Código: %s, Novo nome: %s, Novo preço: R$ %.2f",
                  updatedProduct.getId(), updatedProduct.getCode(), updatedProduct.getName(),
                  updatedProduct.getSalePrice());

        return updatedProduct;
    }
}
