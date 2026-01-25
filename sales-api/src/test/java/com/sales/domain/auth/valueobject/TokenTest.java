package com.sales.domain.auth.valueobject;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class TokenTest {

    @Test
    void shouldCreateValidToken() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        Token token = new Token("token-value", expiresAt);

        assertThat(token.getValue()).isEqualTo("token-value");
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldCreateTokenWithCreateMethod() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        Token token = Token.create("token-value", expiresAt);

        assertThat(token.getValue()).isEqualTo("token-value");
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldFailWhenTokenValueIsNull() {
        assertThatThrownBy(() -> new Token(null, LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Token não pode estar vazio");
    }

    @Test
    void shouldFailWhenTokenValueIsEmpty() {
        assertThatThrownBy(() -> new Token("", LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Token não pode estar vazio");
    }

    @Test
    void shouldFailWhenTokenValueIsBlank() {
        assertThatThrownBy(() -> new Token("   ", LocalDateTime.now().plusHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Token não pode estar vazio");
    }

    @Test
    void shouldFailWhenExpiresAtIsNull() {
        assertThatThrownBy(() -> new Token("token-value", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data de expiração não pode ser nula");
    }

    @Test
    void shouldBeValidWhenNotExpired() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        Token token = new Token("token-value", expiresAt);

        assertThat(token.isValid()).isTrue();
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void shouldBeExpiredWhenPastExpirationDate() {
        LocalDateTime expiresAt = LocalDateTime.now().minusHours(1);
        Token token = new Token("token-value", expiresAt);

        assertThat(token.isExpired()).isTrue();
        assertThat(token.isValid()).isFalse();
    }

    @Test
    void shouldBeEqualWhenSameValueAndExpiration() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        Token token1 = new Token("token-value", expiresAt);
        Token token2 = new Token("token-value", expiresAt);

        assertThat(token1).isEqualTo(token2);
        assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        Token token1 = new Token("token1", expiresAt);
        Token token2 = new Token("token2", expiresAt);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldReturnValueInToString() {
        Token token = new Token("token-value", LocalDateTime.now().plusHours(1));

        assertThat(token.toString()).isEqualTo("token-value");
    }
}
