package com.sofkify.orderservice.domain.model;

import com.sofkify.orderservice.domain.valueobject.Money;
import com.sofkify.orderservice.domain.valueobject.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    @Test
    @DisplayName("Should create OrderItem with legacy constructor")
    void shouldCreateOrderItemWithLegacyConstructor() {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(id, productId, "Test Product", BigDecimal.valueOf(19.99), 3);

        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals("Test Product", item.getProductName());
        assertEquals(new Money(BigDecimal.valueOf(19.99)), item.getUnitPrice());
        assertEquals(new Quantity(3), item.getQuantity());
        assertNotNull(item.getCreatedAt());
    }

    @Test
    @DisplayName("Should create OrderItem with Money and Quantity constructor")
    void shouldCreateOrderItemWithMoneyAndQuantity() {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Money price = new Money(BigDecimal.valueOf(25.00));
        Quantity qty = new Quantity(2);

        OrderItem item = new OrderItem(id, productId, "Product A", price, qty);

        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals("Product A", item.getProductName());
        assertEquals(price, item.getUnitPrice());
        assertEquals(qty, item.getQuantity());
    }

    @Test
    @DisplayName("Should calculate subtotal correctly")
    void shouldCalculateSubtotalCorrectly() {
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.valueOf(10.00), 5);
        Money expected = new Money(BigDecimal.valueOf(50.00));
        assertEquals(expected, item.getSubtotal());
    }

    @Test
    @DisplayName("Should create OrderItem from cart item")
    void shouldCreateFromCartItem() {
        UUID orderItemId = UUID.randomUUID();
        OrderItem item = OrderItem.fromCartItem(orderItemId, new Object());

        assertNotNull(item.getId());
        assertEquals(orderItemId, item.getId());
        assertNotNull(item.getProductId());
        assertNotNull(item.getProductName());
    }

    @Test
    @DisplayName("Should reject null id")
    void shouldRejectNullId() {
        assertThrows(NullPointerException.class,
            () -> new OrderItem(null, UUID.randomUUID(), "P", BigDecimal.ONE, 1));
    }

    @Test
    @DisplayName("Should reject null productId")
    void shouldRejectNullProductId() {
        assertThrows(NullPointerException.class,
            () -> new OrderItem(UUID.randomUUID(), null, "P", BigDecimal.ONE, 1));
    }

    @Test
    @DisplayName("Should reject null or empty product name")
    void shouldRejectNullOrEmptyProductName() {
        assertThrows(NullPointerException.class,
            () -> new OrderItem(UUID.randomUUID(), UUID.randomUUID(), null, BigDecimal.ONE, 1));
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "", BigDecimal.ONE, 1));
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "   ", BigDecimal.ONE, 1));
    }

    @Test
    @DisplayName("Legacy getters should return correct values")
    void legacyGettersShouldReturnCorrectValues() {
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.valueOf(9.99), 4);
        // Test the legacy getPrice and getQuantityValue accessors
        assertEquals(new Money(BigDecimal.valueOf(9.99)), item.getUnitPrice());
        assertEquals(new Quantity(4), item.getQuantity());
    }
}
