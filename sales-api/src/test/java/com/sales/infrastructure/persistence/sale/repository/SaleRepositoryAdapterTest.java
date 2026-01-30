package com.sales.infrastructure.persistence.sale.repository;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.domain.shared.PageResult;
import com.sales.domain.shared.port.EncryptionService;
import com.sales.infrastructure.persistence.sale.entity.SaleEntity;
import com.sales.infrastructure.persistence.sale.entity.SaleItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleRepositoryAdapter Tests")
class SaleRepositoryAdapterTest {

    @Mock
    private SalePanacheRepository panacheRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private SaleRepositoryAdapter repositoryAdapter;

    private Sale testSale;
    private SaleEntity testEntity;
    private List<SaleItem> testItems;

    @BeforeEach
    void setUp() {
        testItems = Arrays.asList(
                new SaleItem(null, "PROD001", "Product 1", 2, BigDecimal.valueOf(50.00)),
                new SaleItem(null, "PROD002", "Product 2", 1, BigDecimal.valueOf(100.00))
        );

        testSale = new Sale(
                null,
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Vendedora",
                PaymentMethod.CARTAO_CREDITO,
                "1234****5678",
                BigDecimal.valueOf(200.00),
                testItems,
                null
        );

        testEntity = createSaleEntity(1L, "SALE001");

        // Mock encryption service (lenient to avoid unnecessary stubbing warnings)
        lenient().when(encryptionService.encrypt(anyString())).thenAnswer(invocation -> "encrypted_" + invocation.getArgument(0));
        lenient().when(encryptionService.decrypt(anyString())).thenAnswer(invocation -> {
            String encrypted = invocation.getArgument(0);
            return encrypted.startsWith("encrypted_") ? encrypted.substring(10) : encrypted;
        });
    }

