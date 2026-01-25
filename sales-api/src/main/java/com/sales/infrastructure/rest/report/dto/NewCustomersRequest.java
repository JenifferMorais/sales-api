package com.sales.infrastructure.rest.report.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCustomersRequest {

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano deve ser maior ou igual a 2000")
    @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100")
    private Integer year;
}
