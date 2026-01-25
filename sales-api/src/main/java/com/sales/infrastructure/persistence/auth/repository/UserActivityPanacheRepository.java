package com.sales.infrastructure.persistence.auth.repository;

import com.sales.infrastructure.persistence.auth.entity.UserActivityEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class UserActivityPanacheRepository implements PanacheRepository<UserActivityEntity> {

    private static final Logger LOG = Logger.getLogger(UserActivityPanacheRepository.class);

    public Optional<UserActivityEntity> findByTokenHash(String tokenHash) {
        return find("tokenHash", tokenHash).firstResultOptional();
    }

    @Transactional
    public void updateActivity(String tokenHash, Long userId) {
        int updated = update(
            "lastActivityAt = ?1 WHERE tokenHash = ?2",
            LocalDateTime.now(),
            tokenHash
        );

        if (updated == 0) {
            try {
                UserActivityEntity entity = new UserActivityEntity();
                entity.setTokenHash(tokenHash);
                entity.setUserId(userId);
                entity.setLastActivityAt(LocalDateTime.now());
                persist(entity);
            } catch (Exception e) {
                LOG.debug("Race condition detectada ao criar atividade, tentando atualizar. Erro: " + e.getMessage());
                int retryUpdated = update(
                    "lastActivityAt = ?1 WHERE tokenHash = ?2",
                    LocalDateTime.now(),
                    tokenHash
                );
                if (retryUpdated == 0) {
                    LOG.warn("Falha ao atualizar atividade do usuário após race condition");
                }
            }
        }
    }

    @Transactional
    public void deleteByTokenHash(String tokenHash) {
        delete("tokenHash", tokenHash);
    }

    @Transactional
    public void cleanupOldActivities(LocalDateTime cutoffDate) {
        delete("lastActivityAt < ?1", cutoffDate);
    }
}
