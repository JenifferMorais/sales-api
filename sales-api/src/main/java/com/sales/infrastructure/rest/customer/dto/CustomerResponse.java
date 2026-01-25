package com.sales.infrastructure.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long id;
    private String code;
    private String fullName;
    private String motherName;
    private String cpf;
    private String rg;
    private AddressDTO address;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String cellPhone;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
