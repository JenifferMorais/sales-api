package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Inject
    public CreateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(Customer customer) {
        validateCustomer(customer);
        return customerRepository.save(customer);
    }

    private void validateCustomer(Customer customer) {

        if (customer.getCode() != null && !customer.getCode().isBlank()) {
            if (customerRepository.existsByCode(customer.getCode())) {
                throw new IllegalArgumentException("Cliente com código " + customer.getCode() + " já existe");
            }
        }
        if (customerRepository.existsByCpf(customer.getDocument().getCpf())) {
            throw new IllegalArgumentException("Cliente com CPF " + customer.getDocument().getFormattedCPF() + " já existe");
        }
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Cliente com email " + customer.getEmail() + " já existe");
        }
    }
}
