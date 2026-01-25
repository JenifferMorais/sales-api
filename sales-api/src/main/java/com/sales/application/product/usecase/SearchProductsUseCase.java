package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.shared.PageResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SearchProductsUseCase {

    private final ProductRepository productRepository;

    @Inject
    public SearchProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public PageResult<Product> execute(String filter, int page, int size) {
        return productRepository.search(filter, page, size);
    }
}
