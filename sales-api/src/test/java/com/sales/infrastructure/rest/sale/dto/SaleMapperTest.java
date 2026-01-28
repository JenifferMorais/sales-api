package com.sales.infrastructure.rest.sale.dto;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.valueobject.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SaleMapperTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SaleMapper mapper;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Document document = new Document("12345678909", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "",
                "Centro", "São Paulo", "SP");
        customer = new Customer("CUST001", "João Silva", "Maria Silva",
                document, address, LocalDate.of(1990, 1, 1), "11987654321", "joao@example.com");

        Dimensions dimensions = new Dimensions(
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0));
        product = new Product("PROD001", "Batom Vermelho", ProductType.LIPS, "Batom matte",
                BigDecimal.valueOf(0.5), BigDecimal.valueOf(15.00), BigDecimal.valueOf(30.00),
                dimensions, "Sedex");
    }

    @Test
    void shouldMapRequestToDomain() {
        SaleRequest request = new SaleRequest();
        request.setCode("SALE001");
        request.setCustomerCode("CUST001");
        request.setSellerCode("SELLER001");
        request.setSellerName("Vendedor Sistema");
        request.setPaymentMethod("CARTAO_CREDITO");
        request.setCardNumber("1234567812345678");

        SaleItemRequest itemRequest = new SaleItemRequest();
        itemRequest.setProductCode("PROD001");
        itemRequest.setQuantity(2);
        request.setItems(List.of(itemRequest));

        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(customer));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(product));

        Sale sale = mapper.toDomain(request);

        assertThat(sale.getCode()).isEqualTo("SALE001");
        assertThat(sale.getCustomerCode()).isEqualTo("CUST001");
        assertThat(sale.getCustomerName()).isEqualTo("João Silva");
        assertThat(sale.getPaymentMethod()).isEqualTo(PaymentMethod.CARTAO_CREDITO);
        assertThat(sale.getItems()).hasSize(1);
        verify(customerRepository, times(1)).findByCode("CUST001");
        verify(productRepository, times(1)).findByCode("PROD001");
    }

    @Test
    void shouldFailWhenCustomerNotFound() {
        SaleRequest request = new SaleRequest();
        request.setCode("SALE001");
        request.setCustomerCode("INVALID");
        request.setSellerCode("SELLER001");
        request.setSellerName("Vendedor Sistema");
        request.setPaymentMethod("PIX");
        request.setItems(List.of());

        when(customerRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mapper.toDomain(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void shouldFailWhenProductNotFound() {
        SaleRequest request = new SaleRequest();
        request.setCode("SALE001");
        request.setCustomerCode("CUST001");
        request.setSellerCode("SELLER001");
        request.setSellerName("Vendedor Sistema");
        request.setPaymentMethod("PIX");

        SaleItemRequest itemRequest = new SaleItemRequest();
        itemRequest.setProductCode("INVALID");
        itemRequest.setQuantity(1);
        request.setItems(List.of(itemRequest));

        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(customer));
        when(productRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mapper.toDomain(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void shouldMapDomainToResponse() {
        Sale sale = new Sale("SALE001", "CUST001", "João Silva", "SELL001", "Vendedor A",
                PaymentMethod.CARTAO_CREDITO, "1234", BigDecimal.valueOf(60.00));
        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));
        sale.addItem(item);

        SaleResponse response = mapper.toResponse(sale);

        assertThat(response.getCode()).isEqualTo("SALE001");
        assertThat(response.getCustomerCode()).isEqualTo("CUST001");
        assertThat(response.getCustomerName()).isEqualTo("João Silva");
        assertThat(response.getPaymentMethod()).isEqualTo("Cartão de Crédito");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getProductCode()).isEqualTo("PROD001");
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldFindProductByCode() {
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(product));

        Product found = mapper.findProductByCode("PROD001");

        assertThat(found).isNotNull();
        assertThat(found.getCode()).isEqualTo("PROD001");
        verify(productRepository, times(1)).findByCode("PROD001");
    }

    @Test
    void shouldFailFindProductByCodeWhenNotFound() {
        when(productRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mapper.findProductByCode("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Produto não encontrado");
    }
}
