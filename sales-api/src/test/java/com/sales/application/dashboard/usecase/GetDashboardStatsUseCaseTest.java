package com.sales.application.dashboard.usecase;

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
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.infrastructure.rest.dashboard.dto.DashboardStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetDashboardStatsUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetDashboardStatsUseCase useCase;

    private Sale currentMonthSale;
    private Sale lastMonthSale;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SaleItem item = new SaleItem("PROD001", "Batom Vermelho", 2, BigDecimal.valueOf(30.00));
        currentMonthSale = new Sale("SALE001", "CUST001", "João Silva", "SELL001", "Vendedor A",
                PaymentMethod.CARTAO_CREDITO, "1234", BigDecimal.valueOf(100.00));
        currentMonthSale.addItem(item);

        SaleItem item2 = new SaleItem("PROD002", "Batom Rosa", 1, BigDecimal.valueOf(25.00));
        lastMonthSale = new Sale("SALE002", "CUST002", "Maria Santos", "SELL002", "Vendedor B",
                PaymentMethod.PIX, null, BigDecimal.valueOf(50.00));
        lastMonthSale.addItem(item2);

        Document document = new Document("12345678909", "123456789");
        Address address = new Address("12345678", "Rua Teste", "100", "", "Centro", "São Paulo", "SP");
        customer = new Customer("CUST001", "João Silva", "Maria Silva", document, address,
                LocalDate.of(1990, 1, 1), "11987654321", "joao@example.com");

        Dimensions dimensions = new Dimensions(BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0));
        product = new Product("PROD001", "Batom Vermelho", ProductType.LIPS, "Batom vermelho matte",
                BigDecimal.valueOf(0.1), BigDecimal.valueOf(15.00), BigDecimal.valueOf(30.00), dimensions, null);
    }

    @Test
    void shouldGetDashboardStats() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentMonthSale))
                .thenReturn(List.of(lastMonthSale));
        when(saleRepository.findAll()).thenReturn(List.of(currentMonthSale, lastMonthSale));
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(productRepository.findAll()).thenReturn(List.of(product));

        DashboardStatsResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getTotalSales()).isEqualTo(2L);
        assertThat(result.getTotalCustomers()).isEqualTo(1L);
        assertThat(result.getTotalProducts()).isEqualTo(1L);
        assertThat(result.getTotalRevenue()).isNotNull();
        assertThat(result.getSalesVariation()).isNotNull();
        assertThat(result.getRevenueVariation()).isNotNull();
        assertThat(result.getCustomersVariation()).isNotNull();

        verify(saleRepository, times(2)).findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(saleRepository, times(1)).findAll();
        verify(customerRepository, times(1)).findAll();
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldCalculatePositiveVariation() {
        // Current month has 2 sales, last month has 1
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentMonthSale, currentMonthSale)) // Current month: 2 sales
                .thenReturn(List.of(lastMonthSale)); // Last month: 1 sale
        when(saleRepository.findAll()).thenReturn(List.of(currentMonthSale, currentMonthSale, lastMonthSale));
        when(customerRepository.findAll()).thenReturn(List.of());
        when(productRepository.findAll()).thenReturn(List.of());

        DashboardStatsResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getSalesVariation()).isPositive(); // Should be 100% increase
    }

    @Test
    void shouldCalculateNegativeVariation() {
        // Current month has 1 sale, last month has 2
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentMonthSale)) // Current month: 1 sale
                .thenReturn(List.of(lastMonthSale, lastMonthSale)); // Last month: 2 sales
        when(saleRepository.findAll()).thenReturn(List.of(currentMonthSale, lastMonthSale, lastMonthSale));
        when(customerRepository.findAll()).thenReturn(List.of());
        when(productRepository.findAll()).thenReturn(List.of());

        DashboardStatsResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getSalesVariation()).isNegative(); // Should be -50% decrease
    }

    @Test
    void shouldHandle100PercentWhenNoPreviousData() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentMonthSale)) // Current month: 1 sale
                .thenReturn(List.of()); // Last month: 0 sales
        when(saleRepository.findAll()).thenReturn(List.of(currentMonthSale));
        when(customerRepository.findAll()).thenReturn(List.of());
        when(productRepository.findAll()).thenReturn(List.of());

        DashboardStatsResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getSalesVariation()).isEqualByComparingTo(BigDecimal.valueOf(100)); // 100% when starting from 0
    }

    @Test
    void shouldHandleZeroWhenNoCurrentData() {
        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of()) // Current month: 0 sales
                .thenReturn(List.of()); // Last month: 0 sales
        when(saleRepository.findAll()).thenReturn(List.of());
        when(customerRepository.findAll()).thenReturn(List.of());
        when(productRepository.findAll()).thenReturn(List.of());

        DashboardStatsResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getTotalSales()).isEqualTo(0L);
        assertThat(result.getTotalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getSalesVariation()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getRevenueVariation()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCalculateTotalRevenueCorrectly() {
        BigDecimal sale1Total = currentMonthSale.getTotalAmount();
        BigDecimal sale2Total = lastMonthSale.getTotalAmount();
        BigDecimal expectedTotal = sale1Total.add(sale2Total);

        when(saleRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(currentMonthSale))
                .thenReturn(List.of(lastMonthSale));
        when(saleRepository.findAll()).thenReturn(List.of(currentMonthSale, lastMonthSale));
        when(customerRepository.findAll()).thenReturn(List.of());
        when(productRepository.findAll()).thenReturn(List.of());

        DashboardStatsResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.getTotalRevenue()).isEqualByComparingTo(expectedTotal);
    }
}
