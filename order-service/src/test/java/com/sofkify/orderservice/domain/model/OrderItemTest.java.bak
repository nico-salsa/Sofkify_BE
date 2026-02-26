package com.sofkify.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    @Test
    @DisplayName("Should create order item with correct subtotal calculation")
    void shouldCreateOrderItemWithCorrectSubtotalCalculation() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productName = "Test Product";
        BigDecimal productPrice = BigDecimal.valueOf(29.99);
        int quantity = 3;

        // Act
        OrderItem orderItem = new OrderItem(itemId, productId, productName, productPrice, quantity);

        // Assert
        assertEquals(itemId, orderItem.getId());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(productPrice, orderItem.getProductPrice());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(BigDecimal.valueOf(89.97), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should create order item from cart item")
    void shouldCreateOrderItemFromCartItem() {
        // Arrange - This should fail because the method doesn't exist yet
        UUID cartItemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productName = "Cart Product";
        BigDecimal price = BigDecimal.valueOf(15.50);
        int quantity = 2;

        // Act - Static factory method that doesn't exist yet
        OrderItem orderItem = OrderItem.fromCartItem(cartItemId, productId, 
            productName, price, quantity); // This method doesn't exist yet

        // Assert
        assertNotNull(orderItem.getId()); // Should generate new UUID
        assertNotEquals(cartItemId, orderItem.getId()); // Should be different from cart item ID
        assertEquals(productId, orderItem.getProductId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(price, orderItem.getProductPrice());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(BigDecimal.valueOf(31.00), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should validate order item cannot have zero quantity")
    void shouldValidateOrderItemCannotHaveZeroQuantity() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(itemId, productId, "Product", BigDecimal.valueOf(10.00), 0));
        
        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate order item cannot have negative price")
    void shouldValidateOrderItemCannotHaveNegativePrice() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(itemId, productId, "Product", BigDecimal.valueOf(-5.00), 1));
        
        assertEquals("Product price must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate order item cannot have empty product name")
    void shouldValidateOrderItemCannotHaveEmptyProductName() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(itemId, productId, "   ", BigDecimal.valueOf(10.00), 1));
        
        assertEquals("Product name cannot be empty", exception.getMessage());
    }
}