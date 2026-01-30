package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDate;

@ApplicationScoped
public class UpdateCustomerUseCase {

    private static final Logger LOG = Logger.getLogger(UpdateCustomerUseCase.class);

    private final CustomerRepository customerRepository;

    @Inject
    public UpdateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer execute(Long id, String fullName, String motherName, Address address,
                           LocalDate birthDate, String cellPhone, String email) {
        LOG.infof("Iniciando atualização do cliente ID: %d", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de atualizar cliente inexistente - ID: %d", id);
                    return new IllegalArgumentException("Cliente não encontrado com id: " + id);
                });

        LOG.debugf("Cliente encontrado - Código: %s, Nome atual: %s",
                   customer.getCode(), customer.getFullName());

        if (!customer.getEmail().equals(email) && customerRepository.existsByEmail(email)) {
            LOG.warnf("Tentativa de atualizar cliente com email já existente - ID: %d, Email: %s",
                      id, email);
            throw new IllegalArgumentException("Email já está em uso");
        }

        customer.updateInfo(fullName, motherName, address, birthDate, cellPhone, email);
        Customer updatedCustomer = customerRepository.save(customer);

        LOG.infof("Cliente atualizado com sucesso - ID: %d, Código: %s, Novo nome: %s",
                  updatedCustomer.getId(), updatedCustomer.getCode(), updatedCustomer.getFullName());

        return updatedCustomer;
    }
}