    @Test
    @DisplayName("Should save new sale")
    void shouldSaveNewSale() {
        doNothing().when(panacheRepository).persist(any(SaleEntity.class));

        Sale result = repositoryAdapter.save(testSale);

        verify(panacheRepository).persist(any(SaleEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should update existing sale")
    void shouldUpdateExistingSale() {
        Sale existingSale = new Sale(
                1L,
                "SALE001",
                "CUST002",
                "Pedro Santos",
                "SELLER002",
                "Ana Vendedora",
                PaymentMethod.CARTAO_DEBITO,
                "9876****4321",
                BigDecimal.valueOf(300.00),
                testItems,
                LocalDateTime.now()
        );

        when(panacheRepository.findById(1L)).thenReturn(testEntity);

        Sale result = repositoryAdapter.save(existingSale);

        verify(panacheRepository).findById(1L);
        verify(panacheRepository, never()).persist(any(SaleEntity.class));
        assertThat(result).isNotNull();
        assertThat(testEntity.getCustomerCode()).isEqualTo("CUST002");
        assertThat(testEntity.getCustomerName()).isEqualTo("Pedro Santos");
        assertThat(testEntity.getPaymentMethod()).isEqualTo("CARTAO_DEBITO");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent sale")
    void shouldThrowExceptionWhenUpdatingNonExistentSale() {
        Sale saleWithId = new Sale(
                999L,
                "SALE999",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Vendedora",
                PaymentMethod.CARTAO_CREDITO,
                "1234****5678",
                BigDecimal.valueOf(200.00),
                testItems,
                LocalDateTime.now()
        );

        when(panacheRepository.findById(999L)).thenReturn(null);

        assertThatThrownBy(() -> repositoryAdapter.save(saleWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Venda não encontrada com id: 999");

        verify(panacheRepository).findById(999L);
        verify(panacheRepository, never()).persist(any(SaleEntity.class));
    }

    @Test
    @DisplayName("Should find sale by ID")
    void shouldFindSaleById() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Sale> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCode()).isEqualTo("SALE001");
        verify(panacheRepository).findByIdOptional(1L);
    }

    @Test
    @DisplayName("Should return empty when sale not found by ID")
    void shouldReturnEmptyWhenSaleNotFoundById() {
        when(panacheRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Optional<Sale> result = repositoryAdapter.findById(999L);

        assertThat(result).isEmpty();
        verify(panacheRepository).findByIdOptional(999L);
    }

    @Test
    @DisplayName("Should find sale by code")
    void shouldFindSaleByCode() {
        when(panacheRepository.findByCode("SALE001")).thenReturn(Optional.of(testEntity));

        Optional<Sale> result = repositoryAdapter.findByCode("SALE001");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("SALE001");
        verify(panacheRepository).findByCode("SALE001");
    }

    @Test
    @DisplayName("Should find all sales")
    void shouldFindAllSales() {
        SaleEntity entity2 = createSaleEntity(2L, "SALE002");
        when(panacheRepository.listAll()).thenReturn(Arrays.asList(testEntity, entity2));

        List<Sale> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("SALE001");
        assertThat(result.get(1).getCode()).isEqualTo("SALE002");
        verify(panacheRepository).listAll();
    }

    @Test
    @DisplayName("Should find sales by customer code")
    void shouldFindSalesByCustomerCode() {
        when(panacheRepository.findByCustomerCode("CUST001")).thenReturn(Arrays.asList(testEntity));

        List<Sale> result = repositoryAdapter.findByCustomerCode("CUST001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerCode()).isEqualTo("CUST001");
        verify(panacheRepository).findByCustomerCode("CUST001");
    }

    @Test
    @DisplayName("Should find sales by seller code")
    void shouldFindSalesBySellerCode() {
        when(panacheRepository.findBySellerCode("SELLER001")).thenReturn(Arrays.asList(testEntity));

        List<Sale> result = repositoryAdapter.findBySellerCode("SELLER001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSellerCode()).isEqualTo("SELLER001");
        verify(panacheRepository).findBySellerCode("SELLER001");
    }

    @Test
    @DisplayName("Should find sales by payment method")
    void shouldFindSalesByPaymentMethod() {
        when(panacheRepository.findByPaymentMethod("CARTAO_CREDITO")).thenReturn(Arrays.asList(testEntity));

        List<Sale> result = repositoryAdapter.findByPaymentMethod(PaymentMethod.CARTAO_CREDITO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPaymentMethod()).isEqualTo(PaymentMethod.CARTAO_CREDITO);
        verify(panacheRepository).findByPaymentMethod("CARTAO_CREDITO");
    }

    @Test
    @DisplayName("Should find sales by date range")
    void shouldFindSalesByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        when(panacheRepository.findByDateRange(start, end)).thenReturn(Arrays.asList(testEntity));

        List<Sale> result = repositoryAdapter.findByDateRange(start, end);

        assertThat(result).hasSize(1);
        verify(panacheRepository).findByDateRange(start, end);
    }

    @Test
    @DisplayName("Should delete sale by ID")
    void shouldDeleteSaleById() {
        when(panacheRepository.deleteById(1L)).thenReturn(true);

        repositoryAdapter.deleteById(1L);

        verify(panacheRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if sale exists by code")
    void shouldCheckIfSaleExistsByCode() {
        when(panacheRepository.existsByCode("SALE001")).thenReturn(true);

        boolean result = repositoryAdapter.existsByCode("SALE001");

        assertThat(result).isTrue();
        verify(panacheRepository).existsByCode("SALE001");
    }

    @Test
    @DisplayName("Should search sales with pagination")
    void shouldSearchSalesWithPagination() {
        SaleEntity entity2 = createSaleEntity(2L, "SALE002");
        when(panacheRepository.search("Silva", 0, 10)).thenReturn(Arrays.asList(testEntity, entity2));
        when(panacheRepository.countSearch("Silva")).thenReturn(2L);

        PageResult<Sale> result = repositoryAdapter.search("Silva", 0, 10);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        verify(panacheRepository).search("Silva", 0, 10);
        verify(panacheRepository).countSearch("Silva");
    }

    @Test
    @DisplayName("Should convert entity to domain with items")
    void shouldConvertEntityToDomainWithItems() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Sale> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        Sale sale = result.get();
        assertThat(sale.getItems()).hasSize(2);
        assertThat(sale.getItems().get(0).getProductCode()).isEqualTo("PROD001");
        assertThat(sale.getItems().get(1).getProductCode()).isEqualTo("PROD002");
    }

    @Test
    @DisplayName("Should update sale items correctly")
    void shouldUpdateSaleItemsCorrectly() {
        List<SaleItem> newItems = Arrays.asList(
                new SaleItem(null, "PROD003", "Product 3", 3, BigDecimal.valueOf(75.00))
        );

        Sale updatedSale = new Sale(
                1L,
                "SALE001",
                "CUST001",
                "João Silva",
                "SELLER001",
                "Maria Vendedora",
                PaymentMethod.CARTAO_CREDITO,
                "1234****5678",
                BigDecimal.valueOf(225.00),
                newItems,
                LocalDateTime.now()
        );

        when(panacheRepository.findById(1L)).thenReturn(testEntity);

        repositoryAdapter.save(updatedSale);

        assertThat(testEntity.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should convert payment method correctly")
    void shouldConvertPaymentMethodCorrectly() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Sale> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPaymentMethod()).isEqualTo(PaymentMethod.CARTAO_CREDITO);
    }

    @Test
    @DisplayName("Should convert entity to domain correctly")
    void shouldConvertEntityToDomainCorrectly() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Sale> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        Sale sale = result.get();
        assertThat(sale.getId()).isEqualTo(testEntity.getId());
        assertThat(sale.getCode()).isEqualTo(testEntity.getCode());
        assertThat(sale.getCustomerCode()).isEqualTo(testEntity.getCustomerCode());
        assertThat(sale.getCustomerName()).isEqualTo(testEntity.getCustomerName());
        assertThat(sale.getSellerCode()).isEqualTo(testEntity.getSellerCode());
        assertThat(sale.getSellerName()).isEqualTo(testEntity.getSellerName());
        assertThat(sale.getPaymentMethod().name()).isEqualTo(testEntity.getPaymentMethod());
        assertThat(sale.getCardNumber()).isEqualTo(testEntity.getCardNumber());
        assertThat(sale.getAmountPaid()).isEqualByComparingTo(testEntity.getAmountPaid());
    }

    // Helper method para criar SaleEntity
    private SaleEntity createSaleEntity(Long id, String code) {
        SaleEntity entity = new SaleEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setCustomerCode("CUST001");
        entity.setCustomerName("João Silva");
        entity.setSellerCode("SELLER001");
        entity.setSellerName("Maria Vendedora");
        entity.setPaymentMethod("CARTAO_CREDITO");
        entity.setCardNumber("1234****5678");
        entity.setAmountPaid(BigDecimal.valueOf(200.00));
        entity.setCreatedAt(LocalDateTime.now());

        SaleItemEntity item1 = new SaleItemEntity();
        item1.setProductCode("PROD001");
        item1.setProductName("Product 1");
        item1.setQuantity(2);
        item1.setUnitPrice(BigDecimal.valueOf(50.00));

        SaleItemEntity item2 = new SaleItemEntity();
        item2.setProductCode("PROD002");
        item2.setProductName("Product 2");
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(100.00));

        entity.addItem(item1);
        entity.addItem(item2);

        return entity;
    }
}
