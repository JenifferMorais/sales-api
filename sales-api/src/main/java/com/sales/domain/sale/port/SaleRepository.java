package com.sales.domain.sale.port;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.domain.shared.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository {
    Sale save(Sale sale);
    Optional<Sale> findById(Long id);
    Optional<Sale> findByCode(String code);
    List<Sale> findAll();
    List<Sale> findByCustomerCode(String customerCode);
    List<Sale> findBySellerCode(String sellerCode);
    List<Sale> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Sale> findByDateRange(LocalDateTime start, LocalDateTime end);
    PageResult<Sale> search(String filter, int page, int size);
    void deleteById(Long id);
    boolean existsByCode(String code);
}
