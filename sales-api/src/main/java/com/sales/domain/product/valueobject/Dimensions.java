package com.sales.domain.product.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class Dimensions {
    private final BigDecimal height;
    private final BigDecimal width;
    private final BigDecimal depth;

    public Dimensions(BigDecimal height, BigDecimal width, BigDecimal depth) {
        this.height = validateDimension(height, "Altura");
        this.width = validateDimension(width, "Largura");
        this.depth = validateDimension(depth, "Profundidade");
    }

    public static Dimensions create(BigDecimal height, BigDecimal width, BigDecimal depth) {
        return new Dimensions(height, width, depth);
    }

    private BigDecimal validateDimension(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero");
        }
        return value;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public BigDecimal getDepth() {
        return depth;
    }

    public BigDecimal getVolume() {
        return height.multiply(width).multiply(depth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimensions that = (Dimensions) o;
        return Objects.equals(height, that.height) &&
               Objects.equals(width, that.width) &&
               Objects.equals(depth, that.depth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width, depth);
    }

    @Override
    public String toString() {
        return String.format("%.2f x %.2f x %.2f cm", height, width, depth);
    }
}
