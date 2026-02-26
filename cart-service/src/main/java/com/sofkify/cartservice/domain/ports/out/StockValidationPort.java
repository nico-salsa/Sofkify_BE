package com.sofkify.cartservice.domain.ports.out;

import java.util.UUID;

/**
 * Port for validating product stock availability.
 * This port abstracts the communication with product service for stock validation.
 */
public interface StockValidationPort {
    
    /**
     * Check if sufficient stock is available for a product.
     * 
     * @param productId the product identifier
     * @param requestedQuantity the quantity requested
     * @return true if stock is available, false otherwise
     */
    boolean isStockAvailable(UUID productId, int requestedQuantity);
    
    /**
     * Gets the current available stock for a product.
     * 
     * @param productId The product ID
     * @return The available stock quantity
     */
    int getAvailableStock(UUID productId);
    
    /**
     * Reserves stock for a product (decrements available stock).
     * 
     * @param productId The product ID
     * @param quantity The quantity to reserve
     * @return true if reservation was successful, false if insufficient stock
     */
    boolean reserveStock(UUID productId, int quantity);
    
    /**
     * Returns stock to available inventory (increments available stock).
     * 
     * @param productId The product ID
     * @param quantity The quantity to return
     */
    void returnStock(UUID productId, int quantity);
}