package com.sales.domain.auth.port;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.valueobject.Email;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByCustomerCode(String customerCode);

    Optional<User> findByResetPasswordToken(String token);

    boolean existsByEmail(Email email);

    boolean existsByCustomerCode(String customerCode);

    void delete(Long id);
}
