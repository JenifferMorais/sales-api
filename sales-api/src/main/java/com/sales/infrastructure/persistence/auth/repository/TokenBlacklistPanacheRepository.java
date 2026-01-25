package com.sales.infrastructure.persistence.auth.repository;

import com.sales.infrastructure.persistence.auth.entity.TokenBlacklistEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class TokenBlacklistPanacheRepository implements PanacheRepository<TokenBlacklistEntity> {

    public boolean isTokenBlacklisted(String tokenHash) {
        return count("tokenHash = ?1 and expiresAt > ?2", tokenHash, LocalDateTime.now()) > 0;
    }

    @Transactional
    public void addToBlacklist(String tokenHash, Long userId, LocalDateTime expiresAt) {
        TokenBlacklistEntity entity = new TokenBlacklistEntity();
        entity.setTokenHash(tokenHash);
        entity.setUserId(userId);
        entity.setExpiresAt(expiresAt);
        persist(entity);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        delete("expiresAt < ?1", LocalDateTime.now());
    }
}
