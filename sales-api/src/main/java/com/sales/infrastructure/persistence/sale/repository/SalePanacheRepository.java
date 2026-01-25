package com.sales.infrastructure.persistence.sale.repository;

import com.sales.infrastructure.persistence.sale.entity.SaleEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SalePanacheRepository implements PanacheRepository<SaleEntity> {

    @PersistenceContext
    EntityManager em;

    public Optional<SaleEntity> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public List<SaleEntity> findByCustomerCode(String customerCode) {
        return find("customerCode", customerCode).list();
    }

    public List<SaleEntity> findBySellerCode(String sellerCode) {
        return find("sellerCode", sellerCode).list();
    }

    public List<SaleEntity> findByPaymentMethod(String paymentMethod) {
        return find("paymentMethod", paymentMethod).list();
    }

    public List<SaleEntity> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return find("createdAt >= ?1 and createdAt <= ?2", start, end).list();
    }

    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    public List<Map<String, Object>> getMonthlyRevenue(LocalDateTime start, LocalDateTime end) {
        String query = """
            SELECT
                EXTRACT(MONTH FROM s.created_at) as month,
                EXTRACT(YEAR FROM s.created_at) as year,
                SUM(si.quantity * si.unit_price) as subtotal
            FROM sales s
            JOIN sale_items si ON si.sale_id = s.id
            WHERE s.created_at >= :start AND s.created_at <= :end
            GROUP BY EXTRACT(YEAR FROM s.created_at), EXTRACT(MONTH FROM s.created_at)
            ORDER BY year DESC, month DESC
            """;

        List<Tuple> tuples = em.createNativeQuery(query, Tuple.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();

        return tuples.stream()
                .map(tuple -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", tuple.get("month"));
                    map.put("year", tuple.get("year"));
                    map.put("subtotal", tuple.get("subtotal"));
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopRevenueProducts(int limit) {
        String query = """
            SELECT
                si.product_code as productCode,
                si.product_name as productName,
                si.unit_price as salePrice,
                SUM(si.quantity * si.unit_price) as totalRevenue
            FROM sale_items si
            GROUP BY si.product_code, si.product_name, si.unit_price
            ORDER BY totalRevenue DESC
            LIMIT :limit
            """;

        List<Tuple> tuples = em.createNativeQuery(query, Tuple.class)
                .setParameter("limit", limit)
                .getResultList();

        return tuples.stream()
                .map(tuple -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productCode", tuple.get("productCode"));
                    map.put("productName", tuple.get("productName"));
                    map.put("salePrice", tuple.get("salePrice"));
                    map.put("totalRevenue", tuple.get("totalRevenue"));
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<SaleEntity> search(String filter, int page, int size) {
        if (filter == null || filter.isBlank()) {
            return find("ORDER BY createdAt DESC")
                    .page(Page.of(page, size))
                    .list();
        }

        String searchPattern = "%" + filter.toLowerCase() + "%";
        return find(
                "LOWER(code) LIKE ?1 OR LOWER(customerName) LIKE ?1 OR LOWER(customerCode) LIKE ?1 OR LOWER(sellerName) LIKE ?1",
                Sort.by("createdAt").descending(),
                searchPattern
        ).page(Page.of(page, size)).list();
    }

    public long countSearch(String filter) {
        if (filter == null || filter.isBlank()) {
            return count();
        }

        String searchPattern = "%" + filter.toLowerCase() + "%";
        return count(
                "LOWER(code) LIKE ?1 OR LOWER(customerName) LIKE ?1 OR LOWER(customerCode) LIKE ?1 OR LOWER(sellerName) LIKE ?1",
                searchPattern
        );
    }
}
