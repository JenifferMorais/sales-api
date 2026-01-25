package com.sales.domain.sale.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class SaleItem {
    private Long id;
    private String productCode;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;

    public SaleItem(String productCode, String productName, Integer quantity, BigDecimal unitPrice) {
        this.productCode = validateNotEmpty(productCode, "Código do produto não pode estar vazio");
        this.productName = validateNotEmpty(productName, "Nome do produto não pode estar vazio");
        this.quantity = validateQuantity(quantity);
        this.unitPrice = validatePrice(unitPrice);
    }

    public SaleItem(Long id, String productCode, String productName, Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.productCode = validateNotEmpty(productCode, "Código do produto não pode estar vazio");
        this.productName = validateNotEmpty(productName, "Nome do produto não pode estar vazio");
        this.quantity = validateQuantity(quantity);
        this.unitPrice = validatePrice(unitPrice);
    }

    private String validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private Integer validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        return quantity;
    }

    private BigDecimal validatePrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Preço não pode ser nulo");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        return price;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    public void updateQuantity(Integer newQuantity) {
        this.quantity = validateQuantity(newQuantity);
    }

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleItem saleItem = (SaleItem) o;
        return Objects.equals(productCode, saleItem.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode);
    }
}
