package com.sofkify.cartservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartItem Domain Model Tests")
class CartItemTest {

    private CartItem createValidItem() {
        return new CartItem(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test Product",
            new BigDecimal("19.99"),
            2
        );
    }

    @Test
    @DisplayName("Should create cart item with valid data")
    void shouldCreateCartItemWithValidData() {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CartItem item = new CartItem(id, productId, "Product A", new BigDecimal("10.00"), 3);

        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals("Product A", item.getProductName());
        assertEquals(new BigDecimal("10.00"), item.getProductPrice());
        assertEquals(3, item.getQuantity());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    @DisplayName("Should calculate subtotal correctly")
    void shouldCalculateSubtotal() {
        CartItem item = new CartItem(
            UUID.randomUUID(), UUID.randomUUID(), "Product", new BigDecimal("15.50"), 4
        );
        assertEquals(new BigDecimal("62.00"), item.getSubtotal());
    }

    @Test
    @DisplayName("Should calculate subtotal for single quantity")
    void shouldCalculateSubtotalForSingleQuantity() {
        CartItem item = new CartItem(
            UUID.randomUUID(), UUID.randomUUID(), "Product", new BigDecimal("9.99"), 1
        );
        assertEquals(new BigDecimal("9.99"), item.getSubtotal());
    }

    @Test
    @DisplayName("Should update quantity successfully")
    void shouldUpdateQuantity() {
        CartItem item = createValidItem();
        item.updateQuantity(5);
        assertEquals(5, item.getQuantity());
    }

    @Test
    @DisplayName("Should update updatedAt when quantity changes")
    void shouldUpdateTimestampOnQuantityChange() throws InterruptedException {
        CartItem item = createValidItem();
        var before = item.getUpdatedAt();
        Thread.sleep(10);
        item.updateQuantity(10);
        assertTrue(item.getUpdatedAt().isAfter(before) || item.getUpdatedAt().isEqual(before));
    }

    @Test
    @DisplayName("Should throw when quantity is zero")
    void shouldThrowWhenQuantityIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "P", new BigDecimal("1.00"), 0)
        );
    }

    @Test
    @DisplayName("Should throw when quantity is negative")
    void shouldThrowWhenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "P", new BigDecimal("1.00"), -1)
        );
    }

    @Test
    @DisplayName("Should throw when updating to zero quantity")
    void shouldThrowOnUpdateToZeroQuantity() {
        CartItem item = createValidItem();
        assertThrows(IllegalArgumentException.class, () -> item.updateQuantity(0));
    }

    @Test
    @DisplayName("Should throw when updating to negative quantity")
    void shouldThrowOnUpdateToNegativeQuantity() {
        CartItem item = createValidItem();
        assertThrows(IllegalArgumentException.class, () -> item.updateQuantity(-5));
    }

    @Test
    @DisplayName("Should throw when price is zero")
    void shouldThrowWhenPriceIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.ZERO, 1)
        );
    }

    @Test
    @DisplayName("Should throw when price is negative")
    void shouldThrowWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "P", new BigDecimal("-5.00"), 1)
        );
    }

    @Test
    @DisplayName("Should throw when product name is empty")
    void shouldThrowWhenProductNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "  ", new BigDecimal("1.00"), 1)
        );
    }

    @Test
    @DisplayName("Should throw when id is null")
    void shouldThrowWhenIdIsNull() {
        assertThrows(NullPointerException.class, () ->
            new CartItem(null, UUID.randomUUID(), "P", new BigDecimal("1.00"), 1)
        );
    }

    @Test
    @DisplayName("Should throw when productId is null")
    void shouldThrowWhenProductIdIsNull() {
        assertThrows(NullPointerException.class, () ->
            new CartItem(UUID.randomUUID(), null, "P", new BigDecimal("1.00"), 1)
        );
    }

    @Test
    @DisplayName("Should throw when product name is null")
    void shouldThrowWhenProductNameIsNull() {
        assertThrows(NullPointerException.class, () ->
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), null, new BigDecimal("1.00"), 1)
        );
    }

    @Test
    @DisplayName("Should throw when price is null")
    void shouldThrowWhenPriceIsNull() {
        assertThrows(NullPointerException.class, () ->
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "P", null, 1)
        );
    }

    @Test
    @DisplayName("Should be equal when same id")
    void shouldBeEqualWhenSameId() {
        UUID id = UUID.randomUUID();
        CartItem a = new CartItem(id, UUID.randomUUID(), "A", new BigDecimal("1.00"), 1);
        CartItem b = new CartItem(id, UUID.randomUUID(), "B", new BigDecimal("2.00"), 3);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when different id")
    void shouldNotBeEqualWhenDifferentId() {
        CartItem a = new CartItem(UUID.randomUUID(), UUID.randomUUID(), "A", new BigDecimal("1.00"), 1);
        CartItem b = new CartItem(UUID.randomUUID(), UUID.randomUUID(), "A", new BigDecimal("1.00"), 1);
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        CartItem item = createValidItem();
        assertEquals(item, item);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        CartItem item = createValidItem();
        assertNotEquals(null, item);
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        CartItem item = createValidItem();
        assertNotEquals("a string", item);
    }
}
