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
}