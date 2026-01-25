package com.sales.infrastructure.rest.product.dto;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMapper {

    public Product toDomain(ProductRequest request) {
        Dimensions dimensions = new Dimensions(
                request.getHeight(),
                request.getWidth(),
                request.getDepth()
        );
        return new Product(
                request.getCode(),
                request.getName(),
                ProductType.fromString(request.getType()),
                request.getDetails(),
                request.getWeight(),
                request.getPurchasePrice(),
                request.getSalePrice(),
                dimensions,
                request.getDestinationVehicle()
        );
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getType().getDescription(),
                product.getDetails(),
                product.getWeight(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getDimensions().getHeight(),
                product.getDimensions().getWidth(),
                product.getDimensions().getDepth(),
                product.getDestinationVehicle(),
                product.getStockQuantity(),
                product.getCreatedAt()
        );
    }
}
