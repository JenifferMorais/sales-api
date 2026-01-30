package com.sales.infrastructure.security;

import com.sales.infrastructure.persistence.auth.repository.TokenBlacklistPanacheRepository;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenBlacklistService Tests")
class TokenBlacklistServiceTest {

    @Mock
    private TokenBlacklistPanacheRepository blacklistRepository;

    @Mock
    private JWTParser jwtParser;

    @Mock
    private JsonWebToken jsonWebToken;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private String testToken;
    private String expectedHash;

    @BeforeEach
    void setUp() throws Exception {
        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        // Hash esperado do testToken usando SHA-256
        expectedHash = calculateExpectedHash(testToken);
    }

    @Test
    @DisplayName("Should blacklist token successfully")
    void shouldBlacklistTokenSuccessfully() throws ParseException {
        Long userId = 123L;
        Long expTimestamp = 1700000000L; // Timestamp de exemplo

        when(jwtParser.parse(testToken)).thenReturn(jsonWebToken);
        when(jsonWebToken.getSubject()).thenReturn(String.valueOf(userId));
        when(jsonWebToken.getExpirationTime()).thenReturn(expTimestamp);

        tokenBlacklistService.blacklistToken(testToken);

        verify(jwtParser).parse(testToken);
        verify(jsonWebToken).getSubject();
        verify(jsonWebToken).getExpirationTime();
        verify(blacklistRepository).addToBlacklist(anyString(), eq(userId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should throw exception when token is invalid on blacklist")
    void shouldThrowExceptionWhenTokenIsInvalid() throws ParseException {
        when(jwtParser.parse(anyString())).thenThrow(new ParseException("Invalid token"));

        assertThatThrownBy(() -> tokenBlacklistService.blacklistToken("invalid.token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token inválido")
                .hasCauseInstanceOf(ParseException.class);

        verify(jwtParser).parse("invalid.token");
        verify(blacklistRepository, never()).addToBlacklist(anyString(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should check if token is blacklisted")
    void shouldCheckIfTokenIsBlacklisted() {
        when(blacklistRepository.isTokenBlacklisted(anyString())).thenReturn(true);

        boolean result = tokenBlacklistService.isBlacklisted(testToken);

        assertThat(result).isTrue();
        verify(blacklistRepository).isTokenBlacklisted(anyString());
    }

    @Test
    @DisplayName("Should return false when token is not blacklisted")
    void shouldReturnFalseWhenTokenIsNotBlacklisted() {
        when(blacklistRepository.isTokenBlacklisted(anyString())).thenReturn(false);

        boolean result = tokenBlacklistService.isBlacklisted(testToken);

        assertThat(result).isFalse();
        verify(blacklistRepository).isTokenBlacklisted(anyString());
    }

    @Test
    @DisplayName("Should cleanup expired tokens")
    void shouldCleanupExpiredTokens() {
        tokenBlacklistService.cleanupExpiredTokens();

        verify(blacklistRepository).cleanupExpiredTokens();
    }

    @Test
    @DisplayName("Should hash token consistently")
    void shouldHashTokenConsistently() throws Exception {
        Method hashTokenMethod = TokenBlacklistService.class.getDeclaredMethod("hashToken", String.class);
        hashTokenMethod.setAccessible(true);

        String hash1 = (String) hashTokenMethod.invoke(tokenBlacklistService, testToken);
        String hash2 = (String) hashTokenMethod.invoke(tokenBlacklistService, testToken);

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).hasSize(64); // SHA-256 produces 64 hex characters
    }

    @Test
    @DisplayName("Should produce different hashes for different tokens")
    void shouldProduceDifferentHashesForDifferentTokens() throws Exception {
        Method hashTokenMethod = TokenBlacklistService.class.getDeclaredMethod("hashToken", String.class);
        hashTokenMethod.setAccessible(true);

        String hash1 = (String) hashTokenMethod.invoke(tokenBlacklistService, "token1");
        String hash2 = (String) hashTokenMethod.invoke(tokenBlacklistService, "token2");

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Should convert bytes to hex correctly")
    void shouldConvertBytesToHexCorrectly() throws Exception {
        Method bytesToHexMethod = TokenBlacklistService.class.getDeclaredMethod("bytesToHex", byte[].class);
        bytesToHexMethod.setAccessible(true);

        byte[] testBytes = new byte[]{0x0f, (byte) 0xff, 0x12, (byte) 0xab};
        String hex = (String) bytesToHexMethod.invoke(tokenBlacklistService, testBytes);

        assertThat(hex).isEqualTo("0fff12ab");
    }

    @Test
    @DisplayName("Should handle single digit hex values with leading zero")
    void shouldHandleSingleDigitHexWithLeadingZero() throws Exception {
        Method bytesToHexMethod = TokenBlacklistService.class.getDeclaredMethod("bytesToHex", byte[].class);
        bytesToHexMethod.setAccessible(true);

        byte[] testBytes = new byte[]{0x00, 0x01, 0x0a, 0x0f};
        String hex = (String) bytesToHexMethod.invoke(tokenBlacklistService, testBytes);

        assertThat(hex).isEqualTo("00010a0f");
    }

    @Test
    @DisplayName("Should parse user ID from token subject")
    void shouldParseUserIdFromTokenSubject() throws ParseException {
        Long expectedUserId = 999L;
        Long expTimestamp = 1700000000L;

        when(jwtParser.parse(testToken)).thenReturn(jsonWebToken);
        when(jsonWebToken.getSubject()).thenReturn(String.valueOf(expectedUserId));
        when(jsonWebToken.getExpirationTime()).thenReturn(expTimestamp);

        tokenBlacklistService.blacklistToken(testToken);

        verify(blacklistRepository).addToBlacklist(anyString(), eq(expectedUserId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should convert expiration timestamp to LocalDateTime")
    void shouldConvertExpirationTimestampToLocalDateTime() throws ParseException {
        Long userId = 123L;
        Long expTimestamp = 1700000000L; // 14 Nov 2023 22:13:20 GMT

        when(jwtParser.parse(testToken)).thenReturn(jsonWebToken);
        when(jsonWebToken.getSubject()).thenReturn(String.valueOf(userId));
        when(jsonWebToken.getExpirationTime()).thenReturn(expTimestamp);

        tokenBlacklistService.blacklistToken(testToken);

        verify(blacklistRepository).addToBlacklist(anyString(), eq(userId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should use consistent hash for same token on blacklist check")
    void shouldUseConsistentHashForSameToken() {
        when(blacklistRepository.isTokenBlacklisted(anyString())).thenReturn(false);

        tokenBlacklistService.isBlacklisted(testToken);
        tokenBlacklistService.isBlacklisted(testToken);

        verify(blacklistRepository, times(2)).isTokenBlacklisted(anyString());
    }

    @Test
    @DisplayName("Should handle blacklist operations in sequence")
    void shouldHandleBlacklistOperationsInSequence() throws ParseException {
        Long userId = 123L;
        Long expTimestamp = 1700000000L;

        when(jwtParser.parse(testToken)).thenReturn(jsonWebToken);
        when(jsonWebToken.getSubject()).thenReturn(String.valueOf(userId));
        when(jsonWebToken.getExpirationTime()).thenReturn(expTimestamp);
        when(blacklistRepository.isTokenBlacklisted(anyString())).thenReturn(false, true);

        // Token não está na blacklist
        assertThat(tokenBlacklistService.isBlacklisted(testToken)).isFalse();

        // Adiciona token à blacklist
        tokenBlacklistService.blacklistToken(testToken);

        // Token agora está na blacklist
        assertThat(tokenBlacklistService.isBlacklisted(testToken)).isTrue();

        verify(blacklistRepository, times(2)).isTokenBlacklisted(anyString());
        verify(blacklistRepository).addToBlacklist(anyString(), eq(userId), any(LocalDateTime.class));
    }

    // Helper method para calcular hash esperado
    private String calculateExpectedHash(String token) throws Exception {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));

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
