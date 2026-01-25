package com.sales.application.customer.usecase;

import com.sales.domain.customer.port.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Inject
    public DeleteCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void execute(Long id) {
        if (!customerRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Cliente não encontrado com id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
