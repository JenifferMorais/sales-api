package com.sales.infrastructure.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION + 1)
public class TokenBlacklistFilter implements ContainerRequestFilter {

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @Inject
    UserActivityService userActivityService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            return;
        }

        String token = authHeader.substring(7).trim();

        if (tokenBlacklistService.isBlacklisted(token)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"message\": \"Token inválido ou expirado\"}")
                            .build()
            );
            return;
        }

        if (userActivityService.checkAndInvalidateIfInactive(token)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"message\": \"Sessão expirada por inatividade. Faça login novamente.\"}")
                            .build()
            );
            return;
        }

        userActivityService.updateActivity(token);
    }
}
