package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.application.dto.CartItemResponse;
import com.sofkify.orderservice.application.dto.CartResponse;
import com.sofkify.orderservice.domain.event.OrderCreatedEvent;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.ports.in.CreateOrderFromCartUseCase;
import com.sofkify.orderservice.domain.ports.out.CartServicePort;
import com.sofkify.orderservice.domain.ports.out.EventPublisherPort;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import com.sofkify.orderservice.domain.exception.CartNotFoundException;
import com.sofkify.orderservice.domain.exception.InvalidCartException;
import com.sofkify.orderservice.domain.exception.OrderAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreateOrderService implements CreateOrderFromCartUseCase {

    private final CartServicePort cartServicePort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public CreateOrderService(CartServicePort cartServicePort, 
                              OrderRepositoryPort orderRepositoryPort,
                              EventPublisherPort eventPublisherPort) {
        this.cartServicePort = cartServicePort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    /**
     * Creates an order from an existing cart and publishes an order-created event.
     *
     * <p>The method ensures cart uniqueness at order level, validates cart existence and
     * non-empty items, persists the new order, and notifies downstream consumers via RabbitMQ.</p>
     *
     * @param cartId source cart identifier
     * @return persisted order
     * @throws OrderAlreadyExistsException when an order already exists for the given cart
     * @throws CartNotFoundException when the cart cannot be fetched from cart-service
     * @throws InvalidCartException when the cart has no items
     */
    @Override
    public Order createOrderFromCart(UUID cartId) {
        // Verificar si ya existe una orden para este carrito
        if (orderRepositoryPort.existsByCartId(cartId)) {
            throw new OrderAlreadyExistsException(cartId);
        }

        // Obtener y validar el carrito
        CartResponse cartResponse = cartServicePort.getCartById(cartId);
        
        if (cartResponse == null) {
            throw new CartNotFoundException(cartId);
        }

        if (cartResponse.items().isEmpty()) {
            throw new InvalidCartException(cartId, "Cart is empty");
        }

        // Convertir items del carrito a items de orden
        List<OrderItem> orderItems = cartResponse.items().stream()
                .map(this::cartItemToOrderItem)
                .collect(Collectors.toList());

        // Crear y guardar la orden
        Order order = new Order(
                UUID.randomUUID(),
                cartId,
                cartResponse.customerId(),
                orderItems
        );

        Order savedOrder = orderRepositoryPort.save(order);

        // Publicar evento de orden creada
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getCartId(),
                orderItems.stream()
                        .map(item -> new OrderCreatedEvent.OrderItemEvent(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getProductPrice(),
                                item.getSubtotal()
                        ))
                        .collect(Collectors.toList()),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );

        eventPublisherPort.publishOrderCreated(event);

        return savedOrder;
    }

    /**
     * Maps an item returned by cart-service into the order domain item model.
     *
     * @param cartItem cart-service DTO item
     * @return order item instance
     */
    private OrderItem cartItemToOrderItem(CartItemResponse cartItem) {
        return new OrderItem(
                UUID.randomUUID(),
                cartItem.productId(),
                cartItem.productName(),
                cartItem.productPrice(),
                cartItem.quantity()
        );
    }
}
