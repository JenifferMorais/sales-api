package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;

@ApplicationScoped
public class UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Inject
    public UpdateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(Long id, String fullName, String motherName, Address address,
                           LocalDate birthDate, String cellPhone, String email) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com id: " + id));

        if (!customer.getEmail().equals(email) && customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        customer.updateInfo(fullName, motherName, address, birthDate, cellPhone, email);
        return customerRepository.save(customer);
    }
}
