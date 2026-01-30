package com.sales.infrastructure.persistence.sale.repository;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.domain.shared.exception.EncryptionException;
import com.sales.domain.shared.port.EncryptionService;
import com.sales.infrastructure.persistence.sale.entity.SaleEntity;
import com.sales.infrastructure.persistence.sale.entity.SaleItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleRepositoryAdapter - Encryption")
class SaleRepositoryAdapterEncryptionTest {

    @Mock
    private SalePanacheRepository panacheRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private SaleRepositoryAdapter repositoryAdapter;

    private Sale testSale;
    private SaleEntity testEntity;
    private static final String CARD_NUMBER = "4532015112830366";
    private static final String ENCRYPTED_CARD = "encrypted_card_base64";
    private static final String MALFORMED_CARD = "1234****5678";

    @BeforeEach
    void setUp() {
        List<SaleItem> items = new ArrayList<>();
        items.add(new SaleItem(null, "PROD001", "Produto 1", 2, new BigDecimal("50.00")));

        testSale = new Sale(
            null,
            "SALE001",
            "CUST001",
            "Cliente Teste",
            "SELLER001",
            "Vendedor Teste",
            PaymentMethod.CARTAO_CREDITO,
            CARD_NUMBER,
            new BigDecimal("100.00"),
            items,
            null
        );

        testEntity = new SaleEntity();
        testEntity.setId(1L);
        testEntity.setCode("SALE001");
        testEntity.setCustomerCode("CUST001");
        testEntity.setCustomerName("Cliente Teste");
        testEntity.setSellerCode("SELLER001");
        testEntity.setSellerName("Vendedor Teste");
        testEntity.setPaymentMethod("CARTAO_CREDITO");
        testEntity.setCardNumber(ENCRYPTED_CARD);
        testEntity.setAmountPaid(new BigDecimal("100.00"));
        testEntity.setCreatedAt(LocalDateTime.now());

        SaleItemEntity itemEntity = new SaleItemEntity();
        itemEntity.setId(1L);
        itemEntity.setProductCode("PROD001");
        itemEntity.setProductName("Produto 1");
        itemEntity.setQuantity(2);
        itemEntity.setUnitPrice(new BigDecimal("50.00"));
        testEntity.addItem(itemEntity);
    }

    @Nested
    @DisplayName("Criptografia ao Salvar")
    class EncryptOnSave {

        @Test
        @DisplayName("Deve criptografar cardNumber ao criar nova venda")
        void shouldEncryptCardNumberWhenCreatingNewSale() {
            when(encryptionService.encrypt(CARD_NUMBER)).thenReturn(ENCRYPTED_CARD);
            when(encryptionService.decrypt(ENCRYPTED_CARD)).thenReturn(CARD_NUMBER);
            doNothing().when(panacheRepository).persist(any(SaleEntity.class));

            Sale result = repositoryAdapter.save(testSale);

            verify(encryptionService, times(1)).encrypt(CARD_NUMBER);
            verify(panacheRepository, times(1)).persist(any(SaleEntity.class));
        }

