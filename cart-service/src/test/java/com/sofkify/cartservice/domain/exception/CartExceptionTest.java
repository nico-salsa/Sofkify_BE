package com.sofkify.cartservice.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartException Tests")
class CartExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateWithMessage() {
        CartException ex = new CartException("Something went wrong");
        assertEquals("Something went wrong", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        CartException ex = new CartException("Wrapped error", cause);
        assertEquals("Wrapped error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("Should be a RuntimeException")
    void shouldBeRuntimeException() {
        CartException ex = new CartException("test");
        assertInstanceOf(RuntimeException.class, ex);
    }
}
