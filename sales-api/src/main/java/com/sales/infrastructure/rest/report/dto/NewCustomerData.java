package com.sales.infrastructure.rest.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCustomerData {
    private String code;
    private String fullName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
