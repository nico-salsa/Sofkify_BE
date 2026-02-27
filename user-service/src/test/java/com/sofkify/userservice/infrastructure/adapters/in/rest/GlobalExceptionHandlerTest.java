package com.sofkify.userservice.infrastructure.adapters.in.rest;

import com.sofkify.userservice.application.exception.UserAlreadyExistsException;
import com.sofkify.userservice.application.exception.UserNotFoundException;
import com.sofkify.userservice.domain.exception.UserValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle UserNotFoundException")
    void shouldHandleUserNotFound() {
        var resp = handler.handleUserNotFound(new UserNotFoundException("not found"));
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("User Not Found", resp.getBody().get("error"));
    }

    @Test
    @DisplayName("Should handle UserAlreadyExistsException")
    void shouldHandleUserAlreadyExists() {
        var resp = handler.handleUserAlreadyExists(new UserAlreadyExistsException("exists"));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("User Already Exists", resp.getBody().get("error"));
    }

    @Test
    @DisplayName("Should handle UserValidationException")
    void shouldHandleUserValidation() {
        var resp = handler.handleUserValidation(new UserValidationException("invalid"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Validation Error", resp.getBody().get("error"));
    }
}
