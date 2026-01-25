package com.sales.infrastructure.security;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TokenBlacklistCleanupJob {

    private static final Logger LOG = Logger.getLogger(TokenBlacklistCleanupJob.class);

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @Inject
    UserActivityService userActivityService;

    @Scheduled(cron = "0 0 0 * * ?")
    void cleanupExpiredTokens() {
        LOG.info("Iniciando limpeza de tokens expirados e atividades antigas");
        try {

            tokenBlacklistService.cleanupExpiredTokens();
            LOG.info("Limpeza de tokens expirados concluída com sucesso");

            userActivityService.cleanupOldActivities();
            LOG.info("Limpeza de atividades antigas concluída com sucesso");

        } catch (Exception e) {
            LOG.error("Erro ao limpar dados expirados", e);
        }
    }
}
