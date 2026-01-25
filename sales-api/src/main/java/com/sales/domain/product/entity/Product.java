package com.sales.domain.product.entity;

import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import com.sales.domain.shared.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Product extends Entity {
    private String name;
    private ProductType type;
    private String details;
    private BigDecimal weight;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Dimensions dimensions;
    private String destinationVehicle;
    private Integer stockQuantity;

    public Product(String code, String name, ProductType type, String details,
                  BigDecimal weight, BigDecimal purchasePrice, BigDecimal salePrice,
                  Dimensions dimensions, String destinationVehicle) {
        super();

        this.code = code;
        this.name = validateNotEmpty(name, "Nome do produto não pode estar vazio");
        this.type = Objects.requireNonNull(type, "Tipo do produto não pode ser nulo");
        this.details = details;
        this.weight = validatePositive(weight, "Peso");
        this.purchasePrice = validatePositive(purchasePrice, "Preço de compra");
        this.salePrice = validatePositive(salePrice, "Preço de venda");
        this.dimensions = Objects.requireNonNull(dimensions, "Dimensões não podem ser nulas");
        this.destinationVehicle = destinationVehicle;
        this.stockQuantity = 0;
        validatePrices();
    }

    public Product(Long id, String code, String name, ProductType type, String details,
                  BigDecimal weight, BigDecimal purchasePrice, BigDecimal salePrice,
                  Dimensions dimensions, String destinationVehicle, Integer stockQuantity,
                  LocalDateTime createdAt) {
        super(id, code, createdAt);
        this.name = validateNotEmpty(name, "Nome do produto não pode estar vazio");
        this.type = Objects.requireNonNull(type, "Tipo do produto não pode ser nulo");
        this.details = details;
        this.weight = validatePositive(weight, "Peso");
        this.purchasePrice = validatePositive(purchasePrice, "Preço de compra");
        this.salePrice = validatePositive(salePrice, "Preço de venda");
        this.dimensions = Objects.requireNonNull(dimensions, "Dimensões não podem ser nulas");
        this.destinationVehicle = destinationVehicle;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        validatePrices();
    }

    private String validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private BigDecimal validatePositive(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero");
        }
        return value;
    }

    private void validatePrices() {
        if (salePrice.compareTo(purchasePrice) < 0) {
            throw new IllegalArgumentException("Preço de venda não pode ser menor que o preço de compra");
        }
    }

    public void updateInfo(String name, ProductType type, String details, BigDecimal weight,
                          BigDecimal purchasePrice, BigDecimal salePrice, Dimensions dimensions,
                          String destinationVehicle) {
        this.name = validateNotEmpty(name, "Nome do produto não pode estar vazio");
        this.type = Objects.requireNonNull(type, "Tipo do produto não pode ser nulo");
        this.details = details;
        this.weight = validatePositive(weight, "Peso");
        this.purchasePrice = validatePositive(purchasePrice, "Preço de compra");
        this.salePrice = validatePositive(salePrice, "Preço de venda");
        this.dimensions = Objects.requireNonNull(dimensions, "Dimensões não podem ser nulas");
        this.destinationVehicle = destinationVehicle;
        validatePrices();
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }
        this.stockQuantity -= quantity;
    }

    public boolean hasStock(int quantity) {
        return this.stockQuantity >= quantity;
    }

    public BigDecimal getProfit() {
        return salePrice.subtract(purchasePrice);
    }

    public BigDecimal getProfitMargin() {
        return getProfit().divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public String getDestinationVehicle() {
        return destinationVehicle;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public static Product create(String code, String name, String type, String details,
                                 BigDecimal weight, BigDecimal purchasePrice, BigDecimal salePrice,
                                 BigDecimal width, BigDecimal height, BigDecimal depth,
                                 String destinationVehicle, int initialStock) {
        ProductType productType = ProductType.fromString(type);
        Dimensions dimensions = new Dimensions(width, height, depth);
        Product product = new Product(code, name, productType, details, weight, purchasePrice, salePrice,
                dimensions, destinationVehicle);
        if (initialStock > 0) {
            product.addStock(initialStock);
        }
        return product;
    }
}
