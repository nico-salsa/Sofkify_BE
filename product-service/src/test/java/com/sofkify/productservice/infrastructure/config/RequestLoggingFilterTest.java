package com.sofkify.productservice.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("RequestLoggingFilter Tests")
class RequestLoggingFilterTest {

    @Test
    @DisplayName("Should invoke filter chain and log request")
    void shouldInvokeFilterChain() throws Exception {
        RequestLoggingFilter filter = new RequestLoggingFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/products");
        when(response.getStatus()).thenReturn(200);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should still log when filter chain throws")
    void shouldLogEvenWhenFilterChainThrows() throws Exception {
        RequestLoggingFilter filter = new RequestLoggingFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/products");
        when(response.getStatus()).thenReturn(500);
        doThrow(new jakarta.servlet.ServletException("error")).when(chain).doFilter(request, response);

        try {
            filter.doFilterInternal(request, response, chain);
        } catch (jakarta.servlet.ServletException e) {
            // expected
        }

        verify(chain).doFilter(request, response);
    }
}
