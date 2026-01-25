package com.sales.application.sale.usecase;

import com.sales.domain.sale.entity.Sale;
import com.sales.domain.sale.entity.SaleItem;
import com.sales.domain.sale.port.SaleRepository;
import com.sales.domain.sale.valueobject.PaymentMethod;
import com.sales.domain.shared.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchSalesUseCaseTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SearchSalesUseCase useCase;

    private Sale sale1;
    private Sale sale2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SaleItem item1 = new SaleItem(
                "PROD001",
                "Batom Vermelho",
                2,
                BigDecimal.valueOf(30.00)
        );

        sale1 = new Sale(
                "SALE001",
                "CUST001",
                "João Silva",
                "SELL001",
                "Vendedor A",
                PaymentMethod.CARTAO_CREDITO,
                "1234",
                BigDecimal.valueOf(100.00)
        );
        sale1.addItem(item1);

        SaleItem item2 = new SaleItem(
                "PROD002",
                "Batom Rosa",
                1,
                BigDecimal.valueOf(25.00)
        );

        sale2 = new Sale(
                "SALE002",
                "CUST002",
                "Maria Santos",
                "SELL002",
                "Vendedor B",
                PaymentMethod.PIX,
                null,
                null
        );
        sale2.addItem(item2);
    }

    @Test
    void shouldSearchSalesWithFilter() {
        String filter = "João";
        PageResult<Sale> expectedPage = new PageResult<>(
                List.of(sale1),
                1L,
                0,
                10
        );

        when(saleRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Sale> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCustomerName()).isEqualTo("João Silva");
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(saleRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldSearchSalesWithoutFilter() {
        String filter = "";
        PageResult<Sale> expectedPage = new PageResult<>(
                List.of(sale1, sale2),
                2L,
                0,
                10
        );

        when(saleRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Sale> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        verify(saleRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldReturnEmptyPageWhenNoSalesFound() {
        String filter = "NonExistent";
        PageResult<Sale> expectedPage = new PageResult<>(
                List.of(),
                0L,
                0,
                10
        );

        when(saleRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Sale> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
        verify(saleRepository, times(1)).search(filter, 0, 10);
    }

    @Test
    void shouldHandlePagination() {
        String filter = "";
        PageResult<Sale> expectedPage = new PageResult<>(
                List.of(sale2),
                2L,
                1,
                1
        );

        when(saleRepository.search(filter, 1, 1)).thenReturn(expectedPage);

        PageResult<Sale> result = useCase.execute(filter, 1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        verify(saleRepository, times(1)).search(filter, 1, 1);
    }

    @Test
    void shouldSearchBySaleCode() {
        String filter = "SALE001";
        PageResult<Sale> expectedPage = new PageResult<>(
                List.of(sale1),
                1L,
                0,
                10
        );

        when(saleRepository.search(filter, 0, 10)).thenReturn(expectedPage);

        PageResult<Sale> result = useCase.execute(filter, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo("SALE001");
        verify(saleRepository, times(1)).search(filter, 0, 10);
    }
}
