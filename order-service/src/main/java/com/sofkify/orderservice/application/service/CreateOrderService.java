package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.application.dto.CreateOrderRequest;
import com.sofkify.orderservice.application.dto.CreateOrderResponse;
import com.sofkify.orderservice.domain.event.OrderCreatedEvent;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.ports.in.CreateOrderUseCase;
import com.sofkify.orderservice.domain.ports.out.EventPublisherPort;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;
    
    public CreateOrderService(OrderRepositoryPort orderRepository, 
                              EventPublisherPort eventPublisher) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }

    @Override
    public CreateOrderResponse execute(CreateOrderRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        // Idempotency check: prevent duplicate orders for same cart
        if (orderRepository.existsByCartId(request.getCartId())) {
            throw new IllegalStateException("Order already exists for cart: " + request.getCartId());
        }
        
        // Minimal fallback item while this use case is still not wired to cart retrieval.
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Placeholder Item",
            BigDecimal.ONE,
            1
        ));
        
        // Create order using factory method (auto-generates ID and events)
        Order order = Order.createFromCart(
            request.getCartId(), 
            request.getCustomerId(), 
            items
        );
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Publish domain events
        savedOrder.getDomainEvents().forEach(event -> {
            if (event instanceof OrderCreatedEvent) {
                eventPublisher.publish((OrderCreatedEvent) event);
            }
        });
        savedOrder.clearDomainEvents();
        
        // Return success response
        return new CreateOrderResponse(
            savedOrder.getId(),
            savedOrder.getCartId(),
            true,
            "Order created successfully"
        );
    }
}
