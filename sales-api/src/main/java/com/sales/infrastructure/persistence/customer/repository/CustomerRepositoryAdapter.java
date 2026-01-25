package com.sales.infrastructure.persistence.customer.repository;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.domain.shared.PageResult;
import com.sales.infrastructure.persistence.customer.entity.CustomerEntity;
import com.sales.infrastructure.persistence.customer.service.CustomerCodeGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerRepositoryAdapter implements CustomerRepository {

    @Inject
    CustomerPanacheRepository panacheRepository;

    @Inject
    CustomerCodeGenerator codeGenerator;

    @Override
    @Transactional
    public Customer save(Customer customer) {
        CustomerEntity entity;

        if (customer.getId() != null) {
            entity = panacheRepository.findById(customer.getId());
            if (entity == null) {
                throw new IllegalArgumentException("Cliente não encontrado com id: " + customer.getId());
            }

            entity.setFullName(customer.getFullName());
            entity.setMotherName(customer.getMotherName());
            entity.setCpf(customer.getDocument().getCpf());
            entity.setRg(customer.getDocument().getRg());
            entity.setZipCode(customer.getAddress().getZipCode());
            entity.setStreet(customer.getAddress().getStreet());
            entity.setNumber(customer.getAddress().getNumber());
            entity.setComplement(customer.getAddress().getComplement());
            entity.setNeighborhood(customer.getAddress().getNeighborhood());
            entity.setCity(customer.getAddress().getCity());
            entity.setState(customer.getAddress().getState());
            entity.setBirthDate(customer.getBirthDate());
            entity.setCellPhone(customer.getCellPhone());
            entity.setEmail(customer.getEmail());
        } else {
            entity = toEntity(customer);

            if (entity.getCode() == null || entity.getCode().isBlank()) {
                String generatedCode = codeGenerator.generateNextCode();
                entity.setCode(generatedCode);
            }

            panacheRepository.persist(entity);
        }

        return toDomain(entity);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return panacheRepository.findByIdOptional(id).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByCode(String code) {
        return panacheRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByCpf(String cpf) {
        return panacheRepository.findByCpf(cpf).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return panacheRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return panacheRepository.listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByNameContaining(String name) {
        return panacheRepository.findByNameContaining(name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        panacheRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return panacheRepository.existsByCode(code);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return panacheRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existsByEmail(String email) {
        return panacheRepository.existsByEmail(email);
    }

    @Override
    public PageResult<Customer> search(String filter, int page, int size) {
        List<CustomerEntity> entities = panacheRepository.search(filter, page, size);
        long total = panacheRepository.countSearch(filter);
        List<Customer> customers = entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new PageResult<>(customers, total, page, size);
    }

    private Customer toDomain(CustomerEntity entity) {
        Document document = Document.fromDatabase(entity.getCpf(), entity.getRg());
        Address address = new Address(
                entity.getZipCode(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState()
        );
        return new Customer(
                entity.getId(),
                entity.getCode(),
                entity.getFullName(),
                entity.getMotherName(),
                document,
                address,
                entity.getBirthDate(),
                entity.getCellPhone(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }

    private CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setCode(customer.getCode());
        entity.setFullName(customer.getFullName());
        entity.setMotherName(customer.getMotherName());
        entity.setCpf(customer.getDocument().getCpf());
        entity.setRg(customer.getDocument().getRg());
        entity.setZipCode(customer.getAddress().getZipCode());
        entity.setStreet(customer.getAddress().getStreet());
        entity.setNumber(customer.getAddress().getNumber());
        entity.setComplement(customer.getAddress().getComplement());
        entity.setNeighborhood(customer.getAddress().getNeighborhood());
        entity.setCity(customer.getAddress().getCity());
        entity.setState(customer.getAddress().getState());
        entity.setBirthDate(customer.getBirthDate());
        entity.setCellPhone(customer.getCellPhone());
        entity.setEmail(customer.getEmail());
        if (customer.getCreatedAt() != null) {
            entity.setCreatedAt(customer.getCreatedAt());
        }
        return entity;
    }
}
