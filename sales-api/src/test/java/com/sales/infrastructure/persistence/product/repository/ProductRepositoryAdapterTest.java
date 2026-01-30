package com.sales.infrastructure.persistence.product.repository;

import com.sales.domain.product.entity.Product;
import com.sales.domain.product.valueobject.Dimensions;
import com.sales.domain.product.valueobject.ProductType;
import com.sales.domain.shared.PageResult;
import com.sales.infrastructure.persistence.product.entity.ProductEntity;
import com.sales.infrastructure.persistence.product.service.ProductCodeGenerator;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductRepositoryAdapter Tests")
class ProductRepositoryAdapterTest {

    @Mock
    private ProductPanacheRepository panacheRepository;

    @Mock
    private ProductCodeGenerator codeGenerator;

    @InjectMocks
    private ProductRepositoryAdapter repositoryAdapter;

    private Product testProduct;
    private ProductEntity testEntity;
    private Dimensions testDimensions;

    @BeforeEach
    void setUp() {
        testDimensions = new Dimensions(
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(20.0),
                BigDecimal.valueOf(30.0)
        );

        testProduct = new Product(
                null,
                null,
                "Product Test",
                ProductType.FACE,
                "Product details",
                BigDecimal.valueOf(5.5),
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(150.00),
                testDimensions,
                "Truck",
                100,
                null
        );

        testEntity = createProductEntity(1L, "PROD001");
    }

