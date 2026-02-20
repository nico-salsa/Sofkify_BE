package com.sofkify.cartservice.domain.ports.out;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StockValidationPort Tests - RED Phase")  
class StockValidationPortTest {

    @Test
    @DisplayName("Should pass - stock availability validation")
    void shouldPassStockAvailabilityValidation() {
        // Mock implementation to test the interface contract
        StockValidationPort stockValidator = new StockValidationPort() {
            @Override
            public boolean isStockAvailable(UUID productId, int requestedQuantity) {
                return requestedQuantity <= 10; // Mock: 10 items available  
            }
            
            @Override
            public int getAvailableStock(UUID productId) {
                return 10; // Mock available stock
            }
            
            @Override
            public boolean reserveStock(UUID productId, int quantity) {
                return quantity <= 10; // Mock reservation logic
            }
            
            @Override
            public void returnStock(UUID productId, int quantity) {
                // Mock return logic
            }
        };
        
        UUID productId = UUID.randomUUID();
        int requestedQuantity = 5;
        
        // Test stock availability check
        int availableStock = stockValidator.getAvailableStock(productId);
        boolean reserved = stockValidator.reserveStock(productId, requestedQuantity);
        
        assertEquals(10, availableStock);
        assertTrue(stockValidator.isStockAvailable(productId, requestedQuantity));
        assertTrue(reserved);
    }

    @Test
    @DisplayName("Should pass - stock reservation operations")
    void shouldPassStockReservationOperations() {
        // This test validates stock reservation behavior
        StockValidationPort stockValidator = new StockValidationPort() {
            private int currentStock = 10;
            
            @Override
            public boolean isStockAvailable(UUID productId, int requestedQuantity) {
                return requestedQuantity <= currentStock;
            }
            
            @Override
            public int getAvailableStock(UUID productId) {
                return currentStock;
            }
            
            @Override
            public boolean reserveStock(UUID productId, int quantity) {
                if (quantity <= currentStock) {
                    currentStock -= quantity;
                    return true; 
                }
                return false;
            }
            
            @Override
            public void returnStock(UUID productId, int quantity) {
                currentStock += quantity;
            }
        };
        
        UUID productId = UUID.randomUUID();
        
        // Initial stock check
        assertEquals(10, stockValidator.getAvailableStock(productId));
        
        // Reserve some stock
        assertTrue(stockValidator.reserveStock(productId, 5));
        assertEquals(5, stockValidator.getAvailableStock(productId));
        
        // Reserve remaining stock
        assertTrue(stockValidator.reserveStock(productId, 5));
        assertEquals(0, stockValidator.getAvailableStock(productId));
        
        // Try to reserve more than available
        assertFalse(stockValidator.reserveStock(productId, 1));
    }
}