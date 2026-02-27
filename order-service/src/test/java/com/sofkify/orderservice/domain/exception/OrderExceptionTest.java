package com.sofkify.orderservice.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Domain Exception Tests")
class OrderExceptionTest {

    @Test
    @DisplayName("OrderException with message")
    void orderExceptionWithMessage() {
        OrderException ex = new OrderException("test");
        assertEquals("test", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("OrderNotFoundException with UUID")
    void orderNotFoundWithUUID() {
        UUID id = UUID.randomUUID();
        OrderNotFoundException ex = new OrderNotFoundException(id);
        assertTrue(ex.getMessage().contains(id.toString()));
    }

    @Test
    @DisplayName("OrderNotFoundException with message")
    void orderNotFoundWithMessage() {
        OrderNotFoundException ex = new OrderNotFoundException("custom msg");
        assertEquals("custom msg", ex.getMessage());
        assertInstanceOf(OrderException.class, ex);
    }

    @Test
    @DisplayName("CartNotFoundException with UUID")
    void cartNotFoundWithUUID() {
        UUID id = UUID.randomUUID();
        CartNotFoundException ex = new CartNotFoundException(id);
        assertTrue(ex.getMessage().contains(id.toString()));
    }

    @Test
    @DisplayName("CartNotFoundException with message")
    void cartNotFoundWithMessage() {
        CartNotFoundException ex = new CartNotFoundException("msg");
        assertEquals("msg", ex.getMessage());
        assertInstanceOf(OrderException.class, ex);
    }

    @Test
    @DisplayName("OrderAlreadyExistsException with UUID")
    void orderAlreadyExistsWithUUID() {
        UUID id = UUID.randomUUID();
        OrderAlreadyExistsException ex = new OrderAlreadyExistsException(id);
        assertTrue(ex.getMessage().contains(id.toString()));
    }

    @Test
    @DisplayName("OrderAlreadyExistsException with message")
    void orderAlreadyExistsWithMessage() {
        OrderAlreadyExistsException ex = new OrderAlreadyExistsException("msg");
        assertEquals("msg", ex.getMessage());
        assertInstanceOf(OrderException.class, ex);
    }

    @Test
    @DisplayName("InvalidCartException with message")
    void invalidCartWithMessage() {
        InvalidCartException ex = new InvalidCartException("bad cart");
        assertEquals("bad cart", ex.getMessage());
        assertInstanceOf(OrderException.class, ex);
    }

    @Test
    @DisplayName("InvalidCartException with UUID and reason")
    void invalidCartWithUUIDAndReason() {
        UUID id = UUID.randomUUID();
        InvalidCartException ex = new InvalidCartException(id, "reason");
        assertTrue(ex.getMessage().contains(id.toString()));
        assertTrue(ex.getMessage().contains("reason"));
    }
}
