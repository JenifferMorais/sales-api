package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FindProductUseCase {

    private final ProductRepository productRepository;

    @Inject
    public FindProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com id: " + id));
    }

    public Product findByCode(String code) {
        return productRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com código: " + code));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAllSortedByName() {
        return productRepository.findAllSortedByName();
    }
}
