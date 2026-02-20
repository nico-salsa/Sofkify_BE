package com.sofkify.cartservice.domain.event;

import com.sofkify.cartservice.domain.model.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Evento de dominio que representa la confirmación de un carrito.
 * Se dispara cuando un carrito pasa de ACTIVE a CONFIRMED.
 */
public class CartConfirmedEvent implements DomainEvent {
    
    private final UUID eventId;
    private final LocalDateTime occurredOn;
    private final UUID cartId;
    private final UUID customerId;
    private final List<CartItemEvent> items;
    private final BigDecimal totalAmount;
    
    public CartConfirmedEvent(UUID cartId, UUID customerId, List<CartItem> cartItems, BigDecimal totalAmount) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
        this.cartId = Objects.requireNonNull(cartId, "Cart ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.items = convertToCartItemEvents(cartItems);
        this.totalAmount = Objects.requireNonNull(totalAmount, "Total amount cannot be null");
    }

    private List<CartItemEvent> convertToCartItemEvents(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> new CartItemEvent(
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(), // Directamente int
                    item.getProductPrice(), // Directamente BigDecimal
                    item.getSubtotal() // Directamente BigDecimal
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
        return "CartConfirmed";
    }

    public UUID getCartId() {
        return cartId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<CartItemEvent> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public record CartItemEvent(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
    ) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartConfirmedEvent that = (CartConfirmedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "CartConfirmedEvent{" +
                "eventId=" + eventId +
                ", cartId=" + cartId +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", occurredOn=" + occurredOn +
                '}';
    }
}