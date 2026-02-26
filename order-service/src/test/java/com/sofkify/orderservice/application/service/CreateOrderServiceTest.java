package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.application.dto.CreateOrderRequest;
import com.sofkify.orderservice.application.dto.CreateOrderResponse;
import com.sofkify.orderservice.domain.event.OrderCreatedEvent;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.ports.out.EventPublisherPort;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CreateOrderService - GREEN Phase Tests")
class CreateOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    private CreateOrderService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CreateOrderService(orderRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should create order successfully from cart data")
    void shouldCreateOrderSuccessfully() {
        // Given
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        
        CreateOrderRequest request = new CreateOrderRequest(cartId, customerId);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        CreateOrderResponse response = service.execute(request);
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getOrderId());
        assertEquals(cartId, response.getCartId());
        assertTrue(response.isSuccess());
        
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("Should publish OrderCreatedEvent after creating order")
    void shouldPublishOrderCreatedEvent() {
        // Given
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        
        CreateOrderRequest request = new CreateOrderRequest(cartId, customerId);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        service.execute(request);
        
        // Then
        verify(eventPublisher).publish(argThat(event -> 
            event instanceof OrderCreatedEvent &&
            ((OrderCreatedEvent) event).getCartId().equals(cartId)
        ));
    }

    @Test
    @DisplayName("Should prevent duplicate orders for same cart (idempotency)")
    void shouldPreventDuplicateOrders() {
        // Given
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        
        CreateOrderRequest request = new CreateOrderRequest(cartId, customerId);
        
        when(orderRepository.existsByCartId(cartId)).thenReturn(true);
        
        // When / Then
        assertThrows(IllegalStateException.class, () -> service.execute(request));
        
        verify(orderRepository).existsByCartId(cartId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publish(any(OrderCreatedEvent.class));
    }
}
