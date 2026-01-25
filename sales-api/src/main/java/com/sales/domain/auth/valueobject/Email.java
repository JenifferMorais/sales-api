package com.sales.domain.auth.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
    );

    private final String value;

    public Email(String value) {
        validate(value);
        this.value = value.toLowerCase();
    }

    public static Email create(String value) {
        return new Email(value);
    }

    private void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode estar vazio");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
