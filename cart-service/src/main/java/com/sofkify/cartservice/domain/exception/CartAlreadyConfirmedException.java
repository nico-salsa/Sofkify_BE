package com.sofkify.cartservice.domain.exception;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Exception thrown when attempting to confirm a cart that is already confirmed.
 */
public class CartAlreadyConfirmedException extends CartException {
    
    private final UUID cartId;
    private final LocalDateTime confirmationTime;
    
    public CartAlreadyConfirmedException(UUID cartId) {
        super("Cart is already confirmed");
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        this.cartId = cartId;
        this.confirmationTime = null;
    }
    
    public CartAlreadyConfirmedException(UUID cartId, String message) {
        super(message);
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        this.cartId = cartId;
        this.confirmationTime = null;
    }
    
    public CartAlreadyConfirmedException(UUID cartId, LocalDateTime confirmationTime) {
        super(String.format("Cart %s was already confirmed at %s", cartId, confirmationTime));
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        this.cartId = cartId;
        this.confirmationTime = confirmationTime;
    }
    
    public CartAlreadyConfirmedException(UUID cartId, Throwable cause) {
        super("Cart is already confirmed", cause);
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        this.cartId = cartId;
        this.confirmationTime = null;
    }
    
    public UUID getCartId() {
        return cartId;
    }
    
    public LocalDateTime getConfirmationTime() {
        return confirmationTime;
    }
    
    public String getErrorCode() {
        return "CART_ALREADY_CONFIRMED";
    }
    
    public String getCartDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Cart ID: ").append(cartId);
        if (confirmationTime != null) {
            details.append(", Confirmation Time: ").append(confirmationTime);
        }
        return details.toString();
    }
}