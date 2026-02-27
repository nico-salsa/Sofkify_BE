package com.sofkify.productservice.infrastructure.persistence.adapter;

import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import com.sofkify.productservice.infrastructure.persistence.mapper.ProductMapper;
import com.sofkify.productservice.infrastructure.persistence.repository.JpaProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProductPersistenceAdapter Tests")
class ProductPersistenceAdapterTest {

    private JpaProductRepository jpaRepository;
    private ProductMapper mapper;
    private ProductPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(JpaProductRepository.class);
        mapper = mock(ProductMapper.class);
        adapter = new ProductPersistenceAdapter(jpaRepository, mapper);
    }

    @Test
    @DisplayName("save should map, persist, and return domain product")
    void shouldSaveProduct() {
        Product product = Product.create("SKU-1", "Product", "Desc", BigDecimal.TEN, 10);
        ProductEntity entity = new ProductEntity();
        ProductEntity savedEntity = new ProductEntity();
        Product expected = Product.create("SKU-1", "Product", "Desc", BigDecimal.TEN, 10);

        when(mapper.toEntity(product)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(expected);

        Product result = adapter.save(product);

        assertSame(expected, result);
        verify(mapper).toEntity(product);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("findById should delegate to JPA and map result")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = new ProductEntity();
        Product expected = Product.create("SKU-1", "Product", "Desc", BigDecimal.TEN, 5);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        Optional<Product> result = adapter.findById(id);

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void shouldReturnEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Product> result = adapter.findById(id);

        assertTrue(result.isEmpty());
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("findAll should map all entities to domain")
    void shouldFindAll() {
        ProductEntity e1 = new ProductEntity();
        ProductEntity e2 = new ProductEntity();
        Product p1 = Product.create("SKU-1", "P1", "D1", BigDecimal.TEN, 1);
        Product p2 = Product.create("SKU-2", "P2", "D2", BigDecimal.ONE, 2);

        when(jpaRepository.findAll()).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(p1);
        when(mapper.toDomain(e2)).thenReturn(p2);

        List<Product> result = adapter.findAll();

        assertEquals(2, result.size());
        assertSame(p1, result.get(0));
        assertSame(p2, result.get(1));
    }

    @Test
    @DisplayName("findByStatus should parse status and delegate")
    void shouldFindByStatus() {
        ProductEntity entity = new ProductEntity();
        Product product = Product.create("SKU-1", "P", "D", BigDecimal.TEN, 1);

        when(jpaRepository.findByStatus(ProductStatus.ACTIVE)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(product);

        List<Product> result = adapter.findByStatus("active");

        assertEquals(1, result.size());
        assertSame(product, result.get(0));
        verify(jpaRepository).findByStatus(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("findByStatus with uppercase should work")
    void shouldFindByStatusUppercase() {
        when(jpaRepository.findByStatus(ProductStatus.INACTIVE)).thenReturn(List.of());

        List<Product> result = adapter.findByStatus("INACTIVE");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("existsBySku should delegate to JPA")
    void shouldCheckExistsBySku() {
        when(jpaRepository.existsBySku("SKU-1")).thenReturn(true);
        when(jpaRepository.existsBySku("SKU-2")).thenReturn(false);

        assertTrue(adapter.existsBySku("SKU-1"));
        assertFalse(adapter.existsBySku("SKU-2"));
    }
}
