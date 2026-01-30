package com.sales.infrastructure.persistence.auth.repository;

import com.sales.infrastructure.persistence.auth.entity.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("UserJpaRepository Tests")
class UserJpaRepositoryTest {

    @Inject
    UserJpaRepository repository;

    private UserEntity testUser;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar base de dados
        repository.deleteAll();

        // Criar usuário de teste
        testUser = new UserEntity();
        testUser.setCustomerCode("CUST0001");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$hashedpassword");
        testUser.setActive(true);

        repository.persist(testUser);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        Optional<UserEntity> result = repository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getCustomerCode()).isEqualTo("CUST0001");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        Optional<UserEntity> result = repository.findByEmail("notfound@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find user by customer code")
    void shouldFindUserByCustomerCode() {
        Optional<UserEntity> result = repository.findByCustomerCode("CUST0001");

        assertThat(result).isPresent();
        assertThat(result.get().getCustomerCode()).isEqualTo("CUST0001");
    }

    @Test
    @DisplayName("Should return empty when user not found by customer code")
    void shouldReturnEmptyWhenUserNotFoundByCustomerCode() {
        Optional<UserEntity> result = repository.findByCustomerCode("CUST9999");

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should find user by reset password token")
    void shouldFindUserByResetPasswordToken() {
        String resetToken = "reset-token-123";
        UserEntity userWithToken = new UserEntity();
        userWithToken.setCustomerCode("CUST0002");
        userWithToken.setEmail("token@example.com");
        userWithToken.setPassword("$2a$10$hashedpassword");
        userWithToken.setActive(true);
        userWithToken.setResetPasswordToken(resetToken);
        userWithToken.setResetPasswordTokenExpiresAt(LocalDateTime.now().plusHours(1));
        repository.persist(userWithToken);

        Optional<UserEntity> result = repository.findByResetPasswordToken(resetToken);

        assertThat(result).isPresent();
        assertThat(result.get().getResetPasswordToken()).isEqualTo(resetToken);
    }

    @Test
    @DisplayName("Should return empty when user not found by reset password token")
    void shouldReturnEmptyWhenUserNotFoundByResetPasswordToken() {
        Optional<UserEntity> result = repository.findByResetPasswordToken("invalid-token");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        boolean exists = repository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        boolean exists = repository.existsByEmail("notfound@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should check if user exists by customer code")
    void shouldCheckIfUserExistsByCustomerCode() {
        boolean exists = repository.existsByCustomerCode("CUST0001");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when user does not exist by customer code")
    void shouldReturnFalseWhenUserDoesNotExistByCustomerCode() {
        boolean exists = repository.existsByCustomerCode("CUST9999");

        assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("Should find user with active status")
    void shouldFindUserWithActiveStatus() {
        Optional<UserEntity> result = repository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("Should find user with inactive status")
    void shouldFindUserWithInactiveStatus() {
        UserEntity inactiveUser = new UserEntity();
        inactiveUser.setCustomerCode("CUST0003");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPassword("$2a$10$hashedpassword");
        inactiveUser.setActive(false);
        repository.persist(inactiveUser);

        Optional<UserEntity> result = repository.findByEmail("inactive@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getActive()).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("Should handle multiple users")
    void shouldHandleMultipleUsers() {
        UserEntity user2 = new UserEntity();
        user2.setCustomerCode("CUST0002");
        user2.setEmail("user2@example.com");
        user2.setPassword("$2a$10$hashedpassword2");
        user2.setActive(true);
        repository.persist(user2);

        assertThat(repository.existsByEmail("test@example.com")).isTrue();
        assertThat(repository.existsByEmail("user2@example.com")).isTrue();
        assertThat(repository.existsByCustomerCode("CUST0001")).isTrue();
        assertThat(repository.existsByCustomerCode("CUST0002")).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("Should find correct user among multiple users")
    void shouldFindCorrectUserAmongMultipleUsers() {
        UserEntity user2 = new UserEntity();
        user2.setCustomerCode("CUST0002");
        user2.setEmail("user2@example.com");
        user2.setPassword("$2a$10$hashedpassword2");
        user2.setActive(true);
        repository.persist(user2);

        Optional<UserEntity> result1 = repository.findByEmail("test@example.com");
        Optional<UserEntity> result2 = repository.findByEmail("user2@example.com");

        assertThat(result1).isPresent();
        assertThat(result2).isPresent();
        assertThat(result1.get().getCustomerCode()).isEqualTo("CUST0001");
        assertThat(result2.get().getCustomerCode()).isEqualTo("CUST0002");
    }

    @Test
    @Transactional
    @DisplayName("Should handle user with null reset token")
    void shouldHandleUserWithNullResetToken() {
        Optional<UserEntity> result = repository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getResetPasswordToken()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("Should handle user with expired reset token")
    void shouldHandleUserWithExpiredResetToken() {
        UserEntity userWithExpiredToken = new UserEntity();
        userWithExpiredToken.setCustomerCode("CUST0004");
        userWithExpiredToken.setEmail("expired@example.com");
        userWithExpiredToken.setPassword("$2a$10$hashedpassword");
        userWithExpiredToken.setActive(true);
        userWithExpiredToken.setResetPasswordToken("expired-token");
        userWithExpiredToken.setResetPasswordTokenExpiresAt(LocalDateTime.now().minusHours(1));
        repository.persist(userWithExpiredToken);

        Optional<UserEntity> result = repository.findByResetPasswordToken("expired-token");

        assertThat(result).isPresent();
        assertThat(result.get().getResetPasswordTokenExpiresAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should handle case sensitive email search")
    void shouldHandleCaseSensitiveEmailSearch() {
        Optional<UserEntity> result = repository.findByEmail("TEST@EXAMPLE.COM");

        // Email deve ser case-sensitive no banco
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should delete user by id")
    void shouldDeleteUserById() {
        Long userId = testUser.getId();
        repository.deleteById(userId);

        Optional<UserEntity> result = repository.findByIdOptional(userId);
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should count all users")
    void shouldCountAllUsers() {
        long count = repository.count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Should persist and find new user")
    void shouldPersistAndFindNewUser() {
        UserEntity newUser = new UserEntity();
        newUser.setCustomerCode("CUST0003");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("$2a$10$newhashedpassword");
        newUser.setActive(true);

        repository.persist(newUser);

        Optional<UserEntity> result = repository.findByEmail("newuser@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerCode()).isEqualTo("CUST0003");
    }
}
