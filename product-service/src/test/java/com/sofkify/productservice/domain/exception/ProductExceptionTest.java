package com.sofkify.productservice.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Exception Tests")
class ProductExceptionTest {

    @Test
    @DisplayName("ProductNotFoundException")
    void productNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException("not found");
        assertEquals("not found", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("InsufficientStockException")
    void insufficientStock() {
        InsufficientStockException ex = new InsufficientStockException("no stock");
        assertEquals("no stock", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("InvalidProductPriceException")
    void invalidPrice() {
        InvalidProductPriceException ex = new InvalidProductPriceException("bad price");
        assertEquals("bad price", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("InvalidProductStockException")
    void invalidStock() {
        InvalidProductStockException ex = new InvalidProductStockException("bad stock");
        assertEquals("bad stock", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("DuplicateSkuException")
    void duplicateSku() {
        DuplicateSkuException ex = new DuplicateSkuException("dup sku");
        assertEquals("dup sku", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }
}
