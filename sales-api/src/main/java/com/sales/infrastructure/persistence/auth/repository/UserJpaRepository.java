package com.sales.infrastructure.persistence.auth.repository;

import com.sales.infrastructure.persistence.auth.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserJpaRepository implements PanacheRepository<UserEntity> {

    public Optional<UserEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<UserEntity> findByCustomerCode(String customerCode) {
        return find("customerCode", customerCode).firstResultOptional();
    }

    public Optional<UserEntity> findByResetPasswordToken(String token) {
        return find("resetPasswordToken", token).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    public boolean existsByCustomerCode(String customerCode) {
        return count("customerCode", customerCode) > 0;
    }
}
