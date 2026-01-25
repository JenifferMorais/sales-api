package com.sales.domain.auth.port;

import com.sales.domain.auth.valueobject.Email;

public interface EmailService {

    void sendResetPasswordEmail(Email email, String resetToken);

    void sendWelcomeEmail(Email email, String customerName);
}
