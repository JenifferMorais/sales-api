package com.sales.infrastructure.rest.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String customerCode;
    }

    public static AuthResponse fromToken(String token, Long userId, String email,
                                          String customerCode, Long expiresInHours) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresInHours * 3600)
                .user(UserInfo.builder()
                        .id(userId)
                        .email(email)
                        .customerCode(customerCode)
                        .build())
                .build();
    }
}
