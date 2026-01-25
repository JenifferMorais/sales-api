package com.sales.domain.auth.port;

import com.sales.domain.auth.entity.User;

public interface TokenService {

    String generateAccessToken(User user);

    String generateResetPasswordToken();

    boolean validateToken(String token);

    Long extractUserId(String token);
}
