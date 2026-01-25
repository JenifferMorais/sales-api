package com.sales.domain.auth.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class Token {

    private final String value;
    private final LocalDateTime expiresAt;

    public Token(String value, LocalDateTime expiresAt) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token não pode estar vazio");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Data de expiração não pode ser nula");
        }
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public static Token create(String value, LocalDateTime expiresAt) {
        return new Token(value, expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isExpired();
    }

    @Override
    public String toString() {
        return value;
    }
}
