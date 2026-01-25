package com.sales.application.product.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@ApplicationScoped
public class UpdateProductUseCase {

    private final ProductRepository productRepository;

    @Inject
    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Long id, String name, ProductType type, String details,
                          BigDecimal weight, BigDecimal purchasePrice, BigDecimal salePrice,
                          Dimensions dimensions, String destinationVehicle) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com id: " + id));

        product.updateInfo(name, type, details, weight, purchasePrice, salePrice, dimensions, destinationVehicle);
        return productRepository.save(product);
    }
}
