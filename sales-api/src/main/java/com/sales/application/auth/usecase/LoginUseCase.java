package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.TokenService;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LoginUseCase {

    private static final Logger LOG = Logger.getLogger(LoginUseCase.class);

    @Inject
    UserRepository userRepository;

    @Inject
    TokenService tokenService;

    public String execute(String emailValue, String plainPassword) {
        LOG.infof("Tentativa de login - Email: %s", emailValue);

        Email email = new Email(emailValue);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de login com email não cadastrado: %s", emailValue);
                    return new IllegalArgumentException("Credenciais inválidas");
                });

        if (!user.authenticate(plainPassword)) {
            LOG.warnf("Tentativa de login com senha incorreta - Email: %s", emailValue);
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        String token = tokenService.generateAccessToken(user);

        LOG.infof("Login realizado com sucesso - Email: %s, Cliente: %s",
                  emailValue, user.getCustomerCode());

        return token;
    }
}
