package com.sofkify.cartservice.infrastructure.adapters.in.rest;

import com.sofkify.cartservice.domain.exception.CartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/carts");
    }

    @Test
    @DisplayName("Should handle CartException as 400")
    void shouldHandleCartException() {
        CartException ex = new CartException("Cart not found");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleCartException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Cart Error", response.getBody().getError());
        assertEquals("Cart not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("uri=/api/v1/carts", response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle RuntimeException as 500")
    void shouldHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleRuntimeException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    @DisplayName("Should handle generic Exception as 500")
    void shouldHandleGenericException() {
        Exception ex = new Exception("Something bad");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Unexpected Error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    @DisplayName("ErrorResponse should have getters and setters")
    void errorResponseGettersSetters() {
        GlobalExceptionHandler.ErrorResponse r = new GlobalExceptionHandler.ErrorResponse();
        r.setStatus(404);
        r.setError("Not Found");
        r.setMessage("Resource missing");
        r.setPath("/api/test");
        r.setValidationErrors(Map.of("field", "required"));

        assertEquals(404, r.getStatus());
        assertEquals("Not Found", r.getError());
        assertEquals("Resource missing", r.getMessage());
        assertEquals("/api/test", r.getPath());
        assertNotNull(r.getValidationErrors());
        assertEquals("required", r.getValidationErrors().get("field"));
    }

    @Test
    @DisplayName("ErrorResponse parameterized constructor")
    void errorResponseParameterizedConstructor() {
        var now = java.time.LocalDateTime.now();
        GlobalExceptionHandler.ErrorResponse r =
            new GlobalExceptionHandler.ErrorResponse(now, 400, "Bad Request", "Invalid", "/api");

        assertEquals(now, r.getTimestamp());
        assertEquals(400, r.getStatus());
        assertEquals("Bad Request", r.getError());
        assertEquals("Invalid", r.getMessage());
        assertEquals("/api", r.getPath());
        assertNull(r.getValidationErrors());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with UUID message")
    void shouldHandleJsonParseExceptionWithUuid() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Cannot deserialize value of type UUID");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleJsonParseException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("UUID"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with quantity message")
    void shouldHandleJsonParseExceptionWithQuantity() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Cannot deserialize quantity field");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleJsonParseException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("quantity"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with generic message")
    void shouldHandleJsonParseExceptionGeneric() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Some other parse error");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleJsonParseException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid JSON format", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with null message")
    void shouldHandleJsonParseExceptionNullMessage() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn(null);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
            handler.handleJsonParseException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid JSON format", response.getBody().getMessage());
    }
}
