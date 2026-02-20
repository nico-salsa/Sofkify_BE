package com.sofkify.orderservice.application.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO de response para la creación de orden.
 * Contiene el resultado de la operación de creación.
 */
public class CreateOrderResponse {
    
    private final UUID orderId;
    private final UUID cartId;
    private final boolean success;
    private final String message;
    private final LocalDateTime createdAt;
    
    public CreateOrderResponse(UUID orderId, UUID cartId, boolean success, String message) {
        this.orderId = orderId;
        this.cartId = Objects.requireNonNull(cartId, "Cart ID cannot be null");
        this.success = success;
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.createdAt = success ? LocalDateTime.now() : null;
    }
    
    public UUID getOrderId() {
        return orderId;
    }
    
    public UUID getCartId() {
        return cartId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrderResponse that = (CreateOrderResponse) o;
        return success == that.success && 
               Objects.equals(orderId, that.orderId) && 
               Objects.equals(cartId, that.cartId) && 
               Objects.equals(message, that.message);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, cartId, success, message);
    }
    
    @Override
    public String toString() {
        return "CreateOrderResponse{" +
                "orderId=" + orderId +
                ", cartId=" + cartId +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}