package com.sales.infrastructure.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomerRequest {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 200, message = "Nome completo deve ter no máximo 200 caracteres")
    private String fullName;

    @NotBlank(message = "Nome da mãe é obrigatório")
    @Size(max = 200, message = "Nome da mãe deve ter no máximo 200 caracteres")
    private String motherName;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private AddressDTO address;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve estar no passado")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "Celular é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\) ?\\d{5}-?\\d{4}", message = "Celular inválido. Formato: (11) 98765-4321")
    private String cellPhone;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;
}
