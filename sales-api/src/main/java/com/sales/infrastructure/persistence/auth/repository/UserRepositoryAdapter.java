package com.sales.infrastructure.persistence.auth.repository;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import com.sales.infrastructure.persistence.auth.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class UserRepositoryAdapter implements UserRepository {

    @Inject
    UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);

        if (entity.getId() == null) {
            jpaRepository.persist(entity);
        } else {
            entity = jpaRepository.getEntityManager().merge(entity);
        }

        return toDomain(entity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findByIdOptional(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByCustomerCode(String customerCode) {
        return jpaRepository.findByCustomerCode(customerCode)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByResetPasswordToken(String token) {
        return jpaRepository.findByResetPasswordToken(token)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByCustomerCode(String customerCode) {
        return jpaRepository.existsByCustomerCode(customerCode);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setCustomerCode(user.getCustomerCode());
        entity.setEmail(user.getEmail().getValue());
        entity.setPassword(user.getPassword().getHashedValue());
        entity.setActive(user.isActive());
        entity.setResetPasswordToken(user.getResetPasswordToken());
        entity.setResetPasswordTokenExpiresAt(user.getResetPasswordTokenExpiresAt());
        entity.setCreatedAt(user.getCreatedAt());

        return entity;
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getCustomerCode(),
                new Email(entity.getEmail()),
                Password.fromHash(entity.getPassword()),
                entity.getActive(),
                entity.getResetPasswordToken(),
                entity.getResetPasswordTokenExpiresAt(),
                entity.getCreatedAt()
        );
    }
}
