package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.application.dto.CartItemResponse;
import com.sofkify.orderservice.application.dto.CartResponse;
import com.sofkify.orderservice.domain.event.OrderCreatedEvent;
import com.sofkify.orderservice.domain.exception.InvalidCartException;
import com.sofkify.orderservice.domain.exception.OrderAlreadyExistsException;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.ports.out.CartServicePort;
import com.sofkify.orderservice.domain.ports.out.EventPublisherPort;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CreateOrderFromCartService Tests")
class CreateOrderFromCartServiceTest {

    @Mock private OrderRepositoryPort orderRepository;
    @Mock private CartServicePort cartServicePort;
    @Mock private EventPublisherPort eventPublisher;

    private CreateOrderFromCartService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CreateOrderFromCartService(orderRepository, cartServicePort, eventPublisher);
    }

    @Test
    @DisplayName("Should create order from confirmed cart")
    void shouldCreateOrderFromConfirmedCart() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CartItemResponse item = new CartItemResponse(
            UUID.randomUUID(), UUID.randomUUID(), "Product", BigDecimal.valueOf(10.00),
            2, BigDecimal.valueOf(20.00), LocalDateTime.now(), LocalDateTime.now()
        );
        CartResponse cart = new CartResponse(
            cartId, customerId, "CONFIRMED", List.of(item),
            BigDecimal.valueOf(20.00), LocalDateTime.now(), LocalDateTime.now()
        );

        when(orderRepository.existsByCartId(cartId)).thenReturn(false);
        when(cartServicePort.getCartById(cartId)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = service.createOrderFromCart(cartId);

        assertNotNull(result);
        assertEquals(cartId, result.getCartId());
        assertEquals(customerId, result.getCustomerId());
        verify(eventPublisher).publish(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("Should reject null cartId")
    void shouldRejectNullCartId() {
        assertThrows(InvalidCartException.class, () -> service.createOrderFromCart(null));
    }

    @Test
    @DisplayName("Should reject duplicate order for same cart")
    void shouldRejectDuplicateOrder() {
        UUID cartId = UUID.randomUUID();
        when(orderRepository.existsByCartId(cartId)).thenReturn(true);

        assertThrows(OrderAlreadyExistsException.class, () -> service.createOrderFromCart(cartId));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject non-confirmed cart")
    void shouldRejectNonConfirmedCart() {
        UUID cartId = UUID.randomUUID();
        CartResponse cart = new CartResponse(
            cartId, UUID.randomUUID(), "ACTIVE", List.of(),
            BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now()
        );

        when(orderRepository.existsByCartId(cartId)).thenReturn(false);
        when(cartServicePort.getCartById(cartId)).thenReturn(cart);

        assertThrows(InvalidCartException.class, () -> service.createOrderFromCart(cartId));
    }

    @Test
    @DisplayName("Should reject null cart response")
    void shouldRejectNullCartResponse() {
        UUID cartId = UUID.randomUUID();
        when(orderRepository.existsByCartId(cartId)).thenReturn(false);
        when(cartServicePort.getCartById(cartId)).thenReturn(null);

        assertThrows(InvalidCartException.class, () -> service.createOrderFromCart(cartId));
    }

    @Test
    @DisplayName("Should reject confirmed cart with empty items")
    void shouldRejectEmptyItems() {
        UUID cartId = UUID.randomUUID();
        CartResponse cart = new CartResponse(
            cartId, UUID.randomUUID(), "CONFIRMED", List.of(),
            BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now()
        );

        when(orderRepository.existsByCartId(cartId)).thenReturn(false);
        when(cartServicePort.getCartById(cartId)).thenReturn(cart);

        assertThrows(InvalidCartException.class, () -> service.createOrderFromCart(cartId));
    }

    @Test
    @DisplayName("Should reject null constructor params")
    void shouldRejectNullConstructorParams() {
        assertThrows(NullPointerException.class,
            () -> new CreateOrderFromCartService(null, cartServicePort, eventPublisher));
        assertThrows(NullPointerException.class,
            () -> new CreateOrderFromCartService(orderRepository, null, eventPublisher));
        assertThrows(NullPointerException.class,
            () -> new CreateOrderFromCartService(orderRepository, cartServicePort, null));
    }
}
