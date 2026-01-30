package com.sales.infrastructure.persistence.customer.service;

import com.sales.infrastructure.persistence.customer.repository.CustomerPanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CustomerCodeGenerator {

    private static final Logger LOG = Logger.getLogger(CustomerCodeGenerator.class);

    @Inject
    CustomerPanacheRepository repository;

    public String generateNextCode() {
        LOG.debug("Iniciando geração de novo código de cliente");

        String lastCode = repository.findLastCode().orElse(null);

        if (lastCode == null || lastCode.isBlank()) {
            LOG.info("Nenhum cliente cadastrado. Gerando primeiro código: CUST0001");
            return "CUST0001";
        }

        try {
            String numberPart = lastCode.substring(4);
            int number = Integer.parseInt(numberPart);
            int nextNumber = number + 1;
            String newCode = String.format("CUST%04d", nextNumber);

            LOG.infof("Código de cliente gerado - Último: %s, Novo: %s", lastCode, newCode);

            return newCode;
        } catch (Exception e) {
            LOG.warnf(e, "Erro ao processar último código: %s. Usando contagem total.", lastCode);

            long count = repository.countAll();
            String newCode = String.format("CUST%04d", count + 1);

            LOG.infof("Código gerado por contagem - Total de clientes: %d, Novo código: %s",
                      count, newCode);

            return newCode;
        }
    }
}