        @Test
        @DisplayName("Deve criptografar cardNumber ao atualizar venda existente")
        void shouldEncryptCardNumberWhenUpdatingSale() {
            testSale = new Sale(
                1L, "SALE001", "CUST001", "Cliente Teste",
                "SELLER001", "Vendedor Teste",
                PaymentMethod.CARTAO_CREDITO, CARD_NUMBER,
                new BigDecimal("100.00"), testSale.getItems(),
                LocalDateTime.now()
            );

            when(panacheRepository.findById(1L)).thenReturn(testEntity);
            when(encryptionService.encrypt(CARD_NUMBER)).thenReturn(ENCRYPTED_CARD);
            when(encryptionService.decrypt(ENCRYPTED_CARD)).thenReturn(CARD_NUMBER);

            Sale result = repositoryAdapter.save(testSale);

            verify(encryptionService, times(1)).encrypt(CARD_NUMBER);
            verify(panacheRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Não deve criptografar quando cardNumber é null")
        void shouldNotEncryptWhenCardNumberIsNull() {
            testSale = new Sale(
                null, "SALE002", "CUST001", "Cliente Teste",
                "SELLER001", "Vendedor Teste",
                PaymentMethod.DINHEIRO, null,
                new BigDecimal("100.00"), testSale.getItems(),
                null
            );

            doNothing().when(panacheRepository).persist(any(SaleEntity.class));

            Sale result = repositoryAdapter.save(testSale);

            verify(encryptionService, never()).encrypt(anyString());
        }

        @Test
        @DisplayName("Não deve criptografar quando cardNumber é vazio")
        void shouldNotEncryptWhenCardNumberIsEmpty() {
            testSale = new Sale(
                null, "SALE002", "CUST001", "Cliente Teste",
                "SELLER001", "Vendedor Teste",
                PaymentMethod.DINHEIRO, "",
                new BigDecimal("100.00"), testSale.getItems(),
                null
            );

            doNothing().when(panacheRepository).persist(any(SaleEntity.class));

            Sale result = repositoryAdapter.save(testSale);

            verify(encryptionService, never()).encrypt(anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando criptografia falha")
        void shouldThrowExceptionWhenEncryptionFails() {
            when(encryptionService.encrypt(CARD_NUMBER))
                .thenThrow(new EncryptionException("Erro ao criptografar"));

            assertThrows(IllegalStateException.class, () -> {
                repositoryAdapter.save(testSale);
            });
        }
    }

    @Nested
    @DisplayName("Descriptografia ao Carregar")
    class DecryptOnLoad {

        @Test
        @DisplayName("Deve descriptografar cardNumber ao buscar por ID")
        void shouldDecryptCardNumberWhenFindingById() {
            when(panacheRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testEntity));
            when(encryptionService.decrypt(ENCRYPTED_CARD))
                .thenReturn(CARD_NUMBER);

            Optional<Sale> result = repositoryAdapter.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(CARD_NUMBER, result.get().getCardNumber());
            verify(encryptionService, times(1)).decrypt(ENCRYPTED_CARD);
        }

        @Test
        @DisplayName("Deve descriptografar cardNumber ao buscar por código")
        void shouldDecryptCardNumberWhenFindingByCode() {
            when(panacheRepository.findByCode("SALE001"))
                .thenReturn(Optional.of(testEntity));
            when(encryptionService.decrypt(ENCRYPTED_CARD))
                .thenReturn(CARD_NUMBER);

            Optional<Sale> result = repositoryAdapter.findByCode("SALE001");

            assertTrue(result.isPresent());
            assertEquals(CARD_NUMBER, result.get().getCardNumber());
            verify(encryptionService, times(1)).decrypt(ENCRYPTED_CARD);
        }

        @Test
        @DisplayName("Deve descriptografar em findAll")
        void shouldDecryptInFindAll() {
            when(panacheRepository.listAll())
                .thenReturn(List.of(testEntity));
            when(encryptionService.decrypt(ENCRYPTED_CARD))
                .thenReturn(CARD_NUMBER);

            List<Sale> results = repositoryAdapter.findAll();

            assertEquals(1, results.size());
            assertEquals(CARD_NUMBER, results.get(0).getCardNumber());
            verify(encryptionService, times(1)).decrypt(ENCRYPTED_CARD);
        }

        @Test
        @DisplayName("Não deve descriptografar quando cardNumber é null")
        void shouldNotDecryptWhenCardNumberIsNull() {
            testEntity.setPaymentMethod("DINHEIRO");
            testEntity.setCardNumber(null);

            when(panacheRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testEntity));

            Optional<Sale> result = repositoryAdapter.findById(1L);

            assertTrue(result.isPresent());
            assertNull(result.get().getCardNumber());
            verify(encryptionService, never()).decrypt(anyString());
        }

        @Test
        @DisplayName("Não deve descriptografar quando cardNumber é vazio")
        void shouldNotDecryptWhenCardNumberIsEmpty() {
            testEntity.setPaymentMethod("DINHEIRO");
            testEntity.setCardNumber("");

            when(panacheRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testEntity));

            Optional<Sale> result = repositoryAdapter.findById(1L);

            assertTrue(result.isPresent());
            assertNull(result.get().getCardNumber());
            verify(encryptionService, never()).decrypt(anyString());
        }

        @Test
        @DisplayName("Deve retornar valor bruto quando cardNumber armazenado é inválido")
        void shouldReturnStoredValueWhenCardNumberMalformed() {
            testEntity.setCardNumber(MALFORMED_CARD);

            when(panacheRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testEntity));
            when(encryptionService.decrypt(MALFORMED_CARD))
                .thenThrow(new EncryptionException("Dados criptografados inválidos (Base64 malformado)",
                    new IllegalArgumentException("Input byte array has wrong 4-byte ending at 3")));

            Optional<Sale> result = repositoryAdapter.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(MALFORMED_CARD, result.get().getCardNumber());
            verify(encryptionService, times(1)).decrypt(MALFORMED_CARD);
        }

        @Test
        @DisplayName("Deve lançar exceção quando descriptografia falha")
        void shouldThrowExceptionWhenDecryptionFails() {
            when(panacheRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testEntity));
            when(encryptionService.decrypt(ENCRYPTED_CARD))
                .thenThrow(new EncryptionException("Erro ao descriptografar"));

