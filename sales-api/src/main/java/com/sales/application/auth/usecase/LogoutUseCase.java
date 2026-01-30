package com.sales.application.auth.usecase;

import com.sales.infrastructure.security.TokenBlacklistService;
import com.sales.infrastructure.security.UserActivityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LogoutUseCase {

    private static final Logger LOG = Logger.getLogger(LogoutUseCase.class);

    private final TokenBlacklistService tokenBlacklistService;
    private final UserActivityService userActivityService;

    @Inject
    public LogoutUseCase(TokenBlacklistService tokenBlacklistService,
                         UserActivityService userActivityService) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.userActivityService = userActivityService;
    }

    public void execute(String token) {
        LOG.debug("Iniciando processo de logout");

        if (token == null || token.isBlank()) {
            LOG.warn("Tentativa de logout com token vazio");
            throw new IllegalArgumentException("Token não pode estar vazio");
        }

        String cleanToken = token.trim();
        if (cleanToken.toLowerCase().startsWith("bearer ")) {
            cleanToken = cleanToken.substring(7).trim();
        }

        LOG.debugf("Token extraído para blacklist - Tamanho: %d caracteres", cleanToken.length());

        tokenBlacklistService.blacklistToken(cleanToken);
        userActivityService.removeActivity(cleanToken);

        LOG.info("Logout realizado com sucesso - Token adicionado à blacklist");
    }
}
