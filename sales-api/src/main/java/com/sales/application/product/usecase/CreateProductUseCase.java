package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    @Inject
    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Product product) {

        if (product.getCode() != null && !product.getCode().isBlank()) {
            if (productRepository.existsByCode(product.getCode())) {
                throw new IllegalArgumentException("Produto com código " + product.getCode() + " já existe");
            }
        }
        return productRepository.save(product);
    }
}
