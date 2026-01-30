package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.EmailService;
import com.sales.domain.auth.port.UserRepository;
import com.sales.domain.auth.valueobject.Email;
import com.sales.domain.auth.valueobject.Password;
import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.infrastructure.persistence.customer.service.CustomerCodeGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDate;

@ApplicationScoped
public class RegisterUserUseCase {

    private static final Logger LOG = Logger.getLogger(RegisterUserUseCase.class);

    @Inject
    UserRepository userRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    EmailService emailService;

    @Inject
    CustomerCodeGenerator customerCodeGenerator;

    @Transactional
    public User execute(String customerCode, String emailValue, String plainPassword) {
        LOG.infof("Iniciando registro de novo usuário - Cliente: %s, Email: %s",
                  customerCode, emailValue);

        var customer = customerRepository.findByCode(customerCode)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de registrar usuário para cliente inexistente: %s", customerCode);
                    return new IllegalArgumentException(
                            "Cliente não encontrado com código: " + customerCode
                    );
                });

        Email email = new Email(emailValue);
        Password password = Password.fromPlainText(plainPassword);

        if (userRepository.existsByEmail(email)) {
            LOG.warnf("Tentativa de registrar usuário com email duplicado: %s", emailValue);
            throw new IllegalArgumentException(
                    "Email " + email.getValue() + " já está em uso"
            );
        }

        if (userRepository.existsByCustomerCode(customerCode)) {
            LOG.warnf("Tentativa de registrar segundo usuário para o mesmo cliente: %s", customerCode);
            throw new IllegalArgumentException(
                    "Já existe usuário cadastrado para o cliente: " + customerCode
            );
        }

        User user = new User(customerCode, email, password);

        User savedUser = userRepository.save(user);

        LOG.infof("Usuário registrado com sucesso - ID: %d, Cliente: %s, Email: %s",
                  savedUser.getId(), customerCode, emailValue);

        try {
            emailService.sendWelcomeEmail(email, customer.getFullName());
            LOG.infof("Email de boas-vindas enviado para: %s", emailValue);
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao enviar email de boas-vindas para: %s", emailValue);
        }

        return savedUser;
    }

    @Transactional
    public User executeWithNewCustomer(String fullName, String motherName, String cpf, String rg,
                                       String zipCode, String street, String number, String complement,
                                       String neighborhood, String city, String state, LocalDate birthDate,
                                       String cellPhone, String emailValue, String plainPassword) {
        LOG.infof("Iniciando registro de novo usuário com criação de cliente - Email: %s", emailValue);

        // Validações
        Email email = new Email(emailValue);

        if (userRepository.existsByEmail(email)) {
            LOG.warnf("Tentativa de registrar usuário com email duplicado: %s", emailValue);
            throw new IllegalArgumentException(
                    "Email " + email.getValue() + " já está em uso"
            );
        }

        if (customerRepository.existsByCpf(cpf)) {
            LOG.warnf("Tentativa de registrar cliente com CPF duplicado: %s", cpf);
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (customerRepository.existsByEmail(emailValue)) {
            LOG.warnf("Tentativa de registrar cliente com email duplicado: %s", emailValue);
            throw new IllegalArgumentException("Email já cadastrado como cliente");
        }

        // Criar novo cliente
        String customerCode = customerCodeGenerator.generateNextCode();
        Document document = Document.create(cpf, rg);
        Address address = Address.create(zipCode, street, number, complement, neighborhood, city, state);

        Customer customer = Customer.create(
                customerCode, fullName, motherName, document,
                address, birthDate, cellPhone, emailValue
        );

        Customer savedCustomer = customerRepository.save(customer);
        LOG.infof("Cliente criado automaticamente - Código: %s, Nome: %s", customerCode, fullName);

        // Criar usuário para o cliente
        Password password = Password.fromPlainText(plainPassword);
        User user = new User(customerCode, email, password);
        User savedUser = userRepository.save(user);

        LOG.infof("Usuário registrado com sucesso - ID: %d, Cliente: %s, Email: %s",
                  savedUser.getId(), customerCode, emailValue);

        // Enviar email de boas-vindas
        try {
            emailService.sendWelcomeEmail(email, fullName);
            LOG.infof("Email de boas-vindas enviado para: %s", emailValue);
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao enviar email de boas-vindas para: %s", emailValue);
        }

        return savedUser;
    }
}
