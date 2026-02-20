package com.sofkify.cartservice.domain.event;

import com.sofkify.cartservice.domain.model.CartItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartConfirmedEvent Domain Event Tests")
class CartConfirmedEventTest {

    @Test
    @DisplayName("Should create CartConfirmedEvent with required fields")
    void shouldCreateCartConfirmedEventWithRequiredFields() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product 1", BigDecimal.valueOf(29.99), 3),
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product 2", BigDecimal.valueOf(15.00), 2)
        );
        LocalDateTime confirmedAt = LocalDateTime.now();

        // Act - This should fail because CartConfirmedEvent doesn't exist yet
        CartConfirmedEvent event = new CartConfirmedEvent(eventId, cartId, customerId, items, confirmedAt);

        // Assert
        assertEquals(eventId, event.getEventId());
        assertEquals(cartId, event.getCartId());
        assertEquals(customerId, event.getCustomerId());
        assertEquals(items, event.getItems());
        assertEquals(confirmedAt, event.getConfirmedAt());
    }

    @Test
    @DisplayName("Should generate event ID and timestamp automatically if not provided")
    void shouldGenerateEventIdAndTimestampAutomaticallyIfNotProvided() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(99.99), 1)
        );

        // Act - Constructor overload doesn't exist yet
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items);

        // Assert
        assertNotNull(event.getEventId());
        assertEquals(cartId, event.getCartId());
        assertEquals(customerId, event.getCustomerId());
        assertNotNull(event.getConfirmedAt());
        assertTrue(event.getConfirmedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(50.00), 2)
        );

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items);

        // Assert - Test immutability by trying to modify returned collections
        List<CartItem> returnedItems = event.getItems();
        assertThrows(UnsupportedOperationException.class, () -> returnedItems.add(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "New Product", BigDecimal.valueOf(25.00), 1)
        ));
    }

    @Test
    @DisplayName("Should implement equals and hashCode based on event ID")
    void shouldImplementEqualsAndHashCodeBasedOnEventId() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(75.00), 1)
        );
        LocalDateTime confirmedAt = LocalDateTime.now();

        CartConfirmedEvent event1 = new CartConfirmedEvent(eventId, cartId, customerId, items, confirmedAt);
        CartConfirmedEvent event2 = new CartConfirmedEvent(eventId, cartId, customerId, items, confirmedAt);
        CartConfirmedEvent event3 = new CartConfirmedEvent(UUID.randomUUID(), cartId, customerId, items, confirmedAt);

        // Assert
        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Should validate required fields are not null")
    void shouldValidateRequiredFieldsAreNotNull() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(40.00), 1)
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> new CartConfirmedEvent(null, customerId, items));
        assertThrows(IllegalArgumentException.class, 
            () -> new CartConfirmedEvent(cartId, null, items));
        assertThrows(IllegalArgumentException.class, 
            () -> new CartConfirmedEvent(cartId, customerId, null));
        assertThrows(IllegalArgumentException.class, 
            () -> new CartConfirmedEvent(cartId, customerId, Arrays.asList())); // Empty items
    }

    @Test
    @DisplayName("Should provide event type for routing")
    void shouldProvideEventTypeForRouting() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(60.00), 1)
        );

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items);

        // Assert - Method doesn't exist yet
        assertEquals("CartConfirmedEvent", event.getEventType());
    }

    @Test
    @DisplayName("Should provide correlation ID for tracing")
    void shouldProvideCorrelationIdForTracing() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(35.00), 1)
        );

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items);

        // Assert - Method doesn't exist yet
        assertNotNull(event.getCorrelationId());
        // Correlation ID could be based on cart ID for this event type
        assertEquals(cartId.toString(), event.getCorrelationId());
    }

    @Test
    @DisplayName("Should calculate total amount from items")
    void shouldCalculateTotalAmountFromItems() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product 1", BigDecimal.valueOf(25.00), 2), // 50.00
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product 2", BigDecimal.valueOf(15.00), 3)  // 45.00
        );

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items);

        // Assert - Method doesn't exist yet
        assertEquals(BigDecimal.valueOf(95.00), event.getTotalAmount());
    }

    @Test
    @DisplayName("Should serialize to JSON for messaging")
    void shouldSerializeToJsonForMessaging() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Test Product", BigDecimal.valueOf(45.00), 1)
        );

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items);

        // Assert - Method doesn't exist yet
        String json = event.toJson();
        assertNotNull(json);
        assertTrue(json.contains(cartId.toString()));
        assertTrue(json.contains(customerId.toString()));
        assertTrue(json.contains("Test Product"));
    }

    @Test
    @DisplayName("Should provide item count for analytics")
    void shouldProvideItemCountForAnalytics() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = Arrays.asList(
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product 1", BigDecimal.valueOf(20.00), 1),
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product 2", BigDecimal.valueOf(30.00), 2),
            new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product 3", BigDecimal.valueOf(10.00), 3)
        );

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items);

        // Assert - Methods don't exist yet
        assertEquals(3, event.getUniqueItemCount()); // Different products
        assertEquals(6, event.getTotalItemQuantity()); // Sum of all quantities: 1+2+3
    }
}