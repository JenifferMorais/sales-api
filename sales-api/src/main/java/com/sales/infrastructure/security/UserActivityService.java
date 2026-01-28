package com.sales.infrastructure.security;

import com.sales.infrastructure.persistence.auth.entity.UserActivityEntity;
import com.sales.infrastructure.persistence.auth.repository.UserActivityPanacheRepository;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class UserActivityService {

    private static final Logger LOG = Logger.getLogger(UserActivityService.class);

    @Inject
    UserActivityPanacheRepository activityRepository;

    @Inject
    JWTParser jwtParser;

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @ConfigProperty(name = "jwt.inactivity.timeout.minutes", defaultValue = "15")
    Long inactivityTimeoutMinutes;

    public boolean checkAndInvalidateIfInactive(String token) {
        String tokenHash = hashToken(token);
        Optional<UserActivityEntity> activity = activityRepository.findByTokenHash(tokenHash);

        // If there is no activity record yet, we can't consider it inactive.
        // The request filter calls updateActivity(token) afterwards to create/update the record.
        if (activity.isEmpty()) {
            return false;
        }

        UserActivityEntity activityEntity = activity.get();
        LocalDateTime lastActivity = activityEntity.getLastActivityAt();
        LocalDateTime now = LocalDateTime.now();
        Duration inactivityDuration = Duration.between(lastActivity, now);

        if (inactivityDuration.toMinutes() >= inactivityTimeoutMinutes) {
            LOG.infof("Token inativo há %d minutos. Invalidando token do usuário %d",
                    inactivityDuration.toMinutes(), activityEntity.getUserId());

            tokenBlacklistService.blacklistToken(token);
            activityRepository.deleteByTokenHash(tokenHash);
            return true;
        }

        return false;
    }

    public void updateActivity(String token) {
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            Long userId = Long.parseLong(jwt.getSubject());
            String tokenHash = hashToken(token);
            activityRepository.updateActivity(tokenHash, userId);
        } catch (ParseException | RuntimeException e) {
            LOG.error("Erro ao atualizar atividade do usuário", e);
        }
    }

    public void removeActivity(String token) {
        String tokenHash = hashToken(token);
        activityRepository.deleteByTokenHash(tokenHash);
    }

    public void cleanupOldActivities() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24);
        activityRepository.cleanupOldActivities(cutoffDate);
        LOG.info("Limpeza de atividades antigas concluída");
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
