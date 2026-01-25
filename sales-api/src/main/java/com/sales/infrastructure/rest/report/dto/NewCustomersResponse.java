package com.sales.infrastructure.rest.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCustomersResponse {
    private Integer year;
    private List<NewCustomerData> customers;
}
