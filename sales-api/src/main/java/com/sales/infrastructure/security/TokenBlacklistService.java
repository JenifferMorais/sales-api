package com.sales.infrastructure.security;

import com.sales.infrastructure.persistence.auth.repository.TokenBlacklistPanacheRepository;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
public class TokenBlacklistService {

    @Inject
    TokenBlacklistPanacheRepository blacklistRepository;

    @Inject
    JWTParser jwtParser;

    public void blacklistToken(String token) {
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            String tokenHash = hashToken(token);
            Long userId = Long.parseLong(jwt.getSubject());

            Long expTimestamp = jwt.getExpirationTime();
            LocalDateTime expiresAt = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(expTimestamp),
                    ZoneId.systemDefault()
            );

            blacklistRepository.addToBlacklist(tokenHash, userId, expiresAt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    public boolean isBlacklisted(String token) {
        String tokenHash = hashToken(token);
        return blacklistRepository.isTokenBlacklisted(tokenHash);
    }

    public void cleanupExpiredTokens() {
        blacklistRepository.cleanupExpiredTokens();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash do token", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
