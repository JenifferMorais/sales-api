package com.sales.infrastructure.persistence.sale.repository;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.domain.shared.PageResult;
import com.sales.domain.shared.port.EncryptionService;
import com.sales.infrastructure.persistence.sale.entity.SaleEntity;
import com.sales.infrastructure.persistence.sale.entity.SaleItemEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SaleRepositoryAdapter implements SaleRepository {

    @Inject
    SalePanacheRepository panacheRepository;

    @Inject
    EncryptionService encryptionService;

    @Override
    @Transactional
    public Sale save(Sale sale) {
        SaleEntity entity;

        if (sale.getId() != null) {
            entity = panacheRepository.findById(sale.getId());
            if (entity == null) {
                throw new IllegalArgumentException("Venda não encontrada com id: " + sale.getId());
            }

            entity.setCustomerCode(sale.getCustomerCode());
            entity.setCustomerName(sale.getCustomerName());
            entity.setSellerCode(sale.getSellerCode());
            entity.setSellerName(sale.getSellerName());
            entity.setPaymentMethod(sale.getPaymentMethod().name());
            entity.setCardNumber(encryptCardNumber(sale.getCardNumber()));
            entity.setAmountPaid(sale.getAmountPaid());

            entity.getItems().clear();
            for (SaleItem item : sale.getItems()) {
                SaleItemEntity itemEntity = new SaleItemEntity();
                itemEntity.setProductCode(item.getProductCode());
                itemEntity.setProductName(item.getProductName());
                itemEntity.setQuantity(item.getQuantity());
                itemEntity.setUnitPrice(item.getUnitPrice());
                entity.addItem(itemEntity);
            }
        } else {
            entity = toEntity(sale);
            panacheRepository.persist(entity);
        }

        return toDomain(entity);
    }

    @Override
    public Optional<Sale> findById(Long id) {
        return panacheRepository.findByIdOptional(id).map(this::toDomain);
    }

    @Override
    public Optional<Sale> findByCode(String code) {
        return panacheRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<Sale> findAll() {
        return panacheRepository.listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sale> findByCustomerCode(String customerCode) {
        return panacheRepository.findByCustomerCode(customerCode).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sale> findBySellerCode(String sellerCode) {
        return panacheRepository.findBySellerCode(sellerCode).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sale> findByPaymentMethod(PaymentMethod paymentMethod) {
        return panacheRepository.findByPaymentMethod(paymentMethod.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sale> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return panacheRepository.findByDateRange(start, end).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        panacheRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return panacheRepository.existsByCode(code);
    }

    @Override
    public PageResult<Sale> search(String filter, int page, int size) {
        List<SaleEntity> entities = panacheRepository.search(filter, page, size);
        long total = panacheRepository.countSearch(filter);
        List<Sale> sales = entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return new PageResult<>(sales, total, page, size);
    }

    private Sale toDomain(SaleEntity entity) {
        List<SaleItem> items = entity.getItems().stream()
                .map(itemEntity -> new SaleItem(
                        itemEntity.getId(),
                        itemEntity.getProductCode(),
                        itemEntity.getProductName(),
                        itemEntity.getQuantity(),
                        itemEntity.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new Sale(
                entity.getId(),
                entity.getCode(),
                entity.getCustomerCode(),
                entity.getCustomerName(),
                entity.getSellerCode(),
                entity.getSellerName(),
                PaymentMethod.fromString(entity.getPaymentMethod()),
                decryptCardNumber(entity.getCardNumber()),
                entity.getAmountPaid(),
                items,
                entity.getCreatedAt()
        );
    }

    private SaleEntity toEntity(Sale sale) {
        SaleEntity entity = new SaleEntity();
        entity.setCode(sale.getCode());
        entity.setCustomerCode(sale.getCustomerCode());
        entity.setCustomerName(sale.getCustomerName());
        entity.setSellerCode(sale.getSellerCode());
        entity.setSellerName(sale.getSellerName());
        entity.setPaymentMethod(sale.getPaymentMethod().name());
        entity.setCardNumber(encryptCardNumber(sale.getCardNumber()));
        entity.setAmountPaid(sale.getAmountPaid());

        for (SaleItem item : sale.getItems()) {
            SaleItemEntity itemEntity = new SaleItemEntity();
            itemEntity.setProductCode(item.getProductCode());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice());
            entity.addItem(itemEntity);
        }

        if (sale.getCreatedAt() != null) {
            entity.setCreatedAt(sale.getCreatedAt());
        }
        return entity;
    }

    private String encryptCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return null;
        }
        try {
            return encryptionService.encrypt(cardNumber);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Não foi possível criptografar dados sensíveis", e);
        }
    }

    private String decryptCardNumber(String encryptedCardNumber) {
        if (encryptedCardNumber == null || encryptedCardNumber.isEmpty()) {
            return null;
        }
        try {
            return encryptionService.decrypt(encryptedCardNumber);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Não foi possível descriptografar dados sensíveis", e);
        }
    }
}
