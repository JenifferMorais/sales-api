package com.sales.infrastructure.email;

import com.sales.domain.auth.port.EmailService;
import com.sales.domain.auth.valueobject.Email;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailServiceAdapter implements EmailService {

    private static final Logger LOG = Logger.getLogger(EmailServiceAdapter.class);

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "app.url", defaultValue = "http://localhost:8080")
    String appUrl;

    @ConfigProperty(name = "app.name", defaultValue = "Vendas API")
    String appName;

    @Override
    public void sendResetPasswordEmail(Email email, String resetToken) {
        String resetLink = appUrl + "/auth/reset-password?token=" + resetToken;

        String htmlBody = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #2c3e50;">🔐 Redefinição de Senha</h2>

                        <p>Olá,</p>

                        <p>Você solicitou a redefinição de senha para sua conta no <strong>%s</strong>.</p>

                        <p>Clique no botão abaixo para criar uma nova senha:</p>

                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s"
                               style="background-color: #3498db; color: white; padding: 12px 30px;
                                      text-decoration: none; border-radius: 5px; display: inline-block;">
                                Redefinir Senha
                            </a>
                        </div>

                        <p><strong>Este link é válido por 1 hora.</strong></p>

                        <p>Se você não solicitou esta redefinição, ignore este email.
                           Sua senha permanecerá inalterada.</p>

                        <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">

                        <p style="font-size: 12px; color: #7f8c8d;">
                            Se o botão não funcionar, copie e cole este link no navegador:<br>
                            <a href="%s" style="color: #3498db;">%s</a>
                        </p>

                        <p style="font-size: 12px; color: #7f8c8d; margin-top: 20px;">
                            Atenciosamente,<br>
                            Equipe %s
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(appName, resetLink, resetLink, resetLink, appName);

        try {
            mailer.send(
                    Mail.withHtml(
                            email.getValue(),
                            "[" + appName + "] Redefinição de Senha",
                            htmlBody
                    )
            );
            LOG.infof("Email de reset de senha enviado para: %s", email.getValue());
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao enviar email de reset de senha para: %s", email.getValue());

        }
    }

    @Override
    public void sendWelcomeEmail(Email email, String customerName) {
        String htmlBody = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style="color: #27ae60;">🎉 Bem-vindo ao %s!</h2>

                        <p>Olá <strong>%s</strong>,</p>

                        <p>É um prazer tê-lo(a) conosco!</p>

                        <p>Sua conta foi criada com sucesso. Agora você pode:</p>

                        <ul style="line-height: 2;">
                            <li>✅ Fazer login no sistema</li>
                            <li>✅ Gerenciar suas compras</li>
                            <li>✅ Acompanhar pedidos</li>
                            <li>✅ Atualizar seus dados</li>
                        </ul>

                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s/login"
                               style="background-color: #27ae60; color: white; padding: 12px 30px;
                                      text-decoration: none; border-radius: 5px; display: inline-block;">
                                Fazer Login
                            </a>
                        </div>

                        <p>Se tiver alguma dúvida, não hesite em entrar em contato conosco.</p>

                        <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">

                        <p style="font-size: 12px; color: #7f8c8d; margin-top: 20px;">
                            Atenciosamente,<br>
                            Equipe %s
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(appName, customerName, appUrl, appName);

        try {
            mailer.send(
                    Mail.withHtml(
                            email.getValue(),
                            "[" + appName + "] Bem-vindo!",
                            htmlBody
                    )
            );
            LOG.infof("Email de boas-vindas enviado para: %s", email.getValue());
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao enviar email de boas-vindas para: %s", email.getValue());
        }
    }
}
