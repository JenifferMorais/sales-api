package com.sales.infrastructure.persistence.auth.repository;

import com.sales.infrastructure.persistence.auth.entity.UserActivityEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class UserActivityPanacheRepository implements PanacheRepository<UserActivityEntity> {

    private static final Logger LOG = Logger.getLogger(UserActivityPanacheRepository.class);

    @ConfigProperty(name = "quarkus.datasource.db-kind", defaultValue = "postgresql")
    String dbKind;

    public Optional<UserActivityEntity> findByTokenHash(String tokenHash) {
        return find("tokenHash", tokenHash).firstResultOptional();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void updateActivity(String tokenHash, Long userId) {
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("tokenHash não pode estar vazio");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId não pode ser null");
        }

        LocalDateTime now = LocalDateTime.now();

        // Prefer a single-statement UPSERT (PostgreSQL). This avoids race-condition exceptions that can poison the session.
        if ("postgresql".equalsIgnoreCase(dbKind)) {
            getEntityManager().createNativeQuery(
                            "INSERT INTO user_activity (token_hash, user_id, last_activity_at, created_at) " +
                                    "VALUES (?1, ?2, ?3, ?4) " +
                                    "ON CONFLICT (token_hash) DO UPDATE " +
                                    "SET last_activity_at = EXCLUDED.last_activity_at, user_id = EXCLUDED.user_id"
                    )
                    .setParameter(1, tokenHash)
                    .setParameter(2, userId)
                    .setParameter(3, now)
                    .setParameter(4, now)
                    .executeUpdate();
            return;
        }

        // Fallback for databases that don't support ON CONFLICT (e.g. H2 in tests)
        int updated = update(
                "lastActivityAt = ?1, userId = ?2 WHERE tokenHash = ?3",
                now,
                userId,
                tokenHash
        );

        if (updated == 0) {
            try {
                UserActivityEntity entity = new UserActivityEntity();
                entity.setTokenHash(tokenHash);
                entity.setUserId(userId);
                entity.setLastActivityAt(now);
                persist(entity);
                getEntityManager().flush();
            } catch (RuntimeException e) {
                // After a constraint violation, the persistence context can be in a bad state. Clear and retry update.
                try {
                    getEntityManager().clear();
                } catch (RuntimeException ignored) {
                    // ignore
                }

                int retryUpdated = update(
                        "lastActivityAt = ?1, userId = ?2 WHERE tokenHash = ?3",
                        now,
                        userId,
                        tokenHash
                );

                if (retryUpdated == 0) {
                    LOG.warn("Falha ao atualizar atividade do usuário após fallback/race condition");
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
