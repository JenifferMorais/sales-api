package com.sales.domain.auth.entity;

import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import com.sales.domain.auth.valueobject.Token;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateValidUser() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);

        assertThat(user.getCustomerCode()).isEqualTo("CUST001");
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.isActive()).isTrue();
        assertThat(user.getResetPasswordToken()).isNull();
        assertThat(user.getResetPasswordTokenExpiresAt()).isNull();
    }

    @Test
    void shouldFailWhenCustomerCodeIsEmpty() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        assertThatThrownBy(() -> new User("", email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do cliente não pode estar vazio");
    }

    @Test
    void shouldFailWhenCustomerCodeIsNull() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        assertThatThrownBy(() -> new User(null, email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código do cliente não pode estar vazio");
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        Password password = Password.fromPlainText("SecurePass123!");

        assertThatThrownBy(() -> new User("CUST001", null, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email não pode ser nulo");
    }

    @Test
    void shouldFailWhenPasswordIsNull() {
        Email email = new Email("joao@example.com");

        assertThatThrownBy(() -> new User("CUST001", email, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha não pode ser nula");
    }

    @Test
    void shouldAuthenticateWithCorrectPassword() {
        Email email = new Email("joao@example.com");
        String plainPassword = "SecurePass123!";
        Password password = Password.fromPlainText(plainPassword);

        User user = new User("CUST001", email, password);

        assertThat(user.authenticate(plainPassword)).isTrue();
    }

    @Test
    void shouldFailAuthenticationWithIncorrectPassword() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);

        assertThat(user.authenticate("WrongPassword")).isFalse();
    }

    @Test
    void shouldFailAuthenticationWhenUserIsInactive() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        user.deactivate();

        assertThatThrownBy(() -> user.authenticate("SecurePass123!"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Usuário está inativo");
    }

    @Test
    void shouldChangePassword() {
        Email email = new Email("joao@example.com");
        Password oldPassword = Password.fromPlainText("OldPass123!");
        Password newPassword = Password.fromPlainText("NewPass123!");

        User user = new User("CUST001", email, oldPassword);
        user.changePassword(newPassword);

        assertThat(user.authenticate("NewPass123!")).isTrue();
        assertThat(user.authenticate("OldPass123!")).isFalse();
    }

    @Test
    void shouldClearResetTokenWhenChangingPassword() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        user.generateResetPasswordToken("reset-token-123", LocalDateTime.now().plusHours(1));

        assertThat(user.getResetPasswordToken()).isEqualTo("reset-token-123");

        Password newPassword = Password.fromPlainText("NewPass123!");
        user.changePassword(newPassword);

        assertThat(user.getResetPasswordToken()).isNull();
        assertThat(user.getResetPasswordTokenExpiresAt()).isNull();
    }

    @Test
    void shouldGenerateResetPasswordToken() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        user.generateResetPasswordToken("reset-token-123", expiresAt);

        assertThat(user.getResetPasswordToken()).isEqualTo("reset-token-123");
        assertThat(user.getResetPasswordTokenExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldFailGenerateResetTokenWithEmptyToken() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);

        assertThatThrownBy(() -> user.generateResetPasswordToken("", LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Token não pode estar vazio");
    }

    @Test
    void shouldFailGenerateResetTokenWithNullExpiration() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);

        assertThatThrownBy(() -> user.generateResetPasswordToken("reset-token", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data de expiração não pode ser nula");
    }

    @Test
    void shouldSetResetPasswordTokenFromTokenObject() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        Token token = new Token("token-value", expiresAt);

        user.setResetPasswordToken(token);

        assertThat(user.getResetPasswordToken()).isEqualTo("token-value");
        assertThat(user.getResetPasswordTokenExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldClearResetTokenWhenSetToNull() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        user.generateResetPasswordToken("token", LocalDateTime.now().plusHours(1));

        user.setResetPasswordToken((Token) null);

        assertThat(user.getResetPasswordToken()).isNull();
        assertThat(user.getResetPasswordTokenExpiresAt()).isNull();
    }

    @Test
    void shouldValidateResetToken() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        String token = "reset-token-123";
        user.generateResetPasswordToken(token, LocalDateTime.now().plusHours(1));

        assertThat(user.isResetTokenValid(token)).isTrue();
    }

    @Test
    void shouldInvalidateExpiredResetToken() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        String token = "reset-token-123";
        user.generateResetPasswordToken(token, LocalDateTime.now().minusHours(1)); // Expired

        assertThat(user.isResetTokenValid(token)).isFalse();
    }

    @Test
    void shouldInvalidateWrongResetToken() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        user.generateResetPasswordToken("correct-token", LocalDateTime.now().plusHours(1));

        assertThat(user.isResetTokenValid("wrong-token")).isFalse();
    }

    @Test
    void shouldReturnFalseWhenNoResetTokenSet() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);

        assertThat(user.isResetTokenValid("any-token")).isFalse();
    }

    @Test
    void shouldActivateUser() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);
        user.deactivate();

        assertThat(user.isActive()).isFalse();

        user.activate();

        assertThat(user.isActive()).isTrue();
    }

    @Test
    void shouldDeactivateUser() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");

        User user = new User("CUST001", email, password);

        assertThat(user.isActive()).isTrue();

        user.deactivate();

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void shouldCreateUserWithAllParameters() {
        Email email = new Email("joao@example.com");
        Password password = Password.fromPlainText("SecurePass123!");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        User user = new User(1L, "CUST001", email, password, true,
                "reset-token", expiresAt, createdAt);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getCustomerCode()).isEqualTo("CUST001");
        assertThat(user.isActive()).isTrue();
        assertThat(user.getResetPasswordToken()).isEqualTo("reset-token");
        assertThat(user.getResetPasswordTokenExpiresAt()).isEqualTo(expiresAt);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }
}
