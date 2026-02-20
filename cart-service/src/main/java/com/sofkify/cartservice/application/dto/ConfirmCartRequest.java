package com.sofkify.cartservice.application.dto;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO de request para confirmar un carrito.
 * Contiene los datos necesarios para ejecutar la confirmación.
 */
public class ConfirmCartRequest {
    
    private final UUID cartId;
    private final UUID customerId;
    
    public ConfirmCartRequest(UUID cartId, UUID customerId) {
        this.cartId = Objects.requireNonNull(cartId, "Cart ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
    }
    
    public UUID getCartId() {
        return cartId;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmCartRequest that = (ConfirmCartRequest) o;
        return Objects.equals(cartId, that.cartId) && Objects.equals(customerId, that.customerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cartId, customerId);
    }
    
    @Override
    public String toString() {
        return "ConfirmCartRequest{" +
                "cartId=" + cartId +
                ", customerId=" + customerId +
                '}';
    }
}