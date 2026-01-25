package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.shared.PageResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SearchCustomersUseCase {

    private final CustomerRepository customerRepository;

    @Inject
    public SearchCustomersUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public PageResult<Customer> execute(String filter, int page, int size) {
        return customerRepository.search(filter, page, size);
    }
}
