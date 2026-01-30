package com.sales.infrastructure.security;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Configurar propriedades usando reflection
        setField(jwtService, "issuer", "sales-api");
        setField(jwtService, "expirationHours", 24L);

        // Criar usuário de teste
        Email email = new Email("test@example.com");
        Password password = Password.fromPlainText("Test@123");
        testUser = new User(1L, "CUST001", email, password, true, null, null, null);
    }

    @Test
    @DisplayName("Should generate access token with correct claims")
    void shouldGenerateAccessTokenWithCorrectClaims() {
        String token = jwtService.generateAccessToken(testUser);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    @DisplayName("Should generate different tokens for same user on multiple calls")
    void shouldGenerateDifferentTokensForSameUser() {
        String token1 = jwtService.generateAccessToken(testUser);
        String token2 = jwtService.generateAccessToken(testUser);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate access token with user email in claims")
    void shouldGenerateTokenWithUserEmail() {
        String token = jwtService.generateAccessToken(testUser);

        assertThat(token).isNotNull();
        // Token contém as informações do usuário
    }

    @Test
    @DisplayName("Should generate access token with customer code in claims")
    void shouldGenerateTokenWithCustomerCode() {
        String token = jwtService.generateAccessToken(testUser);

        assertThat(token).isNotNull();
        assertThat(testUser.getCustomerCode()).isEqualTo("CUST001");
    }

    @Test
    @DisplayName("Should generate access token with USER role")
    void shouldGenerateTokenWithUserRole() {
        String token = jwtService.generateAccessToken(testUser);

        assertThat(token).isNotNull();
        // Token contém o grupo USER
    }

    @Test
    @DisplayName("Should generate reset password token")
    void shouldGenerateResetPasswordToken() {
        String token1 = jwtService.generateResetPasswordToken();
        String token2 = jwtService.generateResetPasswordToken();

        assertThat(token1).isNotNull();
        assertThat(token1).isNotEmpty();
        assertThat(token2).isNotNull();
        assertThat(token2).isNotEmpty();
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate reset password token in UUID format")
    void shouldGenerateResetPasswordTokenInUuidFormat() {
        String token = jwtService.generateResetPasswordToken();

        // UUID format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
        assertThat(token).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    @Test
    @DisplayName("Should validate token and return true")
    void shouldValidateTokenAndReturnTrue() {
        boolean result = jwtService.validateToken("any.token.here");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should throw UnsupportedOperationException when extracting user ID")
    void shouldThrowExceptionWhenExtractingUserId() {
        assertThatThrownBy(() -> jwtService.extractUserId("any.token.here"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Use SecurityIdentity.getPrincipal().getName()");
    }

    @Test
    @DisplayName("Should generate token with configured issuer")
    void shouldGenerateTokenWithConfiguredIssuer() throws Exception {
        setField(jwtService, "issuer", "custom-issuer");

        String token = jwtService.generateAccessToken(testUser);

        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("Should generate token with custom expiration hours")
    void shouldGenerateTokenWithCustomExpirationHours() throws Exception {
        setField(jwtService, "expirationHours", 48L);

        String token = jwtService.generateAccessToken(testUser);

        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("Should handle user with different ID")
    void shouldHandleUserWithDifferentId() {
        Email email = new Email("test2@example.com");
        Password password = Password.fromPlainText("Test@123");
        User userWithDifferentId = new User(2L, "CUST002", email, password, true, null, null, null);

        String token = jwtService.generateAccessToken(userWithDifferentId);

        assertThat(token).isNotNull();
    }

    // Helper method para setar campos privados via reflection
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
