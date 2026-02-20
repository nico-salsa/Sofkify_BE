package com.sofkify.orderservice.domain.event;

import com.sofkify.orderservice.domain.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderCreatedEvent implements DomainEvent {
    
    private final UUID eventId;
    private final LocalDateTime occurredOn;
    private final UUID orderId;
    private final UUID customerId;
    private final UUID cartId;
    private final List<OrderItemEvent> items;
    private final BigDecimal totalAmount;

    public OrderCreatedEvent(UUID orderId, UUID cartId, UUID customerId, BigDecimal totalAmount, List<OrderItem> orderItems) {
        this(UUID.randomUUID(), LocalDateTime.now(), orderId, cartId, customerId, totalAmount, convertToOrderItemEvents(orderItems));
    }

    // Constructor legacy para mantener compatibilidad
    public OrderCreatedEvent(UUID orderId, UUID customerId, UUID cartId, List<OrderItemEvent> items, BigDecimal totalAmount, LocalDateTime createdAt) {
        this(UUID.randomUUID(), createdAt != null ? createdAt : LocalDateTime.now(), orderId, cartId, customerId, totalAmount, items);
    }

    // Constructor interno para evitar duplicación
    private OrderCreatedEvent(UUID eventId, LocalDateTime occurredOn, UUID orderId, UUID cartId, UUID customerId, BigDecimal totalAmount, List<OrderItemEvent> items) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.cartId = Objects.requireNonNull(cartId, "Cart ID cannot be null");
        this.totalAmount = Objects.requireNonNull(totalAmount, "Total amount cannot be null");
        this.items = Objects.requireNonNull(items, "Items cannot be null");
    }

    private static List<OrderItemEvent> convertToOrderItemEvents(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> new OrderItemEvent(
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity().value(),
                    item.getUnitPrice().amount(),
                    item.getSubtotal().amount()
                ))
                .toList();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "OrderCreated";
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
    public LocalDateTime getCreatedAt() { return occurredOn; } // Alias para compatibilidad
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "eventId=" + eventId +
                ", orderId=" + orderId +
                ", cartId=" + cartId +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
