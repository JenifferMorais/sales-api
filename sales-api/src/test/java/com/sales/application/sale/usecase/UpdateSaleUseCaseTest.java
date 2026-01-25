package com.sales.application.sale.usecase;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.port.ProductRepository;
import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateSaleUseCase Tests")
class UpdateSaleUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateSaleUseCase updateSaleUseCase;

    private Sale existingSale;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        existingSale = Sale.createCashSale(
                "SALE001",
                "CUST001",
                "John Silva",
                "SELLER001",
                "Vendedor Sistema",
                new BigDecimal("100.00")
        );
        existingSale.addItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));

        product1 = Product.create(
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

        product2 = Product.create(
                "PROD002",
                "Base Líquida",
                "Rosto",
                "Base líquida alta cobertura",
                new BigDecimal("0.120"),
                new BigDecimal("45.00"),
                new BigDecimal("89.00"),
                new BigDecimal("12.00"),
                new BigDecimal("4.00"),
                new BigDecimal("4.00"),
                "Pele mista a oleosa",
                150
        );
    }

    @Test
    @DisplayName("Should update sale successfully when sale exists")
    void shouldUpdateSaleSuccessfully() {

        List<SaleItem> newItems = new ArrayList<>();
        SaleItem newItem = new SaleItem("PROD001", "Batom Matte", 3, new BigDecimal("35.00"));
        newItems.add(newItem);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(product1));
        when(saleRepository.save(any(Sale.class))).thenReturn(existingSale);

        Sale result = updateSaleUseCase.execute(
                1L,
                "SELLER002",
                "Novo Vendedor",
                PaymentMethod.PIX,
                null,
                new BigDecimal("150.00"),
                newItems
        );

        assertThat(result).isNotNull();
        verify(saleRepository).findById(1L);
        verify(productRepository).findByCode("PROD001");
        verify(saleRepository).save(existingSale);
    }

    @Test
    @DisplayName("Should throw exception when sale not found")
    void shouldThrowExceptionWhenSaleNotFound() {

        when(saleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateSaleUseCase.execute(
                999L,
                "SELLER001",
                "Vendedor",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("100.00"),
                new ArrayList<>()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Venda não encontrada com id: 999");

        verify(saleRepository).findById(999L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {

        List<SaleItem> newItems = new ArrayList<>();
        SaleItem newItem = new SaleItem("NONEXISTENT", "Invalid", 1, new BigDecimal("10.00"));
        newItems.add(newItem);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        when(productRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateSaleUseCase.execute(
                1L,
                "SELLER001",
                "Vendedor",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("100.00"),
                newItems
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produto não encontrado com código: NONEXISTENT");

        verify(saleRepository).findById(1L);
        verify(productRepository).findByCode("NONEXISTENT");
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should validate all products before updating sale")
    void shouldValidateAllProductsBeforeUpdate() {

        List<SaleItem> newItems = new ArrayList<>();

        SaleItem item1 = new SaleItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));
        newItems.add(item1);

        SaleItem item2 = new SaleItem("PROD002", "Base Líquida", 1, new BigDecimal("89.00"));
        newItems.add(item2);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(product1));
        when(productRepository.findByCode("PROD002")).thenReturn(Optional.of(product2));
        when(saleRepository.save(any(Sale.class))).thenReturn(existingSale);

        Sale result = updateSaleUseCase.execute(
                1L,
                "SELLER001",
                "Vendedor",
                PaymentMethod.DINHEIRO,
                null,
                new BigDecimal("200.00"),
                newItems
        );

        assertThat(result).isNotNull();
        verify(productRepository).findByCode("PROD001");
        verify(productRepository).findByCode("PROD002");
    }

    @Test
    @DisplayName("Should update sale payment method successfully")
    void shouldUpdateSalePaymentMethod() {

        List<SaleItem> items = new ArrayList<>();
        SaleItem item = new SaleItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));
        items.add(item);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(product1));
        when(saleRepository.save(any(Sale.class))).thenReturn(existingSale);

        Sale result = updateSaleUseCase.execute(
                1L,
                "SELLER001",
                "Vendedor Sistema",
                PaymentMethod.CARTAO_CREDITO,
                "1234",
                null,
                items
        );

        assertThat(result).isNotNull();
        verify(saleRepository).save(existingSale);
    }

    @Test
    @DisplayName("Should verify repository interactions in correct order")
    void shouldVerifyRepositoryInteractionsInOrder() {

        List<SaleItem> items = new ArrayList<>();
        SaleItem item = new SaleItem("PROD001", "Batom Matte", 2, new BigDecimal("35.00"));
        items.add(item);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(existingSale));
        when(productRepository.findByCode("PROD001")).thenReturn(Optional.of(product1));
        when(saleRepository.save(any(Sale.class))).thenReturn(existingSale);

        updateSaleUseCase.execute(
                1L,
                "SELLER001",
                "Vendedor",
                PaymentMethod.PIX,
                null,
                null,
                items
        );

        var inOrder = inOrder(saleRepository, productRepository);
        inOrder.verify(saleRepository).findById(1L);
        inOrder.verify(productRepository).findByCode("PROD001");
        inOrder.verify(saleRepository).save(existingSale);
    }
}