    @Test
    @DisplayName("Should save new product and generate code")
    void shouldSaveNewProductAndGenerateCode() {
        when(codeGenerator.generateNextCode()).thenReturn("PROD001");
        doNothing().when(panacheRepository).persist(any(ProductEntity.class));

        Product result = repositoryAdapter.save(testProduct);

        verify(codeGenerator).generateNextCode();
        verify(panacheRepository).persist(any(ProductEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should save new product with provided code")
    void shouldSaveNewProductWithProvidedCode() {
        Product productWithCode = new Product(
                null,
                "PROD999",
                "Product Test",
                ProductType.FACE,
                "Product details",
                BigDecimal.valueOf(5.5),
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(150.00),
                testDimensions,
                "Truck",
                100,
                null
        );

        doNothing().when(panacheRepository).persist(any(ProductEntity.class));

        Product result = repositoryAdapter.save(productWithCode);

        verify(codeGenerator, never()).generateNextCode();
        verify(panacheRepository).persist(any(ProductEntity.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should update existing product")
    void shouldUpdateExistingProduct() {
        Product existingProduct = new Product(
                1L,
                "PROD001",
                "Product Updated",
                ProductType.LIPS,
                "Updated details",
                BigDecimal.valueOf(7.5),
                BigDecimal.valueOf(120.00),
                BigDecimal.valueOf(180.00),
                testDimensions,
                "Car",
                50,
                LocalDateTime.now()
        );

        when(panacheRepository.findById(1L)).thenReturn(testEntity);

        Product result = repositoryAdapter.save(existingProduct);

        verify(panacheRepository).findById(1L);
        verify(panacheRepository, never()).persist(any(ProductEntity.class));
        assertThat(result).isNotNull();
        assertThat(testEntity.getName()).isEqualTo("Product Updated");
        assertThat(testEntity.getType()).isEqualTo("LIPS");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        Product productWithId = new Product(
                999L,
                "PROD999",
                "Product Test",
                ProductType.FACE,
                "Product details",
                BigDecimal.valueOf(5.5),
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(150.00),
                testDimensions,
                "Truck",
                100,
                LocalDateTime.now()
        );

        when(panacheRepository.findById(999L)).thenReturn(null);

        assertThatThrownBy(() -> repositoryAdapter.save(productWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produto não encontrado com id: 999");

        verify(panacheRepository).findById(999L);
        verify(panacheRepository, never()).persist(any(ProductEntity.class));
    }

    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductById() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Product> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCode()).isEqualTo("PROD001");
        verify(panacheRepository).findByIdOptional(1L);
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void shouldReturnEmptyWhenProductNotFoundById() {
        when(panacheRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Optional<Product> result = repositoryAdapter.findById(999L);

        assertThat(result).isEmpty();
        verify(panacheRepository).findByIdOptional(999L);
    }

    @Test
    @DisplayName("Should find product by code")
    void shouldFindProductByCode() {
        when(panacheRepository.findByCode("PROD001")).thenReturn(Optional.of(testEntity));

        Optional<Product> result = repositoryAdapter.findByCode("PROD001");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("PROD001");
        verify(panacheRepository).findByCode("PROD001");
    }

    @Test
    @DisplayName("Should find all products")
    void shouldFindAllProducts() {
        ProductEntity entity2 = createProductEntity(2L, "PROD002");
        when(panacheRepository.listAll()).thenReturn(Arrays.asList(testEntity, entity2));

        List<Product> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("PROD001");
        assertThat(result.get(1).getCode()).isEqualTo("PROD002");
        verify(panacheRepository).listAll();
    }

    @Test
    @DisplayName("Should find all products sorted by name")
    void shouldFindAllProductsSortedByName() {
        ProductEntity entity2 = createProductEntity(2L, "PROD002");
        when(panacheRepository.findAllSortedByName()).thenReturn(Arrays.asList(testEntity, entity2));

        List<Product> result = repositoryAdapter.findAllSortedByName();

        assertThat(result).hasSize(2);
        verify(panacheRepository).findAllSortedByName();
    }

    @Test
    @DisplayName("Should find products by type")
    void shouldFindProductsByType() {
        when(panacheRepository.findByType("FACE")).thenReturn(Arrays.asList(testEntity));

        List<Product> result = repositoryAdapter.findByType(ProductType.FACE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(ProductType.FACE);
        verify(panacheRepository).findByType("FACE");
    }

    @Test
    @DisplayName("Should find products by name containing")
    void shouldFindProductsByNameContaining() {
        when(panacheRepository.findByNameContaining("Test")).thenReturn(Arrays.asList(testEntity));

        List<Product> result = repositoryAdapter.findByNameContaining("Test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");
        verify(panacheRepository).findByNameContaining("Test");
    }

    @Test
    @DisplayName("Should delete product by ID")
    void shouldDeleteProductById() {
        when(panacheRepository.deleteById(1L)).thenReturn(true);

        repositoryAdapter.deleteById(1L);

        verify(panacheRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if product exists by code")
    void shouldCheckIfProductExistsByCode() {
        when(panacheRepository.existsByCode("PROD001")).thenReturn(true);

        boolean result = repositoryAdapter.existsByCode("PROD001");

        assertThat(result).isTrue();
        verify(panacheRepository).existsByCode("PROD001");
    }

    @Test
    @DisplayName("Should search products with pagination")
    void shouldSearchProductsWithPagination() {
        ProductEntity entity2 = createProductEntity(2L, "PROD002");
        when(panacheRepository.search("Test", 0, 10)).thenReturn(Arrays.asList(testEntity, entity2));
        when(panacheRepository.countSearch("Test")).thenReturn(2L);

        PageResult<Product> result = repositoryAdapter.search("Test", 0, 10);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        verify(panacheRepository).search("Test", 0, 10);
        verify(panacheRepository).countSearch("Test");
    }

    @Test
    @DisplayName("Should convert entity to domain correctly")
    void shouldConvertEntityToDomainCorrectly() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Product> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        Product product = result.get();
        assertThat(product.getId()).isEqualTo(testEntity.getId());
        assertThat(product.getCode()).isEqualTo(testEntity.getCode());
        assertThat(product.getName()).isEqualTo(testEntity.getName());
        assertThat(product.getType().name()).isEqualTo(testEntity.getType());
        assertThat(product.getDetails()).isEqualTo(testEntity.getDetails());
        assertThat(product.getWeight()).isEqualByComparingTo(testEntity.getWeight());
        assertThat(product.getPurchasePrice()).isEqualByComparingTo(testEntity.getPurchasePrice());
        assertThat(product.getSalePrice()).isEqualByComparingTo(testEntity.getSalePrice());
        assertThat(product.getDimensions().getHeight()).isEqualByComparingTo(testEntity.getHeight());
        assertThat(product.getDimensions().getWidth()).isEqualByComparingTo(testEntity.getWidth());
        assertThat(product.getDimensions().getDepth()).isEqualByComparingTo(testEntity.getDepth());
        assertThat(product.getDestinationVehicle()).isEqualTo(testEntity.getDestinationVehicle());
        assertThat(product.getStockQuantity()).isEqualTo(testEntity.getStockQuantity());
    }

    @Test
    @DisplayName("Should update all product fields")
    void shouldUpdateAllProductFields() {
        Dimensions newDimensions = new Dimensions(
                BigDecimal.valueOf(15.0),
                BigDecimal.valueOf(25.0),
                BigDecimal.valueOf(35.0)
        );

        Product updatedProduct = new Product(
                1L,
                "PROD001",
                "Updated Product",
                ProductType.LIPS,
                "Updated details",
                BigDecimal.valueOf(8.0),
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(250.00),
                newDimensions,
                "Van",
                75,
                LocalDateTime.now()
        );

        when(panacheRepository.findById(1L)).thenReturn(testEntity);

        repositoryAdapter.save(updatedProduct);

        assertThat(testEntity.getName()).isEqualTo("Updated Product");
        assertThat(testEntity.getType()).isEqualTo("LIPS");
        assertThat(testEntity.getDetails()).isEqualTo("Updated details");
        assertThat(testEntity.getWeight()).isEqualByComparingTo(BigDecimal.valueOf(8.0));
        assertThat(testEntity.getPurchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(testEntity.getSalePrice()).isEqualByComparingTo(BigDecimal.valueOf(250.00));
        assertThat(testEntity.getHeight()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
        assertThat(testEntity.getWidth()).isEqualByComparingTo(BigDecimal.valueOf(25.0));
        assertThat(testEntity.getDepth()).isEqualByComparingTo(BigDecimal.valueOf(35.0));
        assertThat(testEntity.getDestinationVehicle()).isEqualTo("Van");
        assertThat(testEntity.getStockQuantity()).isEqualTo(75);
    }

    @Test
    @DisplayName("Should convert dimensions correctly")
    void shouldConvertDimensionsCorrectly() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Product> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        Dimensions dimensions = result.get().getDimensions();
        assertThat(dimensions.getHeight()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
        assertThat(dimensions.getWidth()).isEqualByComparingTo(BigDecimal.valueOf(20.0));
        assertThat(dimensions.getDepth()).isEqualByComparingTo(BigDecimal.valueOf(30.0));
    }

    @Test
    @DisplayName("Should convert product type correctly")
    void shouldConvertProductTypeCorrectly() {
        when(panacheRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEntity));

        Optional<Product> result = repositoryAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo(ProductType.FACE);
    }

    // Helper method para criar ProductEntity
    private ProductEntity createProductEntity(Long id, String code) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName("Product Test");
        entity.setType("FACE");
        entity.setDetails("Product details");
        entity.setWeight(BigDecimal.valueOf(5.5));
        entity.setPurchasePrice(BigDecimal.valueOf(100.00));
        entity.setSalePrice(BigDecimal.valueOf(150.00));
        entity.setHeight(BigDecimal.valueOf(10.0));
        entity.setWidth(BigDecimal.valueOf(20.0));
        entity.setDepth(BigDecimal.valueOf(30.0));
        entity.setDestinationVehicle("Truck");
        entity.setStockQuantity(100);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
