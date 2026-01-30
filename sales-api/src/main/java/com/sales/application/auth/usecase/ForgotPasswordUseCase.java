package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.EmailService;
import com.sales.domain.auth.port.TokenService;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class ForgotPasswordUseCase {

    private static final Logger LOG = Logger.getLogger(ForgotPasswordUseCase.class);

    @Inject
    UserRepository userRepository;

    @Inject
    TokenService tokenService;

    @Inject
    EmailService emailService;

    @Transactional
    public void execute(String emailValue) {
        LOG.infof("Solicitação de redefinição de senha para: %s", emailValue);

        Email email = new Email(emailValue);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de redefinir senha para email não cadastrado: %s", emailValue);
                    return new IllegalArgumentException(
                            "Nenhum usuário encontrado com o email: " + emailValue
                    );
                });

        LOG.debugf("Usuário encontrado - Cliente: %s", user.getCustomerCode());

        String resetToken = tokenService.generateResetPasswordToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        user.generateResetPasswordToken(resetToken, expiresAt);
        userRepository.save(user);

        LOG.infof("Token de redefinição gerado - Email: %s, Expira em: %s",
                  emailValue, expiresAt);

        try {
            emailService.sendResetPasswordEmail(email, resetToken);
            LOG.infof("Email de redefinição de senha enviado para: %s", emailValue);
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao enviar email de redefinição de senha para: %s", emailValue);
            throw e;
        }
    }
}
