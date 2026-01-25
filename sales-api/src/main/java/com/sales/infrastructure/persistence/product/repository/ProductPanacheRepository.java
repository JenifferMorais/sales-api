package com.sales.infrastructure.persistence.product.repository;

import com.sales.infrastructure.persistence.product.entity.ProductEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductPanacheRepository implements PanacheRepository<ProductEntity> {

    public Optional<ProductEntity> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public List<ProductEntity> findAllSortedByName() {
        return listAll(Sort.by("name").ascending());
    }

    public List<ProductEntity> findByType(String type) {
        return find("type", type).list();
    }

    public List<ProductEntity> findByNameContaining(String name) {
        return find("LOWER(name) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    public Optional<String> findLastCode() {
        return find("ORDER BY code DESC")
                .firstResultOptional()
                .map(ProductEntity::getCode);
    }

    public long countAll() {
        return count();
    }

    public List<ProductEntity> findOldestProducts(int limit) {
        return find("ORDER BY createdAt ASC, purchasePrice DESC")
                .page(0, limit)
                .list();
    }

    public List<ProductEntity> search(String filter, int page, int size) {
        if (filter == null || filter.isBlank()) {
            return find("ORDER BY name")
                    .page(Page.of(page, size))
                    .list();
        }

        String searchPattern = "%" + filter.toLowerCase() + "%";
        return find(
                "LOWER(name) LIKE ?1 OR LOWER(code) LIKE ?1 OR LOWER(details) LIKE ?1 OR LOWER(type) LIKE ?1",
                Sort.by("name"),
                searchPattern
        ).page(Page.of(page, size)).list();
    }

    public long countSearch(String filter) {
        if (filter == null || filter.isBlank()) {
            return count();
        }

        String searchPattern = "%" + filter.toLowerCase() + "%";
        return count(
                "LOWER(name) LIKE ?1 OR LOWER(code) LIKE ?1 OR LOWER(details) LIKE ?1 OR LOWER(type) LIKE ?1",
                searchPattern
        );
    }
}
