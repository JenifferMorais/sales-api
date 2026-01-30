package com.sales.infrastructure.email;

import com.sales.domain.auth.valueobject.Email;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailServiceAdapter Tests")
class EmailServiceAdapterTest {

    @Mock
    private Mailer mailer;

    @InjectMocks
    private EmailServiceAdapter emailService;

    @Captor
    private ArgumentCaptor<Mail> mailCaptor;

    private Email testEmail;

    @BeforeEach
    void setUp() throws Exception {
        testEmail = new Email("test@example.com");

        // Set config properties using reflection
        setField(emailService, "appUrl", "http://localhost:8080");
        setField(emailService, "appName", "Vendas API");
    }

    @Test
    @DisplayName("Should send reset password email successfully")
    void shouldSendResetPasswordEmailSuccessfully() {
        String resetToken = "reset-token-123";

        emailService.sendResetPasswordEmail(testEmail, resetToken);

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getTo()).contains("test@example.com");
        assertThat(sentMail.getSubject()).isEqualTo("[Vendas API] Redefinição de Senha");
        assertThat(sentMail.getHtml()).contains("Redefinição de Senha");
        assertThat(sentMail.getHtml()).contains(resetToken);
        assertThat(sentMail.getHtml()).contains("http://localhost:8080/auth/reset-password?token=" + resetToken);
    }

    @Test
    @DisplayName("Should include reset link in email body")
    void shouldIncludeResetLinkInEmailBody() {
        String resetToken = "abc123xyz";

        emailService.sendResetPasswordEmail(testEmail, resetToken);

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        String expectedLink = "http://localhost:8080/auth/reset-password?token=abc123xyz";
        assertThat(sentMail.getHtml()).contains(expectedLink);
    }

    @Test
    @DisplayName("Should include app name in reset email")
    void shouldIncludeAppNameInResetEmail() {
        emailService.sendResetPasswordEmail(testEmail, "token");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("Vendas API");
    }

    @Test
    @DisplayName("Should include validity period in reset email")
    void shouldIncludeValidityPeriodInResetEmail() {
        emailService.sendResetPasswordEmail(testEmail, "token");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("Este link é válido por 1 hora");
    }

    @Test
    @DisplayName("Should handle email sending exception gracefully")
    void shouldHandleEmailSendingExceptionGracefully() {
        doThrow(new RuntimeException("Email server error")).when(mailer).send(any(Mail.class));

        assertThatCode(() -> emailService.sendResetPasswordEmail(testEmail, "token"))
                .doesNotThrowAnyException();

        verify(mailer).send(any(Mail.class));
    }

    @Test
    @DisplayName("Should send welcome email successfully")
    void shouldSendWelcomeEmailSuccessfully() {
        String customerName = "João Silva";

        emailService.sendWelcomeEmail(testEmail, customerName);

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getTo()).contains("test@example.com");
        assertThat(sentMail.getSubject()).isEqualTo("[Vendas API] Bem-vindo!");
        assertThat(sentMail.getHtml()).contains("Bem-vindo ao Vendas API");
        assertThat(sentMail.getHtml()).contains(customerName);
    }

    @Test
    @DisplayName("Should include customer name in welcome email")
    void shouldIncludeCustomerNameInWelcomeEmail() {
        String customerName = "Maria Santos";

        emailService.sendWelcomeEmail(testEmail, customerName);

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("Olá <strong>Maria Santos</strong>");
    }

    @Test
    @DisplayName("Should include login link in welcome email")
    void shouldIncludeLoginLinkInWelcomeEmail() {
        emailService.sendWelcomeEmail(testEmail, "João");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("http://localhost:8080/login");
    }

    @Test
    @DisplayName("Should include benefits list in welcome email")
    void shouldIncludeBenefitsListInWelcomeEmail() {
        emailService.sendWelcomeEmail(testEmail, "João");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("Fazer login no sistema");
        assertThat(sentMail.getHtml()).contains("Gerenciar suas compras");
        assertThat(sentMail.getHtml()).contains("Acompanhar pedidos");
        assertThat(sentMail.getHtml()).contains("Atualizar seus dados");
    }

    @Test
    @DisplayName("Should handle welcome email sending exception gracefully")
    void shouldHandleWelcomeEmailSendingExceptionGracefully() {
        doThrow(new RuntimeException("Email server error")).when(mailer).send(any(Mail.class));

        assertThatCode(() -> emailService.sendWelcomeEmail(testEmail, "João"))
                .doesNotThrowAnyException();

        verify(mailer).send(any(Mail.class));
    }

    @Test
    @DisplayName("Should use custom app URL in reset email")
    void shouldUseCustomAppUrlInResetEmail() throws Exception {
        setField(emailService, "appUrl", "https://custom.domain.com");

        emailService.sendResetPasswordEmail(testEmail, "token123");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("https://custom.domain.com/auth/reset-password?token=token123");
    }

    @Test
    @DisplayName("Should use custom app name in emails")
    void shouldUseCustomAppNameInEmails() throws Exception {
        setField(emailService, "appName", "Custom App");

        emailService.sendResetPasswordEmail(testEmail, "token");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getSubject()).isEqualTo("[Custom App] Redefinição de Senha");
        assertThat(sentMail.getHtml()).contains("Custom App");
    }

    @Test
    @DisplayName("Should format reset email as HTML")
    void shouldFormatResetEmailAsHtml() {
        emailService.sendResetPasswordEmail(testEmail, "token");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).startsWith("<html>");
        assertThat(sentMail.getHtml().trim()).endsWith("</html>");
        assertThat(sentMail.getHtml()).contains("<body");
    }

    @Test
    @DisplayName("Should format welcome email as HTML")
    void shouldFormatWelcomeEmailAsHtml() {
        emailService.sendWelcomeEmail(testEmail, "João");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).startsWith("<html>");
        assertThat(sentMail.getHtml().trim()).endsWith("</html>");
        assertThat(sentMail.getHtml()).contains("<body");
    }

    @Test
    @DisplayName("Should include styled button in reset email")
    void shouldIncludeStyledButtonInResetEmail() {
        emailService.sendResetPasswordEmail(testEmail, "token");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("Redefinir Senha");
        assertThat(sentMail.getHtml()).contains("background-color");
        assertThat(sentMail.getHtml()).contains("border-radius");
    }

    @Test
    @DisplayName("Should include styled button in welcome email")
    void shouldIncludeStyledButtonInWelcomeEmail() {
        emailService.sendWelcomeEmail(testEmail, "João");

        verify(mailer).send(mailCaptor.capture());

        Mail sentMail = mailCaptor.getValue();
        assertThat(sentMail.getHtml()).contains("Fazer Login");
        assertThat(sentMail.getHtml()).contains("background-color");
    }

    @Test
    @DisplayName("Should not throw exception when mailer throws error on reset email")
    void shouldNotThrowExceptionWhenMailerThrowsErrorOnResetEmail() {
        doThrow(new RuntimeException("SMTP error")).when(mailer).send(any(Mail.class));

        assertThatCode(() -> emailService.sendResetPasswordEmail(testEmail, "token"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should not throw exception when mailer throws error on welcome email")
    void shouldNotThrowExceptionWhenMailerThrowsErrorOnWelcomeEmail() {
        doThrow(new RuntimeException("SMTP error")).when(mailer).send(any(Mail.class));

        assertThatCode(() -> emailService.sendWelcomeEmail(testEmail, "João"))
                .doesNotThrowAnyException();
    }

    // Helper method para setar campos privados via reflection
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
