package com.sofkify.productservice.infrastructure.persistence.entity;

import com.sofkify.productservice.domain.enums.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductEntity Tests")
class ProductEntityTest {

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgs() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = new ProductEntity(id, "P", "D", "SKU", BigDecimal.TEN, 5, ProductStatus.ACTIVE);

        assertEquals(id, entity.getId());
        assertEquals("P", entity.getName());
        assertEquals("D", entity.getDescription());
        assertEquals("SKU", entity.getSku());
        assertEquals(BigDecimal.TEN, entity.getPrice());
        assertEquals(5, entity.getStock());
        assertEquals(ProductStatus.ACTIVE, entity.getStatus());
    }

    @Test
    @DisplayName("Should create with no-args constructor and setters")
    void shouldCreateWithSetters() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setName("N");
        entity.setDescription("D");
        entity.setSku("S");
        entity.setPrice(BigDecimal.ONE);
        entity.setStock(1);
        entity.setStatus(ProductStatus.INACTIVE);

        assertEquals(id, entity.getId());
        assertEquals("N", entity.getName());
        assertEquals("D", entity.getDescription());
        assertEquals("S", entity.getSku());
        assertEquals(BigDecimal.ONE, entity.getPrice());
        assertEquals(1, entity.getStock());
        assertEquals(ProductStatus.INACTIVE, entity.getStatus());
    }

    @Test
    @DisplayName("toString should not throw")
    void toStringShouldNotThrow() {
        ProductEntity entity = new ProductEntity(UUID.randomUUID(), "P", "D", "S", BigDecimal.ONE, 1, ProductStatus.ACTIVE);
        assertNotNull(entity.toString());
    }
}
