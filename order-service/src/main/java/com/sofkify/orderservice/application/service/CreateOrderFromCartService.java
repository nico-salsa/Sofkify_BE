package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.application.dto.CartItemResponse;
import com.sofkify.orderservice.application.dto.CartResponse;
import com.sofkify.orderservice.domain.event.OrderCreatedEvent;
import com.sofkify.orderservice.domain.exception.InvalidCartException;
import com.sofkify.orderservice.domain.exception.OrderAlreadyExistsException;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.ports.in.CreateOrderFromCartUseCase;
import com.sofkify.orderservice.domain.ports.out.CartServicePort;
import com.sofkify.orderservice.domain.ports.out.EventPublisherPort;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class CreateOrderFromCartService implements CreateOrderFromCartUseCase {

    private final OrderRepositoryPort orderRepository;
    private final CartServicePort cartServicePort;
    private final EventPublisherPort eventPublisher;

    public CreateOrderFromCartService(OrderRepositoryPort orderRepository,
                                      CartServicePort cartServicePort,
                                      EventPublisherPort eventPublisher) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null");
        this.cartServicePort = Objects.requireNonNull(cartServicePort, "Cart service port cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }

    @Override
    public Order createOrderFromCart(UUID cartId) {
        if (cartId == null) {
            throw new InvalidCartException("Cart ID cannot be null");
        }

        if (orderRepository.existsByCartId(cartId)) {
            throw new OrderAlreadyExistsException(cartId);
        }

        CartResponse cart = cartServicePort.getCartById(cartId);
        validateConfirmedCart(cart);

        List<OrderItem> orderItems = cart.items().stream()
                .map(this::toOrderItem)
                .toList();

        Order order = Order.createFromCart(cart.id(), cart.customerId(), orderItems);
        Order savedOrder = orderRepository.save(order);

        savedOrder.getDomainEvents().forEach(event -> {
            if (event instanceof OrderCreatedEvent orderCreatedEvent) {
                eventPublisher.publish(orderCreatedEvent);
            }
        });
        savedOrder.clearDomainEvents();

        return savedOrder;
    }

    private void validateConfirmedCart(CartResponse cart) {
        if (cart == null) {
            throw new InvalidCartException("Cart response is null");
        }

        if (!"CONFIRMED".equalsIgnoreCase(cart.status())) {
            throw new InvalidCartException(cart.id(), "Cart must be in CONFIRMED status");
        }

        if (cart.items() == null || cart.items().isEmpty()) {
            throw new InvalidCartException(cart.id(), "Cart must contain at least one item");
        }
    }

    private OrderItem toOrderItem(CartItemResponse item) {
        return new OrderItem(
                UUID.randomUUID(),
                item.productId(),
                item.productName(),
                item.productPrice(),
                item.quantity()
        );
    }
}

