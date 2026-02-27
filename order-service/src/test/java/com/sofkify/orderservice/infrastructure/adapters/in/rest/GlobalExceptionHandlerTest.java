package com.sofkify.orderservice.infrastructure.adapters.in.rest;

import com.sofkify.orderservice.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle OrderNotFoundException")
    void shouldHandleOrderNotFound() {
        var resp = handler.handleOrderNotFound(new OrderNotFoundException(UUID.randomUUID()));
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals(404, resp.getBody().status());
    }

    @Test
    @DisplayName("Should handle CartNotFoundException")
    void shouldHandleCartNotFound() {
        var resp = handler.handleCartNotFound(new CartNotFoundException(UUID.randomUUID()));
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle InvalidCartException")
    void shouldHandleInvalidCart() {
        var resp = handler.handleInvalidCart(new InvalidCartException("bad"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle OrderAlreadyExistsException")
    void shouldHandleOrderAlreadyExists() {
        var resp = handler.handleOrderAlreadyExists(new OrderAlreadyExistsException(UUID.randomUUID()));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgument() {
        var resp = handler.handleIllegalArgument(new IllegalArgumentException("bad arg"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle IllegalStateException")
    void shouldHandleIllegalState() {
        var resp = handler.handleIllegalState(new IllegalStateException("bad state"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGenericException() {
        var resp = handler.handleGenericException(new Exception("oops"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    @DisplayName("ErrorResponse record should hold values")
    void errorResponseRecord() {
        var now = java.time.LocalDateTime.now();
        var err = new GlobalExceptionHandler.ErrorResponse(400, "Bad Request", "msg", now);
        assertEquals(400, err.status());
        assertEquals("Bad Request", err.error());
        assertEquals("msg", err.message());
        assertEquals(now, err.timestamp());
        assertNull(err.errors());
    }

    @Test
    @DisplayName("ErrorResponse with errors map")
    void errorResponseWithErrors() {
        var errors = java.util.Map.of("field", "required");
        var err = new GlobalExceptionHandler.ErrorResponse(400, "Bad", "msg", java.time.LocalDateTime.now(), errors);
        assertNotNull(err.errors());
        assertEquals("required", err.errors().get("field"));
    }
}
