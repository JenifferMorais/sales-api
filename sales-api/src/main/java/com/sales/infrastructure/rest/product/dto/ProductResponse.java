package com.sales.infrastructure.rest.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String code;
    private String name;
    private String type;
    private String details;
    private BigDecimal weight;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal depth;
    private String destinationVehicle;
    private Integer stockQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
