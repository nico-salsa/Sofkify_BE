package com.sofkify.orderservice.application.dto;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO de request para crear una orden.
 * Contiene los datos necesarios para ejecutar la creación.
 */
public class CreateOrderRequest {
    
    private final UUID cartId;
    private final UUID customerId;
    
    public CreateOrderRequest(UUID cartId, UUID customerId) {
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
        CreateOrderRequest that = (CreateOrderRequest) o;
        return Objects.equals(cartId, that.cartId) && Objects.equals(customerId, that.customerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cartId, customerId);
    }
    
    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "cartId=" + cartId +
                ", customerId=" + customerId +
                '}';
    }
}