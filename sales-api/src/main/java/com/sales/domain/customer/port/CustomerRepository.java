package com.sales.domain.customer.port;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.shared.PageResult;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<Customer> findByCode(String code);
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findByEmail(String email);
    List<Customer> findAll();
    List<Customer> findByNameContaining(String name);
    PageResult<Customer> search(String filter, int page, int size);
    void deleteById(Long id);
    boolean existsByCode(String code);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
