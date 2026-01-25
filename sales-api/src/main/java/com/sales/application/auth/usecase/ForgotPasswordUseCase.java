package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.EmailService;
import com.sales.domain.auth.port.TokenService;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class ForgotPasswordUseCase {

    @Inject
    UserRepository userRepository;

    @Inject
    TokenService tokenService;

    @Inject
    EmailService emailService;

    @Transactional
    public void execute(String emailValue) {
        Email email = new Email(emailValue);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nenhum usuário encontrado com o email: " + emailValue
                ));

        String resetToken = tokenService.generateResetPasswordToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        user.generateResetPasswordToken(resetToken, expiresAt);
        userRepository.save(user);

        emailService.sendResetPasswordEmail(email, resetToken);
    }
}
