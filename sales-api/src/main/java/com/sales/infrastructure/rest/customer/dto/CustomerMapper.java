package com.sales.infrastructure.rest.customer.dto;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerMapper {

    public Customer toDomain(CreateCustomerRequest request) {
        Document document = new Document(
                request.getCpf().replaceAll("[^0-9]", ""),
                request.getRg()
        );
        Address address = new Address(
                request.getAddress().getZipCode(),
                request.getAddress().getStreet(),
                request.getAddress().getNumber(),
                request.getAddress().getComplement(),
                request.getAddress().getNeighborhood(),
                request.getAddress().getCity(),
                request.getAddress().getState()
        );
        return new Customer(
                request.getCode(),
                request.getFullName(),
                request.getMotherName(),
                document,
                address,
                request.getBirthDate(),
                request.getCellPhone().replaceAll("[^0-9]", ""),
                request.getEmail()
        );
    }

    public CustomerResponse toResponse(Customer customer) {
        AddressDTO addressDTO = new AddressDTO(
                customer.getAddress().getFormattedZipCode(),
                customer.getAddress().getStreet(),
                customer.getAddress().getNumber(),
                customer.getAddress().getComplement(),
                customer.getAddress().getNeighborhood(),
                customer.getAddress().getCity(),
                customer.getAddress().getState()
        );
        return new CustomerResponse(
                customer.getId(),
                customer.getCode(),
                customer.getFullName(),
                customer.getMotherName(),
                customer.getDocument().getFormattedCPF(),
                customer.getDocument().getRg(),
                addressDTO,
                customer.getBirthDate(),
                customer.getFormattedCellPhone(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }

    public Address toAddress(AddressDTO dto) {
        return new Address(
                dto.getZipCode(),
                dto.getStreet(),
                dto.getNumber(),
                dto.getComplement(),
                dto.getNeighborhood(),
                dto.getCity(),
                dto.getState()
        );
    }
}
