package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.infrastructure.persistence.customer.service.CustomerCodeGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateCustomerUseCase {

    private static final Logger LOG = Logger.getLogger(CreateCustomerUseCase.class);

    private final CustomerRepository customerRepository;
    private final CustomerCodeGenerator codeGenerator;

    @Inject
    public CreateCustomerUseCase(CustomerRepository customerRepository, CustomerCodeGenerator codeGenerator) {
        this.customerRepository = customerRepository;
        this.codeGenerator = codeGenerator;
    }

    public Customer execute(Customer customer) {
        LOG.infof("Iniciando criação de cliente: %s", customer.getEmail());

        validateCustomer(customer);

        String generatedCode = codeGenerator.generateNextCode();
        LOG.infof("Código gerado para novo cliente: %s", generatedCode);

        Customer customerWithCode = Customer.create(
                generatedCode,
                customer.getFullName(),
                customer.getMotherName(),
                customer.getDocument(),
                customer.getAddress(),
                customer.getBirthDate(),
                customer.getCellPhone(),
                customer.getEmail()
        );

        Customer savedCustomer = customerRepository.save(customerWithCode);
        LOG.infof("Cliente criado com sucesso - ID: %d, Código: %s, Nome: %s",
                  savedCustomer.getId(), savedCustomer.getCode(), savedCustomer.getFullName());

        return savedCustomer;
    }

    private void validateCustomer(Customer customer) {
        LOG.debugf("Validando cliente: CPF=%s, Email=%s",
                   customer.getDocument().getFormattedCPF(), customer.getEmail());

        if (customerRepository.existsByCpf(customer.getDocument().getCpf())) {
            LOG.warnf("Tentativa de criar cliente com CPF duplicado: %s",
                      customer.getDocument().getFormattedCPF());
            throw new IllegalArgumentException("Cliente com CPF " + customer.getDocument().getFormattedCPF() + " já existe");
        }
        if (customerRepository.existsByEmail(customer.getEmail())) {
            LOG.warnf("Tentativa de criar cliente com email duplicado: %s", customer.getEmail());
            throw new IllegalArgumentException("Cliente com email " + customer.getEmail() + " já existe");
        }

        LOG.debug("Validação do cliente concluída com sucesso");
    }
}
