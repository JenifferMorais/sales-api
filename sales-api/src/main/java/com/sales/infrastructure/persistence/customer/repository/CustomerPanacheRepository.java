package com.sales.infrastructure.persistence.customer.repository;

import com.sales.infrastructure.persistence.customer.entity.CustomerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerPanacheRepository implements PanacheRepository<CustomerEntity> {

    public Optional<CustomerEntity> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public Optional<CustomerEntity> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public Optional<CustomerEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<CustomerEntity> findByNameContaining(String name) {
        return find("LOWER(fullName) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    public Optional<String> findLastCode() {
        return find("ORDER BY code DESC")
                .firstResultOptional()
                .map(CustomerEntity::getCode);
    }

    public long countAll() {
        return count();
    }

    public List<CustomerEntity> findByRegistrationYear(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        return find("createdAt >= ?1 and createdAt <= ?2",
                    Sort.by("code").ascending(),
                    startOfYear, endOfYear)
                .list();
    }

    public List<CustomerEntity> search(String filter, int page, int size) {
        if (filter == null || filter.isBlank()) {
            return find("ORDER BY fullName")
                    .page(Page.of(page, size))
                    .list();
        }

        String searchPattern = "%" + filter.toLowerCase() + "%";
        return find(
                "LOWER(fullName) LIKE ?1 OR LOWER(email) LIKE ?1 OR LOWER(code) LIKE ?1 OR LOWER(cpf) LIKE ?1 OR LOWER(cellPhone) LIKE ?1",
                Sort.by("fullName"),
                searchPattern
        ).page(Page.of(page, size)).list();
    }

    public long countSearch(String filter) {
        if (filter == null || filter.isBlank()) {
            return count();
        }

        String searchPattern = "%" + filter.toLowerCase() + "%";
        return count(
                "LOWER(fullName) LIKE ?1 OR LOWER(email) LIKE ?1 OR LOWER(code) LIKE ?1 OR LOWER(cpf) LIKE ?1 OR LOWER(cellPhone) LIKE ?1",
                searchPattern
        );
    }
}
