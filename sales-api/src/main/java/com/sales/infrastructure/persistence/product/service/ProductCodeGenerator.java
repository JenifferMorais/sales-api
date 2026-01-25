package com.sales.infrastructure.persistence.product.service;

import com.sales.infrastructure.persistence.product.repository.ProductPanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductCodeGenerator {

    @Inject
    ProductPanacheRepository repository;

    public String generateNextCode() {
        String lastCode = repository.findLastCode().orElse(null);

        if (lastCode == null || lastCode.isBlank()) {

            return "PROD0001";
        }

        try {

            String numberPart = lastCode.substring(4);
            int number = Integer.parseInt(numberPart);
            int nextNumber = number + 1;

            return String.format("PROD%04d", nextNumber);
        } catch (Exception e) {

            long count = repository.countAll();
            return String.format("PROD%04d", count + 1);
        }
    }
}
