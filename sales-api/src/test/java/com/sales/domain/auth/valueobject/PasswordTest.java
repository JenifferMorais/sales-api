package com.sales.domain.auth.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordTest {

    @Test
    void shouldCreatePasswordFromPlainText() {
        Password password = Password.fromPlainText("SecurePass123!");

        assertThat(password.getHashedValue()).isNotNull();
        assertThat(password.getHashedValue()).isNotEqualTo("SecurePass123!");
    }

    @Test
    void shouldCreatePasswordFromHash() {
        String hash = "$2a$12$abcdefghijklmnopqrstuvwxyz123456789012345678901234";
        Password password = Password.fromHash(hash);

        assertThat(password.getHashedValue()).isEqualTo(hash);
    }

    @Test
    void shouldFailFromHashWhenNull() {
        assertThatThrownBy(() -> Password.fromHash(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hash da senha não pode estar vazio");
    }

    @Test
    void shouldFailFromHashWhenEmpty() {
        assertThatThrownBy(() -> Password.fromHash(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hash da senha não pode estar vazio");
    }

    @Test
    void shouldMatchCorrectPassword() {
        String plainPassword = "SecurePass123!";
        Password password = Password.fromPlainText(plainPassword);

        assertThat(password.matches(plainPassword)).isTrue();
    }

    @Test
    void shouldNotMatchIncorrectPassword() {
        Password password = Password.fromPlainText("SecurePass123!");

        assertThat(password.matches("WrongPassword123!")).isFalse();
    }

    @Test
    void shouldFailWhenPasswordIsNull() {
        assertThatThrownBy(() -> Password.fromPlainText(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha não pode estar vazia");
    }

    @Test
    void shouldFailWhenPasswordIsEmpty() {
        assertThatThrownBy(() -> Password.fromPlainText(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha não pode estar vazia");
    }

    @Test
    void shouldFailWhenPasswordIsTooShort() {
        assertThatThrownBy(() -> Password.fromPlainText("Pass1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha deve ter no mínimo 8 caracteres");
    }

    @Test
    void shouldFailWhenPasswordMissingUpperCase() {
        assertThatThrownBy(() -> Password.fromPlainText("password123!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha deve conter pelo menos uma letra maiúscula");
    }

    @Test
    void shouldFailWhenPasswordMissingLowerCase() {
        assertThatThrownBy(() -> Password.fromPlainText("PASSWORD123!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha deve conter pelo menos uma letra minúscula");
    }

    @Test
    void shouldFailWhenPasswordMissingNumber() {
        assertThatThrownBy(() -> Password.fromPlainText("SecurePass!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha deve conter pelo menos um número");
    }

    @Test
    void shouldFailWhenPasswordMissingSpecialChar() {
        assertThatThrownBy(() -> Password.fromPlainText("SecurePass123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Senha deve conter pelo menos um caractere especial");
    }

    @Test
    void shouldAcceptValidPasswordWithAllRequirements() {
        Password password = Password.fromPlainText("ValidPass123!");

        assertThat(password.getHashedValue()).isNotNull();
    }

    @Test
    void shouldHashDifferentlyForSamePassword() {
        Password password1 = Password.fromPlainText("SecurePass123!");
        Password password2 = Password.fromPlainText("SecurePass123!");

        // Different hashes due to salt
        assertThat(password1.getHashedValue()).isNotEqualTo(password2.getHashedValue());
    }

    @Test
    void shouldReturnMaskedStringInToString() {
        Password password = Password.fromPlainText("SecurePass123!");

        assertThat(password.toString()).isEqualTo("********");
    }

    @Test
    void shouldBeEqualWhenSameHash() {
        String hash = "$2a$12$abcdefghijklmnopqrstuvwxyz123456789012345678901234";
        Password password1 = Password.fromHash(hash);
        Password password2 = Password.fromHash(hash);

        assertThat(password1).isEqualTo(password2);
        assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
    }
}
