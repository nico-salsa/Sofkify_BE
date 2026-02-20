package com.sofkify.cartservice.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartAlreadyConfirmedException Tests")
class CartAlreadyConfirmedExceptionTest {

    @Test
    @DisplayName("Should create exception with cart ID and default message")
    void shouldCreateExceptionWithCartIdAndDefaultMessage() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        String expectedMessage = "Cart is already confirmed";

        // Act - This should fail because CartAlreadyConfirmedException doesn't exist yet
        CartAlreadyConfirmedException exception = new CartAlreadyConfirmedException(cartId);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(cartId, exception.getCartId());
    }

    @Test
    @DisplayName("Should create exception with cart ID and custom message")
    void shouldCreateExceptionWithCartIdAndCustomMessage() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        String customMessage = "Cannot modify a confirmed cart";

        // Act - Constructor overload doesn't exist yet
        CartAlreadyConfirmedException exception = new CartAlreadyConfirmedException(cartId, customMessage);

        // Assert
        assertEquals(customMessage, exception.getMessage());
        assertEquals(cartId, exception.getCartId());
    }

    @Test
    @DisplayName("Should create exception with cart ID and confirmation timestamp")
    void shouldCreateExceptionWithCartIdAndConfirmationTimestamp() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        java.time.LocalDateTime confirmationTime = java.time.LocalDateTime.now();
        String expectedMessage = String.format(
            "Cart %s was already confirmed at %s", cartId, confirmationTime);

        // Act - Constructor overload doesn't exist yet
        CartAlreadyConfirmedException exception = new CartAlreadyConfirmedException(
            cartId, confirmationTime);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(cartId, exception.getCartId());
        assertEquals(confirmationTime, exception.getConfirmationTime());
    }

    @Test
    @DisplayName("Should be a runtime exception")
    void shouldBeARuntimeException() {
        // Act
        UUID cartId = UUID.randomUUID();
        CartAlreadyConfirmedException exception = new CartAlreadyConfirmedException(cartId);

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should have error code for API responses")
    void shouldHaveErrorCodeForApiResponses() {
        // Act
        UUID cartId = UUID.randomUUID();
        CartAlreadyConfirmedException exception = new CartAlreadyConfirmedException(cartId);

        // Assert - Method doesn't exist yet
        assertEquals("CART_ALREADY_CONFIRMED", exception.getErrorCode());
    }

    @Test
    @DisplayName("Should provide cart details for logging")
    void shouldProvideCartDetailsForLogging() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        java.time.LocalDateTime confirmationTime = java.time.LocalDateTime.now();

        // Act
        CartAlreadyConfirmedException exception = new CartAlreadyConfirmedException(
            cartId, confirmationTime);

        // Assert - Method doesn't exist yet
        String details = exception.getCartDetails();
        assertTrue(details.contains(cartId.toString()));
        assertTrue(details.contains(confirmationTime.toString()));
    }

    @Test
    @DisplayName("Should handle null cart ID gracefully")
    void shouldHandleNullCartIdGracefully() {
        // Act & Assert - Should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new CartAlreadyConfirmedException(null));

        assertEquals("Cart ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with cause")
    void shouldCreateExceptionWithCause() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Exception cause = new RuntimeException("Database constraint violation");

        // Act - Constructor overload doesn't exist yet
        CartAlreadyConfirmedException exception = new CartAlreadyConfirmedException(cartId, cause);

        // Assert
        assertEquals(cartId, exception.getCartId());
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Cart is already confirmed"));
    }
}