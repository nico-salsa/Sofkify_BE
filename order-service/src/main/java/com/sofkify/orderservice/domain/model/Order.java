package com.sofkify.orderservice.domain.model;

import com.sofkify.orderservice.domain.event.DomainEvent;
import com.sofkify.orderservice.domain.event.OrderCreatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID cartId;
    private final UUID customerId;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Order(UUID id, UUID cartId, UUID customerId, List<OrderItem> items) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.cartId = Objects.requireNonNull(cartId, "Cart ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "Order items cannot be null"));
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        this.status = OrderStatus.PENDING;
        this.totalAmount = calculateTotalAmount();
        this.createdAt = LocalDateTime.now();
        
        // Registrar evento de dominio
        this.domainEvents.add(new OrderCreatedEvent(id, cartId, customerId, totalAmount, items));
    }

    private BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(item -> item.getSubtotal().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Order createFromCart(UUID orderId, UUID cartId, UUID customerId, List<Object> cartItems) {
        // Por ahora, asumir que cartItems son similares a OrderItems
        // En una implementación real, esto requeriría transformar CartItem -> OrderItem
        List<OrderItem> orderItems = new ArrayList<>();
        // TODO: Implementar transformación de CartItem a OrderItem cuando esté disponible
        
        return new Order(orderId, cartId, customerId, orderItems);
    }

    public void updateStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "Order status cannot be null");
        
        // Validar transiciones de estado permitidas
        if (status == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of cancelled order");
        }
        
        this.status = newStatus;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCartId() { return cartId; }
    public UUID getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
