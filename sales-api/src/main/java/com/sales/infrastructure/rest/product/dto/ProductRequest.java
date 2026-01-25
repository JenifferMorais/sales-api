package com.sales.infrastructure.rest.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    @NotBlank(message = "Tipo é obrigatório")
    private String type;

    private String details;

    @NotNull(message = "Peso é obrigatório")
    @DecimalMin(value = "0.001", message = "Peso deve ser maior que zero")
    private BigDecimal weight;

    @NotNull(message = "Preço de compra é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de compra deve ser maior que zero")
    private BigDecimal purchasePrice;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço de venda deve ser maior que zero")
    private BigDecimal salePrice;

    @NotNull(message = "Altura é obrigatória")
    @DecimalMin(value = "0.01", message = "Altura deve ser maior que zero")
    private BigDecimal height;

    @NotNull(message = "Largura é obrigatória")
    @DecimalMin(value = "0.01", message = "Largura deve ser maior que zero")
    private BigDecimal width;

    @NotNull(message = "Profundidade é obrigatória")
    @DecimalMin(value = "0.01", message = "Profundidade deve ser maior que zero")
    private BigDecimal depth;

    private String destinationVehicle;
}
