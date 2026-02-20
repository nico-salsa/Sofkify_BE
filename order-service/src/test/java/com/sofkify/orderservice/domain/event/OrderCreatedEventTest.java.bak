package com.sofkify.orderservice.domain.event;

import com.sofkify.orderservice.domain.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderCreatedEvent Domain Event Tests")
class OrderCreatedEventTest {

    @Test
    @DisplayName("Should create OrderCreatedEvent with required fields")
    void shouldCreateOrderCreatedEventWithRequiredFields() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product 1", BigDecimal.valueOf(99.99), 2),
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product 2", BigDecimal.valueOf(49.99), 1)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(249.97);
        LocalDateTime occurredOn = LocalDateTime.now();

        // Act - This should fail because OrderCreatedEvent doesn't exist yet
        OrderCreatedEvent event = new OrderCreatedEvent(eventId, orderId, cartId, customerId, items, totalAmount, occurredOn);

        // Assert
        assertEquals(eventId, event.getEventId());
        assertEquals(orderId, event.getOrderId());
        assertEquals(cartId, event.getCartId());
        assertEquals(customerId, event.getCustomerId());
        assertEquals(items, event.getItems());
        assertEquals(totalAmount, event.getTotalAmount());
        assertEquals(occurredOn, event.getOccurredOn());
    }

    @Test
    @DisplayName("Should generate event ID automatically if not provided")
    void shouldGenerateEventIdAutomaticallyIfNotProvided() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(50.00), 1)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(50.00);

        // Act - Constructor overload doesn't exist yet
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, cartId, customerId, items, totalAmount);

        // Assert
        assertNotNull(event.getEventId());
        assertEquals(orderId, event.getOrderId());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(25.00), 2)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(50.00);

        // Act
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, cartId, customerId, items, totalAmount);

        // Assert - Test immutability by trying to modify returned collections
        List<OrderItem> returnedItems = event.getItems();
        assertThrows(UnsupportedOperationException.class, () -> returnedItems.add(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "New Product", BigDecimal.valueOf(10.00), 1)
        ));
    }

    @Test
    @DisplayName("Should implement equals and hashCode based on event ID")
    void shouldImplementEqualsAndHashCodeBasedOnEventId() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(100.00), 1)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(100.00);
        LocalDateTime occurredOn = LocalDateTime.now();

        OrderCreatedEvent event1 = new OrderCreatedEvent(eventId, orderId, cartId, customerId, items, totalAmount, occurredOn);
        OrderCreatedEvent event2 = new OrderCreatedEvent(eventId, orderId, cartId, customerId, items, totalAmount, occurredOn);
        OrderCreatedEvent event3 = new OrderCreatedEvent(UUID.randomUUID(), orderId, cartId, customerId, items, totalAmount, occurredOn);

        // Assert
        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Should validate required fields are not null")
    void shouldValidateRequiredFieldsAreNotNull() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(50.00), 1)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(50.00);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> new OrderCreatedEvent(null, cartId, customerId, items, totalAmount));
        assertThrows(IllegalArgumentException.class, 
            () -> new OrderCreatedEvent(orderId, null, customerId, items, totalAmount));
        assertThrows(IllegalArgumentException.class, 
            () -> new OrderCreatedEvent(orderId, cartId, null, items, totalAmount));
        assertThrows(IllegalArgumentException.class, 
            () -> new OrderCreatedEvent(orderId, cartId, customerId, null, totalAmount));
        assertThrows(IllegalArgumentException.class, 
            () -> new OrderCreatedEvent(orderId, cartId, customerId, items, null));
    }

    @Test
    @DisplayName("Should provide event type for routing")
    void shouldProvideEventTypeForRouting() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(75.00), 1)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(75.00);

        // Act
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, cartId, customerId, items, totalAmount);

        // Assert - Method doesn't exist yet
        assertEquals("OrderCreatedEvent", event.getEventType());
    }

    @Test
    @DisplayName("Should provide correlation ID for tracing")
    void shouldProvideCorrelationIdForTracing() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(30.00), 1)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(30.00);

        // Act
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, cartId, customerId, items, totalAmount);

        // Assert - Method doesn't exist yet
        assertNotNull(event.getCorrelationId());
        // Correlation ID could be based on order ID for this event type
        assertEquals(orderId.toString(), event.getCorrelationId());
    }

    @Test
    @DisplayName("Should serialize to JSON for messaging")
    void shouldSerializeToJsonForMessaging() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<OrderItem> items = Arrays.asList(
            new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Test Product", BigDecimal.valueOf(40.00), 1)
        );
        BigDecimal totalAmount = BigDecimal.valueOf(40.00);

        // Act
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, cartId, customerId, items, totalAmount);

        // Assert - Method doesn't exist yet
        String json = event.toJson();
        assertNotNull(json);
        assertTrue(json.contains(orderId.toString()));
        assertTrue(json.contains(cartId.toString()));
        assertTrue(json.contains("Test Product"));
    }
}