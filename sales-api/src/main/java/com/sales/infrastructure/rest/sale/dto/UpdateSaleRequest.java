package com.sales.infrastructure.rest.sale.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSaleRequest {

    @NotBlank(message = "Código do vendedor é obrigatório")
    private String sellerCode;

    @NotBlank(message = "Nome do vendedor é obrigatório")
    private String sellerName;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    private String paymentMethod;

    private String cardNumber;

    @DecimalMin(value = "0.0", inclusive = false, message = "Valor pago deve ser maior que zero")
    private BigDecimal amountPaid;

    @NotNull(message = "Itens são obrigatórios")
    @Size(min = 1, message = "Venda deve ter pelo menos um item")
    @Valid
    private List<SaleItemRequest> items;
}
