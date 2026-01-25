package com.sales.application.product.usecase;

import com.sales.domain.product.port.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    @Inject
    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(Long id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Produto não encontrado com id: " + id);
        }
        productRepository.deleteById(id);
    }
}
