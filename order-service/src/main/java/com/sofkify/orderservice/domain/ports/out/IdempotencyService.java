package com.sofkify.orderservice.domain.ports.out;

import java.util.UUID;

/**
 * Port for idempotency checking - prevents duplicate event processing.
 * Implemented by infrastructure layer (typically with database persistence).
 */
public interface IdempotencyService {
    
    /**
     * Check if an event has already been processed.
     * 
     * @param eventId The unique event identifier
     * @return true if event was already processed, false otherwise
     */
    boolean isEventProcessed(UUID eventId);
    
    /**
     * Mark an event as processed to prevent duplicate handling.
     * 
     * @param eventId The unique event identifier
     * @param eventType The type of event (e.g., "OrderCreatedEvent")
     * @param aggregateId The ID of the aggregate affected by the event
     */
    void markEventProcessed(UUID eventId, String eventType, UUID aggregateId);
}
