package com.sofkify.orderservice.domain.event;

import com.sofkify.orderservice.domain.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderCreatedEvent Tests")
class OrderCreatedEventTest {

    @Test
    @DisplayName("Should create event with all fields")
    void shouldCreateEventWithAllFields() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OrderCreatedEvent.OrderItemEvent itemEvent = new OrderCreatedEvent.OrderItemEvent(
            UUID.randomUUID(), "Product A", 2, BigDecimal.valueOf(10.00), BigDecimal.valueOf(20.00)
        );

        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId, customerId, cartId, List.of(itemEvent), BigDecimal.valueOf(20.00), now
        );

        assertEquals(orderId, event.getOrderId());
        assertEquals(customerId, event.getCustomerId());
        assertEquals(cartId, event.getCartId());
        assertEquals(1, event.getItems().size());
        assertEquals(BigDecimal.valueOf(20.00), event.getTotalAmount());
        assertEquals(now, event.getCreatedAt());
    }

    @Test
    @DisplayName("Should create event with five-arg constructor (domain items)")
    void shouldCreateWithFiveArgConstructor() {
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        OrderItem orderItem = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "P",
            java.math.BigDecimal.TEN, 1);

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, cartId, customerId,
            java.math.BigDecimal.TEN, List.of(orderItem));

        assertEquals(orderId, event.getOrderId());
        assertEquals(customerId, event.getCustomerId());
        assertEquals(cartId, event.getCartId());
        assertNotNull(event.getEventId());
        assertEquals("OrderCreated", event.getEventType());
    }

    @Test
    @DisplayName("OrderItemEvent should hold values")
    void orderItemEventShouldHoldValues() {
        UUID productId = UUID.randomUUID();
        OrderCreatedEvent.OrderItemEvent itemEvent = new OrderCreatedEvent.OrderItemEvent(
            productId, "Test", 3, BigDecimal.valueOf(5.00), BigDecimal.valueOf(15.00)
        );

        assertEquals(productId, itemEvent.productId());
        assertEquals("Test", itemEvent.productName());
        assertEquals(3, itemEvent.quantity());
        assertEquals(BigDecimal.valueOf(5.00), itemEvent.unitPrice());
        assertEquals(BigDecimal.valueOf(15.00), itemEvent.totalPrice());
    }

    @Test
    @DisplayName("Should return occurred on timestamp")
    void shouldReturnOccurredOn() {
        UUID orderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId, UUID.randomUUID(), UUID.randomUUID(), List.of(), BigDecimal.ZERO, now
        );
        assertEquals(now, event.getOccurredOn());
    }

    @Test
    @DisplayName("toString should contain orderId")
    void toStringShouldContainOrderId() {
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId, UUID.randomUUID(), UUID.randomUUID(), List.of(), BigDecimal.ZERO, LocalDateTime.now()
        );
        assertTrue(event.toString().contains(orderId.toString()));
    }
}
