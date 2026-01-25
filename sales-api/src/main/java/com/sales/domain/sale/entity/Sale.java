package com.sales.domain.sale.entity;

import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.domain.shared.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Sale extends Entity {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.09");

    private String customerCode;
    private String customerName;
    private String sellerCode;
    private String sellerName;
    private PaymentMethod paymentMethod;
    private String cardNumber;
    private BigDecimal amountPaid;
    private List<SaleItem> items;

    public Sale(String code, String customerCode, String customerName, String sellerCode, String sellerName,
               PaymentMethod paymentMethod, String cardNumber, BigDecimal amountPaid) {
        super();
        this.code = validateCode(code);
        this.customerCode = validateNotEmpty(customerCode, "Código do cliente não pode estar vazio");
        this.customerName = validateNotEmpty(customerName, "Nome do cliente não pode estar vazio");
        this.sellerCode = validateNotEmpty(sellerCode, "Código do vendedor não pode estar vazio");
        this.sellerName = validateNotEmpty(sellerName, "Nome do vendedor não pode estar vazio");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Forma de pagamento não pode ser nula");
        this.cardNumber = validateCardNumber(paymentMethod, cardNumber);
        this.amountPaid = validateAmountPaid(amountPaid);
        this.items = new ArrayList<>();
    }

    public Sale(Long id, String code, String customerCode, String customerName, String sellerCode, String sellerName,
               PaymentMethod paymentMethod, String cardNumber, BigDecimal amountPaid,
               List<SaleItem> items, LocalDateTime createdAt) {
        super(id, code, createdAt);
        this.customerCode = validateNotEmpty(customerCode, "Código do cliente não pode estar vazio");
        this.customerName = validateNotEmpty(customerName, "Nome do cliente não pode estar vazio");
        this.sellerCode = validateNotEmpty(sellerCode, "Código do vendedor não pode estar vazio");
        this.sellerName = validateNotEmpty(sellerName, "Nome do vendedor não pode estar vazio");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Forma de pagamento não pode ser nula");
        this.cardNumber = validateCardNumber(paymentMethod, cardNumber);
        this.amountPaid = validateAmountPaid(amountPaid);
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    private String validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Código não pode estar vazio");
        }
        return code;
    }

    private String validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private String validateCardNumber(PaymentMethod paymentMethod, String cardNumber) {
        if (paymentMethod == PaymentMethod.CARTAO_CREDITO || paymentMethod == PaymentMethod.CARTAO_DEBITO) {
            if (cardNumber == null || cardNumber.isBlank()) {
                throw new IllegalArgumentException("Número do cartão é obrigatório para pagamento com cartão");
            }
        }
        return cardNumber;
    }

    private BigDecimal validateAmountPaid(BigDecimal amountPaid) {
        if (amountPaid != null && amountPaid.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor pago não pode ser negativo");
        }
        return amountPaid;
    }

    public void addItem(SaleItem item) {
        Objects.requireNonNull(item, "Item não pode ser nulo");
        this.items.add(item);
    }

    public void addItem(String productCode, String productName, int quantity, BigDecimal unitPrice) {
        SaleItem item = new SaleItem(productCode, productName, quantity, unitPrice);
        this.items.add(item);
    }

    public void removeItem(String productCode) {
        this.items.removeIf(item -> item.getProductCode().equals(productCode));
    }

    public BigDecimal getSubtotal() {
        return items.stream()
                .map(SaleItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTaxAmount() {
        return getSubtotal().multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalAmount() {
        return getSubtotal().add(getTaxAmount()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getChange() {
        if (amountPaid == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal change = amountPaid.subtract(getTotalAmount());
        return change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO;
    }

    public void validateSale() {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Venda deve ter pelo menos um item");
        }
        if (paymentMethod == PaymentMethod.DINHEIRO && amountPaid != null) {
            if (amountPaid.compareTo(getTotalAmount()) < 0) {
                throw new IllegalArgumentException("Valor pago é insuficiente");
            }
        }
    }

    public void update(String sellerCode, String sellerName, PaymentMethod paymentMethod,
                      String cardNumber, BigDecimal amountPaid, List<SaleItem> items) {
        this.sellerCode = validateNotEmpty(sellerCode, "Código do vendedor não pode estar vazio");
        this.sellerName = validateNotEmpty(sellerName, "Nome do vendedor não pode estar vazio");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "Forma de pagamento não pode ser nula");
        this.cardNumber = validateCardNumber(paymentMethod, cardNumber);
        this.amountPaid = validateAmountPaid(amountPaid);
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        validateSale();
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public String getSellerName() {
        return sellerName;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public List<SaleItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getTotalItems() {
        return items.stream().mapToInt(SaleItem::getQuantity).sum();
    }

    public static Sale createCashSale(String code, String customerCode, String customerName,
                                      String sellerCode, String sellerName, BigDecimal amountPaid) {
        return new Sale(code, customerCode, customerName, sellerCode, sellerName,
                PaymentMethod.DINHEIRO, null, amountPaid);
    }

    public static Sale createCreditCardSale(String code, String customerCode, String customerName,
                                            String sellerCode, String sellerName, String cardNumber, BigDecimal amountPaid) {
        return new Sale(code, customerCode, customerName, sellerCode, sellerName,
                PaymentMethod.CARTAO_CREDITO, cardNumber, amountPaid);
    }
}
