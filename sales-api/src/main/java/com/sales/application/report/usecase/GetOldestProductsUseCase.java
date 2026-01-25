package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.product.entity.ProductEntity;
import com.sales.infrastructure.persistence.product.repository.ProductPanacheRepository;
import com.sales.infrastructure.rest.report.dto.OldestProductData;
import com.sales.infrastructure.rest.report.dto.OldestProductsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetOldestProductsUseCase {

    private final ProductPanacheRepository productRepository;

    @Inject
    public GetOldestProductsUseCase(ProductPanacheRepository productRepository) {
        this.productRepository = productRepository;
    }

    public OldestProductsResponse execute() {
        List<ProductEntity> oldestProducts = productRepository.findOldestProducts(3);

        List<OldestProductData> products = oldestProducts.stream()
                .map(entity -> OldestProductData.builder()
                        .name(entity.getName())
                        .weight(entity.getWeight())
                        .registrationDate(entity.getCreatedAt())
                        .purchasePrice(entity.getPurchasePrice())
                        .build())
                .collect(Collectors.toList());

        return OldestProductsResponse.builder()
                .products(products)
                .build();
    }
}
