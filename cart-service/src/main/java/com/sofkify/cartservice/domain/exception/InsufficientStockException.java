package com.sofkify.cartservice.domain.exception;

import java.util.Objects;
import java.util.UUID;

/**
 * Exception thrown when there is insufficient stock to fulfill a cart confirmation.
 */
public class InsufficientStockException extends RuntimeException {
    
    private final UUID productId;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;
    
    public InsufficientStockException(UUID productId) {
        super("Stock not available for product: " + productId);
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.requestedQuantity = null;
        this.availableQuantity = null;
    }
    
    public InsufficientStockException(UUID productId, String message) {
        super(message);
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.requestedQuantity = null;
        this.availableQuantity = null;
    }
    
    public InsufficientStockException(UUID productId, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product %s. Requested: %d, Available: %d", 
              productId, requestedQuantity, availableQuantity));
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
    
    public String getErrorCode() {
        return "INSUFFICIENT_STOCK";
    }
    
    public String getProductDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Product ID: ").append(productId);
        if (requestedQuantity != null) {
            details.append(", Requested: ").append(requestedQuantity);
        }
        if (availableQuantity != null) {
            details.append(", Available: ").append(availableQuantity);
        }
        return details.toString();
    }
}