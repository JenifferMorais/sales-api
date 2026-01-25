package com.sales.domain.product.valueobject;

public enum ProductType {
    LIPS("Lábios"),
    FACE("Rosto"),
    EYES("Olhos"),
    NAILS("Unhas"),
    SKIN_CARE("Cuidados com a Pele"),
    HAIR("Cabelos"),
    FRAGRANCE("Fragrância"),
    OTHER("Outro");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ProductType fromString(String text) {
        for (ProductType type : ProductType.values()) {
            if (type.name().equalsIgnoreCase(text) ||
                type.description.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de produto desconhecido: " + text);
    }

    public static ProductType fromDatabase(String text) {
        if (text == null || text.isBlank()) {
            return OTHER;
        }
        for (ProductType type : ProductType.values()) {
            if (type.name().equalsIgnoreCase(text) ||
                type.description.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return OTHER;
    }

    public static ProductType create(String text) {
        return fromString(text);
    }
}
