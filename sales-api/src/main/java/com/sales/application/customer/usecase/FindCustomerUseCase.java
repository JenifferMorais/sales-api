package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FindCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Inject
    public FindCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com id: " + id));
    }

    public Customer findByCode(String code) {
        return customerRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com código: " + code));
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findByNameContaining(String name) {
        return customerRepository.findByNameContaining(name);
    }
}