            assertThrows(IllegalStateException.class, () -> {
                repositoryAdapter.findById(1L);
            });
        }
    }

    @Nested
    @DisplayName("Round-trip de Criptografia")
    class EncryptionRoundTrip {

        @Test
        @DisplayName("Deve preservar cardNumber após salvar e carregar")
        void shouldPreserveCardNumberAfterSaveAndLoad() {
            when(encryptionService.encrypt(CARD_NUMBER)).thenReturn(ENCRYPTED_CARD);
            when(encryptionService.decrypt(ENCRYPTED_CARD)).thenReturn(CARD_NUMBER);
            when(panacheRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testEntity));

            doAnswer(invocation -> {
                SaleEntity entity = invocation.getArgument(0);
                entity.setId(1L);
                return null;
            }).when(panacheRepository).persist(any(SaleEntity.class));

            Sale saved = repositoryAdapter.save(testSale);
            Optional<Sale> loaded = repositoryAdapter.findById(1L);

            assertTrue(loaded.isPresent());
            assertEquals(CARD_NUMBER, loaded.get().getCardNumber());
        }

        @Test
        @DisplayName("Deve lidar com atualização de cardNumber")
        void shouldHandleCardNumberUpdate() {
            String newCardNumber = "5425233430109903";
            String newEncrypted = "new_encrypted_card_base64";

            testSale = new Sale(
                1L, "SALE001", "CUST001", "Cliente Teste",
                "SELLER001", "Vendedor Teste",
                PaymentMethod.CARTAO_CREDITO, newCardNumber,
                new BigDecimal("100.00"), testSale.getItems(),
                LocalDateTime.now()
            );

            when(panacheRepository.findById(1L)).thenReturn(testEntity);
            when(encryptionService.encrypt(newCardNumber)).thenReturn(newEncrypted);
            when(encryptionService.decrypt(newEncrypted)).thenReturn(newCardNumber);

            Sale result = repositoryAdapter.save(testSale);

            verify(encryptionService, times(1)).encrypt(newCardNumber);
            verify(encryptionService, times(1)).decrypt(newEncrypted);
        }
    }

    @Nested
    @DisplayName("Casos Especiais")
    class EdgeCases {

        @Test
        @DisplayName("Deve lidar com múltiplas vendas com diferentes cardNumbers")
        void shouldHandleMultipleSalesWithDifferentCards() {
            SaleEntity entity2 = new SaleEntity();
            entity2.setId(2L);
            entity2.setCode("SALE002");
            entity2.setCustomerCode("CUST002");
            entity2.setCustomerName("Cliente 2");
            entity2.setSellerCode("SELLER001");
            entity2.setSellerName("Vendedor Teste");
            entity2.setPaymentMethod("CARTAO_CREDITO");
            entity2.setCardNumber("encrypted_card_2");
            entity2.setAmountPaid(new BigDecimal("200.00"));
            entity2.setCreatedAt(LocalDateTime.now());

            when(panacheRepository.listAll())
                .thenReturn(List.of(testEntity, entity2));
            when(encryptionService.decrypt(ENCRYPTED_CARD))
                .thenReturn(CARD_NUMBER);
            when(encryptionService.decrypt("encrypted_card_2"))
                .thenReturn("5425233430109903");

            List<Sale> results = repositoryAdapter.findAll();

            assertEquals(2, results.size());
            assertEquals(CARD_NUMBER, results.get(0).getCardNumber());
            assertEquals("5425233430109903", results.get(1).getCardNumber());
            verify(encryptionService, times(2)).decrypt(anyString());
        }

        @Test
        @DisplayName("Deve lidar com venda mista (com e sem cartão)")
        void shouldHandleMixedSales() {
            SaleEntity cashEntity = new SaleEntity();
            cashEntity.setId(2L);
            cashEntity.setCode("SALE002");
            cashEntity.setCustomerCode("CUST002");
            cashEntity.setCustomerName("Cliente 2");
            cashEntity.setSellerCode("SELLER001");
            cashEntity.setSellerName("Vendedor Teste");
            cashEntity.setPaymentMethod("DINHEIRO");
            cashEntity.setCardNumber(null);
            cashEntity.setAmountPaid(new BigDecimal("200.00"));
            cashEntity.setCreatedAt(LocalDateTime.now());

            when(panacheRepository.listAll())
                .thenReturn(List.of(testEntity, cashEntity));
            when(encryptionService.decrypt(ENCRYPTED_CARD))
                .thenReturn(CARD_NUMBER);

            List<Sale> results = repositoryAdapter.findAll();

            assertEquals(2, results.size());
            assertEquals(CARD_NUMBER, results.get(0).getCardNumber());
            assertNull(results.get(1).getCardNumber());
            verify(encryptionService, times(1)).decrypt(anyString());
        }
    }
}
