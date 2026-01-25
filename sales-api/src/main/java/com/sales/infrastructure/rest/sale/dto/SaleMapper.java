package com.sales.infrastructure.rest.sale.dto;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.valueobject.PaymentMethod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.stream.Collectors;

@ApplicationScoped
public class SaleMapper {

    @Inject
    CustomerRepository customerRepository;

    @Inject
    ProductRepository productRepository;

    public Sale toDomain(SaleRequest request) {
        Customer customer = customerRepository.findByCode(request.getCustomerCode())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Sale sale = new Sale(
                request.getCode(),
                request.getCustomerCode(),
                customer.getFullName(),
                request.getSellerCode(),
                request.getSellerName(),
                PaymentMethod.fromString(request.getPaymentMethod()),
                request.getCardNumber(),
                request.getAmountPaid()
        );

        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findByCode(itemRequest.getProductCode())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductCode()));

            SaleItem item = new SaleItem(
                    product.getCode(),
                    product.getName(),
                    itemRequest.getQuantity(),
                    product.getSalePrice()
            );
            sale.addItem(item);
        }

        return sale;
    }

    public SaleResponse toResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setId(sale.getId());
        response.setCode(sale.getCode());
        response.setCustomerCode(sale.getCustomerCode());
        response.setCustomerName(sale.getCustomerName());
        response.setSellerCode(sale.getSellerCode());
        response.setSellerName(sale.getSellerName());
        response.setPaymentMethod(sale.getPaymentMethod().getDescription());
        response.setCardNumber(sale.getMaskedCardNumber());
        response.setAmountPaid(sale.getAmountPaid());
        response.setSubtotal(sale.getSubtotal());
        response.setTaxAmount(sale.getTaxAmount());
        response.setTotalAmount(sale.getTotalAmount());
        response.setChange(sale.getChange());
        response.setCreatedAt(sale.getCreatedAt());
        response.setItems(sale.getItems().stream()
                .map(item -> new SaleResponse.SaleItemResponse(
                        item.getProductCode(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList()));
        return response;
    }

    public Product findProductByCode(String productCode) {
        return productRepository.findByCode(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + productCode));
    }
}
