package com.sofkify.orderservice.infrastructure.adapters.in.rest;

import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.model.OrderStatus;
import com.sofkify.orderservice.domain.ports.in.CreateOrderFromCartUseCase;
import com.sofkify.orderservice.domain.ports.in.GetOrderUseCase;
import com.sofkify.orderservice.domain.ports.in.GetOrdersByCustomerUseCase;
import com.sofkify.orderservice.domain.ports.in.UpdateOrderStatusUseCase;
import com.sofkify.orderservice.application.dto.UpdateOrderStatusRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("OrderController Tests")
class OrderControllerTest {

    @Mock private CreateOrderFromCartUseCase createOrderFromCartUseCase;
    @Mock private GetOrderUseCase getOrderUseCase;
    @Mock private GetOrdersByCustomerUseCase getOrdersByCustomerUseCase;
    @Mock private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new OrderController(createOrderFromCartUseCase, getOrderUseCase,
            getOrdersByCustomerUseCase, updateOrderStatusUseCase);
    }

    private Order createTestOrder() {
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "P", BigDecimal.ONE, 1);
        return new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(item));
    }

    @Test
    @DisplayName("Should create order from cart")
    void shouldCreateOrderFromCart() {
        UUID cartId = UUID.randomUUID();
        Order order = createTestOrder();
        when(createOrderFromCartUseCase.createOrderFromCart(cartId)).thenReturn(order);

        var response = controller.createOrderFromCart(cartId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order.getId(), response.getBody().id());
    }

    @Test
    @DisplayName("Should get order by id")
    void shouldGetOrder() {
        Order order = createTestOrder();
        when(getOrderUseCase.getOrderById(order.getId())).thenReturn(order);

        var response = controller.getOrder(order.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order.getId(), response.getBody().id());
    }

    @Test
    @DisplayName("Should get orders by customer")
    void shouldGetOrdersByCustomer() {
        Order order = createTestOrder();
        UUID customerId = order.getCustomerId();
        when(getOrdersByCustomerUseCase.getOrdersByCustomerId(customerId)).thenReturn(List.of(order));

        var response = controller.getOrdersByCustomer(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        Order order = createTestOrder();
        order.updateStatus(OrderStatus.CONFIRMED);
        when(updateOrderStatusUseCase.updateOrderStatus(order.getId(), OrderStatus.CONFIRMED)).thenReturn(order);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        var response = controller.updateOrderStatus(order.getId(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CONFIRMED", response.getBody().status());
    }
}
