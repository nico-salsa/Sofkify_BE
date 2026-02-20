package com.sofkify.orderservice.domain.model;

import com.sofkify.orderservice.domain.event.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Aggregate Tests")
class OrderTest {

    private UUID orderId;
    private UUID cartId;
    private UUID customerId;
    private UUID productId1;
    private UUID productId2;
    
    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create order from confirmed cart with correct total")
    void shouldCreateOrderFromConfirmedCartWithCorrectTotal() {
        // Arrange
        OrderItem item1 = new OrderItem(UUID.randomUUID(), productId1, "Product 1", 
            BigDecimal.valueOf(99.99), 2);
        OrderItem item2 = new OrderItem(UUID.randomUUID(), productId2, "Product 2", 
            BigDecimal.valueOf(50.00), 1);
        List<OrderItem> items = Arrays.asList(item1, item2);

        // Act
        Order order = new Order(orderId, cartId, customerId, items);

        // Assert
        assertEquals(orderId, order.getId());
        assertEquals(cartId, order.getCartId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(OrderStatus.PENDING, order.getStatus()); // Note: PENDING, not PENDING_PAYMENT
        assertEquals(BigDecimal.valueOf(249.98), order.getTotalAmount());
        assertEquals(2, order.getItems().size());
    }

    @Test
    @DisplayName("Should generate OrderCreatedEvent when order is created")
    void shouldGenerateOrderCreatedEventWhenOrderIsCreated() {
        // Arrange
        OrderItem item = new OrderItem(UUID.randomUUID(), productId1, "Test Product", 
            BigDecimal.valueOf(100.00), 1);
        List<OrderItem> items = Arrays.asList(item);

        // Act
        Order order = new Order(orderId, cartId, customerId, items);
        
        // Assert - This should fail because we haven't implemented domain events yet
        assertNotNull(order.getDomainEvents()); // This method doesn't exist yet
        assertEquals(1, order.getDomainEvents().size());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderCreatedEvent); // Class doesn't exist yet
    }

    @Test
    @DisplayName("Should create order with idempotency support")
    void shouldCreateOrderWithIdempotencySupport() {
        // Arrange
        OrderItem item = new OrderItem(UUID.randomUUID(), productId1, "Test Product", 
            BigDecimal.valueOf(75.00), 1);
        List<OrderItem> items = Arrays.asList(item);

        // Act
        Order order = Order.createFromCart(cartId, customerId, items); // Static factory method doesn't exist yet

        // Assert
        assertNotNull(order.getId());
        assertEquals(cartId, order.getCartId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("Should validate order cannot be created with empty items")
    void shouldValidateOrderCannotBeCreatedWithEmptyItems() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Order(orderId, cartId, customerId, Arrays.asList()));
        
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate order status transitions")
    void shouldValidateOrderStatusTransitions() {
        // Arrange
        OrderItem item = new OrderItem(UUID.randomUUID(), productId1, "Test Product", 
            BigDecimal.valueOf(100.00), 1);
        Order order = new Order(orderId, cartId, customerId, Arrays.asList(item));

        // Act & Assert - Test valid transition
        order.updateStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        // Test invalid transition from cancelled
        order.updateStatus(OrderStatus.CANCELLED);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> order.updateStatus(OrderStatus.CONFIRMED));
        assertEquals("Cannot change status of cancelled order", exception.getMessage());
    }
}