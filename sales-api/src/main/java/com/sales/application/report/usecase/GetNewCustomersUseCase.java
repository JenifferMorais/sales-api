package com.sales.application.report.usecase;

import com.sales.infrastructure.persistence.customer.entity.CustomerEntity;
import com.sales.infrastructure.persistence.customer.repository.CustomerPanacheRepository;
import com.sales.infrastructure.rest.report.dto.NewCustomerData;
import com.sales.infrastructure.rest.report.dto.NewCustomersResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetNewCustomersUseCase {

    private final CustomerPanacheRepository customerRepository;

    @Inject
    public GetNewCustomersUseCase(CustomerPanacheRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public NewCustomersResponse execute(int year) {
        List<CustomerEntity> customers = customerRepository.findByRegistrationYear(year);

        List<NewCustomerData> customerDataList = customers.stream()
                .map(entity -> NewCustomerData.builder()
                        .code(entity.getCode())
                        .fullName(entity.getFullName())
                        .birthDate(entity.getBirthDate())
                        .build())
                .collect(Collectors.toList());

        return NewCustomersResponse.builder()
                .year(year)
                .customers(customerDataList)
                .build();
    }
}
