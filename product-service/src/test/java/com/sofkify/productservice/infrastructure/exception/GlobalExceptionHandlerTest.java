package com.sofkify.productservice.infrastructure.exception;

import com.sofkify.productservice.domain.exception.*;
import com.sofkify.productservice.infrastructure.web.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Product GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/products");
    }

    @Test
    @DisplayName("Should handle ProductNotFoundException")
    void shouldHandleProductNotFound() {
        var resp = handler.handleProductNotFoundException(new ProductNotFoundException("not found"), webRequest);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle InsufficientStockException")
    void shouldHandleInsufficientStock() {
        var resp = handler.handleInsufficientStockException(new InsufficientStockException("no stock"), webRequest);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle InvalidProductPriceException")
    void shouldHandleInvalidPrice() {
        var resp = handler.handleInvalidProductPriceException(new InvalidProductPriceException("bad price"), webRequest);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle InvalidProductStockException")
    void shouldHandleInvalidStock() {
        var resp = handler.handleInvalidProductStockException(new InvalidProductStockException("bad stock"), webRequest);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle DuplicateSkuException")
    void shouldHandleDuplicateSku() {
        var resp = handler.handleDuplicateSkuException(new DuplicateSkuException("dup"), webRequest);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgument() {
        var resp = handler.handleIllegalArgumentException(new IllegalArgumentException("bad"), webRequest);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle RuntimeException")
    void shouldHandleRuntime() {
        var resp = handler.handleRuntimeException(new RuntimeException("err"), webRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGeneric() {
        var resp = handler.handleGlobalException(new Exception("err"), webRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void shouldHandleValidationExceptions() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "name", "must not be blank");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        var resp = handler.handleValidationExceptions(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertNotNull(resp.getBody());
    }
}
