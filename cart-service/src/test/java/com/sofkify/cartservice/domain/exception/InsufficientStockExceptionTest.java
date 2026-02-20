package com.sofkify.cartservice.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InsufficientStockException Tests")
class InsufficientStockExceptionTest {

    @Test
    @DisplayName("Should create exception with product ID and message")
    void shouldCreateExceptionWithProductIdAndMessage() {
        // Arrange
        UUID productId = UUID.randomUUID();
        String expectedMessage = "Stock not available for product: " + productId;

        // Act - This should fail because InsufficientStockException doesn't exist yet
        InsufficientStockException exception = new InsufficientStockException(productId);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(productId, exception.getProductId());
    }

    @Test
    @DisplayName("Should create exception with product ID, requested and available quantities")
    void shouldCreateExceptionWithProductIdRequestedAndAvailableQuantities() {
        // Arrange
        UUID productId = UUID.randomUUID();
        int requestedQuantity = 10;
        int availableQuantity = 3;
        String expectedMessage = String.format(
            "Insufficient stock for product %s. Requested: %d, Available: %d", 
            productId, requestedQuantity, availableQuantity);

        // Act - Constructor overload doesn't exist yet
        InsufficientStockException exception = new InsufficientStockException(
            productId, requestedQuantity, availableQuantity);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(productId, exception.getProductId());
        assertEquals(requestedQuantity, exception.getRequestedQuantity());
        assertEquals(availableQuantity, exception.getAvailableQuantity());
    }

    @Test
    @DisplayName("Should create exception with custom message")
    void shouldCreateExceptionWithCustomMessage() {
        // Arrange
        UUID productId = UUID.randomUUID();
        String customMessage = "Custom stock validation error";

        // Act - Constructor overload doesn't exist yet
        InsufficientStockException exception = new InsufficientStockException(productId, customMessage);

        // Assert
        assertEquals(customMessage, exception.getMessage());
        assertEquals(productId, exception.getProductId());
    }

    @Test
    @DisplayName("Should be a runtime exception")
    void shouldBeARuntimeException() {
        // Act
        UUID productId = UUID.randomUUID();
        InsufficientStockException exception = new InsufficientStockException(productId);

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should have error code for API responses")
    void shouldHaveErrorCodeForApiResponses() {
        // Act
        UUID productId = UUID.randomUUID();
        InsufficientStockException exception = new InsufficientStockException(productId);

        // Assert - Method doesn't exist yet
        assertEquals("INSUFFICIENT_STOCK", exception.getErrorCode());
    }

    @Test
    @DisplayName("Should provide product details for logging")
    void shouldProvideProductDetailsForLogging() {
        // Arrange
        UUID productId = UUID.randomUUID();
        int requestedQuantity = 5;
        int availableQuantity = 2;

        // Act
        InsufficientStockException exception = new InsufficientStockException(
            productId, requestedQuantity, availableQuantity);

        // Assert - Method doesn't exist yet
        String details = exception.getProductDetails();
        assertTrue(details.contains(productId.toString()));
        assertTrue(details.contains("5"));
        assertTrue(details.contains("2"));
    }
}