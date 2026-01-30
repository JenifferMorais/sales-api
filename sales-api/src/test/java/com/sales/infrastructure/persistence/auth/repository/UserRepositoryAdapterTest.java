package com.sales.infrastructure.persistence.auth.repository;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import com.sales.infrastructure.persistence.auth.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter Tests")
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserRepositoryAdapter repositoryAdapter;

    private User testUser;
    private UserEntity testEntity;
    private Email testEmail;
    private Password testPassword;

    @BeforeEach
    void setUp() {
        testEmail = new Email("test@example.com");
        testPassword = Password.fromPlainText("Test@123");

        testUser = new User(
                "CUST001",
                testEmail,
                testPassword
        );

        testEntity = createUserEntity(1L, "CUST001", "test@example.com");
    }

    @Test
    @DisplayName("Should save new user")
    void shouldSaveNewUser() {
        doNothing().when(jpaRepository).persist(any(UserEntity.class));

        User result = repositoryAdapter.save(testUser);

        verify(jpaRepository).persist(any(UserEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should update existing user")
    void shouldUpdateExistingUser() {
        User existingUser = new User(
                1L,
                "CUST001",
                testEmail,
                testPassword,
                true,
                null,
                null,
                LocalDateTime.now()
        );

        when(jpaRepository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.merge(any(UserEntity.class))).thenReturn(testEntity);

        User result = repositoryAdapter.save(existingUser);

        verify(jpaRepository, never()).persist(any(UserEntity.class));
        verify(entityManager).merge(any(UserEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        when(jpaRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCustomerCode()).isEqualTo("CUST001");
        verify(jpaRepository).findByIdOptional(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void shouldReturnEmptyWhenUserNotFoundById() {
        when(jpaRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Optional<User> result = repositoryAdapter.findById(999L);

        assertThat(result).isEmpty();
        verify(jpaRepository).findByIdOptional(999L);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        when(jpaRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findByEmail(testEmail);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail().getValue()).isEqualTo("test@example.com");
        verify(jpaRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        Email notFoundEmail = new Email("notfound@example.com");
        when(jpaRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = repositoryAdapter.findByEmail(notFoundEmail);

        assertThat(result).isEmpty();
        verify(jpaRepository).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should find user by customer code")
    void shouldFindUserByCustomerCode() {
        when(jpaRepository.findByCustomerCode("CUST001")).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findByCustomerCode("CUST001");

        assertThat(result).isPresent();
        assertThat(result.get().getCustomerCode()).isEqualTo("CUST001");
        verify(jpaRepository).findByCustomerCode("CUST001");
    }

    @Test
    @DisplayName("Should return empty when user not found by customer code")
    void shouldReturnEmptyWhenUserNotFoundByCustomerCode() {
        when(jpaRepository.findByCustomerCode("CUST999")).thenReturn(Optional.empty());

        Optional<User> result = repositoryAdapter.findByCustomerCode("CUST999");

        assertThat(result).isEmpty();
        verify(jpaRepository).findByCustomerCode("CUST999");
    }

    @Test
    @DisplayName("Should find user by reset password token")
    void shouldFindUserByResetPasswordToken() {
        String token = "reset-token-123";
        testEntity.setResetPasswordToken(token);
        when(jpaRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findByResetPasswordToken(token);

        assertThat(result).isPresent();
        assertThat(result.get().getResetPasswordToken()).isEqualTo(token);
        verify(jpaRepository).findByResetPasswordToken(token);
    }

    @Test
    @DisplayName("Should return empty when user not found by reset password token")
    void shouldReturnEmptyWhenUserNotFoundByResetPasswordToken() {
        when(jpaRepository.findByResetPasswordToken("invalid-token")).thenReturn(Optional.empty());

        Optional<User> result = repositoryAdapter.findByResetPasswordToken("invalid-token");

        assertThat(result).isEmpty();
        verify(jpaRepository).findByResetPasswordToken("invalid-token");
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        when(jpaRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = repositoryAdapter.existsByEmail(testEmail);

        assertThat(result).isTrue();
        verify(jpaRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        Email notFoundEmail = new Email("notfound@example.com");
        when(jpaRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        boolean result = repositoryAdapter.existsByEmail(notFoundEmail);

        assertThat(result).isFalse();
        verify(jpaRepository).existsByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should check if user exists by customer code")
    void shouldCheckIfUserExistsByCustomerCode() {
        when(jpaRepository.existsByCustomerCode("CUST001")).thenReturn(true);

        boolean result = repositoryAdapter.existsByCustomerCode("CUST001");

        assertThat(result).isTrue();
        verify(jpaRepository).existsByCustomerCode("CUST001");
    }

    @Test
    @DisplayName("Should return false when user does not exist by customer code")
    void shouldReturnFalseWhenUserDoesNotExistByCustomerCode() {
        when(jpaRepository.existsByCustomerCode("CUST999")).thenReturn(false);

        boolean result = repositoryAdapter.existsByCustomerCode("CUST999");

        assertThat(result).isFalse();
        verify(jpaRepository).existsByCustomerCode("CUST999");
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUserById() {
        when(jpaRepository.deleteById(1L)).thenReturn(true);

        repositoryAdapter.delete(1L);

        verify(jpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should convert entity to domain correctly")
    void shouldConvertEntityToDomainCorrectly() {
        when(jpaRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        User user = result.get();
        assertThat(user.getId()).isEqualTo(testEntity.getId());
        assertThat(user.getCustomerCode()).isEqualTo(testEntity.getCustomerCode());
        assertThat(user.getEmail().getValue()).isEqualTo(testEntity.getEmail());
        assertThat(user.isActive()).isEqualTo(testEntity.getActive());
    }

    @Test
    @DisplayName("Should convert password from hash")
    void shouldConvertPasswordFromHash() {
        when(jpaRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPassword()).isNotNull();
        assertThat(result.get().getPassword().getHashedValue()).isNotNull();
    }

    @Test
    @DisplayName("Should preserve reset password token info")
    void shouldPreserveResetPasswordTokenInfo() {
        String token = "reset-token-123";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        testEntity.setResetPasswordToken(token);
        testEntity.setResetPasswordTokenExpiresAt(expiresAt);

        when(jpaRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getResetPasswordToken()).isEqualTo(token);
        assertThat(result.get().getResetPasswordTokenExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("Should handle inactive user")
    void shouldHandleInactiveUser() {
        testEntity.setActive(false);
        when(jpaRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<User> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().isActive()).isFalse();
    }

    // Helper method para criar UserEntity
    private UserEntity createUserEntity(Long id, String customerCode, String email) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setCustomerCode(customerCode);
        entity.setEmail(email);
        entity.setPassword("$2a$10$hashedpassword");
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
