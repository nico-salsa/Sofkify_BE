package com.sofkify.orderservice.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderCreatedEvent {
    private final UUID orderId;
    private final UUID customerId;
    private final UUID cartId;
    private final List<OrderItemEvent> items;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;

    public OrderCreatedEvent(UUID orderId, UUID customerId, UUID cartId, List<OrderItemEvent> items, BigDecimal totalAmount, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public record OrderItemEvent(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
    ) {}

    // Getters
    public UUID getOrderId() { return orderId; }
    public UUID getCustomerId() { return customerId; }
    public UUID getCartId() { return cartId; }
    public List<OrderItemEvent> getItems() { return items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
