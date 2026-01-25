package com.sales.domain.product.port;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.valueobject.ProductType;
import com.sales.domain.shared.PageResult;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    Optional<Product> findByCode(String code);
    List<Product> findAll();
    List<Product> findAllSortedByName();
    List<Product> findByType(ProductType type);
    List<Product> findByNameContaining(String name);
    PageResult<Product> search(String filter, int page, int size);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
