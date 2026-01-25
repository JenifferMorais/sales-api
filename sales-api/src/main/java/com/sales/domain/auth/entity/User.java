package com.sales.domain.auth.entity;

import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import com.sales.domain.shared.Entity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User extends Entity {

    private final String customerCode;
    private final Email email;
    private Password password;
    private boolean active;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpiresAt;

    public User(Long id, String customerCode, Email email, Password password,
                boolean active, String resetPasswordToken,
                LocalDateTime resetPasswordTokenExpiresAt,
                LocalDateTime createdAt) {
        super(id, customerCode, createdAt);
        validateCustomerCode(customerCode);
        validateEmail(email);
        validatePassword(password);

        this.customerCode = customerCode;
        this.email = email;
        this.password = password;
        this.active = active;
        this.resetPasswordToken = resetPasswordToken;
        this.resetPasswordTokenExpiresAt = resetPasswordTokenExpiresAt;
    }

    public User(String customerCode, Email email, Password password) {
        this(null, customerCode, email, password, true, null, null, null);
    }

    private void validateCustomerCode(String customerCode) {
        if (customerCode == null || customerCode.isBlank()) {
            throw new IllegalArgumentException("Código do cliente não pode estar vazio");
        }
    }

    private void validateEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email não pode ser nulo");
        }
    }

    private void validatePassword(Password password) {
        if (password == null) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }
    }

    public boolean authenticate(String plainPassword) {
        if (!active) {
            throw new IllegalStateException("Usuário está inativo");
        }
        return password.matches(plainPassword);
    }

    public void changePassword(Password newPassword) {
        validatePassword(newPassword);
        this.password = newPassword;
        this.resetPasswordToken = null;
        this.resetPasswordTokenExpiresAt = null;
    }

    public void generateResetPasswordToken(String token, LocalDateTime expiresAt) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token não pode estar vazio");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Data de expiração não pode ser nula");
        }
        this.resetPasswordToken = token;
        this.resetPasswordTokenExpiresAt = expiresAt;
    }

    public void setResetPasswordToken(com.sales.domain.auth.valueobject.Token token) {
        if (token == null) {
            this.resetPasswordToken = null;
            this.resetPasswordTokenExpiresAt = null;
        } else {
            this.resetPasswordToken = token.getValue();
            this.resetPasswordTokenExpiresAt = token.getExpiresAt();
        }
    }

    public void setResetPasswordToken(String token) {
        this.resetPasswordToken = token;
    }

    public boolean isResetTokenValid(String token) {
        if (resetPasswordToken == null || resetPasswordTokenExpiresAt == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(resetPasswordTokenExpiresAt)) {
            return false;
        }
        return resetPasswordToken.equals(token);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
