package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.TokenService;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoginUseCase {

    @Inject
    UserRepository userRepository;

    @Inject
    TokenService tokenService;

    public String execute(String emailValue, String plainPassword) {

        Email email = new Email(emailValue);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (!user.authenticate(plainPassword)) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        return tokenService.generateAccessToken(user);
    }
}
