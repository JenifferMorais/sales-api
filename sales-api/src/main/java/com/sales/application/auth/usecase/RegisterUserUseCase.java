package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.EmailService;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import com.sales.domain.customer.port.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RegisterUserUseCase {

    @Inject
    UserRepository userRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    EmailService emailService;

    @Transactional
    public User execute(String customerCode, String emailValue, String plainPassword) {

        var customer = customerRepository.findByCode(customerCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente não encontrado com código: " + customerCode
                ));

        Email email = new Email(emailValue);
        Password password = Password.fromPlainText(plainPassword);

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email " + email.getValue() + " já está em uso"
            );
        }

        if (userRepository.existsByCustomerCode(customerCode)) {
            throw new IllegalArgumentException(
                    "Já existe usuário cadastrado para o cliente: " + customerCode
            );
        }

        User user = new User(customerCode, email, password);

        User savedUser = userRepository.save(user);

        emailService.sendWelcomeEmail(email, customer.getFullName());

        return savedUser;
    }
}
