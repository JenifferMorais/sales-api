package com.sales.infrastructure.rest.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRevenueRequest {

    @NotNull(message = "Data de referência é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate referenceDate;
}
