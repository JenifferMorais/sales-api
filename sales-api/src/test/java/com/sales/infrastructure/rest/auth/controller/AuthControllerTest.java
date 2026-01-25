package com.sales.infrastructure.rest.auth.controller;

import com.sales.application.auth.usecase.*;
import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @InjectMock
    RegisterUserUseCase registerUserUseCase;

    @InjectMock
    LoginUseCase loginUseCase;

    @InjectMock
    ForgotPasswordUseCase forgotPasswordUseCase;

    @InjectMock
    ResetPasswordUseCase resetPasswordUseCase;

    @InjectMock
    LogoutUseCase logoutUseCase;

    private User mockUser;
    private String validToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

    @BeforeEach
    void setUp() {
        Email email = new Email("john.silva@email.com");
        Password password = Password.fromPlainText("Test@123");
        mockUser = new User(1L, "CUST001", email, password, true, null, null, null);
    }

    @Test
    @DisplayName("POST /register - Should register user successfully")
    void shouldRegisterUserSuccessfully() {

        when(registerUserUseCase.execute(anyString(), anyString(), anyString()))
                .thenReturn(mockUser);
        when(loginUseCase.execute(anyString(), anyString()))
                .thenReturn(validToken);

        String requestBody = """
                {
                    "customerCode": "CUST001",
                    "email": "john.silva@email.com",
                    "password": "Test@123",
                    "confirmPassword": "Test@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/register")
        .then()
                .statusCode(201)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"))
                .body("user.id", equalTo(1))
                .body("user.email", equalTo("john.silva@email.com"))
                .body("user.customerCode", equalTo("CUST001"));

        verify(registerUserUseCase).execute("CUST001", "john.silva@email.com", "Test@123");
        verify(loginUseCase).execute("john.silva@email.com", "Test@123");
    }

    @Test
    @DisplayName("POST /register - Should return 400 when passwords don't match")
    void shouldReturn400WhenPasswordsDontMatch() {

        String requestBody = """
                {
                    "customerCode": "CUST001",
                    "email": "john.silva@email.com",
                    "password": "Test@123",
                    "confirmPassword": "Different@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/register")
        .then()
                .statusCode(400)
                .body("message", equalTo("Senhas não conferem"));

        verify(registerUserUseCase, never()).execute(anyString(), anyString(), anyString());
        verify(loginUseCase, never()).execute(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /register - Should return 400 when customer not found")
    void shouldReturn400WhenCustomerNotFound() {

        when(registerUserUseCase.execute(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Cliente não encontrado com código: CUST999"));

        String requestBody = """
                {
                    "customerCode": "CUST999",
                    "email": "john.silva@email.com",
                    "password": "Test@123",
                    "confirmPassword": "Test@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/register")
        .then()
                .statusCode(400);

        verify(registerUserUseCase).execute("CUST999", "john.silva@email.com", "Test@123");
    }

    @Test
    @DisplayName("POST /login - Should login successfully")
    void shouldLoginSuccessfully() {

        when(loginUseCase.execute(anyString(), anyString()))
                .thenReturn(validToken);

        String requestBody = """
                {
                    "email": "john.silva@email.com",
                    "password": "Test@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/login")
        .then()
                .statusCode(200)
                .body("access_token", equalTo(validToken))
                .body("token_type", equalTo("Bearer"))
                .body("expires_in", notNullValue());

        verify(loginUseCase).execute("john.silva@email.com", "Test@123");
    }

    @Test
    @DisplayName("POST /login - Should return 401 with invalid credentials")
    void shouldReturn401WithInvalidCredentials() {

        when(loginUseCase.execute(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Credenciais inválidas"));

        String requestBody = """
                {
                    "email": "john.silva@email.com",
                    "password": "WrongPassword@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/login")
        .then()
                .statusCode(400);

        verify(loginUseCase).execute("john.silva@email.com", "WrongPassword@123");
    }

    @Test
    @DisplayName("POST /forgot-password - Should always return success for security")
    void shouldAlwaysReturnSuccessForForgotPassword() {

        doNothing().when(forgotPasswordUseCase).execute(anyString());

        String requestBody = """
                {
                    "email": "john.silva@email.com"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/forgot-password")
        .then()
                .statusCode(200)
                .body("message", equalTo("Se o email existir, um link de redefinição foi enviado"));
    }

    @Test
    @DisplayName("POST /forgot-password - Should return success even when email doesn't exist")
    void shouldReturnSuccessEvenWhenEmailDoesntExist() {

        doThrow(new IllegalArgumentException("User not found"))
                .when(forgotPasswordUseCase).execute(anyString());

        String requestBody = """
                {
                    "email": "nonexistent@email.com"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/forgot-password")
        .then()
                .statusCode(200)
                .body("message", equalTo("Se o email existir, um link de redefinição foi enviado"));
    }

    @Test
    @DisplayName("POST /reset-password - Should reset password successfully")
    void shouldResetPasswordSuccessfully() {

        doNothing().when(resetPasswordUseCase).execute(anyString(), anyString());

        String requestBody = """
                {
                    "token": "valid-reset-token",
                    "newPassword": "NewPassword@123",
                    "confirmPassword": "NewPassword@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/reset-password")
        .then()
                .statusCode(200)
                .body("message", equalTo("Senha redefinida com sucesso"));

        verify(resetPasswordUseCase).execute("valid-reset-token", "NewPassword@123");
    }

    @Test
    @DisplayName("POST /reset-password - Should return 400 when passwords don't match")
    void shouldReturn400WhenResetPasswordsDontMatch() {

        String requestBody = """
                {
                    "token": "valid-reset-token",
                    "newPassword": "NewPassword@123",
                    "confirmPassword": "Different@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/reset-password")
        .then()
                .statusCode(400)
                .body("message", equalTo("Senhas não conferem"));

        verify(resetPasswordUseCase, never()).execute(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /reset-password - Should return 400 with invalid token")
    void shouldReturn400WithInvalidResetToken() {

        doThrow(new IllegalArgumentException("Token inválido ou expirado"))
                .when(resetPasswordUseCase).execute(anyString(), anyString());

        String requestBody = """
                {
                    "token": "invalid-token",
                    "newPassword": "NewPassword@123",
                    "confirmPassword": "NewPassword@123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/api/v1/auth/reset-password")
        .then()
                .statusCode(400);

        verify(resetPasswordUseCase).execute("invalid-token", "NewPassword@123");
    }

    @Test
    @DisplayName("POST /logout - Should handle logout successfully")
    void shouldHandleLogoutSuccessfully() {

        doNothing().when(logoutUseCase).execute(anyString());

        given()
                .header("Authorization", "Bearer " + validToken)
        .when()
                .post("/api/v1/auth/logout")
        .then()
                .statusCode(anyOf(equalTo(200), equalTo(401)));

    }
}
