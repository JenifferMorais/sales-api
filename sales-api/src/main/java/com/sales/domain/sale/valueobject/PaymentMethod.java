package com.sales.domain.sale.valueobject;

import java.util.Map;

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

    private static final Map<String, PaymentMethod> ALIASES = Map.ofEntries(
            Map.entry("CREDIT_CARD", CARTAO_CREDITO),
            Map.entry("DEBIT_CARD", CARTAO_DEBITO),
            Map.entry("BANK_TRANSFER", TRANSFERENCIA_BANCARIA),
            Map.entry("TRANSFERENCIA", TRANSFERENCIA_BANCARIA),
            Map.entry("PIX", PIX),
            Map.entry("DINHEIRO", DINHEIRO)
    );

    public static PaymentMethod fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Forma de pagamento desconhecida: null");
        }
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(text) ||
                    method.description.equalsIgnoreCase(text)) {
                return method;
            }
        }
        String normalized = normalize(text);
        if (ALIASES.containsKey(normalized)) {
            return ALIASES.get(normalized);
        }
        throw new IllegalArgumentException("Forma de pagamento desconhecida: " + text);
    }

    private static String normalize(String text) {
        if (text == null) {
            return null;
        }
        return text.trim().toUpperCase().replaceAll("[\\s-]+", "_");
    }
}
