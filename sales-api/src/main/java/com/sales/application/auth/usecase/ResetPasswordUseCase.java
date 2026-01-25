package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Password;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ResetPasswordUseCase {

    @Inject
    UserRepository userRepository;

    @Transactional
    public void execute(String resetToken, String newPlainPassword) {

        User user = userRepository.findByResetPasswordToken(resetToken)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado"));

        if (!user.isResetTokenValid(resetToken)) {
            throw new IllegalArgumentException("Token inválido ou expirado");
        }

        Password newPassword = Password.fromPlainText(newPlainPassword);

        user.changePassword(newPassword);

        userRepository.save(user);
    }
}
