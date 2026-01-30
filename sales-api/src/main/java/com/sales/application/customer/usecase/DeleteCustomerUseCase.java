package com.sales.application.customer.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DeleteCustomerUseCase {

    private static final Logger LOG = Logger.getLogger(DeleteCustomerUseCase.class);

    private final CustomerRepository customerRepository;

    @Inject
    public DeleteCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void execute(Long id) {
        LOG.infof("Iniciando exclusão do cliente ID: %d", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    LOG.warnf("Tentativa de excluir cliente inexistente - ID: %d", id);
                    return new IllegalArgumentException("Cliente não encontrado com id: " + id);
                });

        LOG.debugf("Cliente encontrado para exclusão - Código: %s, Nome: %s",
                   customer.getCode(), customer.getFullName());

        customerRepository.deleteById(id);

        LOG.infof("Cliente excluído com sucesso - ID: %d, Código: %s, Nome: %s",
                  id, customer.getCode(), customer.getFullName());
    }
}
