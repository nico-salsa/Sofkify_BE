package com.sofkify.productservice.infrastructure.web.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Product DTO and Factory Tests")
class ProductDtoFactoryTest {

    @Test
    @DisplayName("ErrorDtoFactory should build ErrorResponse")
    void errorDtoFactoryShouldBuild() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/products/1");

        ErrorResponse resp = ErrorDtoFactory.build(HttpStatus.NOT_FOUND, "Not found", request);

        assertEquals(404, resp.getStatus());
        assertEquals("Not Found", resp.getError());
        assertEquals("Not found", resp.getMessage());
        assertEquals("/api/products/1", resp.getPath());
        assertNotNull(resp.getTimestamp());
    }

    @Test
    @DisplayName("ErrorDtoFactory should build with details")
    void errorDtoFactoryShouldBuildWithDetails() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/products");

        ErrorResponse resp = ErrorDtoFactory.build(HttpStatus.BAD_REQUEST, "Validation failed", "details", request);

        assertEquals(400, resp.getStatus());
        assertEquals("details", resp.getDetails());
    }

    @Test
    @DisplayName("ErrorResponse builder should create object")
    void errorResponseBuilderShouldCreate() {
        ErrorResponse resp = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .error("Internal Server Error")
            .message("oops")
            .details(null)
            .path("/api/test")
            .build();

        assertEquals(500, resp.getStatus());
        assertEquals("Internal Server Error", resp.getError());
        assertEquals("oops", resp.getMessage());
        assertEquals("/api/test", resp.getPath());
    }

    @Test
    @DisplayName("ErrorResponse setters should work")
    void errorResponseSettersShouldWork() {
        ErrorResponse resp = ErrorResponse.builder().build();
        LocalDateTime now = LocalDateTime.now();
        resp.setTimestamp(now);
        resp.setStatus(400);
        resp.setError("Bad Request");
        resp.setMessage("msg");
        resp.setDetails("det");
        resp.setPath("/p");

        assertEquals(now, resp.getTimestamp());
        assertEquals(400, resp.getStatus());
        assertEquals("Bad Request", resp.getError());
        assertEquals("msg", resp.getMessage());
        assertEquals("det", resp.getDetails());
        assertEquals("/p", resp.getPath());
    }
}
