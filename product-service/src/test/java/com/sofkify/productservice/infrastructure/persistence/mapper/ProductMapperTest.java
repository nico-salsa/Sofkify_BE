package com.sofkify.productservice.infrastructure.persistence.mapper;

import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductMapper Tests")
class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    @DisplayName("Should map domain to entity")
    void shouldMapToEntity() {
        Product product = Product.create("Laptop", "Desc", "SKU-001", BigDecimal.valueOf(999.99), 10);

        ProductEntity entity = mapper.toEntity(product);

        assertEquals(product.getId(), entity.getId());
        assertEquals(product.getName(), entity.getName());
        assertEquals(product.getDescription(), entity.getDescription());
        assertEquals(product.getSku(), entity.getSku());
        assertEquals(product.getPrice(), entity.getPrice());
        assertEquals(product.getStock(), entity.getStock());
        assertEquals(product.getStatus(), entity.getStatus());
    }

    @Test
    @DisplayName("Should map entity to domain")
    void shouldMapToDomain() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = new ProductEntity(id, "P", "D", "SKU", BigDecimal.TEN, 5, ProductStatus.ACTIVE);

        Product product = mapper.toDomain(entity);

        assertEquals(id, product.getId());
        assertEquals("P", product.getName());
        assertEquals("D", product.getDescription());
        assertEquals("SKU", product.getSku());
        assertEquals(BigDecimal.TEN, product.getPrice());
        assertEquals(5, product.getStock());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }
}
