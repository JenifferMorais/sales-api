package com.sales.infrastructure.persistence.customer.service;

import com.sales.infrastructure.persistence.customer.repository.CustomerPanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CustomerCodeGenerator {

    @Inject
    CustomerPanacheRepository repository;

    public String generateNextCode() {
        String lastCode = repository.findLastCode().orElse(null);

        if (lastCode == null || lastCode.isBlank()) {

            return "CUST0001";
        }

        try {

            String numberPart = lastCode.substring(4);
            int number = Integer.parseInt(numberPart);
            int nextNumber = number + 1;

            return String.format("CUST%04d", nextNumber);
        } catch (Exception e) {

            long count = repository.countAll();
            return String.format("CUST%04d", count + 1);
        }
    }
}
