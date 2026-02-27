package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.domain.exception.OrderNotFoundException;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.model.OrderStatus;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UpdateOrderStatusService Tests")
class UpdateOrderStatusServiceTest {

    @Mock private OrderRepositoryPort orderRepository;
    private UpdateOrderStatusService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UpdateOrderStatusService(orderRepository);
    }

    private Order createTestOrder() {
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.ONE, 1);
        return new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(item));
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateStatus() {
        Order order = createTestOrder();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = service.updateOrderStatus(order.getId(), OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw when order not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.updateOrderStatus(id, OrderStatus.CONFIRMED));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate illegal state for invalid transition")
    void shouldPropagateIllegalTransition() {
        Order order = createTestOrder();
        order.updateStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
            () -> service.updateOrderStatus(order.getId(), OrderStatus.CONFIRMED));
    }
}
