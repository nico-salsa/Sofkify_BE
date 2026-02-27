package com.sofkify.productservice.domain.model;

import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.exception.InvalidProductPriceException;
import com.sofkify.productservice.domain.exception.InvalidProductStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Model Tests")
class ProductTest {

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProduct() {
        Product product = Product.create("Laptop", "Desc", "SKU-001", BigDecimal.valueOf(999.99), 10);

        assertNotNull(product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("Desc", product.getDescription());
        assertEquals("SKU-001", product.getSku());
        assertEquals(BigDecimal.valueOf(999.99), product.getPrice());
        assertEquals(10, product.getStock());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    @DisplayName("Should reconstitute product")
    void shouldReconstituteProduct() {
        UUID id = UUID.randomUUID();
        Product product = Product.reconstitute(id, "P", "D", "SKU", BigDecimal.ONE, 5, ProductStatus.INACTIVE);

        assertEquals(id, product.getId());
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }

    @Test
    @DisplayName("Should reject null price")
    void shouldRejectNullPrice() {
        assertThrows(InvalidProductPriceException.class,
            () -> Product.create("P", "D", "SKU", null, 10));
    }

    @Test
    @DisplayName("Should reject zero price")
    void shouldRejectZeroPrice() {
        assertThrows(InvalidProductPriceException.class,
            () -> Product.create("P", "D", "SKU", BigDecimal.ZERO, 10));
    }

    @Test
    @DisplayName("Should reject negative price")
    void shouldRejectNegativePrice() {
        assertThrows(InvalidProductPriceException.class,
            () -> Product.create("P", "D", "SKU", BigDecimal.valueOf(-1), 10));
    }

    @Test
    @DisplayName("Should reject negative stock")
    void shouldRejectNegativeStock() {
        assertThrows(InvalidProductStockException.class,
            () -> Product.create("P", "D", "SKU", BigDecimal.ONE, -1));
    }

    @Test
    @DisplayName("Should reject null SKU")
    void shouldRejectNullSku() {
        assertThrows(IllegalArgumentException.class,
            () -> Product.create("P", "D", null, BigDecimal.ONE, 10));
    }

    @Test
    @DisplayName("Should reject empty SKU")
    void shouldRejectEmptySku() {
        assertThrows(IllegalArgumentException.class,
            () -> Product.create("P", "D", "", BigDecimal.ONE, 10));
        assertThrows(IllegalArgumentException.class,
            () -> Product.create("P", "D", "   ", BigDecimal.ONE, 10));
    }

    @Test
    @DisplayName("Should decrement stock successfully")
    void shouldDecrementStock() {
        Product product = Product.create("P", "D", "SKU", BigDecimal.ONE, 10);
        product.decrementStock(3);
        assertEquals(7, product.getStock());
    }

    @Test
    @DisplayName("Should reject decrement with zero quantity")
    void shouldRejectZeroDecrement() {
        Product product = Product.create("P", "D", "SKU", BigDecimal.ONE, 10);
        assertThrows(InvalidProductStockException.class, () -> product.decrementStock(0));
    }

    @Test
    @DisplayName("Should reject decrement with negative quantity")
    void shouldRejectNegativeDecrement() {
        Product product = Product.create("P", "D", "SKU", BigDecimal.ONE, 10);
        assertThrows(InvalidProductStockException.class, () -> product.decrementStock(-1));
    }

    @Test
    @DisplayName("Should reject decrement exceeding stock")
    void shouldRejectInsufficientStock() {
        Product product = Product.create("P", "D", "SKU", BigDecimal.ONE, 5);
        assertThrows(InvalidProductStockException.class, () -> product.decrementStock(10));
    }

    @Test
    @DisplayName("Should allow zero stock on creation")
    void shouldAllowZeroStock() {
        Product product = Product.create("P", "D", "SKU", BigDecimal.ONE, 0);
        assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("ProductStatus enum values")
    void productStatusValues() {
        assertEquals(3, ProductStatus.values().length);
        assertNotNull(ProductStatus.valueOf("ACTIVE"));
        assertNotNull(ProductStatus.valueOf("INACTIVE"));
        assertNotNull(ProductStatus.valueOf("DELETED"));
    }
}
