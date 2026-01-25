package com.sales.infrastructure.rest.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardChartDataPoint {
    private String label;
    private String shortLabel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long salesCount;
    private BigDecimal revenue;
}
