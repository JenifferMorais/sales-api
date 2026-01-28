package com.sales.application.auth.usecase;

import com.sales.infrastructure.security.TokenBlacklistService;
import com.sales.infrastructure.security.UserActivityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LogoutUseCase {

    private final TokenBlacklistService tokenBlacklistService;
    private final UserActivityService userActivityService;

    @Inject
    public LogoutUseCase(TokenBlacklistService tokenBlacklistService,
                         UserActivityService userActivityService) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.userActivityService = userActivityService;
    }

    public void execute(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token não pode estar vazio");
        }

        String cleanToken = token.trim();
        if (cleanToken.toLowerCase().startsWith("bearer ")) {
            cleanToken = cleanToken.substring(7).trim();
        }

        tokenBlacklistService.blacklistToken(cleanToken);
        userActivityService.removeActivity(cleanToken);
    }
}
