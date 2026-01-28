package com.sales.application.sale.usecase;

import com.sales.domain.customer.entity.Customer;
import com.sales.domain.customer.port.CustomerRepository;
import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateSaleUseCase Tests")
class CreateSaleUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateSaleUseCase createSaleUseCase;

    private Sale validSale;
    private Customer validCustomer;
    private Product validProduct;

    @BeforeEach
    void setUp() {

        Document document = Document.create("12345678909", "MG1234567");
        Address address = Address.create(
                "30130100",
                "Av. Afonso Pena",
                "1500",
                "Apt 101",
                "Centro",
                "Belo Horizonte",
                "MG"
        );
        validCustomer = Customer.create(
                "CUST001",
                "John Silva Santos",
                "Maria Silva",
                document,
                address,
                LocalDate.of(1990, 5, 15),
                "31987654321",
                "john.silva@email.com"
        );

        validProduct = Product.create(
                "PROD001",
                "Batom Matte",
                "Lábios",
                "Batom matte de longa duração",
                new BigDecimal("0.050"),
                new BigDecimal("18.00"),
                new BigDecimal("35.00"),
                new BigDecimal("8.00"),
                new BigDecimal("2.00"),
                new BigDecimal("2.00"),
                "Todos os tipos de pele",
                100
        );

        validSale = Sale.createCashSale(
                "SALE001",
                "CUST001",
                "John Silva Santos",
                "SELLER001",
                "Vendedor Sistema",
                new BigDecimal("500.00")
        );
        validSale.addItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));
    }

    @Test
    @DisplayName("Should create sale successfully when all validations pass")
    void shouldCreateSaleSuccessfully() {

        when(saleRepository.existsByCode("SALE001")).thenReturn(false);
        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(validCustomer));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(validProduct));
        when(saleRepository.save(any(Sale.class))).thenReturn(validSale);

        Sale result = createSaleUseCase.execute(validSale);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("SALE001");
        verify(saleRepository).existsByCode("SALE001");
        verify(customerRepository).findByCode("CUST001");
        verify(productRepository).findByCode("PROD001");
        verify(saleRepository).save(validSale);
    }

    @Test
    @DisplayName("Should throw exception when sale code already exists")
    void shouldThrowExceptionWhenCodeAlreadyExists() {

        when(saleRepository.existsByCode("SALE001")).thenReturn(true);

        assertThatThrownBy(() -> createSaleUseCase.execute(validSale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Venda com código SALE001 já existe");

        verify(saleRepository).existsByCode("SALE001");
        verify(customerRepository, never()).findByCode(anyString());
        verify(productRepository, never()).findByCode(anyString());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(saleRepository.existsByCode("SALE001")).thenReturn(false);
        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createSaleUseCase.execute(validSale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente não encontrado com código: CUST001");

        verify(saleRepository).existsByCode("SALE001");
        verify(customerRepository).findByCode("CUST001");
        verify(productRepository, never()).findByCode(anyString());
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {

        when(saleRepository.existsByCode("SALE001")).thenReturn(false);
        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(validCustomer));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createSaleUseCase.execute(validSale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produto não encontrado com código: PROD001");

        verify(saleRepository).existsByCode("SALE001");
        verify(customerRepository).findByCode("CUST001");
        verify(productRepository).findByCode("PROD001");
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should validate sale with multiple items")
    void shouldValidateSaleWithMultipleItems() {

        validSale.addItem("PROD002", "Base Líquida", 1, new BigDecimal("89.00"));

        when(saleRepository.existsByCode("SALE001")).thenReturn(false);
        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(validCustomer));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(validProduct));
        when(productRepository.findByCode("PROD002")).thenReturn(Optional.of(validProduct));
        when(saleRepository.save(any(Sale.class))).thenReturn(validSale);

        Sale result = createSaleUseCase.execute(validSale);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        verify(productRepository).findByCode("PROD001");
        verify(productRepository).findByCode("PROD002");
    }

    @Test
    @DisplayName("Should throw exception when sale has no items")
    void shouldThrowExceptionWhenSaleHasNoItems() {

        Sale saleWithoutItems = Sale.createCashSale(
                "SALE002",
                "CUST001",
                "John Silva Santos",
                "SELLER001",
                "Vendedor Sistema",
                new BigDecimal("0.00")
        );

        when(saleRepository.existsByCode("SALE002")).thenReturn(false);
        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(validCustomer));

        assertThatThrownBy(() -> createSaleUseCase.execute(saleWithoutItems))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Venda deve ter pelo menos um item");

        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should create credit card sale successfully")
    void shouldCreateCreditCardSaleSuccessfully() {

        Sale creditCardSale = Sale.createCreditCardSale(
                "SALE002",
                "CUST001",
                "John Silva Santos",
                "SELLER001",
                "Vendedor Sistema",
                "1234",
                new BigDecimal("70.00")
        );
        creditCardSale.addItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));

        when(saleRepository.existsByCode("SALE002")).thenReturn(false);
        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(validCustomer));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(validProduct));
        when(saleRepository.save(any(Sale.class))).thenReturn(creditCardSale);

        Sale result = createSaleUseCase.execute(creditCardSale);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.CARTAO_CREDITO);
        verify(saleRepository).save(creditCardSale);
    }

    @Test
    @DisplayName("Should verify all validations are executed in correct order")
    void shouldVerifyValidationsOrder() {

        when(saleRepository.existsByCode("SALE001")).thenReturn(false);
        when(customerRepository.findByCode("CUST001")).thenReturn(Optional.of(validCustomer));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(validProduct));
        when(saleRepository.save(any(Sale.class))).thenReturn(validSale);

        createSaleUseCase.execute(validSale);

        var inOrder = inOrder(saleRepository, customerRepository, productRepository);
        inOrder.verify(saleRepository).existsByCode("SALE001");
        inOrder.verify(customerRepository).findByCode("CUST001");
        inOrder.verify(productRepository).findByCode("PROD001");
        inOrder.verify(saleRepository).save(validSale);
    }
}
