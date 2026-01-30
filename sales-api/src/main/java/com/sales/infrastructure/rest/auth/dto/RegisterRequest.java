package com.sales.infrastructure.rest.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    // Opcional - Se fornecido, associa a um cliente existente
    // Se não fornecido, cria um novo cliente com os dados abaixo
    private String customerCode;

    // Dados de autenticação (sempre obrigatórios)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Senha deve conter: maiúscula, minúscula, número e caractere especial"
    )
    private String password;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmPassword;

    // Dados do cliente (obrigatórios se customerCode não for fornecido)
    private String fullName;
    private String motherName;
    private String cpf;
    private String rg;
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private LocalDate birthDate;
    private String cellPhone;

    public boolean hasCustomerCode() {
        return customerCode != null && !customerCode.isBlank();
    }

    public boolean hasCustomerData() {
        return fullName != null && !fullName.isBlank();
    }
}
