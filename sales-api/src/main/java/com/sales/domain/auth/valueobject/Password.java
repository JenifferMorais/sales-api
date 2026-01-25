package com.sales.domain.auth.valueobject;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Password {

    private static final int MIN_LENGTH = 8;
    private static final BCrypt.Hasher hasher = BCrypt.withDefaults();
    private static final BCrypt.Verifyer verifyer = BCrypt.verifyer();

    private final String hashedValue;

    private Password(String hashedValue, boolean alreadyHashed) {
        if (alreadyHashed) {
            this.hashedValue = hashedValue;
        } else {
            validate(hashedValue);
            this.hashedValue = hash(hashedValue);
        }
    }

    public static Password fromPlainText(String plainPassword) {
        return new Password(plainPassword, false);
    }

    public static Password fromHash(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("Hash da senha não pode estar vazio");
        }
        return new Password(hashedPassword, true);
    }

    private void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Senha não pode estar vazia");
        }

        if (password.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Senha deve ter no mínimo %d caracteres", MIN_LENGTH)
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos uma letra maiúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos uma letra minúscula");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos um número");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos um caractere especial");
        }
    }

    private String hash(String plainPassword) {
        return hasher.hashToString(12, plainPassword.toCharArray());
    }

    public boolean matches(String plainPassword) {
        BCrypt.Result result = verifyer.verify(plainPassword.toCharArray(), hashedValue);
        return result.verified;
    }

    @Override
    public String toString() {
        return "********";
    }
}
