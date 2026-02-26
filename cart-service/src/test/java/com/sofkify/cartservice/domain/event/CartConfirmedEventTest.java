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

@DisplayName("CartConfirmedEvent Domain Event Tests - GREEN Phase")
class CartConfirmedEventTest {

    @Test
    @DisplayName("Should create CartConfirmedEvent with all required fields")
    void shouldCreateCartConfirmedEventWithAllRequiredFields() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = createTestCartItems();
        BigDecimal totalAmount = new BigDecimal("49.98");

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items, totalAmount);

        // Assert
        assertNotNull(event.getEventId());
        assertEquals(cartId, event.getCartId());
        assertEquals(customerId, event.getCustomerId());
        assertEquals(items.size(), event.getItems().size());
        assertEquals(totalAmount, event.getTotalAmount());
        assertEquals("CartConfirmed", event.getEventType());
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should auto-generate eventId and timestamp")
    void shouldAutoGenerateEventIdAndTimestamp() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = createTestCartItems();
        BigDecimal totalAmount = new BigDecimal("49.98");

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items, totalAmount);

        // Assert
        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should convert CartItems to CartItemEvents correctly")
    void shouldConvertCartItemsToCartItemEvents() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = createTestCartItems();
        BigDecimal totalAmount = new BigDecimal("49.98");

        // Act
        CartConfirmedEvent event = new CartConfirmedEvent(cartId, customerId, items, totalAmount);

        // Assert
        List<CartConfirmedEvent.CartItemEvent> eventItems = event.getItems();
        assertEquals(2, eventItems.size());
        
        CartConfirmedEvent.CartItemEvent firstEventItem = eventItems.get(0);
        CartItem firstItem = items.get(0);
        assertEquals(firstItem.getProductId(), firstEventItem.productId());
        assertEquals(firstItem.getProductName(), firstEventItem.productName());
        assertEquals(firstItem.getQuantity(), firstEventItem.quantity());
        assertEquals(firstItem.getProductPrice(), firstEventItem.unitPrice());
        assertEquals(firstItem.getSubtotal(), firstEventItem.totalPrice());
    }

    @Test
    @DisplayName("Should implement equality based on eventId")
    void shouldImplementEqualityBasedOnEventId() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = createTestCartItems();
        BigDecimal totalAmount = new BigDecimal("49.98");

        // Act
        CartConfirmedEvent event1 = new CartConfirmedEvent(cartId, customerId, items, totalAmount);
        CartConfirmedEvent event2 = new CartConfirmedEvent(cartId, customerId, items, totalAmount);

        // Assert - Los eventos serán diferentes porque cada uno genera su propio eventId
        assertNotEquals(event1, event2);
        assertNotEquals(event1.hashCode(), event2.hashCode());
        
        // Pero deben tener el mismo contenido de negocio
        assertEquals(event1.getCartId(), event2.getCartId());
        assertEquals(event1.getCustomerId(), event2.getCustomerId());
        assertEquals(event1.getTotalAmount(), event2.getTotalAmount());
    }

    @Test
    @DisplayName("Should reject null cartId")
    void shouldRejectNullCartId() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = createTestCartItems();
        BigDecimal totalAmount = new BigDecimal("49.98");

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new CartConfirmedEvent(null, customerId, items, totalAmount));
    }

    @Test
    @DisplayName("Should reject null customerId")
    void shouldRejectNullCustomerId() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        List<CartItem> items = createTestCartItems();
        BigDecimal totalAmount = new BigDecimal("49.98");

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new CartConfirmedEvent(cartId, null, items, totalAmount));
    }

    @Test
    @DisplayName("Should reject null totalAmount")
    void shouldRejectNullTotalAmount() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<CartItem> items = createTestCartItems();

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new CartConfirmedEvent(cartId, customerId, items, null));
    }

    private List<CartItem> createTestCartItems() {
        return Arrays.asList(
                new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product A", new BigDecimal("19.99"), 2),
                new CartItem(UUID.randomUUID(), UUID.randomUUID(), "Product B", new BigDecimal("29.99"), 1)
        );
    }
}