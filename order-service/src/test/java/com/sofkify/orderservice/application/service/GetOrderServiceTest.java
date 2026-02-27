package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.domain.exception.OrderNotFoundException;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
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
import static org.mockito.Mockito.*;

@DisplayName("GetOrderService Tests")
class GetOrderServiceTest {

    @Mock private OrderRepositoryPort orderRepository;
    private GetOrderService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GetOrderService(orderRepository);
    }

    private Order createTestOrder() {
        UUID orderId = UUID.randomUUID();
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.ONE, 1);
        return new Order(orderId, UUID.randomUUID(), UUID.randomUUID(), List.of(item));
    }

    @Test
    @DisplayName("Should get order by id")
    void shouldGetOrderById() {
        Order order = createTestOrder();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Order result = service.getOrderById(order.getId());
        assertEquals(order.getId(), result.getId());
    }

    @Test
    @DisplayName("Should throw when order not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.getOrderById(id));
    }

    @Test
    @DisplayName("Should get orders by customer id")
    void shouldGetOrdersByCustomerId() {
        Order order = createTestOrder();
        UUID customerId = order.getCustomerId();
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of(order));

        List<Order> result = service.getOrdersByCustomerId(customerId);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return empty list when no orders for customer")
    void shouldReturnEmptyList() {
        UUID customerId = UUID.randomUUID();
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of());

        List<Order> result = service.getOrdersByCustomerId(customerId);
        assertTrue(result.isEmpty());
    }
}
