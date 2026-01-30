package com.sales.infrastructure.persistence.product.repository;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import com.sales.domain.shared.PageResult;
import com.sales.infrastructure.persistence.product.entity.ProductEntity;
import com.sales.infrastructure.persistence.product.service.ProductCodeGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductRepositoryAdapter implements ProductRepository {

    @Inject
    ProductPanacheRepository panacheRepository;

    @Inject
    ProductCodeGenerator codeGenerator;

    @Override
    @Transactional
    public Product save(Product product) {
        ProductEntity entity;

        if (product.getId() != null) {
            entity = panacheRepository.findById(product.getId());
            if (entity == null) {
                throw new IllegalArgumentException("Produto não encontrado com id: " + product.getId());
            }

            entity.setName(product.getName());
            entity.setType(product.getType().name());
            entity.setDetails(product.getDetails());
            entity.setWeight(product.getWeight());
            entity.setPurchasePrice(product.getPurchasePrice());
            entity.setSalePrice(product.getSalePrice());
            entity.setHeight(product.getDimensions().getHeight());
            entity.setWidth(product.getDimensions().getWidth());
            entity.setDepth(product.getDimensions().getDepth());
            entity.setDestinationVehicle(product.getDestinationVehicle());
            entity.setStockQuantity(product.getStockQuantity());
        } else {
            entity = toEntity(product);

            if (entity.getCode() == null || entity.getCode().isBlank()) {
                String generatedCode = codeGenerator.generateNextCode();
                entity.setCode(generatedCode);
            }

            panacheRepository.persist(entity);
        }

        return toDomain(entity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return panacheRepository.findByIdOptional(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return panacheRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return panacheRepository.listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllSortedByName() {
        return panacheRepository.findAllSortedByName().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByType(ProductType type) {
        return panacheRepository.findByType(type.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return panacheRepository.findByNameContaining(name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        panacheRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return panacheRepository.existsByCode(code);
    }

    @Override
    public PageResult<Product> search(String filter, int page, int size) {
        List<ProductEntity> entities = panacheRepository.search(filter, page, size);
        long total = panacheRepository.countSearch(filter);
        List<Product> products = entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new PageResult<>(products, total, page, size);
    }

    private Product toDomain(ProductEntity entity) {
        Dimensions dimensions = new Dimensions(
                entity.getHeight(),
                entity.getWidth(),
                entity.getDepth()
        );
        return new Product(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                ProductType.fromDatabase(entity.getType()),
                entity.getDetails(),
                entity.getWeight(),
                entity.getPurchasePrice(),
                entity.getSalePrice(),
                dimensions,
                entity.getDestinationVehicle(),
                entity.getStockQuantity(),
                entity.getCreatedAt()
        );
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setCode(product.getCode());
        entity.setName(product.getName());
        entity.setType(product.getType().name());
        entity.setDetails(product.getDetails());
        entity.setWeight(product.getWeight());
        entity.setPurchasePrice(product.getPurchasePrice());
        entity.setSalePrice(product.getSalePrice());
        entity.setHeight(product.getDimensions().getHeight());
        entity.setWidth(product.getDimensions().getWidth());
        entity.setDepth(product.getDimensions().getDepth());
        entity.setDestinationVehicle(product.getDestinationVehicle());
        entity.setStockQuantity(product.getStockQuantity());
        if (product.getCreatedAt() != null) {
            entity.setCreatedAt(product.getCreatedAt());
        }
        return entity;
    }
}
