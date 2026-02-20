package com.sofkify.cartservice.application.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO de response para la confirmación de carrito.
 * Contiene el resultado de la operación de confirmación.
 */
public class ConfirmCartResponse {
    
    private final UUID cartId;
    private final boolean success;
    private final String message;
    private final LocalDateTime confirmedAt;
    
    public ConfirmCartResponse(UUID cartId, boolean success, String message) {
        this.cartId = Objects.requireNonNull(cartId, "Cart ID cannot be null");
        this.success = success;
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.confirmedAt = success ? LocalDateTime.now() : null;
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
    
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmCartResponse that = (ConfirmCartResponse) o;
        return success == that.success && 
               Objects.equals(cartId, that.cartId) && 
               Objects.equals(message, that.message);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cartId, success, message);
    }
    
    @Override
    public String toString() {
        return "ConfirmCartResponse{" +
                "cartId=" + cartId +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", confirmedAt=" + confirmedAt +
                '}';
    }
}