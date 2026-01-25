package com.sales.infrastructure.security;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.TokenService;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class JwtService implements TokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "sales-api")
    String issuer;

    @ConfigProperty(name = "jwt.expiration.hours", defaultValue = "24")
    Long expirationHours;

    @Override
    public String generateAccessToken(User user) {
        Set<String> roles = new HashSet<>();
        roles.add("USER");

        return Jwt.issuer(issuer)
                .subject(user.getId().toString())
                .claim("email", user.getEmail().getValue())
                .claim("customerCode", user.getCustomerCode())
                .groups(roles)
                .expiresAt(Instant.now().plus(Duration.ofHours(expirationHours)))
                .sign();
    }

    @Override
    public String generateResetPasswordToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean validateToken(String token) {

        return true;
    }

    @Override
    public Long extractUserId(String token) {

        throw new UnsupportedOperationException(
                "Use SecurityIdentity.getPrincipal().getName() para obter o user ID"
        );
    }
}
