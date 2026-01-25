package com.sales.domain.sale.valueobject;

public enum PaymentMethod {
    DINHEIRO("Dinheiro"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    PIX("PIX"),
    TRANSFERENCIA_BANCARIA("Transferência Bancária");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentMethod fromString(String text) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(text) ||
                method.description.equalsIgnoreCase(text)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Forma de pagamento desconhecida: " + text);
    }
}
